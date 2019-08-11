/* Copyright 2018-2019 Sylvain Cambon
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

import io.github.scambon.cliwrapper4j.Result;

/**
 * A converter that provides the base {@link Result} or its properties. The conversion uses the
 * following rules, based on the output type:
 * <ul>
 * <li>{@link Result} returns the same {@link Result}</li>
 * <li>{@link String} returns to the method output stream contents</li>
 * <li>{@link int} and {@link Integer} returns to the return code</li>
 * <li>{@link void} and {@link Void} returns nothing</li>
 * <li>Other: the {@link ReflectiveResultConverter} is used to create the output instance</li>
 * </ul>
 */
public final class ResultConverter extends CompositeConverter<Result, Object> {

  /**
   * Instantiates a new result converter.
   */
  public ResultConverter() {
    super(
        new LambdaConverter<>(Result.class, Result.class, result -> result),
        new LambdaConverter<>(Result.class, String.class, Result::getOutput),
        new LambdaConverter<>(Result.class, int.class, Result::getReturnCode),
        new LambdaConverter<>(Result.class, Integer.class, Result::getReturnCode),
        new LambdaConverter<>(Result.class, void.class, result -> null),
        new LambdaConverter<>(Result.class, Void.class, result -> null),
        new ReflectiveResultConverter<>());
  }
}