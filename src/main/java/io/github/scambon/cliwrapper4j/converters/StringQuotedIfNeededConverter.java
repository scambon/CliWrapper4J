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

package io.github.scambon.cliwrapper4j.converters;

import java.util.Map;

/**
 * A converter that returns the given string, within double quotes if there is a space in the
 * string.
 */
public class StringQuotedIfNeededConverter extends StringConverter {

  @Override
  public String convert(
      Object in, Class<String> outClass,
      Map<String, Object> extraParameterName2ValueMap) {
    String convertedValue = super.convert(in, outClass, extraParameterName2ValueMap);
    if (convertedValue.contains(" ")) {
      convertedValue = '"' + convertedValue + '"';
    }
    return convertedValue;
  }
}