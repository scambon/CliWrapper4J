package io.github.scambon.cliwrapper4j.test.example;

import java.io.File;
import java.lang.Runtime.Version;
import java.util.Collection;

import io.github.scambon.cliwrapper4j.Aggregator;
import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.Flattener;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.converters.FilesWithPathSeparatorConverter;
import io.github.scambon.cliwrapper4j.converters.Result;

@Executable("java")
public interface IJavaCommandLine extends ICommandLineWrapper {
	
	@Command("--help")
	int help();
	
	@Option("-classpath")
	IJavaCommandLine classpath(@Converter(FilesWithPathSeparatorConverter.class) File... classpathElements);
	
	@Option("-classpath")
	IJavaCommandLine classpath(@Converter(FilesWithPathSeparatorConverter.class) Collection<File> classpathElements);
	
	@Option("-D")
	@Aggregator("")
	@Flattener("=")
	IJavaCommandLine systemProperty(String property, Object value);
	
	@Option("-D")
	@Aggregator("")
	@Flattener("=")
	IJavaCommandLine systemPropertyAsStringLength(String property, @Converter(StringLengthConverter.class) Object value);
	
	@Command("")
	Result main(String mainQualifiedName);
	
	@Command(value = "--version", converter=VersionResultConverter.class)
	Version version();
	@Command(value = "--version", converter=VersionResultConverter.class, validateReturnCode=false)
	Version versionWithoutReturnCodeCheck();
	@Command(value = "--version", converter=VersionResultConverter.class, expectedReturnCodes=1)
	Version versionWithCustomReturnCodeCheck();
}