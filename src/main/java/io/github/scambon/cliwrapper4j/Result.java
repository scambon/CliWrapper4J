/*
 * Copyright 2018-2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j;

import io.github.scambon.cliwrapper4j.executors.IExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that represents the raw output of calling a command line. Filling this result depends on
 * what the originating {@link IExecutor} deems appropriate.
 * 
 * @see IExecutor
 */
public final class Result {

  /** The output. */
  private final String output;
  /** The error. */
  private final String error;
  /** The return code. */
  private final int returnCode;

  /**
   * Instantiates a new result.
   *
   * @param output
   *          the output
   * @param error
   *          the error
   * @param returnCode
   *          the return code
   */
  public Result(String output, String error, int returnCode) {
    this.output = output;
    this.error = error;
    this.returnCode = returnCode;
  }

  /**
   * Gets the output stream contents.
   *
   * @return the output stream contents
   */
  public String getOutput() {
    return output;
  }

  /**
   * Gets the error stream contents.
   *
   * @return the error stream contents
   */
  public String getError() {
    return error;
  }

  /**
   * Gets the return code.
   *
   * @return the return code
   */
  public int getReturnCode() {
    return returnCode;
  }

  /**
   * Wraps this result as an array specified by the given types.
   * The extraction occurs according to the following table:
   * <table border="1">
   * <caption>Parameters</caption>
   * <tr>
   * <th>Parameter type</th>
   * <th>Parameter value</th>
   * </tr>
   * <tr>
   * <td><code>Result</code></td>
   * <td>this <code>result</code></td>
   * </tr>
   * <tr>
   * <td><code>String</code></td>
   * <td>
   * <ol>
   * <li><code>output</code> (first use only)</li>
   * <li><code>error</code> (all later use)</li>
   * </ol>
   * </td>
   * </tr>
   * <tr>
   * <td><code>Integer</code> or <code>int</code></td>
   * <td><code>return code</code></td>
   * </tr>
   * <tr>
   * <td><code>Void</code> or <code>void</code></td>
   * <td><code>null</code></td>
   * </tr>
   * </table>
   * 
   * @param types
   *          the array element types
   * @return the value array
   */
  public Object[] toArray(Class<?>[] types) {
    List<Object> values = new ArrayList<>();
    boolean outputAdded = false;
    for (Class<?> type : types) {
      if (Result.class.equals(type)) {
        values.add(this);
      } else if (String.class.equals(type)) {
        if (!outputAdded) {
          values.add(output);
          outputAdded = true;
        } else {
          values.add(error);
        }
      } else if (Integer.class.equals(type) || int.class.equals(type)) {
        values.add(returnCode);
      } else if (Void.class.equals(type) || void.class.equals(type)) {
        values.add(null);
      } else {
        throw new CommandLineException(
            "Cannot convert result '" + this + "' or its fields to type '" + type + "'.");
      }
    }
    return values.toArray();
  }

  @Override
  public String toString() {
    return "Result [output='" + shorten(output) + "', error='" + shorten(error) + "', returnCode='"
        + returnCode + "']";
  }

  /**
   * Shortens a String for display if too long.
   *
   * @param string
   *          the string
   * @return the shortened string
   */
  private static String shorten(String string) {
    int length = string.length();
    if (length >= 100) {
      return string.substring(0, 100) + "...";
    } else {
      return string;
    }
  }
}