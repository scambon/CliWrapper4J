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

package io.github.scambon.cliwrapper4j.example;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.executors.AbstractInteractiveProcessExecutor;

import java.io.PrintWriter;
import java.util.Map;

public class InterractiveHelloProcessExecutor extends AbstractInteractiveProcessExecutor {

  private final StringBuilder standardSuccess = new StringBuilder();
  private final StringBuilder errorSuccess = new StringBuilder();

  @Override
  protected void onStandard(String outputChunk, PrintWriter writer,
      Map<String, Object> extraParameterName2ValueMap) {
    standardSuccess.append(outputChunk);
    Object name = extraParameterName2ValueMap.get("name");
    if (outputChunk.contains("What is your name?")) {
      writer.println(name);
    }
  }

  @Override
  protected void onError(String errorChunk, PrintWriter outputStream,
      Map<String, Object> extraParameterName2ValueMap) {
    errorSuccess.append(errorChunk);
  }

  @Override
  protected Result getResult(int returnCode, Map<String, Object> extraParameterName2ValueMap) {
    return new Result(standardSuccess.toString(), errorSuccess.toString(), returnCode);
  }
}