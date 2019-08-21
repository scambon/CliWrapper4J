/*
 * Copyright 2018 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.check;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ExecuteLater;
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.Executor;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.ReturnCode;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.aggregators.IAggregator;
import io.github.scambon.cliwrapper4j.converters.FilesWithSpaceSeparatorParameterConverter;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.converters.StringConverter;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.example.VersionResultConverter;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.internal.check.Diagnostic;
import io.github.scambon.cliwrapper4j.internal.check.ExecutableSubInterfaceChecker;
import io.github.scambon.cliwrapper4j.internal.check.Issue;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

public class ExecutableSubInterfaceCheckerTest {
  public interface NotExecutableClass extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever();
  }

  @Test
  public void testFailOnCreatingNotExecutableClass() {
    List<Issue> issues = getIssues(NotExecutableClass.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("NotExecutableClass", "No @Executable"));
  }

  @Executable({})
  public interface EmptyArrayExecutable extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever();
  }

  @Test
  public void testFailOnCreatingEmptyArrayExecutable() {
    List<Issue> issues = getIssues(EmptyArrayExecutable.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("EmptyArrayExecutable", "empty executable array"));
  }

  @Executable("")
  public interface EmptyNameExecutable extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever();
  }

  @Test
  public void testFailOnCreatingEmptyExecutable() {
    List<Issue> issues = getIssues(EmptyNameExecutable.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("EmptyNameExecutable", "empty executable name"));
  }

  @Executable("!")
  public static class NotInterface implements IExecutable {
    @Override
    public <O> O execute() {
      throw new UnsupportedOperationException();
    }
  }

  @Test
  public void testFailOnCreatingNonInterface() {
    List<Issue> issues = getIssues(NotInterface.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("NotInterface", "not an interface"));
  }

  @Executable("!")
  public interface NotSwitchNorHandledMethod extends IExecutable {

    @Flattener
    int shouldNotHaveAFlattener();

    @Aggregator
    int shouldNotHaveAnAggregator();

    @Converter(ResultConverter.class)
    int shouldNotHaveAConverter(String s1, String s2);
    
    int shouldNotHaveAParameterConverter(@Converter(StringConverter.class) String s1, String s2);
  }

  @Test
  public void testFailOnCreatingNotSwitchNorHandledMethod() {
    List<Issue> issues = getIssues(NotSwitchNorHandledMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAnAggregator", "Aggregator"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAnAggregator", "nor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAFlattener", "Flattener"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAFlattener", "nor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAConverter", "Converter"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAConverter", "nor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAParameterConverter", "Converter"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAParameterConverter", "nor"));
  }

  @Executable("!")
  public interface ExecuteNowAndLaterMethod extends IExecutable {
    @Switch("!")
    @ExecuteNow
    @ExecuteLater(Object.class)
    int whatever();
  }

  @Test
  public void testFailOnCreatingExecuteNowAndLaterMethod() {
    List<Issue> issues = getIssues(ExecuteNowAndLaterMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "both @ExecuteNow and @ExecuteLater"));
  }

  @Executable("!")
  public interface ConverterAndExtraOnCommandMethod extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever(@Converter(StringConverter.class) @Extra("extra") String whatever);
  }

  @Test
  public void testFailOnCreatingConverterAndExtraOnCommandMethod() {
    List<Issue> issues = getIssues(ConverterAndExtraOnCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "@Converter and @Extra"));
  }

  @Executable("!")
  public interface MultipleExtraWithSameNameOnCommandMethod extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever(@Extra("extra") String whatever1, @Extra("extra") String whatever2);
  }

  @Test
  public void testFailOnCreatingMultipleExtraWithSameNameOnOptionMethod() {
    List<Issue> issues = getIssues(MultipleExtraWithSameNameOnCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Multiple parameters"));
  }

  @Executable("!")
  public interface ParameterConversionFailureMethod extends IExecutable {
    @Switch("!")
    @ExecuteNow
    int whatever(@Converter(FilesWithSpaceSeparatorParameterConverter.class) Properties properties);
  }

  @Test
  public void testFailOnCreatingParameterConversionFailureMethod() {
    List<Issue> issues = getIssues(ParameterConversionFailureMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Properties"));
  }

  @Executable("!")
  public interface ReturnValueConversionFailureCommandMethod extends IExecutable {
    @Switch("!")
    @ExecuteNow
    @Converter(VersionResultConverter.class)
    Properties whatever();
  }

  @Test
  public void testFailOnCreatingReturnValueConversionFailureCommandMethod() {
    List<Issue> issues = getIssues(ReturnValueConversionFailureCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Properties"));
  }

  @Executable("!")
  public interface ReturnValueLaterConversionFailureCommandMethod extends IExecutable {
    @Switch("!")
    @ExecuteLater(value = Collection.class)
    ReturnValueLaterConversionFailureCommandMethod whatever();
  }

  @Test
  public void testFailOnCreatingReturnValueLaterConversionFailureCommandMethod() {
    List<Issue> issues = getIssues(ReturnValueLaterConversionFailureCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Collection"));
  }

  @Executable("!")
  public interface IgnoredMethodWithCommand extends IExecutable {
    @Switch("!")
    @ExecuteLater(value = Properties.class)
    default int whatever(@Converter(StringConverter.class) String p, @Extra("e") String e) {
      return 42;
    }
  }

  @Test
  public void testFailOnCreatingIgnoredMethodWithCommand() {
    List<Issue> issues = getIssues(IgnoredMethodWithCommand.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Ignored method"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "@Converter"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "@Extra"));
  }

  @Executable("!")
  public interface BrokenAnnotationDependencyMethods extends IExecutable {

    @ExecuteNow
    void executeNowWithoutSwitch();

    @ExecuteLater(Properties.class)
    void executeLaterWithoutSwitch();

    @Flattener
    void flattenerWithoutSwitch();

    @Aggregator
    void aggregatorWithoutSwitch();

    @Executor
    void executorWithoutExecute();

    @ReturnCode
    void returnCodeWithoutExecute();
  }

  @Test
  public void testFailOnCreatingBrokenAnnotationDependencyMethods() {
    List<Issue> issues = getIssues(BrokenAnnotationDependencyMethods.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("executeNowWithoutSwitch", "Switch"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("executeLaterWithoutSwitch", "Switch"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("flattenerWithoutSwitch", "Switch"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("aggregatorWithoutSwitch", "Switch"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("executorWithoutExecute", "ExecuteNow"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("returnCodeWithoutExecute", "ExecuteNow"));
  }

  public static class NonReflectivelyCreatableExecutor implements IExecutor {

    public NonReflectivelyCreatableExecutor(String s) {
    }

    @SuppressWarnings("unused")
    private NonReflectivelyCreatableExecutor() {
    }

    @Override
    public Result execute(List<String> elements, IExecutionEnvironment environment,
        Map<String, Object> extraParameterName2ValueMap) {
      throw new UnsupportedOperationException();
    }
  }

  public static class NonReflectivelyCreatableFlattener implements IFlattener {

    public NonReflectivelyCreatableFlattener(String s) {
    }

    @SuppressWarnings("unused")
    private NonReflectivelyCreatableFlattener() {
    }

    @Override
    public String flatten(List<String> parameterValues, String flattenerParameter,
        Map<String, Object> extraParameterName2ValueMap) {
      throw new UnsupportedOperationException();
    }
  }

  public static class NonReflectivelyCreatableAggregator implements IAggregator {

    public NonReflectivelyCreatableAggregator(String s) {
    }

    @SuppressWarnings("unused")
    private NonReflectivelyCreatableAggregator() {
    }

    @Override
    public String aggregate(String zwitch, String flattenedParameterValues,
        String aggregatorParameter, Map<String, Object> extraParameterName2ValueMap) {
      throw new UnsupportedOperationException();
    }
  }

  @Executable("!")
  public interface NotReflectivelyCreatableClassesMethods extends IExecutable {

    @Switch("s")
    @ExecuteNow
    @Executor(NonReflectivelyCreatableExecutor.class)
    void brokenExecutable();

    @Switch("s")
    @Flattener(flattener = NonReflectivelyCreatableFlattener.class)
    void brokenFlattener();

    @Switch("s")
    @Aggregator(aggregator = NonReflectivelyCreatableAggregator.class)
    void brokenAggregator();
  }
  
  @Test
  public void testFailOnCreatingNonReflectivelyCreatibleClasses() {
    List<Issue> issues = getIssues(NotReflectivelyCreatableClassesMethods.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains(
            "brokenExecutable", "NonReflectivelyCreatableExecutor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains(
            "brokenFlattener", "NonReflectivelyCreatableFlattener"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains(
            "brokenAggregator", "NonReflectivelyCreatableAggregator"));
  }

  @Test
  public void testCheckDiagnostic() {
    ExecutableSubInterfaceChecker checker = new ExecutableSubInterfaceChecker();
    Diagnostic diagnostic = checker.validateInterface(IgnoredMethodWithCommand.class);
    assertThrows(CommandLineException.class, () -> diagnostic.check());
  }

  private <W extends IExecutable> List<Issue> getIssues(Class<W> clazz) {
    ExecutableSubInterfaceChecker checker = new ExecutableSubInterfaceChecker();
    Diagnostic diagnostic = checker.validateInterface(clazz);
    List<Issue> issues = diagnostic.getIssues();
    return issues;
  }

  private BiFunction<AnnotatedElement, String, Boolean> annotatedElementAndDescriptionContains(
      String expectedAnnotatedElementPart, String expectedDescriptionPart) {
    return (annotatedElement, description) -> annotatedElement.toString()
        .contains(expectedAnnotatedElementPart)
        && description.contains(expectedDescriptionPart);
  }

  private void assertOneIssueMatches(Collection<Issue> issues,
      BiFunction<AnnotatedElement, String, Boolean> matcher) {
    long count = issues.stream()
        .filter(issue -> matcher.apply(issue.getElement(), issue.getDescription()))
        .count();
    assertNotEquals(0, count);
  }
}