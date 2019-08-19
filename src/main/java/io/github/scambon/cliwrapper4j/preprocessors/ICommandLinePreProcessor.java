/*
 * Copyright 2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.preprocessors;

import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.util.List;

/**
 * <p>
 * An interface that processes command line elements before execution.
 * </p>
 * <p>
 * Pre-processors are chained, i.e. the result of pre-processor 1 is passed to pre-processor 2...
 * The final command line elements are then executed.
 * </p>
 */
public interface ICommandLinePreProcessor {

  /**
   * Processes the command line elements.
   *
   * @param commandLineElements
   *          the command line elements
   * @param environment
   *          the environment
   * @return the processed command line elements
   */
  List<String> process(List<String> commandLineElements, IExecutionEnvironment environment);
}