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

package io.github.scambon.cliwrapper4j.example;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.converters.AbstractRegexResultConverter;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.LambdaConverter;

public final class VersionResultConverter extends AbstractRegexResultConverter<Version> {

  private static final IConverter<String, Version> CONVERTER = new LambdaConverter<>(String.class,
      Version.class, output -> Version.parse(output));

  public VersionResultConverter() {
    super(CONVERTER, "(?:java|version) \"?(\\d+(\\.\\d+)*)");
  }

  @Override
  protected String extractSourceText(Result result) {
    return result.getError() + result.getOutput();
  }
}