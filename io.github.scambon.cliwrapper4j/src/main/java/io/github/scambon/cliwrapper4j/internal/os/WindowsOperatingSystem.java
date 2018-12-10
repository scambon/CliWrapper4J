package io.github.scambon.cliwrapper4j.internal.os;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;

import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

public class WindowsOperatingSystem implements IOperatingSystem {
	
	private final ICommandLineExecutor executor;
	private Charset consoleCharset;
	
	public WindowsOperatingSystem(ICommandLineExecutor executor) {
		this.executor = executor;
	}

	@Override
	public Charset getConsoleCharset() throws IOException {
		if(consoleCharset==null) {
			Result chcpResult = executor.execute(
				Arrays.asList("chcp.com"),
				StandardCharsets.US_ASCII);
			String output = chcpResult.getOutput();
			String codePageName = output.replaceAll("[^\\d]", "");
			String charsetName = codePageToCharsetName(codePageName);
			this.consoleCharset = Charset.forName(charsetName);
		}
		return consoleCharset;
	}

	private String codePageToCharsetName(String windowsCodepageName) throws IOException {
		try(InputStream windowsCodepage2JavaInputStream =
			WindowsOperatingSystem.class.getResourceAsStream("windows_codepage-2-java.properties")) {
			Properties windowsCodepage2Java = new Properties();
			windowsCodepage2Java.load(windowsCodepage2JavaInputStream);
			return windowsCodepage2Java.getProperty(windowsCodepageName);
		}
	}
}