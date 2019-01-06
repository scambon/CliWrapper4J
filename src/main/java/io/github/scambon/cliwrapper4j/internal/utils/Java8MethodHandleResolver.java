package io.github.scambon.cliwrapper4j.internal.utils;

import io.github.scambon.cliwrapper4j.CommandLineException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * A method handle resolver that works on Java 8.
 */
final class Java8MethodHandleResolver implements IMethodHandleResolver {

  /** The lookup constructor. */
  private Constructor<Lookup> lookupConstructor;

  /**
   * Instantiates a new java 8 method handle resolver.
   */
  @SuppressWarnings("squid:S3011")
  public Java8MethodHandleResolver() {
    try {
      lookupConstructor = Lookup.class.getDeclaredConstructor(Class.class);
      lookupConstructor.setAccessible(true);
    } catch (ReflectiveOperationException exception) {
      throw new CommandLineException(exception);
    }
  }

  @Override
  public MethodHandle resolveMethod(Class<?> clazz, Method method)
      throws ReflectiveOperationException {
    return lookupConstructor.newInstance(clazz)
        .in(clazz)
        .unreflectSpecial(method, clazz);
  }
}