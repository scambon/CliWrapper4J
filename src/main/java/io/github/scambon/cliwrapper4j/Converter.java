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

import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation that describes what {@link IConverter} must be used to convert from one method
 * parameter value to {@link String}s. This annotation is not compatible with {@link Extra}.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface Converter {

  /**
   * The {@link IConverter} to be used to convert from one method parameter value to
   * {@link String}s.
   *
   * @return the converter, defaults to {@link StringQuotedIfNeededConverter} if not explicitly
   *         defined
   */
  Class<? extends IConverter<?, String>> value() default StringQuotedIfNeededConverter.class;
}