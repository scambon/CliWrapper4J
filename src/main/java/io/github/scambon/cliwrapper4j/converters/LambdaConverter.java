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

package io.github.scambon.cliwrapper4j.converters;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * A converter that converts based on a lambda-friendly function.
 *
 * @param <I>
 *          the input type
 * @param <O>
 *          the output type
 */
public class LambdaConverter<I, O> implements IConverter<I, O> {

  /** The input class. */
  private final Class<I> inClass;
  /** The output class. */
  private final Class<O> outClass;
  /** The converter. */
  private final BiFunction<I, Map<String, Object>, O> converter;

  /**
   * Instantiates a new lambda converter.
   *
   * @param inClass
   *          the input class
   * @param outClass
   *          the output class
   * @param convertlet
   *          the conversion function
   */
  public LambdaConverter(Class<I> inClass, Class<O> outClass,
      Function<I, O> convertlet) {
    this(inClass, outClass, (in, extra) -> convertlet.apply(in));
  }
  
  /**
   * Instantiates a new lambda converter.
   *
   * @param inClass
   *          the input class
   * @param outClass
   *          the output class
   * @param convertlet
   *          the conversion function
   */
  public LambdaConverter(
      Class<I> inClass, Class<O> outClass,
      BiFunction<I, Map<String, Object>, O> convertlet) {
    this.inClass = inClass;
    this.outClass = outClass;
    this.converter = convertlet;
  }

  @Override
  public final boolean canConvert(Class<I> inClass, Class<O> outClass) {
    return this.inClass.equals(inClass) && this.outClass.equals(outClass);
  }

  @Override
  public final O convert(
      I in, Class<O> outClass, Map<String, Object> extraParameterName2ValueMap) {
    return converter.apply(in, extraParameterName2ValueMap);
  }
}