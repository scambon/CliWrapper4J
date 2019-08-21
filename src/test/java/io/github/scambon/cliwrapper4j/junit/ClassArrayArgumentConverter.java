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

package io.github.scambon.cliwrapper4j.junit;

import static java.util.Arrays.stream;

import io.github.scambon.cliwrapper4j.Result;

import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class ClassArrayArgumentConverter implements ArgumentConverter {

  @Override
  public Object convert(Object source, ParameterContext context)
      throws ArgumentConversionException {
    String string = (String) source;
    return stream(string.split(";"))
        .map(this::toClass)
        .toArray(Class<?>[]::new);
  }

  private Class<?> toClass(String className) {
    switch (className) {
      case "Result" :
        return Result.class;
      case "String" :
        return String.class;
      case "int" :
        return int.class;
      case "Integer" :
        return Integer.class;
      case "Void" :
        return Void.class;
      case "void" :
        return void.class;
      default :
        throw new IllegalArgumentException(className);
    }
  }
}