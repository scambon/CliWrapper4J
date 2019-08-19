/*
 * Copyright 2018-2019 Sylvain Cambon
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
import io.github.scambon.cliwrapper4j.instantiators.CachingInstantiator;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;
import io.github.scambon.cliwrapper4j.instantiators.ReflectiveInstantiator;
import io.github.scambon.cliwrapper4j.internal.ExecutableHandler;
import io.github.scambon.cliwrapper4j.internal.handlers.IMethodHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Function;

/**
 * The standard factory that creates {@link IExecutable} sub-interfaces objects using JDK proxies.
 *
 * @param <W>
 *          the wrapper type
 */
public class ReflectiveExecutableFactory<W extends IExecutable>
    implements IExecutableFactory<W> {

  /** The executable interface. */
  private final Class<W> executableInterface;
  /** The method 2 handler map. */
  private final Map<Method, IMethodHandler> method2HandlerMap;
  /** The proxy constructor. */
  private final Function<ExecutableHandler<W>, W> proxyConstructor;
  /** The instantiator. */
  private final IInstantiator instantiator;

  /**
   * Instantiates a factory with default reflective handling of constructors.
   *
   * @param executableInterface
   *          the executable interface
   */
  public ReflectiveExecutableFactory(Class<W> executableInterface) {
    this(executableInterface, new CachingInstantiator(new ReflectiveInstantiator()));
  }

  /**
   * Instantiates a factory.
   *
   * @param executableInterface
   *          the executable interface
   * @param instantiator
   *          the instantiator
   */
  @SuppressWarnings("unchecked")
  public ReflectiveExecutableFactory(Class<W> executableInterface, IInstantiator instantiator) {
    this.executableInterface = executableInterface;
    this.instantiator = instantiator;
    this.method2HandlerMap = ExecutableHandler.createHandlers(executableInterface, instantiator);
    ClassLoader classLoader = executableInterface.getClassLoader();
    Class<?>[] interfaces = new Class<?>[]{executableInterface};
    this.proxyConstructor = handler -> (W) Proxy.newProxyInstance(classLoader, interfaces, handler);
  }

  @Override
  public W create(IExecutionEnvironment executionEnvironment) {
    ExecutableHandler<W> invocationHandler = new ExecutableHandler<>(
        executableInterface, method2HandlerMap, instantiator, executionEnvironment);
    return proxyConstructor.apply(invocationHandler);
  }
}