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

package io.github.scambon.cliwrapper4j.example;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.converters.FilesWithPathSeparatorConverter;

import java.io.File;
import java.util.Collection;

@Executable("java")
public interface IJavaCommandLine extends ICommandLineWrapper {

  @Command("--help")
  int help();

  @Option("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorConverter.class) File... classpathElements);

  @Option("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorConverter.class) Collection<File> classpathElements);

  @Option("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemProperty(String property, Object value);

  @Option("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemPropertyAsStringLength(String property,
      @Converter(StringLengthConverter.class) Object value);

  @Command("")
  Result main(String mainQualifiedName);

  @Command(value = "-version", converter = VersionResultConverter.class)
  Version version();  

  @Command(value = "-version", converter = VersionResultConverter.class, expectedReturnCodes = {})
  Version versionWithoutReturnCodeCheck();

  @Command(value = "-version", converter = VersionResultConverter.class, expectedReturnCodes = 1)
  Version versionWithCustomReturnCodeCheck();

  default int getMajorVersion() {
    return version().getMajorVersion();
  }
}