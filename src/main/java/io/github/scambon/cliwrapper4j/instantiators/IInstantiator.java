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

/**
 * An interface that creates class instances.
 */
public interface IInstantiator {

  /**
   * Tests whether this instantiator can create an instance of the given class.
   *
   * @param clazz
   *          the class to instantiate
   * @return whether this instantiator can create an instance of the given class
   * @see #create(Class)
   */
  boolean canCreate(Class<?> clazz);

  /**
   * Creates an instance of the given class. {@link #canCreate(Class)} is always called before.
   *
   * @param <T>
   *          the instance type
   * @param clazz
   *          the class to instantiate
   * @return the class instance
   * @see #canCreate(Class)
   */
  <T> T create(Class<T> clazz);
}