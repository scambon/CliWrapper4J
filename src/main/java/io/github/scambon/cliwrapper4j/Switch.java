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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that describes a switch, i.e. a tag and values to be added to the command line.
 * </p>
 * <h2>Parameters</h2>
 * <p>
 * The method can have parameters:
 * </p>
 * <ul>
 * <li>Annotated with @{@link Converter} to convert the parameters to strings to be passed to
 * command lines (this is the default, implicit behavior, using a
 * {@link StringQuotedIfNeededConverter})</li>
 * <li>Annotated with @{@link Extra} to be provided (unconverted) to the framework instead of the
 * command line</li>
 * </ul>
 * <p>
 * Parameters then need to be processed with:
 * </p>
 * <ol>
 * <li>Annotating the method with @{@link Flattener} to flatten multiple parameter value strings
 * into a single one (defaults to {@link JoiningOnDelimiterFlattener} with <code>" "</code> if
 * annotation omitted)</li>
 * <li>Annotating the method with @{@link Aggregator} to aggregate the command name and the
 * flattened parameter values (defaults to {@link SymbolAggregator} with <code>" "</code> if
 * annotation omitted)</li>
 * </ol>
 * 
 * <h2>Return type</h2> If the method is not annotated with {@link ExecuteNow}, the method must
 * return its interface type.
 * 
 * <h2>Execution</h2> To trigger execution, use {@link ExecuteNow} or {@link ExecuteLater} in
 * addition to this annotation.
 * 
 * @see Converter
 * @see Extra
 * @see Aggregator
 * @see Flattener
 * @see ExecuteNow
 * @see ExecuteLater
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Switch {

  /**
   * The switch name.
   *
   * @return the switch name, can be empty
   */
  String value();
}