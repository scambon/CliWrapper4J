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

public class LinuxOperatingSystemTest {

  @Test
  @EnabledOnOs(OS.LINUX)
  public void testGetConsoleEncoding() {
    LinuxOperatingSystem linuxOperatingSystem = new LinuxOperatingSystem();
    Charset consoleCharset = linuxOperatingSystem.getConsoleEncoding();
    String charsetName = consoleCharset.name();
    assertEquals("UTF-8", charsetName);
    // Doing so multiple times should be OK
    consoleCharset = linuxOperatingSystem.getConsoleEncoding();
    charsetName = consoleCharset.name();
    assertEquals("UTF-8", charsetName);
  }

  @Test
  public void testGetConsoleEncodingFailed() {
    IExecutor executor = MockExecutionHelper.createExecutor("locale", "-m", "failed");
    LinuxOperatingSystem linuxOperatingSystem = new LinuxOperatingSystem();
    assertThrows(CommandLineException.class,
        () -> linuxOperatingSystem.getConsoleEncoding(executor));
  }

  @Test
  public void testGetBrokenConsoleEncoding() {
    IExecutor executor = MockExecutionHelper.createExecutor("locale", "-m", "broken");
    LinuxOperatingSystem linuxOperatingSystem = new LinuxOperatingSystem();
    assertThrows(CommandLineException.class,
        () -> linuxOperatingSystem.getConsoleEncoding(executor));
  }
}