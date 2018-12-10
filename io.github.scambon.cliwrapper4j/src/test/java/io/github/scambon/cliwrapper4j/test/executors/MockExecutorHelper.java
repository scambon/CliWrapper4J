package io.github.scambon.cliwrapper4j.test.executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

public final class MockExecutorHelper {
	
	private MockExecutorHelper() {
		// NOP
	}

	public static ICommandLineExecutor create(String... filenameElements) throws IOException {
		Properties properties = new Properties();
		String resultFile = String.join("_", filenameElements) + ".properties";
		try(InputStream in = MockExecutorHelper.class.getResourceAsStream(resultFile)){
			properties.load(in);
		}
		String output = properties.getProperty("output");
		String error = properties.getProperty("error");
		int returnCode = Integer.parseInt(properties.getProperty("returnCode"));
		Result result = new Result(output, error, returnCode);
		ICommandLineExecutor executor = mock(ICommandLineExecutor.class);
		Result codepageResult = new Result(getCodepageOutput(), "", 0);
		when(executor.execute(any(), any()))
			.thenReturn(codepageResult, result);
		doCallRealMethod().when(executor)
			.execute(anyList());
		doCallRealMethod().when(executor)
			.traced();
		return executor;
	}

	private static String getCodepageOutput() {
		return "Page de codes active : 850";
	}
}