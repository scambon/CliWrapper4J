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

package io.github.scambon.cliwrapper4j;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

/**
 * A class that represents the raw output of calling a command line. Filling this result depends on
 * what the originating {@link ICommandLineExecutor} deems appropriate.
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
}