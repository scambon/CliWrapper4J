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

package io.github.scambon.cliwrapper4j.executors;

import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A command line executor that traces calls to the execute methods and delegates the actual
 * execution.
 */
public class TracingCommandLineExecutor extends AbstractDelegatingCommandLineExecutor {

  /** The printer. */
  private final Consumer<List<String>> printer;

  /**
   * Instantiates a new tracing command line executor.
   *
   * @param delegate
   *          the delegate
   */
  public TracingCommandLineExecutor(ICommandLineExecutor delegate) {
    this(delegate, System.out::println);
  }

  /**
   * Instantiates a new tracing command line executor.
   *
   * @param delegate
   *          the delegate
   * @param printer
   *          the printer, which is call once for every execute method call
   */
  public TracingCommandLineExecutor(
      ICommandLineExecutor delegate, Consumer<List<String>> printer) {
    super(delegate);
    this.printer = printer;
  }

  @Override
  public Result execute(
      List<String> elements, IExecutionEnvironment executionEnvironment,
      Map<String, Object> extraParameterName2ValueMap) {
    printer.accept(elements);
    return super.execute(elements, executionEnvironment, extraParameterName2ValueMap);
  }
}