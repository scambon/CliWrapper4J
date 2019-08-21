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

package io.github.scambon.cliwrapper4j.internal.handlers;

import io.github.scambon.cliwrapper4j.ExecuteLater;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;

import java.lang.reflect.Method;

/**
 * A method handler that works for @{@link ExecuteLater} methods.
 */
public class ExecuteLaterSwitchMethodHandler extends AbstractExecuteSwitchMethodHandler {

  /**
   * Instantiates a new command with parameters method handler.
   *
   * @param method
   *          the method
   * @param executeLater
   *          the command
   * @param instantiator
   *          the instantiator
   */
  public ExecuteLaterSwitchMethodHandler(
      Method method, Switch zwitch, ExecuteLater executeLater, IInstantiator instantiator) {
    super(method, zwitch, instantiator,
        (executableNode, extraParameterName2ValueMap) -> executableNode.setExecuteLaterContext(
            method, executeLater, extraParameterName2ValueMap));
  }
}