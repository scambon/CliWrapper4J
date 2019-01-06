package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>
 * An annotation that checks that an {@link ExecuteNow} or {@link ExecuteLater} execution return
 * codes are as expected.
 * </p>
 * <p>
 * By default, the return code is checked to be <code>0</code>. This is also the case if this
 * annotation is not explicitly used. You can customize the expected returns code with
 * {@link #value()}. You can also disable the checking by setting {@link #value()} to
 * <code>{}</code>.
 * </p>
 *
 * @see ExecuteNow
 * @see ExecuteLater
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ReturnCode {
  /**
   * The expected return codes to check against.
   *
   * @return the expected return codes to check against, defaults to <code>{0}</code>, use
   *         <code>{}</code> to disable checking
   */
  int[] value() default 0;
}