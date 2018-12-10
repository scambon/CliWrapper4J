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

package io.github.scambon.cliwrapper4j.environment;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An interface that describes an execution environment.
 */
public interface IExecutionEnvironment {

  /**
   * Gets the path.
   *
   * @return the path
   */
  Optional<Path> getPath();

  /**
   * Gets the encoding.
   *
   * @return the encoding
   */
  Charset getEncoding();

  /**
   * Gets the environment variables.
   *
   * @return the environment variables
   */
  Map<String, String> getEnvironmentVariables();

  /**
   * Sets the environment variable.
   *
   * @param variable
   *          the variable
   * @param value
   *          the value
   */
  void setEnvironmentVariable(String variable, String value);
  
  /**
   * Runs the execution.
   *
   * @param executor
   *          the executor
   * @param elements
   *          the elements
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   * @return the result
   */
  Result run(
      ICommandLineExecutor executor, List<String> elements,
      Map<String, Object> extraParameterName2ValueMap);

  /**
   * Configures the process builder with the elements from this execution environment.
   *
   * @param processBuilder
   *          the process builder to configure
   */
  default void configure(ProcessBuilder processBuilder) {
    Optional<Path> path = getPath();
    if (path.isPresent()) {
      File workingDirectory = path.get()
          .toFile();
      processBuilder.directory(workingDirectory);
    }
    Map<String, String> environmentVariables = getEnvironmentVariables();
    processBuilder.environment()
        .putAll(environmentVariables);
  }
}