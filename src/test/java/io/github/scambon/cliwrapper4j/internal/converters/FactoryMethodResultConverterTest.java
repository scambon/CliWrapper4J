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

package io.github.scambon.cliwrapper4j.internal.converters;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import io.github.scambon.cliwrapper4j.Result;

import java.io.File;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Test;

public class FactoryMethodResultConverterTest {

  public enum Converted {
    VALUE;

    public static Converted whatever(Result result) {
      return null;
    }
    public static Converted valueOf(Result result) {
      return null;
    }
    public static Converted make(Result result) {
      return null;
    }
    public static Converted build(Result result) {
      return null;
    }
    public static Converted create(Result result) {
      return null;
    }
    public static Converted of(Result result) {
      return null;
    }
    public static Converted from(Result result) {
      return null;
    }
    public static Converted parse(Result result) {
      return null;
    }
    public Converted nonStaticIsNotExpected(Result result) {
      return null;
    }
    public static Converted otherParameterTypeIsNotExpected(File file) {
      return null;
    }
    static Converted nonPublicMethodIsNotExpected(Result result) {
      return null;
    }
  }

  @Test
  public void testMethodOrderForResult() {
    List<String> expectedMethodNames = asList(
        "parse",
        "from",
        "of",
        "create",
        "build",
        "make",
        "valueOf",
        "whatever");
    List<String> actualMethodNames = FactoryMethodResultConverterUtils.findFactoryMethods(Converted.class, new Class[] {Result.class})
        .map(Method::getName)
        .collect(toList());
    assertIterableEquals(expectedMethodNames, actualMethodNames);
  }
}