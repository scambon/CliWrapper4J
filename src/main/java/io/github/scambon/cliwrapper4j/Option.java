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

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that describes an option, i.e. a tag and values to be added to the command line
 * that does not triggers execution.
 * </p>
 * <p>
 * The method can have parameters that will be marshalled. Use a combination of:
 * </p>
 * <ol>
 * <li>parameters annotated with @{@link Converter} to convert the parameters to strings</li>
 * <li>annotating the method with @{@link Flattener} to flatten multiple parameter value strings
 * into a single one</li>
 * <li>annotating the method with @{@link Aggregator} to aggregate the command name and the
 * flattened parameter values</li>
 * </ol>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Option {

  /**
   * The option name.
   *
   * @return the option name, can be empty
   */
  String value();
}