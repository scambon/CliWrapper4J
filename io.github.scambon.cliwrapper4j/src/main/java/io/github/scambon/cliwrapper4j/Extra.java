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

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * An annotation for command method parameters that are to be passed to the executor instead of
 * directly to the command line. This is usually associated to custom implementations of
 * {@link ICommandLineExecutor#execute(
 * java.util.List, io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment,
 * java.util.Map)}, for which the value can be retrieved from the {@link Map} parameter.
 * This annotation is not compatible with {@link Converter}.
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