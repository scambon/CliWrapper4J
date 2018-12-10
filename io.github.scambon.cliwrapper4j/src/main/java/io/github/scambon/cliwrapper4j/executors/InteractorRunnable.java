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

package io.github.scambon.cliwrapper4j.executors;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A runnable that reads a command line output and possibly
 * responds to it on the command line input.
 */
public final class InteractorRunnable implements Runnable {
  
  /** The Constant DEFAULT_CHARACTER_BUFFER_SIZE. */
  private static final int DEFAULT_CHARACTER_BUFFER_SIZE = 256;
  
  /** The character buffer size. */
  private final int characterBufferSize;
  /** The in reader. */
  private final BufferedReader inReader;
  /** The interactor. */
  private final Consumer<String> interactor;

  /**
   * Instantiates a new interactor runnable.
   *
   * @param in
   *          the in
   * @param out
   *          the out
   * @param encoding
   *          the encoding
   * @param interactor
   *          the interactor
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   */
  public InteractorRunnable(InputStream in, OutputStream out, Charset encoding,
      IInteractor interactor, Map<String, Object> extraParameterName2ValueMap) {
    this(in, out, encoding, interactor, extraParameterName2ValueMap, DEFAULT_CHARACTER_BUFFER_SIZE);
  }
  
  /**
   * Instantiates a new interactor runnable.
   *
   * @param in
   *          the in
   * @param out
   *          the out
   * @param encoding
   *          the encoding
   * @param interactor
   *          the interactor
   * @param extraParameterName2ValueMap
   *          the extra parameter name 2 value map
   * @param characterBufferSize
   *          the character buffer size
   */
  public InteractorRunnable(InputStream in, OutputStream out, Charset encoding,
      IInteractor interactor, Map<String, Object> extraParameterName2ValueMap,
      int characterBufferSize) {
    this.characterBufferSize = characterBufferSize;
    this.inReader = new BufferedReader(new InputStreamReader(in, encoding));
    PrintWriter outWriter = new PrintWriter(
        new BufferedWriter(new OutputStreamWriter(out, encoding)),
        true);
    this.interactor =
        inString -> interactor.react(inString, outWriter, extraParameterName2ValueMap);
  }

  @Override
  public void run() {
    try {
      char[] characterBuffer = new char[characterBufferSize];
      int readCharacters = 0;
      while ((readCharacters = inReader.read(characterBuffer)) != -1) {
        String stringBuffer = new String(characterBuffer, 0, readCharacters);
        interactor.accept(stringBuffer);
      }
    } catch (IOException ioe) {
      throw new CommandLineException(ioe);
    }
  }
  
  /**
   * An interface that reacts to a chunk of output.
   */
  @FunctionalInterface
  public interface IInteractor {
    
    /**
     * Reacts.
     *
     * @param chunk
     *          the command line output chunk
     * @param writer
     *          the writer to send some input to the command line
     * @param extraParameterName2ValueMap
     *          the extra parameter name 2 value map
     */
    void react(String chunk, PrintWriter writer, Map<String, Object> extraParameterName2ValueMap);
  }
}