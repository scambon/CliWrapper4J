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

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.internal.CommandLineInvocationHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A checker that ensures a command line wrapper interface is well-formed.
 */
public class CommandLineWrapperChecker {

  /** The annotations for methods. */
  private static final List<Class<? extends Annotation>> METHOD_ANNOTATIONS = Arrays.asList(
      Command.class,
      Option.class,
      Flattener.class,
      Aggregator.class);
  /** The annotations for parameters. */
  private static final List<Class<? extends Annotation>> PARAMETER_ANNOTATIONS = Arrays.asList(
      Converter.class,
      Extra.class);

  /**
   * Validates the interface.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @return the diagnostic
   */
  public Diagnostic validateInterface(
      Class<? extends ICommandLineWrapper> commandLineWrapperInterface) {
    Diagnostic diagnostic = new Diagnostic(commandLineWrapperInterface);
    if (!commandLineWrapperInterface.isInterface()) {
      diagnostic.addIssue(commandLineWrapperInterface, "Is not an interface");
    }
    checkExecutableAnnotation(commandLineWrapperInterface, diagnostic);
    Method[] methods = commandLineWrapperInterface.getDeclaredMethods();
    for (Method method : methods) {
      checkMethod(commandLineWrapperInterface, method, diagnostic);
    }
    return diagnostic;
  }

  /**
   * Validates the executable annotation.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param diagnostic
   *          the diagnostic
   */
  private void checkExecutableAnnotation(
      Class<? extends ICommandLineWrapper> commandLineWrapperInterface, Diagnostic diagnostic) {
    Executable executableAnnotation = commandLineWrapperInterface.getAnnotation(Executable.class);
    if (executableAnnotation == null) {
      diagnostic.addIssue(commandLineWrapperInterface, "No @Executable annotation found");
    } else {
      String[] executable = executableAnnotation.value();
      if (executable.length == 0) {
        diagnostic.addIssue(commandLineWrapperInterface, "Illegal empty executable array");
      } else if (executable[0].isEmpty()) {
        diagnostic.addIssue(commandLineWrapperInterface, "Illegal empty executable name");
      }
    }
  }

  /**
   * Checks a single method.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param method
   *          the method
   * @param diagnostic
   *          the diagnostic
   */
  private void checkMethod(
      Class<? extends ICommandLineWrapper> commandLineWrapperInterface, Method method,
      Diagnostic diagnostic) {
    if (CommandLineInvocationHandler.EXECUTE_METHOD.equals(method) || method.isSynthetic()) {
      return;
    }
    List<String> modifiers = new ArrayList<>();
    int methodModifiers = method.getModifiers();
    boolean isDefaultMethod = method.isDefault();
    if (isDefaultMethod) {
      modifiers.add("default");
    }
    boolean isStaticMethod = Modifier.isStatic(methodModifiers);
    if (isStaticMethod) {
      modifiers.add("!public");
    }
    boolean isNotPublicMethod = !Modifier.isPublic(methodModifiers);
    if (isStaticMethod) {
      modifiers.add("static");
    }
    String modifiersString = String.join(",", modifiers);
    Class<?> returnType = method.getReturnType();
    Parameter[] parameters = method.getParameters();

    Command commandAnnotation = method.getAnnotation(Command.class);
    boolean isCommand = commandAnnotation != null;
    Option optionAnnotation = method.getAnnotation(Option.class);
    boolean isOption = optionAnnotation != null;

    // Non-handled methods should have no annotation
    boolean isIgnored = isDefaultMethod || isStaticMethod || isNotPublicMethod;
    if (isIgnored || (!isCommand && !isOption)) {
      checkIgnoredMethod(method, modifiersString, diagnostic);
    }

    // If we have the command annotation
    if (isCommand) {
      checkCommandMethod(commandLineWrapperInterface, method,
          returnType, parameters, commandAnnotation, isOption,
          diagnostic);
    }

    // If we have an @Option
    if (isOption) {
      checkOptionMethod(commandLineWrapperInterface, method,
          returnType, parameters, isCommand, diagnostic);
    }

    // If not @Command nor @Option
    if (!isCommand && !isOption) {
      checkNotCommandNorOptionMethod(method, modifiersString, parameters, diagnostic);
    }

    // If not ignored and not @Command nor @Option
    if (!isIgnored && !isCommand && !isOption) {
      checkUnhandledMethod(method, diagnostic);
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
  private void checkIgnoredMethod(Method method, String modifiersString, Diagnostic diagnostic) {
    for (Class<? extends Annotation> methodAnnotation : METHOD_ANNOTATIONS) {
      Annotation annotation = method.getAnnotation(methodAnnotation);
      if (annotation != null) {
        diagnostic.addIssue(method, "With modifier(s) '" + modifiersString
            + "' should not have an '" + methodAnnotation + "' annotation");
      }
    }
  }

  /**
   * Checks a command method.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param method
   *          the method
   * @param returnType
   *          the return type
   * @param parameters
   *          the parameters
   * @param commandAnnotation
   *          the command annotation
   * @param isOption
   *          whether the method is an option
   * @param diagnostic
   *          the diagnostic
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void checkCommandMethod(Class<? extends ICommandLineWrapper> commandLineWrapperInterface,
      Method method,
      Class<?> returnType, Parameter[] parameters, Command commandAnnotation, boolean isOption,
      Diagnostic diagnostic) {
    // Check the conversion
    IConverter resultConverter = createInstance(commandAnnotation, Command::converter);
    if (returnType.equals(commandLineWrapperInterface)) {
      Class<?> outType = commandAnnotation.outType();
      boolean canConvert = resultConverter.canConvert(Result.class, outType);
      if (!canConvert) {
        diagnostic.addIssue(method,
            "Cannot convert its future return type to '" + outType + "'");
      }
    } else {
      boolean canConvert = resultConverter.canConvert(Result.class, returnType);
      if (!canConvert) {
        diagnostic.addIssue(method,
            "Cannot convert its return type to '" + returnType + "'");
      }
    }

    // Check the parameters
    Set<String> extraParameterNames = new HashSet<>();
    for (Parameter parameter : parameters) {
      Converter converterAnnotation = parameter.getAnnotation(Converter.class);
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);
      if (converterAnnotation != null && extraAnnotation != null) {
        diagnostic.addIssue(method,
            "Parameters cannot be annotated with both @Converter and @Extra");
      }
      if (extraAnnotation != null) {
        String extraParameterName = extraAnnotation.value();
        if (extraParameterNames.contains(extraParameterName)) {
          diagnostic.addIssue(method, "Multiple parameters should not have the same @Extra name '"
              + extraParameterName + "'");
        }
        extraParameterNames.add(extraParameterName);
      }
    }

    checkCommandOrOptionMethodSharedBehaviors(method, parameters, true, isOption, diagnostic);
  }

  /**
   * Checks an option method.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param method
   *          the method
   * @param returnType
   *          the return type
   * @param parameters
   *          the parameters
   * @param isCommand
   *          whether the method is a command
   * @param diagnostic
   *          the diagnostic
   */
  private void checkOptionMethod(Class<? extends ICommandLineWrapper> commandLineWrapperInterface,
      Method method, Class<?> returnType, Parameter[] parameters, boolean isCommand,
      Diagnostic diagnostic) {
    // Methods must return their interface
    if (!returnType.equals(commandLineWrapperInterface)) {
      diagnostic.addIssue(method, "Must return its interface '"
          + commandLineWrapperInterface + "' instead of '" + returnType + "'");
    }

    // Options should not have Extra parameters
    for (Parameter parameter : parameters) {
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);
      if (extraAnnotation != null) {
        diagnostic.addIssue(method, "@Extra is not allowed on @Option method parameters");
      }
    }

    // Check shared behaviors for both @Command and @Option
    checkCommandOrOptionMethodSharedBehaviors(method, parameters, isCommand, true, diagnostic);
  }

  /**
   * Checks the shared behaviors for a command or option method.
   *
   * @param method
   *          the method
   * @param parameters
   *          the parameters
   * @param isCommand
   *          whether the method is a command
   * @param isOption
   *          whether the method is an option
   * @param diagnostic
   *          the diagnostic
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private void checkCommandOrOptionMethodSharedBehaviors(Method method,
      Parameter[] parameters, boolean isCommand, boolean isOption, Diagnostic diagnostic) {
    // Cannot be @Command and @Option at the same times
    if (isCommand && isOption) {
      diagnostic.addIssue(method,
          "Should not have both the '"
              + Command.class + "' and '" + Option.class + "' annotations");
    }

    // Check parameters
    for (Parameter parameter : parameters) {
      Converter converterAnnotation = parameter.getAnnotation(Converter.class);
      if (converterAnnotation != null) {
        IConverter converter = createInstance(converterAnnotation, Converter::value);
        Class<?> parameterType = parameter.getType();
        boolean canConvert = converter.canConvert(parameterType, String.class);
        if (!canConvert) {
          diagnostic.addIssue(method,
              "Cannot convert its parameter '" + parameter
                  + "' from '" + parameterType + "' to '" + String.class + "'");
        }
      }
    }
  }

  /**
   * Checks a method that is neither a command nor an option.
   *
   * @param method
   *          the method
   * @param modifiersString
   *          the modifiers string
   * @param parameters
   *          the parameters
   * @param diagnostic
   *          the diagnostic
   * @return the list
   */
  private void checkNotCommandNorOptionMethod(Method method, String modifiersString,
      Parameter[] parameters, Diagnostic diagnostic) {
    for (Parameter parameter : parameters) {
      for (Class<? extends Annotation> annotationClass : PARAMETER_ANNOTATIONS) {
        Annotation annotation = parameter.getAnnotation(annotationClass);
        if (annotation != null) {
          diagnostic.addIssue(parameter,
              "Should not have a '" + annotation + "' annotation");
        }
      }
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
        "Is neither static, nor default, nor not public, nor annoted with @Command or @Option");
  }
}