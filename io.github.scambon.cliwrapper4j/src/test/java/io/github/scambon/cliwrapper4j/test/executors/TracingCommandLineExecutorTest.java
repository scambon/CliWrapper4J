package io.github.scambon.cliwrapper4j.test.executors;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.executors.TracingCommandLineExecutor;

public class TracingCommandLineExecutorTest {
	
	@Test
	public void testTraceExecutionContent() throws IOException {
		List<String> expectedCommands = Arrays.asList("java", "--version");
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version");
		List<String> actualCommands = new ArrayList<>(); 
		executor = new TracingCommandLineExecutor(executor, actualCommands::addAll);
		executor.execute(expectedCommands);
		// Skip 1 for the chcp call
		assertEquals(expectedCommands, actualCommands.subList(1, 3));
	}
	
	@Test
	public void testTraceExecutionResult() throws IOException {
		List<String> expectedCommands = Arrays.asList("java", "--version");
		ICommandLineExecutor executor = MockExecutorHelper.create("java", "--version");
		executor = executor.traced();
		Result result = executor.execute(expectedCommands);
		assertFalse(result.getOutput().isEmpty());
		assertEquals(0, result.getReturnCode());
	}
}