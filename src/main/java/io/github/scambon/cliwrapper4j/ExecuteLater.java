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

import io.github.scambon.cliwrapper4j.converters.IConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that makes a @{@link Switch}-annotated method run the execution of the command line
 * when the {@link IExecutable#execute()} method is called. This is used when the method is not the
 * last segment of the command line.
 * </p>
 * <p>
 * The semantics (and rules) around this annotation are the same as with {@link ExecuteNow}, but:
 * </p>
 * <ul>
 * <li>The @{@link Switch} method must return its interface</li>
 * <li>The execution return type is defined here in {@link #value()} and must be compatible with
 * the @{@link Converter}</li>
 * </ul>
 * 
 * @see ExecuteNow
 * @see Switch
 * @see Executor
 * @see ReturnCode
 * @see Converter
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExecuteLater {
  /**
   * The type asked to the {@link IConverter}, if the command is to execute later with the
   * {@link IExecutable#execute()} method.
   *
   * @return the type asked to the converter
   */
  Class<?> value();
}