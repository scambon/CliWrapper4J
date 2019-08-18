/* Copyright 2019 Sylvain Cambon
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

import io.github.scambon.cliwrapper4j.IExecutableFactory;
import io.github.scambon.cliwrapper4j.ReflectiveExecutableFactory;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.example.IEnvironmentVariablesCommandLine;
import io.github.scambon.cliwrapper4j.executors.MockExecutionEnvironment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnvironmentVariablesPreProcessorTest {

  private final IExecutableFactory<IEnvironmentVariablesCommandLine> factory =
      new ReflectiveExecutableFactory<>(IEnvironmentVariablesCommandLine.class);
  private final Result result = new Result("output", "error", 0);
  private final MockExecutionEnvironment env = new MockExecutionEnvironment(result);
  
  @BeforeEach
  public void before() {
    // System
    // No "NOT_OVERWRITTEN"
    env.setEnvironmentVariable("OVERWRITTEN", "O");

    // Environment
    env.setEnvironmentVariable("arg_executable_alone", "aeo");
    env.setEnvironmentVariable("arg_executable_middle", "aem");
    env.setEnvironmentVariable("arg_switch", "as");
    env.setEnvironmentVariable("arg_executable_alone", "aeo");
    env.setEnvironmentVariable("arg_executable_middle", "aem");
    // No "arg_unprovided"
  }

  @Test
  public void testEnvironmentVariableReplacement() {
    IEnvironmentVariablesCommandLine commandLine = factory.create(env);
    commandLine.executeWithEnvironmentVariables();
    env.checkElements("foo", "aeo", "before-aem-after", "as - ${arg_unprovided}");
  }
  
  @Test
  public void testSystemPropertiesReplacement() {
    IEnvironmentVariablesCommandLine commandLine = factory.create(env);
    commandLine.executeWithSystemAndEnvironmentVariables();
    env.checkElements("foo", "aeo", "before-aem-after", "N - O");
  }
}