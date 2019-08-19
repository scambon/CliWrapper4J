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

package io.github.scambon.cliwrapper4j.internal.os;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.executors.MockExecutionHelper;

import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class WindowsOperatingSystemTest {

  @Test
  @EnabledOnOs(OS.WINDOWS)
  public void testGetConsoleEncoding() {
    WindowsOperatingSystem windowsOperatingSystem = new WindowsOperatingSystem();
    Charset consoleCharset = windowsOperatingSystem.getConsoleEncoding();
    String charsetName = consoleCharset.name();
    assertEquals("IBM850", charsetName);
    // Doing so multiple times should be OK
    consoleCharset = windowsOperatingSystem.getConsoleEncoding();
    charsetName = consoleCharset.name();
    assertEquals("IBM850", charsetName);
  }

  @Test
  public void testGetConsoleEncodingFailed() {
    IExecutor executor = MockExecutionHelper.createExecutor("chcp.com", "failed");
    WindowsOperatingSystem windowsOperatingSystem = new WindowsOperatingSystem();
    assertThrows(CommandLineException.class,
        () -> windowsOperatingSystem.getConsoleEncoding(executor));
  }

  @Test
  public void testGetBrokenConsoleEncoding() {
    IExecutor executor = MockExecutionHelper.createExecutor("chcp.com", "broken");
    WindowsOperatingSystem windowsOperatingSystem = new WindowsOperatingSystem();
    assertThrows(CommandLineException.class,
        () -> windowsOperatingSystem.getConsoleEncoding(executor));
  }
}