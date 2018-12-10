package io.github.scambon.cliwrapper4j.internal;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.aggregators.IAgregator;
import io.github.scambon.cliwrapper4j.aggregators.SymbolAggregator;
import io.github.scambon.cliwrapper4j.converters.IConverter;
import io.github.scambon.cliwrapper4j.converters.Result;
import io.github.scambon.cliwrapper4j.converters.ResultConverter;
import io.github.scambon.cliwrapper4j.converters.StringQuotedIfNeededConverter;
import io.github.scambon.cliwrapper4j.executors.ICommandLineExecutor;
import io.github.scambon.cliwrapper4j.flatteners.IFlattener;
import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

public final class CommandLineInvocationHandler<C extends ICommandLineWrapper> implements InvocationHandler {

	private final ICommandLineExecutor processExecutor;
	private final Class<C> commandLineWrapperInterface;

	private final List<String> elements = new ArrayList<>();

	private boolean shouldValidateReturnCode = false;
	private int[] expectedReturnCodes = { 0 };
	private IConverter<Result, ? extends Object> resultConverter = new ResultConverter();
	private Method commandMethod;
	private Class<?> outType;

	public CommandLineInvocationHandler(Class<C> commandLineWrapperInterface, ICommandLineExecutor processExecutor) {
		this.commandLineWrapperInterface = commandLineWrapperInterface;
		Executable executableAnnotation = commandLineWrapperInterface.getAnnotation(Executable.class);
		if(executableAnnotation==null) {
			throw new IllegalArgumentException("No @Executable annotation found on '"+commandLineWrapperInterface+"'");
		}
		String executable = executableAnnotation.value();
		elements.add(executable);
		this.processExecutor = processExecutor;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Method executerMethod = ICommandLineWrapper.class.getMethod("execute");
		if(method.equals(executerMethod)) {
			return runCommand();
		} else {
			Optional<Object> resultOpt = tryHandleOption(proxy, method, args);
			if(!resultOpt.isPresent()) {
				resultOpt = tryHandleCommand(proxy, method, args);
			}
			return resultOpt.orElse(null);
		}
	}
	
	private Optional<Object> tryHandleOption(Object proxy, Method method, Object[] args) throws ReflectiveOperationException {
		Option option = method.getAnnotation(Option.class);
		if (option != null) {
			String optionName = option.value();
			registerElement(method, optionName, args);
			return Optional.of(proxy);
		}
		return Optional.empty();
	}

	private Optional<Object> tryHandleCommand(Object proxy, Method method, Object[] args) throws ReflectiveOperationException, IOException {
		Command command = method.getAnnotation(Command.class);
		if (command != null) {
			this.shouldValidateReturnCode = command.validateReturnCode();
			if (shouldValidateReturnCode) {
				this.expectedReturnCodes = command.expectedReturnCodes();
			}

			String executable = command.value();
			registerElement(method, executable, args);
			commandMethod = method;
			resultConverter = getOrDefaultClass(method, Command.class, Command::converter, ResultConverter::new);

			// Should we execute right now?
			Class<?> returnType = method.getReturnType();
			if (returnType.equals(commandLineWrapperInterface)) {
				outType = command.outType();
				return Optional.of(proxy);
			} else {
				outType = returnType;
				Object result = runCommand();
				return Optional.of(result);
			}
		}
		return Optional.empty();
	}

	private void registerElement(Method method, String commandOrOption, Object[] args)
			throws ReflectiveOperationException {
		String flattenedParameters = handleRawParameters(method, args);
		String aggregatedCommand = aggregate(method, commandOrOption, flattenedParameters);
		elements.add(aggregatedCommand);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Object runCommand() throws ReflectiveOperationException, IOException {
		Result result = processExecutor.execute(elements);
		if(shouldValidateReturnCode) {
			int returnCode = result.getReturnCode();
			Arrays.stream(expectedReturnCodes)
				.filter(expectedReturnCode -> returnCode==expectedReturnCode)
				.findAny()
				.orElseThrow(() -> new IllegalArgumentException("Finished with code '"+returnCode+"' but expected it in '"+Arrays.toString(expectedReturnCodes)+"'"));
		}
		Object convertedResult = tryHandleConversion(commandMethod, Result.class, result, resultConverter, (Class) outType);
		return convertedResult;
	}

	private String aggregate(Method method, String commandOrOption, String flattenedConvertedValues) throws ReflectiveOperationException {
		IAgregator aggregator = getOrDefaultClass(method, Aggregator.class, Aggregator::aggregator, SymbolAggregator::new);
		String aggregatorParameter = getOrDefault(method, Aggregator.class, Aggregator::value, () -> " ");
		String aggregate = aggregator.aggregate(commandOrOption, flattenedConvertedValues, aggregatorParameter);
		return aggregate;
	}

	private String handleRawParameters(Method method, Object[] values) throws ReflectiveOperationException {
		List<String> convertedValues = new ArrayList<>();
		Parameter[] parameters = method.getParameters();
		for (int index = 0; index < parameters.length; index++) {
			Parameter parameter = parameters[index];
			Object value = values[index];
			String convertedValue = tryHandleParameterAnnotation(parameter, value);
			convertedValues.add(convertedValue);
		}
		String flattenedValues = flatten(method, convertedValues);
		return flattenedValues; 
	}
	
	
	private String flatten(Method method, List<String> convertedValues) throws ReflectiveOperationException {
		IFlattener flattener = getOrDefaultClass(method, Flattener.class, Flattener::flattener, JoiningOnDelimiterFlattener::new);
		String flattenerParameter = getOrDefault(method, Flattener.class, Flattener::value, () -> " ");
		String flattenedConvertedValues = flattener.flatten(convertedValues, flattenerParameter);
		return flattenedConvertedValues;
	}

	private <A extends Annotation, V> V getOrDefault(AnnotatedElement annotated, Class<A> annotationClass,
			IReflectiveFunction<A, ? extends V> getter, Supplier<? extends V> defaultValueSupplier) throws ReflectiveOperationException {
		A annotation = annotated.getAnnotation(annotationClass);
		V value;
		if (annotation != null) {
			value = getter.call(annotation);
		}else {
			value = defaultValueSupplier.get();
		}
		return value;
	}
	
	private interface IReflectiveFunction<In, Out> {
		Out call(In in) throws ReflectiveOperationException;
	}
	
	private <A extends Annotation, V> V getOrDefaultClass(AnnotatedElement annotated, Class<A> annotationClass,
			Function<A, Class<? extends V>> getter, Supplier<? extends V> defaultValueSupplier) throws ReflectiveOperationException {
		IReflectiveFunction<A, V> instanceGetter = annotation -> {
			Class<? extends V> classValue = getter.apply(annotation);
			Constructor<? extends V> constructor = classValue.getConstructor();
			return constructor.newInstance();
		};
		return getOrDefault(annotated, annotationClass, instanceGetter, defaultValueSupplier);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String tryHandleParameterAnnotation(Parameter parameter, Object value) throws ReflectiveOperationException {
		IConverter converter = getOrDefaultClass(parameter, Converter.class, Converter::value, StringQuotedIfNeededConverter::new);
		Class parameterType = parameter.getType();
		String convertedValue = tryHandleConversion(parameter, parameterType, value, converter, String.class);
		return convertedValue;
	}

	private <In, Out> Out tryHandleConversion(Object holder, Class<In> inType, In value, IConverter<In, Out> converter,
			Class<Out> outType) throws ReflectiveOperationException {
		boolean canConvert = converter.canConvert(inType, outType);
		if (!canConvert) {
			throw new IllegalArgumentException("Cannot convert value '" + value + "' from '" + inType + "' to '"
					+ outType + "' on element '" + holder + "'");
		}
		Out convertedValue = converter.convert(value, outType);
		return convertedValue;
	}
}