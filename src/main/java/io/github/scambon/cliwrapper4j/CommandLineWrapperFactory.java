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

package io.github.scambon.cliwrapper4j;

import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.internal.CommandLineInvocationHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.IMethodHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Function;

/**
 * The standard factory that creates {@link ICommandLineWrapper} sub-interfaces objects using JDK
 * proxies.
 *
 * @param <W>
 *          the wrapper type
 */
public class CommandLineWrapperFactory<W extends ICommandLineWrapper>
    implements ICommandLineWrapperFactory<W> {

  /** The command line wrapper interface. */
  private final Class<W> commandLineWrapperInterface;
  /** The method 2 handler map. */
  private final Map<Method, IMethodHandler> method2HandlerMap;
  /** The proxy constructor. */
  private final Function<CommandLineInvocationHandler<W>, W> proxyConstructor;

  /**
   * Instantiates a new command line wrapper factory.
   *
   * @param commandLineWrapperInterface
   *          the command line wrapper interface
   */
  @SuppressWarnings("unchecked")
  public CommandLineWrapperFactory(Class<W> commandLineWrapperInterface) {
    this.commandLineWrapperInterface = commandLineWrapperInterface;
    this.method2HandlerMap = CommandLineInvocationHandler
        .createHandlers(commandLineWrapperInterface);
    ClassLoader classLoader = CommandLineWrapperFactory.class.getClassLoader();
    Class<?>[] interfaces = new Class<?>[]{commandLineWrapperInterface};
    this.proxyConstructor = handler -> (W) Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  @Override
  public W create(IExecutionEnvironment executionEnvironment) {
    CommandLineInvocationHandler<W> invocationHandler = new CommandLineInvocationHandler<>(
        commandLineWrapperInterface, method2HandlerMap, executionEnvironment);
    W proxy = this.proxyConstructor.apply(invocationHandler);
    return proxy;
  }
}