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

import io.github.scambon.cliwrapper4j.Result;

import java.util.Arrays;

/**
 * The operating system helper that runs on Linux.
 */
public final class LinuxOperatingSystem extends AbstractCommandBasedOperatingSystem {

  /**
   * Instantiates a new Linux operating system.
   */
  protected LinuxOperatingSystem() {
    super("linux", Arrays.asList("locale", "charmap"),
        LinuxOperatingSystem.class.getResource("linux_charmap-2-java.properties"));
  }

  @Override
  protected String extractSystemName(Result commandResult) {
    String output = commandResult.getOutput();
    String charmapName = output.replaceAll("[\r\n]", "");
    return charmapName;
  }
}