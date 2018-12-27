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
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.executors.AbstractInteractiveProcessExecutor;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that describes a command, i.e. a tag and values to be added to the command line
 * that triggers execution.
 * </p>
 * <p>
 * The standard usage is to define the tag name using the {@link #value()} and to provide a
 * {@link Converter} to transform the command execution {@link Result} into something meaningful to
 * you. The annotated method return type must be a class compatible with the converter.
 * </p>
 * <p>
 * In case the command is not the last segment of the command line, i.e. the execution is to be
 * triggered later, fill the {@link #outType()} in addition to the {@link #converter()} and make the
 * method return the specific sub-interface of {@link ICommandLineWrapper}. When you want to execute
 * the command line, call the {@link ICommandLineWrapper#execute()} method. The result will be
 * converted by the {@link #converter()} into the {@link #outType()}.
 * </p>
 * <p>
 * The command line execution is handled by the {@link #executor()}. By default, a
 * {@link ProcessExecutor} is used, which is suitable for non-interactive, short running command
 * lines. Interactive command lines can be executed using subclasses of
 * {@link AbstractInteractiveProcessExecutor} or even custom implementations of
 * {@link ICommandLineExecutor}.
 * </p>
 * <p>
 * By default, the command line return code is checked to be <code>0</code>. You can customize the
 * expected returns code with {@link #expectedReturnCodes()}. You can also disable the checking by
 * setting {@link #expectedReturnCodes()} to <code>{}</code>.
 * </p>
 * <p>
 * The method can have parameters that will be marshalled. Use a combination of:
 * </p>
 * <ol>
 * <li>parameters annotated with @{@link Converter} to convert the parameters to strings to be
 * passed to command lines (this is the default, implicit behavior)</li>
 * <li>parameters annotated with @{@link Extra} to be provided (unconverted) to the
 * {@link #executor()} instead of the command line</li>
 * <li>annotating the method with @{@link Flattener} to flatten multiple parameter value strings
 * into a single one</li>
 * <li>annotating the method with @{@link Aggregator} to aggregate the command name and the
 * flattened parameter values</li>
 * </ol>
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Command {

  /**
   * The name of the command.
   *
   * @return the name of the command, can be empty
   */
  String value();

  /**
   * The executor used to run the command.
   *
   * @return the command line executor, defaults to {@link ProcessExecutor}
   */
  Class<? extends ICommandLineExecutor> executor() default ProcessExecutor.class;

  /**
   * <p>
   * The converter that transforms the command execution {@link Result} into something meaningful to
   * you.
   * </p>
   * <p>
   * Depending on what you want to do...
   * </p>
   * <ul>
   * <li>If the command line is to execute now, the annotated method return type must be a class
   * compatible with this converter</li>
   * <li>If the command is to execute later, the converter will be used when running the
   * {@link ICommandLineWrapper#execute()} method, asking for a conversion to the
   * {@link #outType()}. The annotated method must return the specific sub-interface of
   * {@link ICommandLineWrapper}.</li>
   * </ul>
   * 
   * @return the converter class, defaults to {@link ResultConverter} if not explicitly configured
   */
  Class<? extends IConverter<Result, ?>> converter() default ResultConverter.class;

  /**
   * The type asked to the {@link #converter()}, if the command is to execute later with the
   * {@link ICommandLineWrapper#execute()} method.
   *
   * @return the type asked to the converter, defaults to {@link Object} if not explicitly
   *         configured
   */
  Class<?> outType() default Object.class;

  /**
   * The expected return codes to check against.
   *
   * @return the expected return codes to check against, defaults to <code>{0}</code>, use
   *         <code>{}</code> to disable checking
   */
  int[] expectedReturnCodes() default 0;
}