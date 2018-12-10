package io.github.scambon.cliwrapper4j.test.os;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.charset.Charset;

import org.junit.jupiter.api.Test;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.internal.os.WindowsOperatingSystem;
import io.github.scambon.cliwrapper4j.test.executors.MockExecutorHelper;

public class WindowsOperatingSystemTest {
	
	@Test
	public void testGetConsoleEncoding() throws IOException {
		ICommandLineExecutor executor = MockExecutorHelper.create("chcp.com");
		WindowsOperatingSystem windowsOperatingSystem = new WindowsOperatingSystem(executor);
		Charset consoleCharset = windowsOperatingSystem.getConsoleCharset();
		String charsetName = consoleCharset.name();
		assertEquals("IBM850", charsetName);
	}
}