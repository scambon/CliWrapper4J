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

package io.github.scambon.cliwrapper4j.internal.nodes;

import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A command line node that works for a command or option.
 */
public class CommandOrOptionWithParametersNode implements ICommandLineNode {

  /** The command or option. */
  private final String commandOrOption;
  /** The aggregator. */
  private final IAggregator aggregator;
  /** The aggregator parameter. */
  private final String aggregatorParameter;
  /** The flattener. */
  private final IFlattener flattener;
  /** The flattener parameter. */
  private final String flattenerParameter;
  /** The parameters. */
  private final List<ParameterNode<?>> parameters = new ArrayList<>();

  /**
   * Instantiates a new command or option with parameters node.
   *
   * @param commandOrOption
   *          the command or option
   * @param aggregator
   *          the aggregator
   * @param aggregatorParameter
   *          the aggregator parameter
   * @param flattener
   *          the flattener
   * @param flattenerParameter
   *          the flattener parameter
   */
  public CommandOrOptionWithParametersNode(String commandOrOption, IAggregator aggregator,
      String aggregatorParameter, IFlattener flattener, String flattenerParameter) {
    this.commandOrOption = commandOrOption;
    this.aggregator = aggregator;
    this.aggregatorParameter = aggregatorParameter;
    this.flattener = flattener;
    this.flattenerParameter = flattenerParameter;
  }

  /**
   * Adds the given parameter.
   *
   * @param parameter
   *          the parameter
   */
  public void addParameter(ParameterNode<?> parameter) {
    parameters.add(parameter);
  }

  @Override
  public List<String> flatten() {
    List<String> convertedParameters = parameters.stream()
        .map(ParameterNode::flatten)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    String flattenedConvertedParameters = flattener.flatten(convertedParameters,
        flattenerParameter);
    String aggregatedCommandOrOptionWithParameters = aggregator.aggregate(commandOrOption,
        flattenedConvertedParameters, aggregatorParameter);
    return Collections.singletonList(aggregatedCommandOrOptionWithParameters);
  }
}