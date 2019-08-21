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

import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

import java.io.File;
import java.nio.file.Path;

/**
 * A parameter converter that takes an array or an {@link Iterable} of {@link File}s or
 * {@link Path}s and returns a string where they are joined with a space.
 */
public final class FilesWithSpaceSeparatorParameterConverter extends MultipleParameterConverter {

  /**
   * Instantiates a new files with space separator converter.
   */
  public FilesWithSpaceSeparatorParameterConverter() {
    super(Object.class,
        new ShortFileParameterConverter(),
        new JoiningOnDelimiterFlattener(),
        " ");
  }
}