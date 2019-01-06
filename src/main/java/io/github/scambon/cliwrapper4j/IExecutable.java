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

/**
 * <p>
 * The base interface that defines the command line.
 * </p>
 * <p>
 * Your sub-interface must be annotated with @{@link Executable} to provide the executable name.
 * Then, define your methods and annotate them with @{@link Switch}; some of them will be executable
 * using an additional @{@link ExecuteNow} or @{@link ExecuteLater}.
 * </p>
 * <p>
 * There is no need to implement this interface yourself, use a {@link IExecutableFactory} instead.
 * You can still define and implement static methods, default methods or even private methods
 * (JDK9+) in your sub-interface.
 * </p>
 */
public interface IExecutable {

  /**
   * Executes the command line defined by the previous method calls in case
   * of @{@link ExecuteLater}.
   *
   * @param <O>
   *          the output type
   * @return the command line result
   */
  <O> O execute();
}