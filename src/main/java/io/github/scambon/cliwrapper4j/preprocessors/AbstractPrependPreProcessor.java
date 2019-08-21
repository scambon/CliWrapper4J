/*
 * Copyright 2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.preprocessors;

import static java.util.Arrays.asList;

import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.util.ArrayList;
import java.util.List;

/**
 * A pre-processor that prepends some base elements to the command line.
 */
public abstract class AbstractPrependPreProcessor implements ICommandLinePreProcessor {
  /** The base command line element to prepend. */
  private final List<String> base;
  
  /**
   * Instantiates a new abstract prepend pre-processor.
   *
   * @param base
   *          the base command line element to prepend
   */
  public AbstractPrependPreProcessor(String... base) {
    this(asList(base));
  }
  
  /**
   * Instantiates a new abstract prepend pre processor.
   *
   * @param base
   *          the base command line element to prepend
   */
  public AbstractPrependPreProcessor(List<String> base) {
    this.base = base;
  }

  @Override
  public final List<String> process(
      List<String> rawCommandLineElements, IExecutionEnvironment environment) {
    List<String> finalCommandLine = new ArrayList<>(base);
    finalCommandLine.addAll(rawCommandLineElements);
    return finalCommandLine;
  }
}