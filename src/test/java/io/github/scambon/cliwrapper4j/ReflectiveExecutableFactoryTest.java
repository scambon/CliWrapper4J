/*
 * Copyright 2018-2019 Sylvain Cambon
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

package io.github.scambon.cliwrapper4j;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.scambon.cliwrapper4j.environment.DefaultExecutionEnvironment;
import io.github.scambon.cliwrapper4j.environment.IExecutionEnvironment;
import io.github.scambon.cliwrapper4j.example.IGitCommandLine;
import io.github.scambon.cliwrapper4j.example.IJavaCommandLine;
import io.github.scambon.cliwrapper4j.example.ILinuxInterractiveHelloCommandLine;
import io.github.scambon.cliwrapper4j.example.ILinuxSystemVariableCommandLine;
import io.github.scambon.cliwrapper4j.example.IUnhandledMethodsCommandLine;
import io.github.scambon.cliwrapper4j.example.IWindowsInterractiveHelloCommandLine;
import io.github.scambon.cliwrapper4j.example.IWindowsSystemVariableCommandLine;
import io.github.scambon.cliwrapper4j.example.Version;
import io.github.scambon.cliwrapper4j.example.VersionResultConverter;
import io.github.scambon.cliwrapper4j.example.VirtualMachineType;
import io.github.scambon.cliwrapper4j.executors.IExecutor;
import io.github.scambon.cliwrapper4j.executors.MockExecutionEnvironment;
import io.github.scambon.cliwrapper4j.executors.MockExecutionHelper;
import io.github.scambon.cliwrapper4j.instantiators.IInstantiator;
import io.github.scambon.cliwrapper4j.instantiators.ReflectiveInstantiator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ReflectiveExecutableFactoryTest {

  private static IExecutableFactory<IJavaCommandLine> javaFactory;
  private static IExecutableFactory<IGitCommandLine> gitFactory;
  private static IExecutableFactory<IUnhandledMethodsCommandLine> unhandledFactory;
  private static IExecutableFactory<IWindowsInterractiveHelloCommandLine> windowsInteractiveFactory;
  private static IExecutableFactory<ILinuxInterractiveHelloCommandLine> linuxInteractiveFactory;
  private static IExecutableFactory<IWindowsSystemVariableCommandLine> windowsSystemVariableFactory;
  private static IExecutableFactory<ILinuxSystemVariableCommandLine> linuxSystemVariableFactory;

  @BeforeAll
  public static void setUp() {
    javaFactory = new ReflectiveExecutableFactory<>(IJavaCommandLine.class);
    gitFactory = new ReflectiveExecutableFactory<>(IGitCommandLine.class);
    unhandledFactory = new ReflectiveExecutableFactory<>(IUnhandledMethodsCommandLine.class);
    windowsInteractiveFactory = new ReflectiveExecutableFactory<>(
        IWindowsInterractiveHelloCommandLine.class);
    linuxInteractiveFactory = new ReflectiveExecutableFactory<>(
        ILinuxInterractiveHelloCommandLine.class);
    windowsSystemVariableFactory = new ReflectiveExecutableFactory<>(IWindowsSystemVariableCommandLine.class);
    linuxSystemVariableFactory = new ReflectiveExecutableFactory<>(ILinuxSystemVariableCommandLine.class);
  }

  @Test
  public void testWorkingCommandWith0ParametersAndConversion() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment(
        "java", "-version");
    IJavaCommandLine java = javaFactory.create(environment);
    Version version = java.version();
    assertNotNull(version);
    environment.checkElements("java", "-version");
  }

  @Test
  public void testMethodWithVoidReturnType() {
    IExecutionEnvironment environment = new DefaultExecutionEnvironment() {
      @Override
      public Result run(IExecutor executor, List<String> cliElements,
          Map<String, Object> extraParameterName2ValueMap) {
        return new Result("", "", 0);
      }
    };
    IGitCommandLine git = gitFactory.create(environment);
    git.configAdd("foo", "bar");
  }

  @Test
  public void testDefaultReflectiveConversion() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java",
        "-version");
    IJavaCommandLine java = javaFactory.create(environment);
    VirtualMachineType virtualMachineType = java.getVirtualMachineType();
    // The file used by the mock uses HotSpot
    assertEquals(VirtualMachineType.HOTSPOT, virtualMachineType);
  }

  @Test
  public void testCommandFailingConversion() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IJavaCommandLine java = javaFactory.create(environment);
    assertThrows(CommandLineException.class, () -> java.version());
  }

  @Test
  public void testFailedCommand() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment(
        "java", "-version", "failed");
    IJavaCommandLine java = javaFactory.create(environment);
    assertThrows(CommandLineException.class, () -> java.version());
    environment.checkElements("java", "-version");
  }

  @Test
  public void testFailedCommandWithDisabledChecking() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment(
        "java", "-version", "failed");
    IJavaCommandLine java = javaFactory.create(environment);
    java.versionWithoutReturnCodeCheck();
    environment.checkElements("java", "-version");
  }

  @Test
  public void testFailedCommandWithCustomExpectedRetunCode() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment(
        "java", "-version", "failed");
    IJavaCommandLine java = javaFactory.create(environment);
    java.versionWithCustomReturnCodeCheck();
    environment.checkElements("java", "-version");
  }

  @Test
  public void testOptionWithFlattenerAndPathConversionOnArray() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IJavaCommandLine java = javaFactory.create(environment);
    java.classpath(new File("llama.jar"), new File("chicken.jar"))
        .main("com.example.Whatever");
    environment.checkElements(
        "java",
        "-classpath llama.jar" + File.pathSeparatorChar + "chicken.jar",
        "com.example.Whatever");
  }

  @Test
  public void testOptionWithFlattenerAndPathConversionOnCollection() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IJavaCommandLine java = javaFactory.create(environment);
    java.classpath(asList(new File("llama.jar"), new File("chicken.jar")))
        .main("com.example.Whatever");
    environment.checkElements(
        "java",
        "-classpath llama.jar" + File.pathSeparatorChar + "chicken.jar",
        "com.example.Whatever");
  }

  @Test
  public void testOptionWithFlattenerAndCustomConversion() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IJavaCommandLine java = javaFactory.create(environment);
    java.systemPropertyAsStringLength("key", "value")
        .main("com.example.Whatever");
    environment.checkElements("java", "-Dkey=5", "com.example.Whatever");
  }

  @Test
  public void testDefaultMethod() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment(
        "java", "-version");
    IJavaCommandLine java = javaFactory.create(environment);
    int featureVersion = java.getMajorVersion();
    assertTrue(8 < featureVersion);
  }

  @Test
  public void testPrivateMethod() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IUnhandledMethodsCommandLine unhandled = unhandledFactory.create(environment);
    int value = unhandled.get42();
    assertEquals(42, value);
  }

  @Test
  public void testStaticMethod() {
    int value = IUnhandledMethodsCommandLine.get42Static();
    assertEquals(42, value);
  }

  @Test
  public void testUnhandledMethodThrowingException() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("java");
    IUnhandledMethodsCommandLine unhandled = unhandledFactory.create(environment);
    assertThrows(CommandLineException.class, () -> unhandled.throwException());
  }

  @Test
  public void testExecuteMethod() {
    MockExecutionEnvironment environment = MockExecutionHelper.createExecutionEnvironment("git");
    IGitCommandLine git = gitFactory.create(environment);
    int returnCode = git.commit()
        .message("Some message")
        .files(Paths.get("whatever.txt"))
        .execute();
    assertEquals(0, returnCode);
    environment.checkElements("git", "commit", "-m \"Some message\"", "whatever.txt");
  }

  @Test
  public void testExecutableWithActualProcessExecutor() {
    IJavaCommandLine java = javaFactory.create();
    Version version = java.version();
    assertNotNull(version);
  }

  @Test
  public void testInteractiveProcessExecutor() {
    Path path = Paths.get("src/test/resources/io/github/scambon/cliwrapper4j/examples");
    IExecutionEnvironment environment = new DefaultExecutionEnvironment(path);
    int expectedDurationInSecond = 2;
    Result result;
    long beforeNanos;
    boolean isWindows = isWindows();
    if (isWindows) {
      IWindowsInterractiveHelloCommandLine greeter = windowsInteractiveFactory.create(environment);
      beforeNanos = System.nanoTime();
      result = greeter.hello(expectedDurationInSecond, "Llama");
    } else {
      ILinuxInterractiveHelloCommandLine greeter = linuxInteractiveFactory.create(environment);
      beforeNanos = System.nanoTime();
      result = greeter.hello(expectedDurationInSecond, "Llama");
    }
    long afterNanos = System.nanoTime();
    String output = result.getOutput();
    assertThat(output, containsString("What is your name?"));
    String error = result.getError();
    assertThat(error, containsString("This is the error stream"));
    long deltaNanos = afterNanos - beforeNanos;
    assertTrue(expectedDurationInSecond * 1_000_000 <= deltaNanos);
  }

  @Test
  public void testSystemVariablePassing() {
    Path path = Paths.get("src/test/resources/io/github/scambon/cliwrapper4j/examples");
    IExecutionEnvironment environment = new DefaultExecutionEnvironment(path);
    environment.setEnvironmentVariable("SOME_VARIABLE", "LLAMA");
    String output;
    if(isWindows()) {
      IWindowsSystemVariableCommandLine cli = windowsSystemVariableFactory.create(environment);
      output = cli.doIt();
    } else {
      ILinuxSystemVariableCommandLine cli = linuxSystemVariableFactory.create(environment);
      output = cli.doIt();
    }
    assertTrue(output.contains("Hello LLAMA"));
  }
  
  private boolean isWindows() {
    String osName = System.getProperty("os.name");
    osName = osName.toLowerCase();
    return osName.contains("win");
  }

  @Test
  public void testCustomInstantiator() {
    List<Class<?>> requestedClasses = new ArrayList<>();
    IInstantiator interceptingInstantiator = new IInstantiator() {
      private IInstantiator delegate = new ReflectiveInstantiator();
      @Override
      public boolean canCreate(Class<?> clazz) {
        return delegate.canCreate(clazz);
      }

      @Override
      public <T> T create(Class<T> clazz) throws CommandLineException {
        requestedClasses.add(clazz);
        return delegate.create(clazz);
      }
    };
    IExecutableFactory<IJavaCommandLine> javaFactory2 = new ReflectiveExecutableFactory<>(
        IJavaCommandLine.class, interceptingInstantiator);
    IJavaCommandLine java = javaFactory2.create();
    Version version = java.version();
    assertNotNull(version);
    assertTrue(requestedClasses.contains(VersionResultConverter.class));
  }
}