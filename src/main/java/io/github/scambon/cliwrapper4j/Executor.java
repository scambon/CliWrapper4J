package io.github.scambon.cliwrapper4j;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import io.github.scambon.cliwrapper4j.executors.AbstractInteractiveProcessExecutor;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * An annotation for @{@link Switch} methods that defines the executor to use to run the command
 * line. By default, a {@link ProcessExecutor} is used, which is suitable for non-interactive, short
 * running command lines. Interactive command lines can be executed using subclasses of
 * {@link AbstractInteractiveProcessExecutor} or even custom implementations of {@link IExecutor}.
 * 
 * @see Switch
 * @see IExecutor
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface Executor {
  /**
   * The executor used to run the command.
   *
   * @return the command line executor, defaults to {@link ProcessExecutor}
   */
  Class<? extends IExecutor> value() default ProcessExecutor.class;
}
