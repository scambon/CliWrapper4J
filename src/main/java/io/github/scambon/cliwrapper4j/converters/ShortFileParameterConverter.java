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

import java.io.File;
import java.nio.file.Path;

/**
 * A converter that handles {@link File}s and {@link Path}s by getting the path as is, without
 * resolving or relativization.
 */
public final class ShortFileParameterConverter extends CompositeConverter<Object, String> {

  /**
   * Instantiates a new short file parameter converter.
   */
  public ShortFileParameterConverter() {
    super(
        new LambdaConverter<>(File.class, String.class, File::getPath),
        new LambdaConverter<>(Path.class, String.class, path -> path.toFile()
            .getPath()),
        new StringQuotedIfNeededConverter());
  }
}