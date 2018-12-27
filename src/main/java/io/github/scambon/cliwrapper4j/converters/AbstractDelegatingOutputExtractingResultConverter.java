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

package io.github.scambon.cliwrapper4j.converters;

import io.github.scambon.cliwrapper4j.Result;

/**
 * A result converter that extracts some texts from the output and converts it through a delegate
 * converter.
 *
 * @param <O>
 *          the output type
 */
public abstract class AbstractDelegatingOutputExtractingResultConverter<O>
    implements IConverter<Result, O> {

  /** The delegate converter. */
  private final IConverter<String, O> delegate;

  /**
   * Instantiates a new abstract delegating output extracting result converter.
   *
   * @param delegate
   *          the delegate
   */
  protected AbstractDelegatingOutputExtractingResultConverter(IConverter<String, O> delegate) {
    this.delegate = delegate;
  }

  @Override
  public final boolean canConvert(Class<Result> inClass, Class<O> outClass) {
    return Result.class.equals(inClass) && delegate.canConvert(String.class, outClass);
  }

  @Override
  public final O convert(Result in, Class<O> outClass) {
    String output = selectOutput(in);
    String relevantString = extractRelevantOutput(output);
    O convertedValue = delegate.convert(relevantString, outClass);
    return convertedValue;
  }
  
  /**
   * Selects the output.
   *
   * @param result
   *          the result
   * @return the output
   */
  protected String selectOutput(Result result) {
    return result.getOutput();
  }

  /**
   * Extracts the relevant part of the output, to be fed into the delegate converter.s
   *
   * @param output
   *          the output
   * @return the relevant part of the output
   */
  protected abstract String extractRelevantOutput(String output);
}