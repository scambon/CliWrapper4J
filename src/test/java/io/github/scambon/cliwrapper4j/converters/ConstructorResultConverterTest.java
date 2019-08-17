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
package io.github.scambon.cliwrapper4j.converters;

import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.*;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;

import org.junit.jupiter.api.Test;

public class ConstructorResultConverterTest {
  
  private final Result result = new Result("output", "error", 0);

  public static class Constructible0Arg {
    // Nothing
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void test0ArgConstructor() {
    ConstructorResultConverter converter = new ConstructorResultConverter();
    boolean canConvert = converter.canConvert(Result.class, (Class) Constructible0Arg.class);
    assertTrue(canConvert);
    Constructible0Arg convertedResult =
        (Constructible0Arg) converter.convert(result, (Class) Constructible0Arg.class, emptyMap());
    assertNotNull(convertedResult);
  }

  public static class Constructible1Arg {
    private final String output;
    public Constructible1Arg(String output) {
      this.output = output;
    }
  }
  
  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void test1ArgConstructor() {
    ConstructorResultConverter converter = new ConstructorResultConverter(
        new Class<?>[] {String.class}, result -> new Object[] {result.getOutput()});
    boolean canConvert = converter.canConvert(Result.class, (Class) Constructible1Arg.class);
    assertTrue(canConvert);
    Constructible1Arg convertedResult =
        (Constructible1Arg) converter.convert(result, (Class) Constructible1Arg.class, emptyMap());
    assertNotNull(convertedResult);
    assertEquals("output", convertedResult.output);
  }
  
  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testFailing1ArgConstructor() {
    ConstructorResultConverter converter = new ConstructorResultConverter(
        new Class<?>[] {}, result -> new Object[] {});
    boolean canConvert = converter.canConvert(Result.class, (Class) Constructible1Arg.class);
    assertFalse(canConvert);
    assertThrows(CommandLineException.class,
        () -> converter.convert(result, (Class) Constructible1Arg.class, emptyMap()));
  }
}