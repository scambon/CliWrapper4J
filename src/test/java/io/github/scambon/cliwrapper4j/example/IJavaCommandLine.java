/*
 * Copyright 2018-2019 Sylvain Cambon
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
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.ReturnCode;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.converters.FilesWithPathSeparatorParameterConverter;

import java.io.File;
import java.util.Collection;

@Executable("java")
public interface IJavaCommandLine extends IExecutable {

  @Switch("--help")
  @ExecuteNow
  int help();

  @Switch("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorParameterConverter.class) File... classpath);

  @Switch("-classpath")
  IJavaCommandLine classpath(
      @Converter(FilesWithPathSeparatorParameterConverter.class) Collection<File> classpath);

  @Switch("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemProperty(String property, Object value);

  @Switch("-D")
  @Aggregator("")
  @Flattener("=")
  IJavaCommandLine systemPropertyAsStringLength(String property,
      @Converter(StringLengthConverter.class) Object value);

  @Switch("")
  @ExecuteNow
  Result main(String mainQualifiedName);

  @Switch("-version")
  @ExecuteNow
  @Converter(VersionResultConverter.class)
  Version version();

  @Switch("-version")
  @ExecuteNow
  @ReturnCode({})
  @Converter(VersionResultConverter.class)
  Version versionWithoutReturnCodeCheck();

  @Switch("-version")
  @ExecuteNow
  @ReturnCode(1)
  @Converter(VersionResultConverter.class)
  Version versionWithCustomReturnCodeCheck();
  
  @Switch("-version")
  @ExecuteNow
  VirtualMachineType getVirtualMachineType();

  default int getMajorVersion() {
    return version().getMajorVersion();
  }
}