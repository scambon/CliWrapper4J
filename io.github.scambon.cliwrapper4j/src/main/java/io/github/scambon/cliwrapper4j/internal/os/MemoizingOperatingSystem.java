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

package io.github.scambon.cliwrapper4j.internal.os;

import java.nio.charset.Charset;


/**
 * A delegating implementation that stores the method results.
 */
public class MemoizingOperatingSystem implements IOperatingSystem {

  /** The delegate. */
  private final IOperatingSystem delegate;
  /** The encoding. */
  private Charset encoding;

  /**
   * Instantiates a new memoizing operating system.
   *
   * @param delegate
   *          the delegate
   */
  public MemoizingOperatingSystem(IOperatingSystem delegate) {
    this.delegate = delegate;
  }

  @Override
  public Charset getConsoleEncoding() {
    if (encoding == null) {
      encoding = delegate.getConsoleEncoding();
    }
    return encoding;
  }

  @Override
  public boolean isCase(String osName) {
    return delegate.isCase(osName);
  }
}