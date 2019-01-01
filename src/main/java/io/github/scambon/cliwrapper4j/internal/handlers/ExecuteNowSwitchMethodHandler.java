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

import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

import java.lang.reflect.Method;

/**
 * A method handler that works for @{@link ExecuteNow} methods.
 */
public class ExecuteNowSwitchMethodHandler
    extends
      AbstractExecuteSwitchMethodHandler {

  /**
   * Instantiates a new executable command with parameters method handler.
   *
   * @param method
   *          the method
   * @param zwitch
   *          the switch
   * @param executeNow
   *          the execute now
   */
  public ExecuteNowSwitchMethodHandler(Method method, Switch zwitch, ExecuteNow executeNow) {
    super(method, zwitch,
        (executableNode, extraParameterName2ValueMap) ->
            executableNode.setExecuteNowContext(method, extraParameterName2ValueMap));
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    super.handle(proxy, arguments, executableNode);
    return executableNode.execute();
  }
}