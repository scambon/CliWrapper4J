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

import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

import java.lang.reflect.Method;

/**
 * A method handler that works for commands that require execution.
 */
public class ExecutableCommandWithParametersMethodHandler
    extends CommandWithParametersMethodHandler {

  /**
   * Instantiates a new executable command with parameters method handler.
   *
   * @param method
   *          the method
   * @param command
   *          the command
   */
  public ExecutableCommandWithParametersMethodHandler(Method method, Command command) {
    super(method, command);
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    super.handle(proxy, arguments, executableNode);
    ExecuteMethodHandler executeMethodHandler = new ExecuteMethodHandler();
    return executeMethodHandler.handle(proxy, arguments, executableNode);
  }
}