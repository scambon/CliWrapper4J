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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.scambon.cliwrapper4j.CommandLineException;

import org.junit.jupiter.api.Test;

public class OperatingSystemTest {

  @Test
  public void testGetOperatingSystem() {
    AbstractOperatingSystem operatingSystem = AbstractOperatingSystem.get();
    assertNotNull(operatingSystem);
  }

  @Test
  public void testGetWindowsOperatingSystemExplicitly() {
    AbstractOperatingSystem operatingSystem = AbstractOperatingSystem.get("windows 10");
    assertNotNull(operatingSystem);
  }

  @Test
  public void testGetOperatingSystemFailure() {
    assertThrows(CommandLineException.class, () -> AbstractOperatingSystem.get("whatever"));
  }
}