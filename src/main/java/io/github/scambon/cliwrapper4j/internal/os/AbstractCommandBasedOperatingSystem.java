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

import static java.util.Collections.emptyMap;

import io.github.scambon.cliwrapper4j.CommandLineException;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.environment.DefaultExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;

/**
 * The operating system helper that uses commands to obtain informations.
 */
public abstract class AbstractCommandBasedOperatingSystem implements IOperatingSystem {

  /** The operating system name marker. */
  private String operatingSystemNameMarker;
  /** The command. */
  private final List<String> command;
  /** The system 2 charset properties file URL. */
  private final URL system2CharsetPropertiesFileUrl;

  /**
   * Instantiates a new abstract command based operating system.
   *
   * @param operatingSystemNameMarker
   *          the operating system name marker
   * @param command
   *          the command
   * @param system2CharsetPropertiesFileUrl
   *          the system 2 charset properties file url
   */
  protected AbstractCommandBasedOperatingSystem(
      String operatingSystemNameMarker,
      List<String> command, URL system2CharsetPropertiesFileUrl) {
    this.operatingSystemNameMarker = operatingSystemNameMarker;
    this.command = command;
    this.system2CharsetPropertiesFileUrl = system2CharsetPropertiesFileUrl;
  }
  
  @Override
  public boolean isCase(String osName) {
    return osName.contains(operatingSystemNameMarker);
  }

  @Override
  public final Charset getConsoleEncoding() {
    return getConsoleEncoding(new ProcessExecutor());
  }

  /**
   * Gets the console encoding.
   *
   * @param executor
   *          the executor
   * @return the console encoding
   */
  protected final Charset getConsoleEncoding(ICommandLineExecutor executor) {
    DefaultExecutionEnvironment executionEnvironment = new DefaultExecutionEnvironment();
    executionEnvironment.setEncoding(StandardCharsets.US_ASCII);
    Result commandResult = executionEnvironment.run(executor, command, emptyMap());
    int returnCode = commandResult.getReturnCode();
    if (returnCode != 0) {
      throw new CommandLineException(
          "Command '" + command + "' failed with return code '" + returnCode
              + "' instead of expected '0'");
    }
    String systemName = extractSystemName(commandResult);
    String charsetName = systemToCharsetName(systemName);
    Charset consoleEncoding = Charset.forName(charsetName);
    return consoleEncoding;
  }

  /**
   * Extracts the system name from the command output.
   *
   * @param commandResult
   *          the command result
   * @return the system name
   */
  protected abstract String extractSystemName(Result commandResult);

  /**
   * Converts the system name to a Java charset name.
   *
   * @param systemName
   *          the system name
   * @return the Java charset name
   */
  private String systemToCharsetName(String systemName) {
    try (InputStream systemName2JavaInputStream = system2CharsetPropertiesFileUrl.openStream()) {
      Properties systemNameJava = new Properties();
      systemNameJava.load(systemName2JavaInputStream);
      String javaEncodingName = systemNameJava.getProperty(systemName);
      if (javaEncodingName == null) {
        throw new CommandLineException(
            "System name '" + systemName + "' has no mapping defined to Java encodings");
      }
      return javaEncodingName;
    } catch (IOException exception) {
      throw new CommandLineException(exception);
    }
  }
}