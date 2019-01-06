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

package io.github.scambon.cliwrapper4j.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.DefaultExecutionEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class TracingCommandLineExecutorTest {

  @Test
  public void testTraceExecutionContent() {
    List<String> expectedCommands = Arrays.asList("java", "-version");
    IExecutor executor = MockExecutionHelper.createExecutor("java", "-version");
    List<String> actualCommands = new ArrayList<>();
    executor = new TracingExecutor(executor, actualCommands::addAll);
    executor.execute(expectedCommands, new DefaultExecutionEnvironment(), null);
    assertEquals(expectedCommands, actualCommands);
  }

  @Test
  public void testTraceExecutionResult() {
    List<String> expectedCommands = Arrays.asList("java", "-version");
    IExecutor executor = MockExecutionHelper.createExecutor("java", "-version");
    executor = executor.traced();
    Result result = executor.execute(expectedCommands, new DefaultExecutionEnvironment(), null);
    assertFalse(result.getOutput()
        .isEmpty());
    assertEquals(0, result.getReturnCode());
  }
}