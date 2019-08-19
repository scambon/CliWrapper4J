/*
 * Copyright 2019 Sylvain Cambon
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

import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ExecuteNow;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.preprocessors.EnvironmentVariablesPreProcessor;

@Executable(value = {"foo", "${arg_executable_alone}",
    "before-${arg_executable_middle}-after"}, preProcessors = EnvironmentVariablesPreProcessor.class)
public interface IEnvironmentVariablesCommandLine extends IExecutable {

  @Switch("${arg_switch} - ${arg_unprovided}")
  @ExecuteNow
  void executeWithEnvironmentVariables();

  @Switch("${NOT_OVERWRITTEN} - ${OVERWRITTEN}")
  @ExecuteNow
  void executeWithSystemAndEnvironmentVariables();
}