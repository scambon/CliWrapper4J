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

package io.github.scambon.cliwrapper4j.executors;

import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.util.List;
import java.util.Map;

/**
 * An interface that actually executes the command line marshalled elements.
 */
public interface IExecutor {

  /**
   * Executes the given elements as a command line.
   *
   * @param elements
   *          the command line elements
   * @param environment
   *          the environment
   * @param extraParameterName2ValueMap
   *          the map from an {@link Extra} parameter name to its value
   * @return the result
   */
  Result execute(
      List<String> elements, IExecutionEnvironment environment,
      Map<String, Object> extraParameterName2ValueMap);

  /**
   * Gets a command line executor around this one that traces calls to the
   * {@link #execute(List, IExecutionEnvironment, Map)} method.
   *
   * @return the new tracing command line executor around this one
   */
  default IExecutor traced() {
    return new TracingExecutor(this);
  }
}