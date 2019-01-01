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
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.InteractorRunnable.IInteractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * A base executor that helps implementing interacting with an executable.
 */
public abstract class AbstractInteractiveProcessExecutor implements IExecutor {

  @Override
  @SuppressWarnings("squid:S4721")
  public final Result execute(
      List<String> elements, IExecutionEnvironment environment,
      Map<String, Object> extraParameterName2ValueMap) {
    ProcessBuilder processBuilder = new ProcessBuilder(elements);
    environment.configure(processBuilder);
    try {
      Process process = processBuilder.start();
      OutputStream out = process.getOutputStream();
      Charset encoding = environment.getEncoding();
      InputStream in = process.getInputStream();
      Thread standardStreamGobblerThread = createInteractorThread(
          "Standard", in, out, encoding, this::onStandard, extraParameterName2ValueMap);
      standardStreamGobblerThread.start();
      InputStream error = process.getErrorStream();
      Thread errorStreamGobblerThread = createInteractorThread(
          "Error", error, out, encoding, this::onError, extraParameterName2ValueMap);
      errorStreamGobblerThread.start();
      int returnCode = process.waitFor();
      return getResult(returnCode, extraParameterName2ValueMap);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      throw new CommandLineException(interruptedException);
    } catch (IOException ioException) {
      throw new CommandLineException(ioException);
    }
  }

  /**
   * Creates the thread to interact with the executable.
   *
   * @param name
   *          the thread name (or at last a part of it)
   * @param in
   *          the in
   * @param out
   *          the out
   * @param encoding
   *          the encoding
   * @param interactor
   *          the interactor
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   * @return the thread to be started
   */
  protected Thread createInteractorThread(
      String name, InputStream in, OutputStream out, Charset encoding,
      IInteractor interactor, Map<String, Object> extraParameterName2ValueMap) {
    InteractorRunnable interactorRunnable = new InteractorRunnable(
        in, out, encoding, interactor, extraParameterName2ValueMap);
    return new Thread(interactorRunnable, name + " interactor");
  }

  /**
   * Reacts to some content received on the command line standard output.
   *
   * @param outputChunk
   *          the output chunk (not necessarily a line or delimited by line endings)
   * @param writer
   *          the writer to send some input to the command line, if needed
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   */
  protected abstract void onStandard(String outputChunk, PrintWriter writer,
      Map<String, Object> extraParameterName2ValueMap);

  /**
   * Reacts to some content received on the command line error output.
   *
   * @param errorChunk
   *          the error chunk
   * @param outputStream
   *          the output stream
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   */
  protected void onError(String errorChunk, PrintWriter outputStream,
      Map<String, Object> extraParameterName2ValueMap) {
    // Nothing
  }

  /**
   * Gets the result from running the process. The way the result if filled depends on the
   * implementation.
   *
   * @param returnCode
   *          the return code
   * @param extraParameterName2ValueMap
   *          the {@link Extra} parameter name 2 value map
   * @return the result
   */
  protected abstract Result getResult(
      int returnCode, Map<String, Object> extraParameterName2ValueMap);
}