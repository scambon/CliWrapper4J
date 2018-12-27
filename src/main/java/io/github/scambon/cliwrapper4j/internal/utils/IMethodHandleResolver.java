package io.github.scambon.cliwrapper4j.internal.utils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * An interface that resolves a method handler.
 */
interface IMethodHandleResolver {

  /**
   * Resolves a method into a method handle.
   *
   * @param clazz
   *          the class
   * @param method
   *          the method
   * @return the method handle
   * @throws ReflectiveOperationException
   *           if a reflective operation failed
   */
  MethodHandle resolveMethod(Class<?> clazz, Method method) throws ReflectiveOperationException;
}