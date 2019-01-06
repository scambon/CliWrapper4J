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

import io.github.scambon.cliwrapper4j.Extra;

import java.util.Map;

/**
 * An interface that aggregates a switch with its flattened parameter values.
 */
public interface IAggregator {

  /**
   * Aggregates a switch with its flattened parameter values.
   *
   * @param zwitch
   *          the switch
   * @param flattenedParameterValues
   *          the flattened parameter values
   * @param aggregatorParameter
   *          the aggregagtor parameter, interpretation depends on the implementation
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   * @return the aggregated string
   */
  String aggregate(
      String zwitch, String flattenedParameterValues, String aggregatorParameter,
      Map<String, Object> extraParameterName2ValueMap);
}