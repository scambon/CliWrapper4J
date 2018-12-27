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

package io.github.scambon.cliwrapper4j.internal.handlers;

import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

/**
 * An interface that defines how to react to a method call by delegation from an invocation handler.
 */
@FunctionalInterface
public interface IMethodHandler {

  /**
   * Handles the associated method call.
   *
   * @param proxy
   *          the proxy
   * @param arguments
   *          the arguments
   * @param executableNode
   *          the executable node
   * @return the object
   */
  Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode);
}