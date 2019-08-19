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

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class MockExecutionHelper {

  private MockExecutionHelper() {
    // NOP
  }

  public static IExecutor createExecutor(String... filenameElements) {
    Result result = createResult(filenameElements);
    IExecutor executor = (elements, environment, extra) -> result;
    return executor;
  }

  public static MockExecutionEnvironment createExecutionEnvironment(String... filenameElements) {
    Result result = createResult(filenameElements);
    MockExecutionEnvironment environment = new MockExecutionEnvironment(result);
    return environment;
  }

  private static Result createResult(String... filenameElements) {
    Properties properties = new Properties();
    String resultFile = String.join("_", filenameElements) + ".properties";
    try (InputStream in = MockExecutionHelper.class.getResourceAsStream(resultFile)) {
      properties.load(in);
    } catch (IOException e) {
      throw new CommandLineException(e);
    }
    String output = properties.getProperty("output");
    String error = properties.getProperty("error");
    int returnCode = Integer.parseInt(properties.getProperty("returnCode"));
    Result result = new Result(output, error, returnCode);
    return result;
  }
}