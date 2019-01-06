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

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * <p>
 * An annotation for @{@link Switch} method parameters that are to be passed to the framework
 * instead of directly to the command line.
 * </p>
 * <p>
 * Usage include:
 * </p>
 * <ul>
 * <li>{@link IConverter#convert(Object, Class, Map)}</li>
 * <li>{@link IFlattener#flatten(java.util.List, String, Map)}</li>
 * <li>{@link IAggregator#aggregate(String, String, String, Map)}</li>
 * <li>
 * {@link IExecutor#execute(
 * java.util.List, io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment, Map)}
 * </li>
 * </ul>
 * <p>
 * The meaning of the parameter values passed as {@link Extra} arguments is left to the
 * implementation of the above interfaces.
 * </p>
 * <p>
 * This annotation is not compatible with {@link Converter}: values are passed without conversion.
 * </p>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Extra {

  /**
   * The extra parameter name, used to obtain the value.
   *
   * @return the parameter name
   */
  String value();
}