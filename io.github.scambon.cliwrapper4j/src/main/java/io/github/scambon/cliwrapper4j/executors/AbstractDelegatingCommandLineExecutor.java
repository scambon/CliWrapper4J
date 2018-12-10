package io.github.scambon.cliwrapper4j.executors;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import io.github.scambon.cliwrapper4j.converters.Result;

public abstract class AbstractDelegatingCommandLineExecutor implements ICommandLineExecutor {
	
	private final ICommandLineExecutor delegate;
	
	public AbstractDelegatingCommandLineExecutor(ICommandLineExecutor delegate) {
		this.delegate = delegate;
	}

	@Override
	public Result execute(List<String> elements, Charset charset) throws IOException {
		return delegate.execute(elements, charset);
	}
}