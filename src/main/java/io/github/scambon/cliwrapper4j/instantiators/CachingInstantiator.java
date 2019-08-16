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

import java.util.HashMap;
import java.util.Map;

/**
 * An instantiator that caches the results created by a delegate.
 */
public class CachingInstantiator implements IInstantiator {
  
  /** The delegate. */
  private final IInstantiator delegate;
  /** The class 2 instance map. */
  private final Map<Class<?>, Object> class2InstanceMap = new HashMap<>();

  /**
   * Instantiates a new caching instantiator.
   *
   * @param delegate
   *          the delegate
   */
  public CachingInstantiator(IInstantiator delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean canCreate(Class<?> clazz) {
    return class2InstanceMap.containsKey(clazz) || delegate.canCreate(clazz);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T create(Class<T> clazz) {
    return (T) class2InstanceMap.computeIfAbsent(clazz, delegate::create);
  }
}