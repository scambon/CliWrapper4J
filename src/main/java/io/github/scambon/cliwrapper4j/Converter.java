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

package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that describes what {@link IConverter} is to be used to transform from one type to
 * another. This must be placed only on @{@link Switch}-annotated methods.
 * </p>
 * <p>
 * Depending on the need, the expectations differ:
 * </p>
 * <table border="1">
 * <caption>Converter requirements</caption>
 * <tr>
 * <th>Case</th>
 * <th>Location</th>
 * <th>Input type</th>
 * <th>Output type</th>
 * <th>Default if not set</th>
 * <th>Comments</th>
 * </tr>
 * <tr>
 * <td>Convert execution result</td>
 * <td>Method</td>
 * <td>{@link Result}</td>
 * <td>(anything)</td>
 * <td>{@link ResultConverter}</td>
 * <td>Only on {@link ExecuteNow}- or {@link ExecuteLater}-annotated methods</td>
 * </tr>
 * <tr>
 * <td>Convert parameter to be passed to the command line</td>
 * <td>Parameter</td>
 * <td>(anything)</td>
 * <td>{@link String}</td>
 * <td>{@link StringQuotedIfNeededConverter}</td>
 * <td>Not compatible with {@link Extra}</td>
 * </tr>
 * </table>
 * 
 * @see Switch
 * @see IConverter
 * @see ExecuteNow
 * @see ExecuteLater
 * @see Result
 */
@Retention(RUNTIME)
@Target({METHOD, PARAMETER})
public @interface Converter {

  /**
   * The converter to be used to convert the value.
   *
   * @return the converter class
   */
  Class<? extends IConverter<?, ?>> value();
}