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

import java.lang.reflect.AnnotatedElement;

/**
 * A class that describes an issue.
 */
public final class Issue {

  /** The element having a problem. */
  private final AnnotatedElement element;
  /** The problem description. */
  private final String description;

  /**
   * Instantiates a new issue.
   *
   * @param element
   *          the element
   * @param description
   *          the description
   */
  public Issue(AnnotatedElement element, String description) {
    this.element = element;
    this.description = description;
  }

  /**
   * Gets the element having a problem.
   *
   * @return the element having a problem
   */
  public AnnotatedElement getElement() {
    return element;
  }

  /**
   * Gets the problem description.
   *
   * @return the problem description
   */
  public String getDescription() {
    return description;
  }

  @Override
  public String toString() {
    return element + "|" + description;
  }
}