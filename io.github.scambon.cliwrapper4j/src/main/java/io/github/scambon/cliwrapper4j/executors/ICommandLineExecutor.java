package io.github.scambon.cliwrapper4j.executors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.internal.os.IOperatingSystem;

public interface ICommandLineExecutor {
	default Result execute(List<String> elements) throws IOException{
		IOperatingSystem os = IOperatingSystem.get(this);
		Charset consoleCharset = os.getConsoleCharset();
		Result result = execute(elements, consoleCharset);
		return result;
	}
	Result execute(List<String> elements, Charset charset) throws IOException;
	
	default ICommandLineExecutor traced() {
		return new TracingCommandLineExecutor(this);
	}
}