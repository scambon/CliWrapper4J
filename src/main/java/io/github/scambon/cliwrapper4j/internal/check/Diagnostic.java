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

package io.github.scambon.cliwrapper4j.internal.check;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A class that helps accumulating issues.
 */
public final class Diagnostic {

  /** The class being checked. */
  private final Class<?> clazz;
  /** The issues. */
  private final List<Issue> issues = new ArrayList<>();

  /**
   * Instantiates a new diagnostic.
   *
   * @param clazz
   *          the class being checked
   */
  public Diagnostic(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Adds an issue.
   *
   * @param element
   *          the element
   * @param description
   *          the description
   */
  public void addIssue(AnnotatedElement element, String description) {
    Issue issue = new Issue(element, description);
    issues.add(issue);
  }

  /**
   * Gets the issues.
   *
   * @return the issues
   */
  public List<Issue> getIssues() {
    return issues;
  }

  /**
   * Checks that no issue was found, or throws an exception.
   */
  public void check() {
    if (!issues.isEmpty()) {
      String message = "Some issues were found in interface '" + clazz + "'";
      String issuesString = issues.stream()
          .map(Object::toString)
          .map(string -> "\t" + string)
          .collect(Collectors.joining("\n"));
      message += "\n" + issuesString + "\n";
      throw new CommandLineException(message);
    }
  }
}