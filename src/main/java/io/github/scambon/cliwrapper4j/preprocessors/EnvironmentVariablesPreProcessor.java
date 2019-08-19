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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * <p>
 * A command line pre-processor that replaces occurrences of <code>${SOME_VARIABLE}</code> by the
 * corresponding value. Values come from (by order or priority):
 * </p>
 * <ol>
 * <li>{@link IExecutionEnvironment#getEnvironmentVariables()}</li>
 * <li>System properties</li>
 * </ol>
 */
public class EnvironmentVariablesPreProcessor implements ICommandLinePreProcessor {

  @Override
  public List<String> process(List<String> commandLineElements, IExecutionEnvironment environment) {
    Map<String, String> variable2ValueMap = createVariable2ValueMap(environment);
    return commandLineElements.stream()
        .map(commandLineElement -> processCommandLineElement(commandLineElement, variable2ValueMap))
        .collect(toList());
  }

  /**
   * Creates the variable to value map.
   *
   * @param environment
   *          the environment
   * @return the variable to value map
   */
  private Map<String, String> createVariable2ValueMap(IExecutionEnvironment environment) {
    Map<String, String> variable2ValueMap = new TreeMap<>();
    Map<String, String> environmentVariable2ValueMap = asVariable2ValueMap(
        System.getenv());
    variable2ValueMap.putAll(environmentVariable2ValueMap);
    Map<String, String> additionalEnvironmentVariable2ValueMap = asVariable2ValueMap(
        environment.getEnvironmentVariables());
    variable2ValueMap.putAll(additionalEnvironmentVariable2ValueMap);
    return variable2ValueMap;
  }

  /**
   * Transforms the property name to value map into a variable name to value map.
   *
   * @param propertyName2ValueMap
   *          the property name to value map
   * @return the variable name to value map
   */
  private Map<String, String> asVariable2ValueMap(
      Map<String, String> propertyName2ValueMap) {
    return propertyName2ValueMap
        .entrySet()
        .stream()
        .collect(toMap(
            entry -> "${" + entry.getKey() + "}",
            Entry::getValue));
  }

  /**
   * Replaces a single command line element.
   * 
   * @param rawCommandLineElement
   *          the raw command line element
   * @param variable2ValueMap
   *          the variable to value map
   * @return the process command line element
   */
  private String processCommandLineElement(String rawCommandLineElement,
      Map<String, String> variable2ValueMap) {
    String processedCommandLineElement = rawCommandLineElement;
    for (Entry<String, String> entry : variable2ValueMap.entrySet()) {
      String variable = entry.getKey();
      String value = entry.getValue();
      processedCommandLineElement = processedCommandLineElement.replace(variable, value);
    }
    return processedCommandLineElement;
  }
}