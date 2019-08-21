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

package io.github.scambon.cliwrapper4j.converters;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A result converter that extracts the relevant text from the output using a regular expression and
 * feeds it into a delegate converter.
 *
 * @param <O>
 *          the output type
 */
public abstract class AbstractRegexResultConverter<O>
    extends AbstractDelegatingOutputExtractingResultConverter<O> {

  /** The pattern. */
  private final Pattern pattern;

  /**
   * Instantiates a new abstract regex result converter.
   *
   * @param delegate
   *          the delegate
   * @param regex
   *          the regex
   */
  @SuppressWarnings("squid:S4784")
  protected AbstractRegexResultConverter(IConverter<String, O> delegate, String regex) {
    super(delegate);
    this.pattern = Pattern.compile(regex);
  }

  @Override
  protected final String extractRelevantText(String output) {
    Matcher matcher = pattern.matcher(output);
    if (matcher.find()) {
      return matcher.group(1);
    }
    throw new CommandLineException(
        "Pattern '" + pattern + "' did not match output '" + output + "'");
  }
}