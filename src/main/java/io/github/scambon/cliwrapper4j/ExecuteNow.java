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

import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that makes a @{@link Switch}-annotated method run the execution of the command line
 * now.
 * </p>
 * 
 * <h2>Execution</h2>
 * <p>
 * The execution is handled as defined by the {@link Executor} on the method. If none is specified,
 * a {@link ProcessExecutor} is implicitly used.
 * </p>
 * 
 * <h2>Command line return code</h2>
 * <p>
 * The return code is checked against the expectations defined by the {@link ReturnCode} on the
 * method. If none is specified, <code>0</code> is implicitly expected.
 * </p>
 * 
 * <h2>Return value</h2>
 * <p>
 * The return type must be compatible with the {@link Converter} on the method. If none is
 * specified, a {@link ResultConverter} is implicitly used.
 * </p>
 * 
 * @see Switch
 * @see Executor
 * @see ReturnCode
 * @see Converter
 * @see ExecuteLater
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ExecuteNow {

}