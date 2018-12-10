package io.github.scambon.cliwrapper4j.internal.utils;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * A method handle resolver that works on Java 9+.
 */
final class Java9MethodHandleResolver implements IMethodHandleResolver {
  @Override
  public MethodHandle resolveMethod(Class<?> clazz, Method method)
      throws ReflectiveOperationException {
    Class<?> returnType = method.getReturnType();
    Class<?>[] parameterTypes = method.getParameterTypes();
    MethodType methodType = MethodType.methodType(returnType, parameterTypes);
    String methodName = method.getName();
    return privateLookupIn(clazz, MethodHandles.lookup())
        .findSpecial(clazz,
            methodName, methodType, clazz);
  }

  /**
   * Calls Java 9 {@link MethodHandles}#privateLookupIn(Class, Lookup) but compiles on Java 8.
   *
   * @param targetClass
   *          the target class
   * @param lookup
   *          the lookup
   * @return the private lookup
   * @throws ReflectiveOperationException
   *           the reflective operation exception
   */
  public static Lookup privateLookupIn(Class<?> targetClass, Lookup lookup)
      throws ReflectiveOperationException {
    Method method = MethodHandles.class.getMethod("privateLookupIn", Class.class, Lookup.class);
    Lookup privateLookupIn = (Lookup) method.invoke(null, targetClass, lookup);
    return privateLookupIn;
  }
}