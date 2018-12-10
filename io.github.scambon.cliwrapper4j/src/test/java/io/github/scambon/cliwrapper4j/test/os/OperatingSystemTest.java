package io.github.scambon.cliwrapper4j.test.os;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.internal.os.IOperatingSystem;
import io.github.scambon.cliwrapper4j.test.executors.MockExecutorHelper;

public class OperatingSystemTest {
	
	@Test
	@EnabledOnOs(OS.WINDOWS)
	public void testGetOperatingWindowsSystem() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("chcp.com");
		IOperatingSystem operatingSystem = IOperatingSystem.get(executor);
		assertNotNull(operatingSystem);
	}
	
	@Test
	public void testGetOperatingWindowsSystemExplicitly() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("chcp.com");
		IOperatingSystem operatingSystem = IOperatingSystem.get(executor, "windows 10");
		assertNotNull(operatingSystem);
	}
	
	@Test
	public void testGetOperatingWindowsSystemFailure() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("chcp.com");
		assertThrows(IllegalArgumentException.class, () -> IOperatingSystem.get(executor, "whatever"));
	}
}