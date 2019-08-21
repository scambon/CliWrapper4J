/*
 * Copyright 2018 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.internal.handlers;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;
import io.github.scambon.cliwrapper4j.internal.utils.MethodHandleUtils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * A method handler that works for user-defined methods.
 */
public final class UnhandledMethodHandler implements IMethodHandler {

  /** The method handle. */
  private final MethodHandle methodHandle;

  /**
   * Instantiates a new unhandled method handler.
   *
   * @param method
   *          the method
   */
  public UnhandledMethodHandler(Method method) {
    try {
      this.methodHandle = MethodHandleUtils.getMethodHandle(method);
    } catch (ReflectiveOperationException reflectiveOperationException) {
      throw new CommandLineException(reflectiveOperationException);
    }
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    try {
      return methodHandle.bindTo(proxy)
          .invokeWithArguments(arguments);
    } catch (Throwable throwable) {
      throw new CommandLineException(throwable);
    }
  }
}