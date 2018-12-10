package io.github.scambon.cliwrapper4j.executors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.function.Consumer;

import io.github.scambon.cliwrapper4j.converters.Result;

public class TracingCommandLineExecutor extends AbstractDelegatingCommandLineExecutor {
	
	private final Consumer<List<String>> printer;

	public TracingCommandLineExecutor(ICommandLineExecutor delegate) {
		this(delegate, System.out::println);
	}
	
	public TracingCommandLineExecutor(ICommandLineExecutor delegate, Consumer<List<String>> printer) {
		super(delegate);
		this.printer = printer;
	}
	
	@Override
	public Result execute(List<String> elements, Charset charset) throws IOException {
		printer.accept(elements);
		return super.execute(elements, charset);
	}
}