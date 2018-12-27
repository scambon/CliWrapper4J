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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.DefaultExecutionEnvironment;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class ProcessExecutorTest {

  @Test
  @EnabledOnOs(OS.LINUX)
  public void testExecuteWorkingCommandOnLinux() {
    testExecuteWorkingEchoWhateverCommand("echo", "whatever");
  }

  @Test
  @EnabledOnOs(OS.WINDOWS)
  public void testExecuteWorkingCommandOnWindows() {
    testExecuteWorkingEchoWhateverCommand("cmd", "/C", "echo", "whatever");
  }

  public void testExecuteWorkingEchoWhateverCommand(String... elements) {
    ProcessExecutor processExecutor = new ProcessExecutor();
    IExecutionEnvironment environment = new DefaultExecutionEnvironment();
    Result result = processExecutor.execute(Arrays.asList(elements), environment, null);
    assertEquals(0, result.getReturnCode());
    assertTrue(result.getOutput()
        .matches("\\s*whatever\\s*"));
    assertTrue(result.getError()
        .isEmpty());
  }

  @Test
  public void testExecuteBrokenCommand() {
    ProcessExecutor processExecutor = new ProcessExecutor();
    IExecutionEnvironment environment = new DefaultExecutionEnvironment();
    assertThrows(CommandLineException.class,
        () -> processExecutor.execute(Arrays.asList("broken_command"), environment, null));
  }
}