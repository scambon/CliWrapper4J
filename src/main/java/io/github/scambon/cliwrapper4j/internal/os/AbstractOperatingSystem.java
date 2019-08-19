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

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.nio.charset.Charset;
import java.util.List;

/**
 * A base class that helps in interacting with the operating system.
 */
public abstract class AbstractOperatingSystem {

  /** The operating system factories. */
  @SuppressWarnings("squid:S2390")
  private static final List<AbstractOperatingSystem> OPERATING_SYSTEMS = asList(
      new LinuxOperatingSystem(),
      new WindowsOperatingSystem())
          .stream()
          .map(MemoizingOperatingSystem::new)
          .collect(toList());

  /**
   * Gets the operating system using the <code>os.name</code> property.
   * 
   * @return the operating system
   */
  public static AbstractOperatingSystem get() {
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
  public static AbstractOperatingSystem get(String osName) {
    return OPERATING_SYSTEMS.stream()
        .filter(os -> os.isCase(osName))
        .findFirst()
        .orElseThrow(() -> new CommandLineException(
            "Unhandled operating system  '" + osName + "'"));
  }

  /**
   * Checks if this operating system is the current one.
   *
   * @param osName
   *          the operating system name
   * @return this operating system is the current one
   */
  protected abstract boolean isCase(String osName);

  /**
   * Gets the console encoding.
   *
   * @return the console encoding
   */
  public abstract Charset getConsoleEncoding();
}