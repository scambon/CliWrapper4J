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

package io.github.scambon.cliwrapper4j;

/**
 * An exception that denotes that something went wrong in the CLiWrapper4J library.
 */
public final class CommandLineException extends RuntimeException {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new command line exception.
   *
   * @param message
   *          the message
   * @param cause
   *          the cause
   */
  public CommandLineException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new command line exception.
   *
   * @param message
   *          the message
   */
  public CommandLineException(String message) {
    super(message);
  }

  /**
   * Instantiates a new command line exception.
   *
   * @param cause
   *          the cause
   */
  public CommandLineException(Throwable cause) {
    super(cause);
  }
}