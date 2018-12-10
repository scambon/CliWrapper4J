package io.github.scambon.cliwrapper4j.internal.os;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;

public interface IOperatingSystem {
	
	static final Map<String, Function<ICommandLineExecutor,  IOperatingSystem>> OPERATING_SYSTEMS_FACTORIES =
		Map.of(
			"win", WindowsOperatingSystem::new
	);
	
	static IOperatingSystem get(ICommandLineExecutor executor) {
		String osName = System.getProperty("os.name").toLowerCase();
		return get(executor, osName);
	}
	
	static IOperatingSystem get(ICommandLineExecutor executor, String osName) {
		IOperatingSystem operatingSystem = OPERATING_SYSTEMS_FACTORIES.entrySet()
				.stream()
				.filter(entry -> osName.contains(entry.getKey()))
				.map(Entry::getValue)
				.map(constructor -> constructor.apply(executor))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Unknown OS '"+osName+"'"));
		return operatingSystem;
	}
	
	Charset getConsoleCharset() throws IOException;
}