/*
 * Copyright 2018-2019 Sylvain Cambon
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

import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.converters.IConverter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * A command line node that works for a @{@link Switch} method parameter.
 *
 * @param <I>
 *          the input type
 */
public final class ParameterNode<I> implements ICommandLineNode {

  /** The converter. */
  private final IConverter<I, String> converter;
  /** The value. */
  private final I value;

  /** The extra parameter name 2 value map. */
  private Map<String, Object> extraParameterName2ValueMap;

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
    String convertedValue = converter.convert(value, String.class, extraParameterName2ValueMap);
    return Collections.singletonList(convertedValue);
  }

  /**
   * Sets the extra parameter name to value map.
   * 
   * @param extraParameterName2ValueMap
   *          the extra parameter name to value map
   */
  public void setExtraParameterName2ValueMap(Map<String, Object> extraParameterName2ValueMap) {
    this.extraParameterName2ValueMap = extraParameterName2ValueMap;
  }
}