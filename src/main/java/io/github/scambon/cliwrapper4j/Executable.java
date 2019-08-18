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

package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.scambon.cliwrapper4j.preprocessors.ICommandLinePreProcessor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>An annotation that provides the name of the executable to run. This must be placed on an
 * {@link IExecutable} sub-interface.</p>
 * <p>You can also add pre-processors, to modify the command line before executing.</p>
 * 
 * @see IExecutable
 * @see ICommandLinePreProcessor
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Executable {

  /**
   * The name of the executable. This can be multi-part.
   *
   * @return the name of the executable, which must be non-empty
   */
  String[] value();
  
  /**
   * The pre-processors to run on command line elements, in execution order.
   * The result of the pre-processor 1 is passed to pre-processor 2, and so on.
   * The final pre-processed command line elements are passed to execution.
   *
   * @return the pre-processor classes
   */
  Class<? extends ICommandLinePreProcessor>[] preProcessors() default {};
}