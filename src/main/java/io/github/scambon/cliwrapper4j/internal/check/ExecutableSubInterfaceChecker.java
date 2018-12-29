/* Copyright 2018 Sylvain Cambon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.scambon.cliwrapper4j.internal.check;

import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.createInstance;
import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefaultClass;
import static java.util.Arrays.stream;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ExecuteLater;
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.Executor;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.ReturnCode;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.internal.ExecutableHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * A checker that ensures an {@link IExecutable} interface is well-formed.
 */
public final class ExecutableSubInterfaceChecker {

  /** The annotations for methods. */
  private static final List<Class<? extends Annotation>> METHOD_ANNOTATIONS = Arrays.asList(
      Switch.class,
      Flattener.class,
      Aggregator.class,
      ExecuteNow.class,
      ExecuteLater.class,
      ReturnCode.class);
  /** The annotations for parameters. */
  private static final List<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Arrays.asList(
      Converter.class,
      Extra.class);

  /**
   * Validates the interface.
   *
   * @param executableInterface
   *          the executable interface
   * @return the diagnostic
   */
  public Diagnostic validateInterface(
      Class<? extends IExecutable> executableInterface) {
    Diagnostic diagnostic = new Diagnostic(executableInterface);
    if (!executableInterface.isInterface()) {
      diagnostic.addIssue(executableInterface, "Is not an interface");
    }
    checkExecutableAnnotation(executableInterface, diagnostic);
    Method[] methods = executableInterface.getDeclaredMethods();
    for (Method method : methods) {
      checkMethod(executableInterface, method, diagnostic);
    }
    return diagnostic;
  }

  /**
   * Checks the executable annotation.
   *
   * @param executableInterface
   *          the executable interface
   * @param diagnostic
   *          the diagnostic
   */
  private void checkExecutableAnnotation(
      Class<? extends IExecutable> executableInterface, Diagnostic diagnostic) {
    Executable executableAnnotation = executableInterface.getAnnotation(Executable.class);
    if (executableAnnotation == null) {
      diagnostic.addIssue(executableInterface, "No @Executable annotation found");
    } else {
      String[] executable = executableAnnotation.value();
      if (executable.length == 0) {
        diagnostic.addIssue(executableInterface, "Illegal empty executable array");
      } else if (executable[0].isEmpty()) {
        diagnostic.addIssue(executableInterface, "Illegal empty executable name");
      }
    }
  }

  /**
   * Checks a single method.
   *
   * @param executableInterface
   *          the executable interface
   * @param method
   *          the method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkMethod(
      Class<? extends IExecutable> executableInterface, Method method,
      Diagnostic diagnostic) {
    // No need to check the execute method or synthetic methods
    if (ExecutableHandler.EXECUTE_METHOD.equals(method) || method.isSynthetic()) {
      return;
    }

    // Check ignored methods
    boolean isDefaultMethod = method.isDefault();
    int methodModifiers = method.getModifiers();
    boolean isStaticMethod = Modifier.isStatic(methodModifiers);
    boolean isNotPublicMethod = !Modifier.isPublic(methodModifiers);
    boolean isIgnored = isDefaultMethod || isStaticMethod || isNotPublicMethod;
    if (isIgnored) {
      checkIgnoredMethod(method, diagnostic);
    }

    checkAnnotationDependencies(method, diagnostic);

    // Check @Switch methods
    Switch switchAnnotation = method.getAnnotation(Switch.class);
    boolean isSwitch = switchAnnotation != null;
    if (isSwitch) {
      checkSwitchMethod(executableInterface, method, diagnostic);
    }

    // If not ignored and not @Switch
    if (!isIgnored && !isSwitch) {
      checkUnhandledMethod(method, diagnostic);
    }
  }

  /**
   * Checks annotation dependencies.
   *
   * @param method
   *          the method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkAnnotationDependencies(Method method, Diagnostic diagnostic) {
    checkAnnotationDependency(method, diagnostic, ExecuteNow.class, Switch.class);
    checkAnnotationDependency(method, diagnostic, ExecuteLater.class, Switch.class);
    checkAnnotationDependency(
        method, diagnostic, Converter.class, ExecuteNow.class, ExecuteLater.class);
    checkAnnotationDependency(
        method, diagnostic, Executor.class, ExecuteNow.class, ExecuteLater.class);
    checkAnnotationDependency(
        method, diagnostic, ReturnCode.class, ExecuteNow.class, ExecuteLater.class);
  }

  /**
   * Checks that an annotation dependency is fulfilled.
   *
   * @param method
   *          the method
   * @param diagnostic
   *          the diagnostic
   * @param ifAs
   *          the dependent annotation
   * @param thenRequire
   *          the possible dependencies
   */
  @SafeVarargs
  private final void checkAnnotationDependency(
      Method method, Diagnostic diagnostic,
      Class<? extends Annotation> ifAs,
      Class<? extends Annotation>... thenRequire) {
    Annotation ifAsAnnotation = method.getAnnotation(ifAs);
    if (ifAsAnnotation != null) {
      boolean thenRequireFound = stream(thenRequire)
          .map(method::getAnnotation)
          .anyMatch(Objects::nonNull);
      if (!thenRequireFound) {
        diagnostic.addIssue(method,
            "Found annotation '@" + ifAs + "' but the required annotation in '"
                + Arrays.toString(thenRequire) + "' is not found");
      }
    }
  }

  /**
   * Checks an ignored method.
   *
   * @param method
   *          the ignored method
   * @param modifiersString
   *          the modifiers string
   * @param diagnostic
   *          the diagnostic
   */
  private void checkIgnoredMethod(Method method, Diagnostic diagnostic) {
    for (Class<? extends Annotation> methodAnnotation : METHOD_ANNOTATIONS) {
      Annotation annotation = method.getAnnotation(methodAnnotation);
      if (annotation != null) {
        diagnostic.addIssue(method,
            "Non-@Switch method should not have an '@" + methodAnnotation + "'");
      }
    }
    for (Parameter parameter : method.getParameters()) {
      for (Class<? extends Annotation> annotationClass : PARAMETER_ANNOTATIONS) {
        Annotation annotation = parameter.getAnnotation(annotationClass);
        if (annotation != null) {
          diagnostic.addIssue(parameter,
              "Non-@Switch method should not have a '@" + annotation + "' on its parameter");
        }
      }
    }
  }

  /**
   * Checks a switch method.
   *
   * @param executableInterface
   *          the executable interface
   * @param switchMethod
   *          the switch method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkSwitchMethod(
      Class<? extends IExecutable> executableInterface,
      Method switchMethod, Diagnostic diagnostic) {
    checkSwitchMethodParameters(switchMethod, diagnostic);

    // Check @ExecuteNow and @ExecuteLater
    ExecuteNow executeNowAnnotation = switchMethod.getAnnotation(ExecuteNow.class);
    boolean isExecuteNow = executeNowAnnotation != null;
    ExecuteLater executeLaterAnnotation = switchMethod.getAnnotation(ExecuteLater.class);
    boolean isExecuteLater = executeLaterAnnotation != null;
    boolean isExecute = isExecuteNow || isExecuteLater;
    if (isExecute) {
      checkExecuteMethod(executableInterface, switchMethod, diagnostic);
    }

    // Check flattener
    Flattener flattenerAnnotation = switchMethod.getAnnotation(Flattener.class);
    if (flattenerAnnotation != null) {
      checkSwitchFlattenerAnnotation(switchMethod, diagnostic);
    }
    
    // Check aggregator
    Aggregator aggregatorAnnotation = switchMethod.getAnnotation(Aggregator.class);
    if (aggregatorAnnotation != null) {
      checkSwitchAggregatorAnnotation(switchMethod, diagnostic);
    }
  }

  /**
   * Checks a switch flattener annotation.
   *
   * @param switchMethod
   *          the switch method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkSwitchFlattenerAnnotation(Method switchMethod, Diagnostic diagnostic) {
    Flattener flattenerAnnotation = switchMethod.getAnnotation(Flattener.class);
    Class<? extends IFlattener> flattenerClass = flattenerAnnotation.flattener();
    if (!isReflectivelyCreatable(flattenerClass)) {
      diagnostic.addIssue(switchMethod, "The @Flattener class '" + flattenerClass
          + "' must have a public 0-arg constructor");
    }
  }
  
  /**
   * Checks switch aggregator annotation.
   *
   * @param switchMethod
   *          the switch method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkSwitchAggregatorAnnotation(Method switchMethod, Diagnostic diagnostic) {
    Aggregator aggregatorAnnotation = switchMethod.getAnnotation(Aggregator.class);
    Class<? extends IAggregator> aggregatorClass = aggregatorAnnotation.aggregator();
    if (!isReflectivelyCreatable(aggregatorClass)) {
      diagnostic.addIssue(switchMethod, "The @Aggregator class '" + aggregatorClass
          + "' must have a public 0-arg constructor");
    }
  }

  /**
   * Checks a switch method parameters.
   *
   * @param switchMethod
   *          the switch method
   * @param diagnostic
   *          the diagnostic
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void checkSwitchMethodParameters(Method switchMethod, Diagnostic diagnostic) {
    Set<String> extraParameterNames = new HashSet<>();
    for (Parameter parameter : switchMethod.getParameters()) {
      Converter converterAnnotation = parameter.getAnnotation(Converter.class);
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);

      // Check @Converter and @Extra incompatibility
      if (converterAnnotation != null && extraAnnotation != null) {
        diagnostic.addIssue(switchMethod,
            "Parameters cannot be annotated with both @Converter and @Extra");
      }

      // Check @Converter compatibility
      if (converterAnnotation != null) {
        IConverter converter = createInstance(converterAnnotation, Converter::value);
        Class<?> parameterType = parameter.getType();
        boolean canConvert = converter.canConvert(parameterType, String.class);
        if (!canConvert) {
          diagnostic.addIssue(switchMethod,
              "Cannot convert its parameter '" + parameter
                  + "' from '" + parameterType + "' to '" + String.class + "'");
        }
      }

      // Check @Extra parameter
      if (extraAnnotation != null) {
        String extraParameterName = extraAnnotation.value();
        if (extraParameterNames.contains(extraParameterName)) {
          diagnostic.addIssue(switchMethod,
              "Multiple parameters should not have the same @Extra name '"
                  + extraParameterName + "'");
        }
        extraParameterNames.add(extraParameterName);
      }
    }
  }

  /**
   * Checks a command method.
   *
   * @param executableInterface
   *          the executable interface
   * @param executeMethod
   *          the method
   * @param returnType
   *          the return type
   * @param parameters
   *          the parameters
   * @param executeAnnotation
   *          the command annotation
   * @param isSwitch
   *          whether the method is a switch
   * @param diagnostic
   *          the diagnostic
   */
  private void checkExecuteMethod(
      Class<? extends IExecutable> executableInterface, Method executeMethod,
      Diagnostic diagnostic) {
    ExecuteNow executeNowAnnotation = executeMethod.getAnnotation(ExecuteNow.class);
    boolean isExecuteNow = executeNowAnnotation != null;
    ExecuteLater executeLaterAnnotation = executeMethod.getAnnotation(ExecuteLater.class);
    boolean isExecuteLater = executeLaterAnnotation != null;

    // If both @ExecuteNow and @ExecuteLater
    if (isExecuteNow && isExecuteLater) {
      diagnostic.addIssue(executeMethod,
          "Cannot be annotated with both @ExecuteNow and @ExecuteLater");
    }

    // If @ExecuteNow
    if (isExecuteNow) {
      checkExecuteNowMethod(executeMethod, diagnostic);
    }

    // If @ExecuteLater
    if (isExecuteLater) {
      checkExecuteLaterMethod(executeMethod, diagnostic);
    }

    // If @Executor
    Executor executorAnnotation = executeMethod.getAnnotation(Executor.class);
    if (executorAnnotation != null) {
      checkExecutorAnnotation(executeMethod, diagnostic);
    }
  }

  /**
   * Checks an @ExecuteNow method.
   *
   * @param executeNowMethod
   *          the execute method
   * @param diagnostic
   *          the diagnostic
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void checkExecuteNowMethod(Method executeNowMethod, Diagnostic diagnostic) {
    IConverter resultConverter = getOrDefaultClass(
        executeNowMethod, Converter.class, Converter::value, ResultConverter::new);
    Class<?> returnType = executeNowMethod.getReturnType();
    boolean canConvert = resultConverter.canConvert(Result.class, returnType);
    if (!canConvert) {
      diagnostic.addIssue(executeNowMethod,
          "Cannot convert its return type to '" + returnType + "'");
    }
  }

  /**
   * Checks and @ExecuteLater method.
   *
   * @param executeLaterMethod
   *          the execute method
   * @param diagnostic
   *          the diagnostic
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void checkExecuteLaterMethod(Method executeLaterMethod, Diagnostic diagnostic) {
    IConverter resultConverter = getOrDefaultClass(
        executeLaterMethod, Converter.class, Converter::value, ResultConverter::new);
    ExecuteLater executeLaterAnnotation = executeLaterMethod.getAnnotation(ExecuteLater.class);
    Class<?> outType = executeLaterAnnotation.value();
    boolean canConvert = resultConverter.canConvert(Result.class, outType);
    if (!canConvert) {
      diagnostic.addIssue(executeLaterMethod,
          "Cannot convert its future return type to '" + outType + "'");
    }
  }

  /**
   * Checks the executor annotation.
   *
   * @param executeMethod
   *          the execute method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkExecutorAnnotation(Method executeMethod, Diagnostic diagnostic) {
    Executor executorAnnotation = executeMethod.getAnnotation(Executor.class);
    Class<? extends IExecutor> executorClass = executorAnnotation.value();
    if (!isReflectivelyCreatable(executorClass)) {
      diagnostic.addIssue(executeMethod,
          "The executor class '" + executorClass + "' must have a public 0-arg constructor");
    }
  }

  /**
   * Checks an unhandled method.
   *
   * @param method
   *          the unhandled method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkUnhandledMethod(Method method, Diagnostic diagnostic) {
    diagnostic.addIssue(method,
        "Is neither static, nor default, nor not public, nor annoted with @Command or @Switch");
  }

  /**
   * Checks if a method is reflectively creatable.
   *
   * @param clazz
   *          the class to check
   * @return whether reflectively creatable
   */
  private boolean isReflectivelyCreatable(Class<?> clazz) {
    return stream(clazz.getConstructors())
        .filter(constructor -> constructor.getParameterCount() == 0)
        .anyMatch(constructor -> Modifier.isPublic(constructor.getModifiers()));
  }
}