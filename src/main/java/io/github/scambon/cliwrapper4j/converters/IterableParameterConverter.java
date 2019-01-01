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

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

import java.util.List;
import java.util.Map;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * A parameter converter that transforms an {@link Iterable} into a String by delegating the
 * conversion of each element to a delegate converter and flattening them using a flattener.
 *
 * @param <I>
 *          the input type
 */
public final class IterableParameterConverter<I> implements IConverter<Iterable<I>, String> {

  /** The element class. */
  private final Class<I> elementClass;
  /** The element converter. */
  private final IConverter<I, String> elementConverter;
  /** The flattener. */
  private final IFlattener flattener;
  /** The flattener parameter. */
  private final String flattenerParameter;

  /**
   * Instantiates a new iterable parameter converter.
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
  public IterableParameterConverter(Class<I> elementClass, IConverter<I, String> elementConverter,
      IFlattener flattener, String flattenerParameter) {
    this.elementClass = elementClass;
    this.elementConverter = elementConverter;
    this.flattener = flattener;
    this.flattenerParameter = flattenerParameter;
  }

  @Override
  public boolean canConvert(Class<Iterable<I>> inClass, Class<String> outClass) {
    return Iterable.class.isAssignableFrom(inClass)
        && elementConverter.canConvert(elementClass, outClass);
  }

  @Override
  public String convert(
      Iterable<I> in, Class<String> outClass, Map<String, Object> extraParameterName2ValueMap) {
    Spliterator<I> spliterator = in.spliterator();
    List<String> convertedValues = StreamSupport.stream(spliterator, false)
        .map(element -> elementConverter.convert(element, outClass, extraParameterName2ValueMap))
        .collect(Collectors.toList());
    return flattener.flatten(convertedValues, flattenerParameter, extraParameterName2ValueMap);
  }
}