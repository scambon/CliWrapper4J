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

package io.github.scambon.cliwrapper4j.internal.handlers;

import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefault;
import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefaultClass;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;
import io.github.scambon.cliwrapper4j.internal.nodes.CommandOrOptionWithParametersNode;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;
import io.github.scambon.cliwrapper4j.internal.nodes.ParameterNode;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * A base method handler that takes care of all shared behaviors for all commands or options
 * flavours.
 */
public class AbstractCommandOrOptionWithParametersMethodHandler implements IMethodHandler {

  /** The (non-extra) parameter index to its converter. */
  private final Map<Integer, IConverter<?, String>> parameterIndex2ConverterMap = new TreeMap<>();
  /** The node supplier. */
  private Supplier<CommandOrOptionWithParametersNode> nodeSupplier;

  /**
   * Instantiates a new abstract command or option with parameters method handler.
   *
   * @param method
   *          the method
   * @param commandOrOption
   *          the command or option
   */
  public AbstractCommandOrOptionWithParametersMethodHandler(Method method, String commandOrOption) {
    IAggregator aggregator = getOrDefaultClass(method, Aggregator.class, Aggregator::aggregator,
        SymbolAggregator::new);
    String aggregatorParameter = getOrDefault(method, Aggregator.class, Aggregator::value,
        () -> " ");
    IFlattener flattener = getOrDefaultClass(method, Flattener.class, Flattener::flattener,
        JoiningOnDelimiterFlattener::new);
    String flattenerParameter = getOrDefault(method, Flattener.class, Flattener::value, () -> " ");
    this.nodeSupplier = () -> new CommandOrOptionWithParametersNode(commandOrOption, aggregator,
        aggregatorParameter, flattener, flattenerParameter);
    Parameter[] parameters = method.getParameters();
    for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
      Parameter parameter = parameters[parameterIndex];
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);
      if (extraAnnotation == null) {
        IConverter<?, String> converter = getOrDefaultClass(
            parameter, Converter.class, Converter::value, StringQuotedIfNeededConverter::new);
        parameterIndex2ConverterMap.put(parameterIndex, converter);
      }
    }
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    CommandOrOptionWithParametersNode commandOrOptionNode = nodeSupplier.get();
    fillParameters(commandOrOptionNode, arguments);
    executableNode.addCommandOrOption(commandOrOptionNode);
    return proxy;
  }

  /**
   * Fills the parameters.
   *
   * @param commandOrOptionNode
   *          the command or option node to fill
   * @param args
   *          the arguments to process
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void fillParameters(
      CommandOrOptionWithParametersNode commandOrOptionNode, Object[] args) {
    for (Entry<Integer, IConverter<?, String>> entry : parameterIndex2ConverterMap.entrySet()) {
      Integer index = entry.getKey();
      IConverter<?, String> converter = entry.getValue();
      Object value = args[index];
      ParameterNode parameterNode = new ParameterNode(converter, value);
      commandOrOptionNode.addParameter(parameterNode);
    }
  }
}