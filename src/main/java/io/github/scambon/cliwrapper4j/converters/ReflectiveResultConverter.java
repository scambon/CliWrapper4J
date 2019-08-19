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

import static java.util.Arrays.asList;

import io.github.scambon.cliwrapper4j.Result;

import java.util.List;

/**
 * <p>
 * A result converter that uses reflection to transform results.
 * </p>
 * 
 * <p>
 * The first matching delegate converter type is used, among (in this order):
 * </p>
 * <ol>
 * <li>{@link ConstructorResultConverter}</li>
 * <li>{@link FactoryMethodResultConverter}</li>
 * </ol>
 * 
 * <p>
 * For each above possibilities, the following parameters are used (in top to bottom order):
 * </p>
 * <table border="1">
 * <caption>Delegate converter parameters</caption>
 * <tr>
 * <th>Parameter types</th>
 * <th>Parameter values</th>
 * </tr>
 * <tr>
 * <td><code>Result</code></td>
 * <td><code>itself</code></td>
 * </tr>
 * <tr>
 * <td><code>{String, String, Integer}</code></td>
 * <td><code>{output, error, return code}</code></td>
 * </tr>
 * <tr>
 * <td><code>{String, String, int}</code></td>
 * <td><code>{output, error, return code}</code></td>
 * </tr>
 * <tr>
 * <td><code>{String, String}</code></td>
 * <td><code>{output, error}</code></td>
 * </tr>
 * <tr>
 * <td><code>{String, Integer}</code></td>
 * <td><code>{output, return code}</code></td>
 * </tr>
 * <tr>
 * <td><code>{String, int}</code></td>
 * <td><code>{output, return code}</code></td>
 * </tr>
 * <tr>
 * <td><code>{Integer, String}</code></td>
 * <td><code>{return code, output}</code></td>
 * </tr>
 * <tr>
 * <td><code>{int, String}</code></td>
 * <td><code>{return code, output}</code></td>
 * </tr>
 * <tr>
 * <td><code>String</code></td>
 * <td><code>output</code></td>
 * </tr>
 * <tr>
 * <td><code>Integer</code></td>
 * <td><code>return code</code></td>
 * </tr>
 * <tr>
 * <td><code>int</code></td>
 * <td><code>return code</code></td>
 * </tr>
 * </table>
 * <p>
 * All these combinations are tested for 1 delegate converter type before moving to the next one.
 * </p>
 */
public class ReflectiveResultConverter extends CompositeConverter<Result, Object> {

  /** The delegate converters. */
  private static final List<IConverter<Result, Object>> DELEGATE_CONVERTERS = asList(
      // Constructors
      new ConstructorResultConverter(Result.class),
      new ConstructorResultConverter(String.class, String.class, Integer.class),
      new ConstructorResultConverter(String.class, String.class, int.class),
      new ConstructorResultConverter(String.class, String.class),
      new ConstructorResultConverter(String.class, Integer.class),
      new ConstructorResultConverter(String.class, int.class),
      new ConstructorResultConverter(Integer.class, String.class),
      new ConstructorResultConverter(int.class, String.class),
      new ConstructorResultConverter(String.class),
      new ConstructorResultConverter(Integer.class),
      new ConstructorResultConverter(int.class),
      // Factory methods
      new FactoryMethodResultConverter(Result.class),
      new FactoryMethodResultConverter(String.class, String.class, Integer.class),
      new FactoryMethodResultConverter(String.class, String.class, int.class),
      new FactoryMethodResultConverter(String.class, String.class),
      new FactoryMethodResultConverter(String.class, Integer.class),
      new FactoryMethodResultConverter(String.class, int.class),
      new FactoryMethodResultConverter(Integer.class, String.class),
      new FactoryMethodResultConverter(int.class, String.class),
      new FactoryMethodResultConverter(String.class),
      new FactoryMethodResultConverter(Integer.class),
      new FactoryMethodResultConverter(int.class));

  /**
   * Creates a reflective result converter.
   */
  public ReflectiveResultConverter() {
    super(DELEGATE_CONVERTERS);
  }
}