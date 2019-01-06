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
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.internal.os.AbstractOperatingSystem;

import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * An base execution environment that handles the most common cases.
 */
public class DefaultExecutionEnvironment implements IExecutionEnvironment {

  /** The path. */
  private final Path path;
  /** The environment variables. */
  private final Map<String, String> environmentVariables = new LinkedHashMap<>();

  /** The encoding. */
  private Charset encoding;

  /**
   * Instantiates a new default execution environment.
   */
  public DefaultExecutionEnvironment() {
    this(null);
  }

  /**
   * Instantiates a new default execution environment.
   *
   * @param path
   *          the path
   */
  public DefaultExecutionEnvironment(Path path) {
    this.path = path;
  }

  @Override
  public Optional<Path> getPath() {
    return Optional.ofNullable(path);
  }

  /**
   * Sets the encoding.
   *
   * @param encoding
   *          the new encoding
   */
  public void setEncoding(Charset encoding) {
    this.encoding = encoding;
  }

  @Override
  public Charset getEncoding() {
    if (encoding == null) {
      AbstractOperatingSystem operatingSystem = AbstractOperatingSystem.get();
      encoding = operatingSystem.getConsoleEncoding();
    }
    return encoding;
  }

  @Override
  public void setEnvironmentVariable(String variable, String value) {
    environmentVariables.put(variable, value);
  }

  @Override
  public Map<String, String> getEnvironmentVariables() {
    return environmentVariables;
  }

  @Override
  public Result run(
      IExecutor executor, List<String> cliElements, 
      Map<String, Object> extraParameterName2ValueMap) {
    return executor.execute(cliElements, this, extraParameterName2ValueMap);
  }
}