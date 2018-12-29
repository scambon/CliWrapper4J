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

import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that describes how to aggregate a switch and its flattened parameter values, using
 * the {@link #aggregator()} class configured with the {@link #value()} as its parameter. This
 * should only defined along with a @{@link Switch} annotation.
 * 
 * @see Switch
 * @see IAggregator
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Aggregator {

  /**
   * The aggregator parameter, which interpretation depends on the {@link #aggregator()} class
   * implementation.
   *
   * @return the aggregator parameter, defaults to <code>" "</code> (single space) if not explicitly
   *         configured
   */
  String value() default " ";

  /**
   * The aggregator class to use to join the switch and the flattened parameter values.
   *
   * @return the aggregator class, defaults to {@link SymbolAggregator} if not explicitly configured
   */
  Class<? extends IAggregator> aggregator() default SymbolAggregator.class;
}