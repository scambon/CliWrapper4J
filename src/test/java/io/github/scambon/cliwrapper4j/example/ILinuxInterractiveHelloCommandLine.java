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
import io.github.scambon.cliwrapper4j.Executor;
import io.github.scambon.cliwrapper4j.Extra;
import io.github.scambon.cliwrapper4j.IExecutable;
import io.github.scambon.cliwrapper4j.Result;
import io.github.scambon.cliwrapper4j.Switch;
import io.github.scambon.cliwrapper4j.preprocessors.PrependLinuxBinBashPreProcessor;

@Executable(value = "hello.sh", preProcessors = PrependLinuxBinBashPreProcessor.class)
public interface ILinuxInterractiveHelloCommandLine extends IExecutable {

  @Switch("")
  @ExecuteNow
  @Executor(InterractiveHelloProcessExecutor.class)
  Result hello(int waitingTime, @Extra("name") String name);
}