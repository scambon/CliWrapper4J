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

package io.github.scambon.cliwrapper4j.internal.converters;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;

import io.github.scambon.cliwrapper4j.converters.FactoryMethodResultConverter;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A utility class that is used by {@link FactoryMethodResultConverter} and makes it testable, yet
 * in the internal package.
 */
public final class FactoryMethodResultConverterUtils {

  /** Methods priority, from low to high. */
  private static final List<String> METHODS_PRIORITY = asList(
      "valueOf",
      "make",
      "build",
      "create",
      "of",
      "from",
      "parse");

  /** The method order comparator. */
  private static final Comparator<Method> METHOD_COMPARATOR = Comparator
      // Use minus to have unknown elements last
      .comparing((Method method) -> -METHODS_PRIORITY.indexOf(method.getName()))
      .thenComparing(Object::toString);

  /**
   * The constructor.
   */
  private FactoryMethodResultConverterUtils() {
    // Utility class
  }

  /**
   * Tries to find a factory method.
   * 
   * @param outClass
   *          the output type
   * @param methodParameterTypes
   *          the method parameters type
   * @return the factory method, if present
   */
  public static Optional<Method> findFactoryMethod(Class<?> outClass,
      Class<?>[] methodParameterTypes) {
    return findFactoryMethods(outClass, methodParameterTypes)
        .findFirst();
  }

  /**
   * Tries to find all factory methods.
   * 
   * @param outClass
   *          the output type
   * @param methodParameterTypes
   *          the method parameters type
   * @return the factory methods
   */
  public static Stream<Method> findFactoryMethods(Class<?> outClass,
      Class<?>[] methodParameterTypes) {
    Method[] methods = outClass.getMethods();
    return stream(methods)
        .filter(method -> Modifier.isStatic(method.getModifiers()))
        .filter(method -> outClass.isAssignableFrom(method.getReturnType()))
        .filter(method -> Arrays.equals(methodParameterTypes, method.getParameterTypes()))
        .sorted(FactoryMethodResultConverterUtils.METHOD_COMPARATOR);
  }
}