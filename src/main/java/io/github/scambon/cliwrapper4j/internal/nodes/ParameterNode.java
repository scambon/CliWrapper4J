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

package io.github.scambon.cliwrapper4j.internal.nodes;

import io.github.scambon.cliwrapper4j.converters.IConverter;

import java.util.Collections;
import java.util.List;

/**
 * A command line node that works for a parameter.
 *
 * @param <I>
 *          the input type
 */
public class ParameterNode<I> implements ICommandLineNode {

  /** The converter. */
  private final IConverter<I, String> converter;
  /** The value. */
  private final I value;

  /**
   * Instantiates a new parameter node.
   *
   * @param converter
   *          the converter
   * @param value
   *          the value
   */
  public ParameterNode(IConverter<I, String> converter, I value) {
    this.converter = converter;
    this.value = value;
  }

  @Override
  public List<String> flatten() {
    String convertedValue = converter.convert(value, String.class);
    return Collections.singletonList(convertedValue);
  }
}