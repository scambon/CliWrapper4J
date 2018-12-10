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

package io.github.scambon.cliwrapper4j.aggregators;

/**
 * An interface that aggregates a command or option with its flattened value.
 */
public interface IAggregator {

  /**
   * Aggregates a command or option with its flattened value.
   *
   * @param commandOrOption
   *          the command or option
   * @param value
   *          the flattened value
   * @param parameter
   *          the parameter, interpretation depends on the implementation
   * @return the aggregated string
   */
  String aggregate(String commandOrOption, String value, String parameter);
}