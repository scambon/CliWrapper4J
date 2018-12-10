package io.github.scambon.cliwrapper4j.test.executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

public class ProcessExecutorTest {

	@Test
	@EnabledOnOs(OS.LINUX)
	public void testExecuteWorkingCommandOnLinux() throws IOException {
		testExecuteWorkingEchoWhateverCommand("echo", "whatever");
	}
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testExecuteWorkingCommandOnWindows() throws IOException {
		testExecuteWorkingEchoWhateverCommand("cmd", "/C", "echo", "whatever");
	}
	
	public void testExecuteWorkingEchoWhateverCommand(String... elements) throws IOException {
		ProcessExecutor processExecutor = new ProcessExecutor();
		Result result = processExecutor.execute(Arrays.asList(elements));
		assertEquals(0, result.getReturnCode());
		assertTrue(result.getOutput().matches("\\s*whatever\\s*"));
		assertTrue(result.getError().isEmpty());
	}
	
	@Test
	public void testExecuteBrokenCommand() throws IOException {
		ProcessExecutor processExecutor = new ProcessExecutor();
		assertThrows(IOException.class, () -> processExecutor.execute(Arrays.asList("broken_command")));
	}
}