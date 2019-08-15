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

package io.github.scambon.cliwrapper4j.internal.handlers;

import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefault;
import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefaultClass;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;
import io.github.scambon.cliwrapper4j.internal.nodes.ParameterNode;
import io.github.scambon.cliwrapper4j.internal.nodes.SwitchNode;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * A method handler that works for @{@link Switch} methods.
 */
public class SwitchMethodHandler implements IMethodHandler {

  /** The (non-extra) parameter index to its converter. */
  private final Map<Integer, IConverter<?, String>> parameterIndex2ConverterMap = new TreeMap<>();
  /** The extra parameter name 2 index map. */
  private final Map<Integer, String> extraParameterName2IndexMap = new TreeMap<>();
  /** The node supplier. */
  private final Supplier<SwitchNode> switchNodeSupplier;

  /**
   * Instantiates a new abstract command or switch with parameters method handler.
   *
   * @param method
   *          the method
   * @param zwitch
   *          the command or switch
   * @param instantiator
   *          the instantiator
   */
  @SuppressWarnings("unchecked")
  public SwitchMethodHandler(Method method, Switch zwitch, IInstantiator instantiator) {
    IAggregator aggregator = getOrDefaultClass(
        method, Aggregator.class, Aggregator::aggregator, instantiator, SymbolAggregator::new);
    String aggregatorParameter = getOrDefault(
        method, Aggregator.class, Aggregator::value, () -> " ");
    IFlattener flattener = getOrDefaultClass(
        method,
        Flattener.class, Flattener::flattener, instantiator,
        JoiningOnDelimiterFlattener::new);
    String flattenerParameter = getOrDefault(method, Flattener.class, Flattener::value, () -> " ");
    this.switchNodeSupplier = () -> new SwitchNode(
        zwitch, aggregator, aggregatorParameter, flattener, flattenerParameter);
    Parameter[] parameters = method.getParameters();
    for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
      Parameter parameter = parameters[parameterIndex];
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);
      if (extraAnnotation == null) {
        IConverter<?, String> converter = (IConverter<?, String>) getOrDefaultClass(
            parameter,
            Converter.class, Converter::value, instantiator,
            StringQuotedIfNeededConverter::new);
        parameterIndex2ConverterMap.put(parameterIndex, converter);
      } else {
        String parameterName = extraAnnotation.value();
        extraParameterName2IndexMap.put(parameterIndex, parameterName);
      }
    }
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    SwitchNode zwitchNode = switchNodeSupplier.get();
    fillParameters(zwitchNode, arguments);
    executableNode.addSwitchNodes(zwitchNode);
    fillExtraParameters(zwitchNode, arguments);
    return proxy;
  }

  /**
   * Fills the parameters.
   *
   * @param switchNode
   *          the command or switch node to fill
   * @param args
   *          the arguments to process
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private void fillParameters(
      SwitchNode switchNode, Object[] args) {
    for (Entry<Integer, IConverter<?, String>> entry : parameterIndex2ConverterMap.entrySet()) {
      Integer index = entry.getKey();
      IConverter<?, String> converter = entry.getValue();
      Object value = args[index];
      ParameterNode parameterNode = new ParameterNode(converter, value);
      switchNode.addParameter(parameterNode);
    }
  }

  /**
   * Fills the extra parameters.
   *
   * @param switchNode
   *          the switch node
   * @param args
   *          the args
   */
  private void fillExtraParameters(
      SwitchNode switchNode, Object[] args) {
    Map<String, Object> extraParameterName2ValueMap = createExtraParameterName2ValueMap(args);
    switchNode.addExtraParameters(extraParameterName2ValueMap);
  }

  /**
   * Creates the extra parameter name 2 value map.
   *
   * @param args
   *          the args
   * @return the map
   */
  protected final Map<String, Object> createExtraParameterName2ValueMap(Object[] args) {
    Map<String, Object> extraParameterName2ValueMap = new TreeMap<>();
    for (Entry<Integer, String> entry : extraParameterName2IndexMap.entrySet()) {
      Integer index = entry.getKey();
      String name = entry.getValue();
      Object value = args[index];
      extraParameterName2ValueMap.put(name, value);
    }
    return extraParameterName2ValueMap;
  }
}