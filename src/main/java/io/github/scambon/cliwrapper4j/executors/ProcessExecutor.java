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

package io.github.scambon.cliwrapper4j.executors;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * A command line executor that uses the {@link Process} class.
 */
public final class ProcessExecutor implements IExecutor {

  @Override
  @SuppressWarnings("squid:S4721")
  public Result execute(
      List<String> elements, IExecutionEnvironment environment,
      Map<String, Object> extraParameterName2ValueMap) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(elements);
      environment.configure(processBuilder);
      Process process = processBuilder.start();
      int returnCode = process.waitFor();
      Charset encoding = environment.getEncoding();
      String output = readInputStream(process.getInputStream(), encoding);
      String error = readInputStream(process.getErrorStream(), encoding);
      return new Result(output, error, returnCode);
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      throw new CommandLineException(interruptedException);
    } catch (IOException ioException) {
      throw new CommandLineException(ioException);
    }
  }

  /**
   * Reads the given input stream.
   *
   * @param inputStream
   *          the input stream
   * @param charset
   *          the charset
   * @return the input stream content
   * @throws IOException
   *           if an I/O exception has occurred
   */
  private String readInputStream(InputStream inputStream, Charset charset) throws IOException {
    byte[] bytes = readAllBytes(inputStream);
    return new String(bytes, charset);
  }

  /**
   * Reads all bytes from the given input stream.
   *
   * @param inputStream
   *          the is
   * @return the bytes
   * @throws IOException
   *           if an I/O exception has occurred
   */
  private final byte[] readAllBytes(InputStream inputStream) throws IOException {
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    int readBytes;
    byte[] data = new byte[8 * 1024];
    while ((readBytes = inputStream.read(data)) != -1) {
      buffer.write(data, 0, readBytes);
    }
    return buffer.toByteArray();
  }
}