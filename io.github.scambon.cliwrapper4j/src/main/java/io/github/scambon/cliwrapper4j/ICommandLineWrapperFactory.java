package io.github.scambon.cliwrapper4j;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.executors.ProcessExecutor;

public interface ICommandLineWrapperFactory {

	default <C extends ICommandLineWrapper> C create(Class<C> commandLineInterface) {
		return create(commandLineInterface, new ProcessExecutor());
	}
	
	public <C extends ICommandLineWrapper> C create(Class<C> commandLineInterface, ICommandLineExecutor processExecutor);
}