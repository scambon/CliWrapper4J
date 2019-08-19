/*
 * Copyright 2019 Sylvain Cambon
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
import io.github.scambon.cliwrapper4j.converters.ConstructorResultConverterTest.Constructible1Arg;

import org.junit.jupiter.api.Test;

public class FactoryMethodResultConverterTest {
  
  private final Result result = new Result("output", "error", 0);

  public static class Creatable0Arg {
    
    private Creatable0Arg() {
      // Nothing
    }
    
    public static Creatable0Arg create() {
      return new Creatable0Arg();
    }
  }

  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void test0ArgFactoryMethod() {
    FactoryMethodResultConverter converter = new FactoryMethodResultConverter();
    boolean canConvert = converter.canConvert(Result.class, (Class) Creatable0Arg.class);
    assertTrue(canConvert);
    Creatable0Arg convertedResult =
        (Creatable0Arg) converter.convert(result, (Class) Creatable0Arg.class, emptyMap());
    assertNotNull(convertedResult);
  }

  public static class Creatable1Arg {
    private final String output;
    private Creatable1Arg(String output) {
      this.output = output;
    }
    
    public static Creatable1Arg create(String output) {
      return new Creatable1Arg(output);
    }
  }
  
  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void test1ArgFactoryMethod() {
    FactoryMethodResultConverter converter = new FactoryMethodResultConverter(
        new Class<?>[] {String.class}, result -> new Object[] {result.getOutput()});
    boolean canConvert = converter.canConvert(Result.class, (Class) Creatable1Arg.class);
    assertTrue(canConvert);
    Creatable1Arg convertedResult =
        (Creatable1Arg) converter.convert(result, (Class) Creatable1Arg.class, emptyMap());
    assertNotNull(convertedResult);
    assertEquals("output", convertedResult.output);
  }
  
  @Test
  @SuppressWarnings({"unchecked", "rawtypes"})
  public void testFailing1ArgFactoryMethod() {
    FactoryMethodResultConverter converter = new FactoryMethodResultConverter(
        new Class<?>[] {}, result -> new Object[] {});
    boolean canConvert = converter.canConvert(Result.class, (Class) Creatable1Arg.class);
    assertFalse(canConvert);
    assertThrows(CommandLineException.class,
        () -> converter.convert(result, (Class) Constructible1Arg.class, emptyMap()));
  }
}