package io.github.scambon.cliwrapper4j.executors;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import io.github.scambon.cliwrapper4j.converters.Result;

public final class ProcessExecutor implements ICommandLineExecutor {
	
	@Override
	public Result execute(List<String> elements, Charset charset) throws IOException {
		Process process = new ProcessBuilder(elements)
			.start();
		try {
			int returnCode = process.waitFor();
			String output = readInputStream(process.getInputStream(), charset);
			String error = readInputStream(process.getErrorStream(), charset);
			Result result = new Result(output, error, returnCode);
			return result;
		} catch (InterruptedException e) {
			throw new IOException(e);
		}
	}

	private String readInputStream(InputStream inputStream, Charset charset) throws IOException {
		byte[] bytes = inputStream.readAllBytes();
		String string = new String(bytes, charset);
		return string;
	}
}