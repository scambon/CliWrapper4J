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

package io.github.scambon.cliwrapper4j.check;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.converters.FilesWithSpaceSeparatorConverter;
import io.github.scambon.cliwrapper4j.converters.StringConverter;
import io.github.scambon.cliwrapper4j.example.VersionResultConverter;
import io.github.scambon.cliwrapper4j.internal.check.CommandLineWrapperChecker;
import io.github.scambon.cliwrapper4j.internal.check.Diagnostic;
import io.github.scambon.cliwrapper4j.internal.check.Issue;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.function.BiFunction;

import org.junit.jupiter.api.Test;

public class CommandLineWrapperCheckerTest {
  public interface NotExecutableClass extends ICommandLineWrapper {
    @Command("!")
    int whatever();
  }

  @Test
  public void testFailOnCreatingNotExecutableClass() {
    List<Issue> issues = getIssues(NotExecutableClass.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("NotExecutableClass", "No @Executable"));
  }

  @Executable({})
  public interface EmptyArrayExecutable extends ICommandLineWrapper {
    @Command("!")
    int whatever();
  }
  
  @Test
  public void testFailOnCreatingEmptyArrayExecutable() {
    List<Issue> issues = getIssues(EmptyArrayExecutable.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("EmptyArrayExecutable", "empty executable array"));
  }
  
  @Executable("")
  public interface EmptyNameExecutable extends ICommandLineWrapper {
    @Command("!")
    int whatever();
  }

  @Test
  public void testFailOnCreatingEmptyExecutable() {
    List<Issue> issues = getIssues(EmptyNameExecutable.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("EmptyNameExecutable", "empty executable name"));
  }

  @Executable("!")
  public static class NotInterface implements ICommandLineWrapper {
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
  public interface NotCommantNotOptionNotHandledMethod extends ICommandLineWrapper {

    @Flattener
    int shouldNotHaveAFlattener();

    @Aggregator
    int shouldNotHaveAnAggregator();

    int shouldNotHaveAConverter(@Converter(StringConverter.class) String s1, String s2);
  }

  @Test
  public void testFailOnCreatingNotCommantNotOptionNotHandledMethod() {
    List<Issue> issues = getIssues(NotCommantNotOptionNotHandledMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAnAggregator", "Aggregator"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAnAggregator", "nor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAFlattener", "Flattener"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAFlattener", "nor"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("arg0", "Converter"));
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("shouldNotHaveAConverter", "nor"));
  }

  @Executable("!")
  public interface CommandOrOptionMethod extends ICommandLineWrapper {
    @Command("!")
    @Option("!")
    int whatever();
  }

  @Test
  public void testFailOnCreatingCommandOrOptionMethod() {
    List<Issue> issues = getIssues(CommandOrOptionMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "both"));
  }
  
  @Executable("!")
  public interface ExtraOnOptionMethod extends ICommandLineWrapper {
    @Option("!")
    int whatever(@Extra("extra") String whatever);
  }

  @Test
  public void testFailOnCreatingExtraOnOptionMethod() {
    List<Issue> issues = getIssues(ExtraOnOptionMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Extra"));
  }
  
  @Executable("!")
  public interface ConverterAndExtraOnCommandMethod extends ICommandLineWrapper {
    @Command("!")
    int whatever(@Converter(StringConverter.class) @Extra("extra") String whatever);
  }
  
  @Test
  public void testFailOnCreatingConverterAndExtraOnCommandMethod() {
    List<Issue> issues = getIssues(ConverterAndExtraOnCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "@Converter and @Extra"));
  }
  
  @Executable("!")
  public interface MultipleExtraWithSameNameOnCommandMethod extends ICommandLineWrapper {
    @Command("!")
    int whatever(@Extra("extra") String whatever1, @Extra("extra") String whatever2);
  }
  
  @Test
  public void testFailOnCreatingMultipleExtraWithSameNameOnOptionMethod() {
    List<Issue> issues = getIssues(MultipleExtraWithSameNameOnCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Multiple parameters"));
  }

  @Executable("!")
  public interface ParameterConversionFailureMethod extends ICommandLineWrapper {
    @Command("!")
    int whatever(@Converter(FilesWithSpaceSeparatorConverter.class) Properties properties);
  }

  @Test
  public void testFailOnCreatingParameterConversionFailureMethod() {
    List<Issue> issues = getIssues(ParameterConversionFailureMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Properties"));
  }

  @Executable("!")
  public interface ReturnValueConversionFailureCommandMethod extends ICommandLineWrapper {
    @Command(value = "!", converter = VersionResultConverter.class)
    Properties whatever();
  }

  @Test
  public void testFailOnCreatingReturnValueConversionFailureCommandMethod() {
    List<Issue> issues = getIssues(ReturnValueConversionFailureCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Properties"));
  }

  @Executable("!")
  public interface ReturnValueLaterConversionFailureCommandMethod extends ICommandLineWrapper {
    @Command(value = "!", outType = Properties.class)
    ReturnValueLaterConversionFailureCommandMethod whatever();
  }

  @Test
  public void testFailOnCreatingReturnValueLaterConversionFailureCommandMethod() {
    List<Issue> issues = getIssues(ReturnValueLaterConversionFailureCommandMethod.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "Properties"));
  }

  @Executable("!")
  public interface IgnoredMethodWithCommand extends ICommandLineWrapper {
    @Command(value = "!", outType = Properties.class)
    default int whatever() {
      return 42;
    }
  }

  @Test
  public void testFailOnCreatingIgnoredMethodWithCommand() {
    List<Issue> issues = getIssues(IgnoredMethodWithCommand.class);
    assertOneIssueMatches(issues,
        annotatedElementAndDescriptionContains("whatever", "default"));
  }

  @Test
  public void testCheckDiagnostic() {
    CommandLineWrapperChecker checker = new CommandLineWrapperChecker();
    Diagnostic diagnostic = checker.validateInterface(IgnoredMethodWithCommand.class);
    assertThrows(CommandLineException.class, () -> diagnostic.check());
  }

  private <W extends ICommandLineWrapper> List<Issue> getIssues(Class<W> clazz) {
    CommandLineWrapperChecker checker = new CommandLineWrapperChecker();
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