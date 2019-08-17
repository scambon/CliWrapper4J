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

package io.github.scambon.cliwrapper4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.scambon.cliwrapper4j.junit.ClassArrayArgumentConverter;

import java.util.Objects;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.lessThan;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.CsvFileSource;

public class ResultTest {

  @ParameterizedTest
  @CsvFileSource(resources = "Result.toString()-ParameterizedTests.csv", numLinesToSkip = 1)
  public void testToStringWithoutShortening(String output, String error, int returnCode,
      int maxSize) {
    Result result = new Result(output, error, returnCode);
    String resultString = result.toString();
    assertTrue(resultString.contains("'" + output.substring(0, 10)));
    assertTrue(resultString.contains("'" + error.substring(0, 10)));
    assertTrue(resultString.contains("'" + returnCode + "'"));
    assertThat(resultString.length(), lessThan(maxSize));
  }

  @ParameterizedTest
  @CsvFileSource(resources = "Result.toArray()-ParameterizedTests.csv", numLinesToSkip = 1)
  public void testToStringWithoutShortening(
      @ConvertWith(ClassArrayArgumentConverter.class) Class<?>[] types, String expected) {
    Result result = new Result("output", "error", 0);
    Object[] array = result.toArray(types);
    String actual = stream(array)
      .map(Objects::toString)
      .collect(joining(";"));
    assertEquals(expected, actual);
  }
}