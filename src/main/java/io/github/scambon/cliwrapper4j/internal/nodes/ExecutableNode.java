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

import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefault;
import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefaultClass;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.ExecuteLater;
import io.github.scambon.cliwrapper4j.Executor;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.ReturnCode;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A command line node that works for @{@link IExecutable}.
 */
public final class ExecutableNode implements ICommandLineNode {

  /** The execution environment. */
  private final IExecutionEnvironment executionEnvironment;
  /** The executable. */
  private final List<String> executable;
  /** The command and options. */
  private final List<SwitchNode> switchNodes = new ArrayList<>();

  /** The executor. */
  private IExecutor executor;
  /** The expected return codes. */
  private List<Integer> expectedReturnCodes;
  /** The result converter. */
  private IConverter<Result, ?> resultConverter;
  /** The out type. */
  private Class<?> outType;
  /** The extra parameter name 2 value map. */
  private Map<String, Object> extraParameterName2ValueMap;

  /**
   * Instantiates a new executable node.
   *
   * @param executable
   *          the executable
   * @param executionEnvironment
   *          the execution environment
   */
  public ExecutableNode(String[] executable, IExecutionEnvironment executionEnvironment) {
    this.executable = asList(executable);
    this.executionEnvironment = executionEnvironment;
  }

  /**
   * Adds the switch node.
   *
   * @param zwitch
   *          the switch
   */
  public void addSwitchNodes(SwitchNode zwitch) {
    switchNodes.add(zwitch);
  }

  /**
   * Sets the execute now context.
   *
   * @param method
   *          the method
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   */
  public void setExecuteNowContext(
      Method method, Map<String, Object> extraParameterName2ValueMap) {
    this.outType = method.getReturnType();
    setExecutionContext(method, extraParameterName2ValueMap);
  }

  /**
   * Sets the execute later context.
   *
   * @param method
   *          the method
   * @param executeLater
   *          the execute later
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   */
  public void setExecuteLaterContext(
      Method method, ExecuteLater executeLater,
      Map<String, Object> extraParameterName2ValueMap) {
    this.outType = executeLater.value();
    setExecutionContext(method, extraParameterName2ValueMap);
  }

  /**
   * Sets the execution context.
   *
   * @param method
   *          the method
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   */
  @SuppressWarnings("unchecked")
  private void setExecutionContext(
      Method method, Map<String, Object> extraParameterName2ValueMap) {
    this.extraParameterName2ValueMap = extraParameterName2ValueMap;
    this.executor = getOrDefaultClass(
        method, Executor.class, Executor::value, ProcessExecutor::new);
    int[] expectedReturnCodeArray = getOrDefault(
        method, ReturnCode.class, ReturnCode::value, () -> new int[]{0});
    this.expectedReturnCodes = Arrays.stream(expectedReturnCodeArray)
        .boxed()
        .collect(toList());
    this.resultConverter = (IConverter<Result, ?>) getOrDefaultClass(
        method, Converter.class, Converter::value, ResultConverter::new);
  }

  @Override
  public List<String> flatten() {
    List<String> elements = new ArrayList<>();
    elements.addAll(executable);
    List<String> children = switchNodes.stream()
        .map(ICommandLineNode::flatten)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
    elements.addAll(children);
    return elements;
  }

  /**
   * Executes the command and the post-processing.
   *
   * @return the result
   */
  public Object execute() {
    List<String> commandLineElements = flatten();
    Result result = executionEnvironment.run(
        executor, commandLineElements, extraParameterName2ValueMap);
    return runPostProcessing(result);
  }

  /**
   * Runs the post-processing.
   *
   * @param result
   *          the execution result
   * @return the converted result
   */
  @SuppressWarnings({"rawtypes", "unchecked"})
  private Object runPostProcessing(Result result) {
    validateIfNeeded(result);
    return resultConverter.convert(result, (Class) outType, extraParameterName2ValueMap);
  }

  /**
   * Validates the return code if needed.
   *
   * @param result
   *          the result
   */
  private void validateIfNeeded(Result result) {
    if (!expectedReturnCodes.isEmpty()) {
      int returnCode = result.getReturnCode();
      if (!expectedReturnCodes.contains(returnCode)) {
        throw new CommandLineException("Finished with code '" + returnCode
            + "' but expected it in '" + expectedReturnCodes + "'");
      }
    }
  }
}