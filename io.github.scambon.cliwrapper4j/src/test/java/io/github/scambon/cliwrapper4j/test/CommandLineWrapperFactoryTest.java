package io.github.scambon.cliwrapper4j.test;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.lang.Runtime.Version;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import io.github.scambon.cliwrapper4j.CommandLineWrapperFactory;
import io.github.scambon.cliwrapper4j.ICommandLineWrapperFactory;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.test.example.IGitCommandLine;
import io.github.scambon.cliwrapper4j.test.example.IJavaCommandLine;
import io.github.scambon.cliwrapper4j.test.executors.MockExecutorHelper;

public class CommandLineWrapperFactoryTest {
	
	private ICommandLineWrapperFactory factory = new CommandLineWrapperFactory();

	@Test
	void testWorkingCommandWith0ParametersAndConversion() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		Version version = java.version();
		assertNotNull(version);
		verify(executor).execute(asList(
			"java", "--version"));
	}
	
	@Test
	void testFailedCommand() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version", "failed");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		assertThrows(IllegalArgumentException.class, () -> java.version());
		verify(executor).execute(asList(
			"java", "--version"));
	}
	
	@Test
	void testFailedCommandWithDisabledChecking() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version", "failed");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		java.versionWithoutReturnCodeCheck();
		verify(executor).execute(asList(
			"java", "--version"));
	}
	
	@Test
	void testFailedCommandWithCustomExpectedRetunCode() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version", "failed");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		java.versionWithCustomReturnCodeCheck();
		verify(executor).execute(asList(
			"java", "--version"));
	}
	
	@Test
	void testOptionWithFlattenerAndPathConversionOnArray() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		java.classpath(new File("llama.jar"), new File("chicken.jar"))
			.main("com.example.Whatever");
		verify(executor).execute(asList(
			"java",
			"-classpath llama.jar"+File.pathSeparatorChar+"chicken.jar",
			"com.example.Whatever"));
	}
	
	@Test
	void testOptionWithFlattenerAndPathConversionOnCollection() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		java.classpath(asList(new File("llama.jar"), new File("chicken.jar")))
			.main("com.example.Whatever");
		verify(executor).execute(asList(
				"java",
				"-classpath llama.jar"+File.pathSeparatorChar+"chicken.jar",
				"com.example.Whatever"));
	}
	
	@Test
	void testOptionWithFlattenerAndCustomConversion() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("java");
		IJavaCommandLine java = factory.create(IJavaCommandLine.class, executor);
		java.systemPropertyAsStringLength("key", "value")
			.main("com.example.Whatever");
		verify(executor).execute(asList(
			"java", "-Dkey=5", "com.example.Whatever"));
	}
	
	@Test
	void testExecuteMethod() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("git");
		IGitCommandLine git = factory.create(IGitCommandLine.class, executor);
		int returnCode = git.commit()
			.message("Some message")
			.files(Paths.get("whatever.txt"))
			.execute();
		assertEquals(0, returnCode);
		verify(executor).execute(asList(
			"git", "commit", "-m \"Some message\"", "whatever.txt"));
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	void testExecutableWithActualProcessExecutor() throws IOException {
		IJavaCommandLine java = factory.create(IJavaCommandLine.class);
		Version version = java.version();
		assertNotNull(version);
	}
}