/* Copyright 2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.instantiators;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.lang.reflect.Constructor;

/**
 * An instantiator that creates instances using public, no-arg constructors.
 */
public class ReflectiveInstantiator implements IInstantiator {

  @Override
  public boolean canCreate(Class<?> clazz) {
    try {
      clazz.getConstructor();
      // If no exception was thrown, then it is OK
      return true;
    } catch (ReflectiveOperationException roe) {
      return false;
    }
  }

  @Override
  public <T> T create(Class<T> clazz) {
    try {
      Constructor<T> constructor = clazz.getConstructor();
      return constructor.newInstance();
    } catch (ReflectiveOperationException roe) {
      throw new CommandLineException(roe);
    }
  }
}