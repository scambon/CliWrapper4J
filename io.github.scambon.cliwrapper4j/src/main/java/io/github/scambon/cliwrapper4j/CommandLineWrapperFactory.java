package io.github.scambon.cliwrapper4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.internal.CommandLineInvocationHandler;

public class CommandLineWrapperFactory implements ICommandLineWrapperFactory {

	@Override
	@SuppressWarnings("unchecked")
	public <C extends ICommandLineWrapper> C create(Class<C> commandLineInterface, ICommandLineExecutor processExecutor) {
		ClassLoader classLoader = CommandLineWrapperFactory.class.getClassLoader();
		Class<?>[] interfaces = new Class<?>[] {commandLineInterface};
		InvocationHandler invocationHandler = new CommandLineInvocationHandler<C>(commandLineInterface, processExecutor);
		C proxy = (C) Proxy.newProxyInstance(classLoader, interfaces, invocationHandler);
		return proxy;
	}
}
