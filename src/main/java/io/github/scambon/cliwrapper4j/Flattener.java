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

import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that describes how to flatten a method parameter values, using the
 * {@link #flattener()} class configured with the {@link #value()} as its parameter. This should
 * only defined along with a @{@link Switch} annotation.
 * 
 * @see Switch
 * @see IFlattener
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Flattener {

  /**
   * The flattener parameter, which interpretation depends on the {@link #flattener()} class
   * implementation.
   *
   * @return the flattener parameter, defaults to <code>" "</code> (single space) if not explicitly
   *         configured
   */
  String value() default " ";

  /**
   * The flattener class to use to flatten the parameter values.
   *
   * @return the flattener class, defaults to {@link JoiningOnDelimiterFlattener} if not explicitly
   *         configured
   */
  Class<? extends IFlattener> flattener() default JoiningOnDelimiterFlattener.class;
}