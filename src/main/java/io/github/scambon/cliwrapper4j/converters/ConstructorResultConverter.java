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

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Function;

/**
 * A result converter that reflectively calls the constructor corresponding to the given
 * parameter types. If such a constructor is not defined, then {@link #canConvert(Class, Class)}
 * returns <code>false</code>.
 */
public class ConstructorResultConverter implements IConverter<Result, Object> {
  /** The parameter types to look for. */
  private final Class<?>[] constructorParameterTypes;
  /** The parameters extractor. */
  private final Function<Result, Object[]> constructorParametersExtractor;

  /**
   * Creates the converter, using {@link Result#toArray(Class[])}.
   * 
   * @param constructorParameterTypes
   *          the expected constructor parameter types
   */
  public ConstructorResultConverter(Class<?>... constructorParameterTypes) {
    this(constructorParameterTypes, result -> result.toArray(constructorParameterTypes));
  }
  
  /**
   * Creates the converter.
   * 
   * @param constructorParameterTypes
   *          the expected constructor parameter types
   * @param constructorParametersExtractor
   *          the function that converts a {@link Result} into constructor parameters
   */
  public ConstructorResultConverter(Class<?>[] constructorParameterTypes,
      Function<Result, Object[]> constructorParametersExtractor) {
    this.constructorParameterTypes = constructorParameterTypes;
    this.constructorParametersExtractor = constructorParametersExtractor;
  }

  @Override
  public boolean canConvert(Class<Result> inClass, Class<Object> outClass) {
    if (!Result.class.equals(inClass)) {
      return false;
    }
    try {
      Constructor<?> constructor = outClass.getConstructor(constructorParameterTypes);
      return constructor != null;
    } catch (ReflectiveOperationException roe) {
      return false;
    }
  }

  @Override
  public Object convert(
      Result in, Class<Object> outClass, Map<String, Object> extraParameterName2ValueMap) {
    try {
      Constructor<?> constructor = outClass.getConstructor(constructorParameterTypes);
      Object[] arguments = constructorParametersExtractor.apply(in);
      return constructor.newInstance(arguments);
    } catch (ReflectiveOperationException roe) {
      throw new CommandLineException(roe);
    }
  }
}