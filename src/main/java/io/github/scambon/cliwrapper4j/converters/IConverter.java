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

import io.github.scambon.cliwrapper4j.Extra;

import java.util.Map;

/**
 * An interface that converts from one type to another.
 *
 * @param <I>
 *          the input type
 * @param <O>
 *          the output type
 */
public interface IConverter<I, O> {

  /**
   * Tests whether this converter can convert from the given input type to the given output type.
   * This method is to be called before {@link #convert(Object, Class, Map)}.
   *
   * @param inClass
   *          the input class
   * @param outClass
   *          the output class
   * @return whether the conversion is possible
   */
  boolean canConvert(Class<I> inClass, Class<O> outClass);

  /**
   * Converts the given element to the given output type. The {@link #canConvert(Class, Class)}
   * method is to be called before.
   *
   * @param in
   *          the element to convert
   * @param outClass
   *          the output type
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   * @return the converted value
   */
  O convert(I in, Class<O> outClass, Map<String, Object> extraParameterName2ValueMap);
}