/* Copyright 2018-2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.internal;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ExecuteLater;
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;
import io.github.scambon.cliwrapper4j.internal.check.Diagnostic;
import io.github.scambon.cliwrapper4j.internal.check.ExecutableSubInterfaceChecker;
import io.github.scambon.cliwrapper4j.internal.handlers.ExecuteLaterSwitchMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.ExecuteMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.ExecuteNowSwitchMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.IMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.SwitchMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.UnhandledMethodHandler;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;
import io.github.scambon.cliwrapper4j.preprocessors.ICommandLinePreProcessor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * The invocation handler that works behind the scene of the CLiWrapper4J library.
 *
 * @param <W>
 *          the command line wrapper type
 */
public final class ExecutableHandler<W extends IExecutable>
    implements InvocationHandler {

  /** The execute method. */
  public static final Method EXECUTE_METHOD;

  static {
    try {
      EXECUTE_METHOD = IExecutable.class.getMethod("execute");
    } catch (NoSuchMethodException | SecurityException exception) {
      throw new CommandLineException(exception);
    }
  }

  /** The method to handler map. */
  private final Map<Method, IMethodHandler> method2HandlerMap;
  /** The executable node. */
  private final ExecutableNode executableNode;

  /**
   * Instantiates a new command line invocation handler.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param method2HandlerMap
   *          the method 2 handler map
   * @param instantiator
   *          the instantiator
   * @param executionEnvironment
   *          the execution environment
   */
  public ExecutableHandler(Class<W> commandLineWrapperInterface,
      Map<Method, IMethodHandler> method2HandlerMap, IInstantiator instantiator,
      IExecutionEnvironment executionEnvironment) {
    this.method2HandlerMap = method2HandlerMap;
    Executable executableAnnotation = commandLineWrapperInterface.getAnnotation(Executable.class);
    String[] executable = executableAnnotation.value();
    Class<? extends ICommandLinePreProcessor>[] preProcessorClasses =
        executableAnnotation.preProcessors();
    this.executableNode = new ExecutableNode(executable, preProcessorClasses, instantiator,
        executionEnvironment);
  }

  /**
   * Creates the handlers.
   *
   * @param <C>
   *          the generic type
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param instantiator
   *          the instantiator
   * @return the handlers map
   */
  public static <C extends IExecutable> Map<Method, IMethodHandler> createHandlers(
      Class<C> commandLineWrapperInterface, IInstantiator instantiator) {
    ExecutableSubInterfaceChecker checker = new ExecutableSubInterfaceChecker(instantiator);
    Diagnostic diagnostic = checker.validateInterface(commandLineWrapperInterface);
    diagnostic.check();
    Map<Method, IMethodHandler> method2HandlerMap = new HashMap<>();
    method2HandlerMap.put(EXECUTE_METHOD, new ExecuteMethodHandler());
    for (Method method : commandLineWrapperInterface.getDeclaredMethods()) {
      // Some methods are ignored:
      // - Synthetic methods, e.g. JaCoCo's methods
      // - Static methods
      int modifiers = method.getModifiers();
      if (!method.isSynthetic() && !Modifier.isStatic(modifiers)) {
        IMethodHandler handler = createHandler(method, instantiator);
        method2HandlerMap.put(method, handler);
      }
    }
    return method2HandlerMap;
  }

  /**
   * Creates the method handler.
   *
   * @param method
   *          the method
   * @param instantiator
   *          the instantiator
   * @return the method handler
   */
  private static IMethodHandler createHandler(Method method, IInstantiator instantiator) {
    int modifiers = method.getModifiers();
    if (method.isDefault() || Modifier.isPrivate(modifiers)) {
      return new UnhandledMethodHandler(method);
    } else {
      Switch zwitchAnnotation = method.getAnnotation(Switch.class);
      ExecuteNow executeNowAnnotation = method.getAnnotation(ExecuteNow.class);
      ExecuteLater executeLaterAnnotation = method.getAnnotation(ExecuteLater.class);
      if (executeNowAnnotation != null) {
        return new ExecuteNowSwitchMethodHandler(method, zwitchAnnotation, executeNowAnnotation,
            instantiator);
      } else if (executeLaterAnnotation != null) {
        return new ExecuteLaterSwitchMethodHandler(
            method, zwitchAnnotation, executeLaterAnnotation, instantiator);
      } else {
        return new SwitchMethodHandler(method, zwitchAnnotation, instantiator);
      }
    }
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
    IMethodHandler methodHandler = method2HandlerMap.get(method);
    if (methodHandler == null) {
      throw new CommandLineException("Unhandled method '" + method + "'");
    }
    return methodHandler.handle(proxy, arguments, executableNode);
  }
}