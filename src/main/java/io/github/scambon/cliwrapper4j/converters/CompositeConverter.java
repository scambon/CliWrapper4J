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

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * A converter that converts with its first delegate which is able to.
 *
 * @param <I>
 *          the input type
 * @param <O>
 *          the output type
 */
public class CompositeConverter<I, O> implements IConverter<I, O> {

  /** The delegate converters. */
  @SuppressWarnings("rawtypes")
  private final List<IConverter> converters;

  /**
   * Instantiates a new composite converter.
   *
   * @param converters
   *          the converters
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  public CompositeConverter(List<IConverter<? extends I, ? extends O>> converters) {
    this.converters = (List) converters;
  }

  /**
   * Instantiates a new composite converter.
   *
   * @param converters
   *          the converters
   */
  @SafeVarargs
  public CompositeConverter(IConverter<? extends I, ? extends O>... converters) {
    this(Arrays.asList(converters));
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean canConvert(Class<I> inClass, Class<O> outClass) {
    return converters.stream()
        .anyMatch(converter -> converter.canConvert(inClass, outClass));
  }

  @Override
  @SuppressWarnings("unchecked")
  public O convert(I in, Class<O> outClass, Map<String, Object> extraParameterName2ValueMap) {
    Class<I> inClass = (Class<I>) in.getClass();
    return (O) converters.stream()
        .filter(converter -> converter.canConvert(inClass, outClass))
        .map(converter -> converter.convert(in, outClass, extraParameterName2ValueMap))
        .findFirst()
        .orElseThrow(
            () -> new CommandLineException("Could not convert '" + in + "' to '" + outClass + "'"));
  }
}