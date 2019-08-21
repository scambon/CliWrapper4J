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

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A converter that transforms an array into a String by delegating the conversion of each element
 * to a delegate converter and flattening them using a flattener.
 *
 * @param <I>
 *          the input type
 */
public final class ArrayParameterConverter<I> implements IConverter<I[], String> {

  /** The element class. */
  private final Class<I> elementClass;
  /** The element converter. */
  private final IConverter<I, String> elementConverter;
  /** The flattener. */
  private final IFlattener flattener;
  /** The flattener parameter. */
  private final String flattenerParameter;

  /**
   * Instantiates a new array parameter converter.
   *
   * @param elementClass
   *          the element class
   * @param elementConverter
   *          the element converter
   * @param flattener
   *          the flattener
   * @param flattenerParameter
   *          the flattener parameter
   */
  public ArrayParameterConverter(
      Class<I> elementClass, IConverter<I, String> elementConverter,
      IFlattener flattener, String flattenerParameter) {
    this.elementClass = elementClass;
    this.elementConverter = elementConverter;
    this.flattener = flattener;
    this.flattenerParameter = flattenerParameter;
  }

  @Override
  @SuppressWarnings("unchecked")
  public boolean canConvert(Class<I[]> inClass, Class<String> outClass) {
    Object array = Array.newInstance(elementClass, 0);
    Class<I[]> arrayClass = (Class<I[]>) array.getClass();
    return arrayClass.isAssignableFrom(inClass)
        && elementConverter.canConvert(elementClass, outClass);
  }

  @Override
  public String convert(
      I[] in, Class<String> outClass, Map<String, Object> extraParameterName2ValueMap) {
    List<String> convertedValues = Arrays.stream(in)
        .map(element -> elementConverter.convert(element, outClass, extraParameterName2ValueMap))
        .collect(Collectors.toList());
    return flattener.flatten(convertedValues, flattenerParameter, extraParameterName2ValueMap);
  }
}