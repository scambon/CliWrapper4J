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

import static java.util.stream.Collectors.toMap;

import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.internal.nodes.ExecutableNode;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * A method handler that works for commands. This class does not handle execution, but subclasses
 * are free to add such behaviors.
 */
public class CommandWithParametersMethodHandler
    extends
      AbstractCommandOrOptionWithParametersMethodHandler {

  /** A consumer that configures the executable node for the handled command. */
  private BiConsumer<ExecutableNode, Map<String, Object>> executableNodeConfigurator;
  /** The extra parameter name 2 index map. */
  private Map<Integer, String> extraParameterName2IndexMap = new TreeMap<>();

  /**
   * Instantiates a new command with parameters method handler.
   *
   * @param method
   *          the method
   * @param command
   *          the command
   */
  public CommandWithParametersMethodHandler(Method method, Command command) {
    super(method, command.value());
    this.executableNodeConfigurator = (executableNode,
        extraParameterName2ValueMap) -> executableNode.setCommand(method, command,
            extraParameterName2ValueMap);
    Parameter[] parameters = method.getParameters();
    for (int parameterIndex = 0; parameterIndex < parameters.length; parameterIndex++) {
      Parameter parameter = parameters[parameterIndex];
      Extra extraAnnotation = parameter.getAnnotation(Extra.class);
      if (extraAnnotation != null) {
        String parameterName = extraAnnotation.value();
        extraParameterName2IndexMap.put(parameterIndex, parameterName);
      }
    }
  }

  @Override
  public Object handle(Object proxy, Object[] arguments, ExecutableNode executableNode) {
    super.handle(proxy, arguments, executableNode);
    Map<String, Object> extraParameterName2ValueMap = extraParameterName2IndexMap.entrySet()
        .stream()
        .collect(toMap(
            Entry::getValue,
            entry -> arguments[entry.getKey()]));
    executableNodeConfigurator.accept(executableNode, extraParameterName2ValueMap);
    return proxy;
  }
}