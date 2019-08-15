/* Copyright 2018-2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j.internal.utils;

import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Function;
import java.util.function.Supplier;

/** A helper method that facilitates some operations on annotations. */
public final class AnnotationUtils {

  /**
   * Instantiates a new annotation utils.
   */
  private AnnotationUtils() {
    // NOP
  }

  /**
   * Gets a value from the annotation if present, or the default value.
   *
   * @param <A>
   *          the annotation type
   * @param <V>
   *          the value type
   * @param annotated
   *          the annotated element
   * @param annotationClass
   *          the annotation class
   * @param getter
   *          the annotation getter
   * @param defaultValueSupplier
   *          the default value supplier
   * @return the resulting value
   */
  public static <A extends Annotation, V> V getOrDefault(AnnotatedElement annotated,
      Class<A> annotationClass, Function<A, ? extends V> getter,
      Supplier<? extends V> defaultValueSupplier) {
    A annotation = annotated.getAnnotation(annotationClass);
    V value;
    if (annotation != null) {
      value = getter.apply(annotation);
    } else {
      value = defaultValueSupplier.get();
    }
    return value;
  }

  /**
   * Gets an instance from the annotation-defined class if present, or the default value.
   *
   * @param <A>
   *          the annotation type
   * @param <V>
   *          the value type
   * @param annotated
   *          the annotated element
   * @param annotationClass
   *          the annotation class
   * @param getter
   *          the annotation getter
   * @param defaultValueSupplier
   *          the default value supplier
   * @param instantiator
   *          the instantiator
   * @return the resulting value
   */
  public static <A extends Annotation, V> V getOrDefaultClass(AnnotatedElement annotated,
      Class<A> annotationClass, Function<A, Class<? extends V>> getter, IInstantiator instantiator,
      Supplier<? extends V> defaultValueSupplier) {
    Function<A, V> instanceGetter = annotation -> createInstance(annotation, getter, instantiator);
    return getOrDefault(annotated, annotationClass, instanceGetter, defaultValueSupplier);
  }

  /**
   * Creates an instance of the class obtained by the getter on the annotation.
   *
   * @param <A>
   *          the annotation type
   * @param <V>
   *          the value type
   * @param annotation
   *          the annotation
   * @param getter
   *          the getter
   * @param instantiator
   *          the instantiator
   * @return the instance
   */
  public static <A extends Annotation, V> V createInstance(A annotation,
      Function<A, Class<? extends V>> getter, IInstantiator instantiator) {
    Class<? extends V> classValue = getter.apply(annotation);
    return instantiator.create(classValue);
  }
}