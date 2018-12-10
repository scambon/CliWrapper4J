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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.nio.charset.Charset;
import java.util.List;

/**
 * An interface that helps in interacting with the operating system.
 */
public interface IOperatingSystem {

  /** The operating system factories. */
  static final List<IOperatingSystem> OPERATING_SYSTEMS = asList(
      new LinuxOperatingSystem(),
      new WindowsOperatingSystem())
      .stream()
      .map(MemoizingOperatingSystem::new)
      .collect(toList());
  
  /**
   * Checks if this operating system is the current one.
   *
   * @param osName
   *          the operating system name
   * @return this operating system is the current one
   */
  boolean isCase(String osName);

  /**
   * Gets the operating system using the <code>os.name</code> property.
   * 
   * @return the operating system
   */
  static IOperatingSystem get() {
    String osName = System.getProperty("os.name")
        .toLowerCase();
    return get(osName);
  }

  /**
   * Gets the operating system.
   *
   * @param osName
   *          the OS name
   * @return the operating system
   */
  static IOperatingSystem get(String osName) {
    IOperatingSystem operatingSystem = OPERATING_SYSTEMS.stream()
        .filter(os -> os.isCase(osName))
        .findFirst()
        .orElseThrow(() -> new CommandLineException(
            "Unhandled operating system  '" + osName + "'"));
    return operatingSystem;
  }

  /**
   * Gets the console encoding.
   *
   * @return the console encoding
   */
  Charset getConsoleEncoding();
}