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

package io.github.scambon.cliwrapper4j.internal;

import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.internal.check.CommandLineWrapperChecker;
import io.github.scambon.cliwrapper4j.internal.check.Diagnostic;
import io.github.scambon.cliwrapper4j.internal.handlers.CommandWithParametersMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.ExecutableCommandWithParametersMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.ExecuteMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.IMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.OptionMethodHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.UnhandledMethodHandler;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

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
public final class CommandLineInvocationHandler<W extends ICommandLineWrapper>
    implements InvocationHandler {

  /** The execute method. */
  public static final Method EXECUTE_METHOD;

  static {
    try {
      EXECUTE_METHOD = ICommandLineWrapper.class.getMethod("execute");
    } catch (NoSuchMethodException | SecurityException exception) {
      throw new RuntimeException(exception);
    }
  }

  /** The method to handler map. */
  private final Map<Method, IMethodHandler> method2HandlerMap;
  /** The executable node. */
  private ExecutableNode executableNode;

  /**
   * Instantiates a new command line invocation handler.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @param method2HandlerMap
   *          the method 2 handler map
   * @param executionEnvironment
   *          the execution environment
   */
  public CommandLineInvocationHandler(Class<W> commandLineWrapperInterface,
      Map<Method, IMethodHandler> method2HandlerMap, IExecutionEnvironment executionEnvironment) {
    this.method2HandlerMap = method2HandlerMap;
    Executable executableAnnotation = commandLineWrapperInterface.getAnnotation(Executable.class);
    String[] executable = executableAnnotation.value();
    this.executableNode = new ExecutableNode(executable, executionEnvironment);
  }

  /**
   * Creates the handlers.
   *
   * @param <C>
   *          the generic type
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   * @return the map
   */
  public static <C extends ICommandLineWrapper> Map<Method, IMethodHandler> createHandlers(
      Class<C> commandLineWrapperInterface) {
    CommandLineWrapperChecker checker = new CommandLineWrapperChecker();
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
        IMethodHandler handler = createHandler(method);
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
   * @return the method handler
   */
  private static IMethodHandler createHandler(Method method) {
    int modifiers = method.getModifiers();
    if (method.isDefault() || Modifier.isPrivate(modifiers)) {
      return new UnhandledMethodHandler(method);
    } else {
      Option option = method.getAnnotation(Option.class);
      if (option != null) {
        return new OptionMethodHandler(method, option);
      } else {
        Command command = method.getAnnotation(Command.class);
        if (command != null) {
          boolean executesOnCommand = ExecutableNode.executesOnCommand(method);
          if (executesOnCommand) {
            return new ExecutableCommandWithParametersMethodHandler(method, command);
          } else {
            return new CommandWithParametersMethodHandler(method, command);
          }
        } else {
          throw new CommandLineException("This should be found earlier...");
        }
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