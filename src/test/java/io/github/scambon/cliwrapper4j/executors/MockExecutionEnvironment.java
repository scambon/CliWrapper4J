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

package io.github.scambon.cliwrapper4j.executors;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.DefaultExecutionEnvironment;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class MockExecutionEnvironment extends DefaultExecutionEnvironment {

  private final Result result;
  private IExecutor actualExecutor;
  private List<String> actualElements;
  private Map<String, Object> actualExtraParameterName2ValueMap;

  public MockExecutionEnvironment(Result result) {
    this.result = result;
  }

  @Override
  public Charset getEncoding() {
    return StandardCharsets.UTF_8;
  }

  @Override
  public Result run(
      IExecutor executor, List<String> elements,
      Map<String, Object> extraParameterName2ValueMap) {
    this.actualExecutor = executor;
    this.actualElements = elements;
    this.actualExtraParameterName2ValueMap = extraParameterName2ValueMap;
    return result;
  }

  public void checkExecutor(Class<? extends IExecutor> expectedExecutorClazz) {
    assertTrue(expectedExecutorClazz.isInstance(actualExecutor));
  }

  public void checkElements(String... expectedElements) {
    assertIterableEquals(asList(expectedElements), actualElements);
  }

  public void checkExtraParameters(Map<String, Object> expectedExtraParameterName2ValueMap) {
    assertEquals(expectedExtraParameterName2ValueMap, actualExtraParameterName2ValueMap);
  }
}