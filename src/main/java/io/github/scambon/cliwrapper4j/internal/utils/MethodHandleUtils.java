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

package io.github.scambon.cliwrapper4j.internal.utils;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

/**
 * A helper class that facilitates working with method handles.
 */
public final class MethodHandleUtils {

  /** The resolver. */
  private static final IMethodHandleResolver RESOLVER;

  static {
    String version = System.getProperty("java.version");
    String[] versionSegments = version.split("\\.");
    String relevantVersion = versionSegments[0];
    if (relevantVersion.equals("1")) {
      relevantVersion = versionSegments[1];
    }
    int javaVersion = Integer.parseInt(relevantVersion);
    if (javaVersion == 8) {
      RESOLVER = new Java8MethodHandleResolver();
    } else {
      RESOLVER = new Java9MethodHandleResolver();
    }
  }
  
  /**
   * Instantiates a new method handle utils.
   */
  private MethodHandleUtils() {
    // Nothing
  }



  /**
   * Gets the method handle.
   *
   * @param method
   *          the method
   * @return the method handle
   * @throws ReflectiveOperationException
   *           if a reflective operation failed
   */
  public static MethodHandle getMethodHandle(Method method) throws ReflectiveOperationException {
    Class<?> clazz = method.getDeclaringClass();
    return RESOLVER.resolveMethod(clazz, method);
  }
}