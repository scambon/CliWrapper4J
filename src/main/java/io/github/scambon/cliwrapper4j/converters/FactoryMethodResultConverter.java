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

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.internal.converters.FactoryMethodResultConverterUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * <p>
 * A result converter that reflectively calls a factory method corresponding to the given
 * parameter types. If no such method exists, {@link #canConvert(Class, Class)} returns
 * <code>false</code>.
 * </p>
 * 
 * <p>
 * A factory method is defined according to the following criteria:
 * </p>
 * <ul>
 * <li>It is public;</li>
 * <li>It is static;</li>
 * <li>It returns a type which is or a subtype of it.</li>
 * </ul>
 * 
 * <p>
 * In case multiple factory methods are found, priority is determined according to the following
 * ordered criteria:
 * </p>
 * <ol>
 * <li>Name, according to the following ranking:
 * <ol>
 * <li><code>parse</code>;</li>
 * <li><code>from</code>;</li>
 * <li><code>of</code>;</li>
 * <li><code>create</code>;</li>
 * <li><code>build</code>;</li>
 * <li><code>make</code>;</li>
 * <li><code>valueOf</code> (may it be generated or not);</li>
 * <li>other names;</li>
 * </ol>
 * </li>
 * <li><code>toString()</code> representation (avoid this one!)</li>
 * </ol>
 */
public class FactoryMethodResultConverter implements IConverter<Result, Object> {

  /** The method parameter types. */
  private final Class<?>[] methodParameterTypes;
  /** The parameters extractor. */
  private final Function<Result, Object[]> methodParametersExtractor;

  /**
   * Creates the converter, using {@link Result#toArray(Class[])}.
   * 
   * @param methodParameterTypes
   *          the expected constructor parameter types
   */
  public FactoryMethodResultConverter(Class<?>... methodParameterTypes) {
    this(methodParameterTypes, result -> result.toArray(methodParameterTypes));
  }

  /**
   * Creates the converter.
   * 
   * @param methodParameterTypes
   *          the expected constructor parameter types
   * @param methodParametersExtractor
   *          the function that converts a {@link Result} into constructor parameters
   */
  public FactoryMethodResultConverter(Class<?>[] methodParameterTypes,
      Function<Result, Object[]> methodParametersExtractor) {
    this.methodParameterTypes = methodParameterTypes;
    this.methodParametersExtractor = methodParametersExtractor;
  }

  @Override
  public boolean canConvert(Class<Result> inClass, Class<Object> outClass) {
    Optional<Method> factoryMethodOpt = FactoryMethodResultConverterUtils.findFactoryMethod(
        outClass, methodParameterTypes);
    return factoryMethodOpt.isPresent();
  }

  @Override
  public Object convert(
      Result in, Class<Object> outClass, Map<String, Object> extraParameterName2ValueMap) {
    try {
      Optional<Method> factoryMethodOpt = FactoryMethodResultConverterUtils.findFactoryMethod(
          outClass, methodParameterTypes);
      Object[] arguments = methodParametersExtractor.apply(in);
      Method factoryMethod = factoryMethodOpt.orElseThrow(
          () -> new CommandLineException(
              "The converter '" + this + "' said it could convert from '"
                  + Result.class + "' to '" + outClass + "', but now it seems it cannot"));
      return factoryMethod.invoke(null, arguments);
    } catch (ReflectiveOperationException roe) {
      throw new CommandLineException(roe);
    }
  }
}