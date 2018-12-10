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

import static io.github.scambon.cliwrapper4j.internal.utils.AnnotationUtils.getOrDefaultClass;
import static java.util.Arrays.asList;

import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A command line node that works for executables.
 */
public class ExecutableNode implements ICommandLineNode {

  /** The execution environment. */
  private final IExecutionEnvironment executionEnvironment;
  /** The executable. */
  private final String[] executable;
  /** The command and options. */
  private final List<CommandOrOptionWithParametersNode> commandAndOptions = new ArrayList<>();
  
  /** The executor. */
  private ICommandLineExecutor executor;
  /** The expected return codes. */
  private int[] expectedReturnCodes;
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
    this.executable = executable;
    this.executionEnvironment = executionEnvironment;
  }

  /**
   * Adds the command or option.
   *
   * @param commandOrOption
   *          the command or option
   */
  public void addCommandOrOption(CommandOrOptionWithParametersNode commandOrOption) {
    commandAndOptions.add(commandOrOption);
  }
  
  /**
   * Sets the command to run.
   *
   * @param method
   *          the method
   * @param command
   *          the associated command annotation
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   */
  public void setCommand(
      Method method, Command command,
      Map<String, Object> extraParameterName2ValueMap) {
    this.executor = getOrDefaultClass(method, Command.class, Command::executor,
        ProcessExecutor::new);
    this.extraParameterName2ValueMap = extraParameterName2ValueMap;
    this.expectedReturnCodes = command.expectedReturnCodes();
    Class<?> returnType = method.getReturnType();
    boolean executesOnCommand = executesOnCommand(method);
    if (executesOnCommand) {
      this.outType = returnType;
    } else {
      this.outType = command.outType();
    }
    this.resultConverter = getOrDefaultClass(method, Command.class, Command::converter,
        ResultConverter::new);
  }

  /**
   * Tells whether to execute with the command.
   *
   * @param method
   *          the method
   * @return whether to execute with the command, or to wait for the
   *         {@link ICommandLineWrapper#execute()} call
   */
  public static boolean executesOnCommand(Method method) {
    Class<?> returnType = method.getReturnType();
    Class<?> commandLineWrapperInterface = method.getDeclaringClass();
    boolean executesOnCommand = !returnType.equals(commandLineWrapperInterface);
    return executesOnCommand;
  }

  @Override
  public List<String> flatten() {
    List<String> elements = new ArrayList<>();
    elements.addAll(asList(executable));
    List<String> children = commandAndOptions.stream()
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
    Object convertedResult = runPostProcessing(result);
    return convertedResult;
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
    Object convertedResult = resultConverter.convert(result, (Class) outType);
    return convertedResult;
  }

  /**
   * Validates the return code if needed.
   *
   * @param result
   *          the result
   */
  private void validateIfNeeded(Result result) {
    if (expectedReturnCodes.length != 0) {
      int returnCode = result.getReturnCode();
      Arrays.stream(expectedReturnCodes)
          .filter(expectedReturnCode -> returnCode == expectedReturnCode)
          .findAny()
          .orElseThrow(() -> new CommandLineException("Finished with code '" + returnCode
              + "' but expected it in '" + Arrays.toString(expectedReturnCodes) + "'"));
    }
  }
}