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
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A base method handler that works for @{@link Switch} methods that also have
 * an @{@link ExecuteNow} or @{@link ExecuteLater}. This class does not handle execution, but
 * subclasses are free to add such behaviors.
 */
public class AbstractExecuteSwitchMethodHandler
    extends
      SwitchMethodHandler {

  /** A consumer that configures the executable node for the handled command. */
  private final BiConsumer<ExecutableNode, Map<String, Object>> executableNodeConfigurator;

  /**
   * Instantiates a new command with parameters method handler.
   *
   * @param method
   *          the method
   * @param zwitch
   *          the switch
   * @param instantiator
   *          the instantiator
   * @param executableNodeConfigurator
   *          the executable node configurator
   */
  public AbstractExecuteSwitchMethodHandler(Method method, Switch zwitch,
      IInstantiator instantiator,
      BiConsumer<ExecutableNode, Map<String, Object>> executableNodeConfigurator) {
    super(method, zwitch, instantiator);
    this.executableNodeConfigurator = executableNodeConfigurator;
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    super.handle(proxy, arguments, executableNode);
    Map<String, Object> extraParameterName2ValueMap = createExtraParameterName2ValueMap(arguments);
    executableNodeConfigurator.accept(executableNode, extraParameterName2ValueMap);
    return proxy;
  }
}