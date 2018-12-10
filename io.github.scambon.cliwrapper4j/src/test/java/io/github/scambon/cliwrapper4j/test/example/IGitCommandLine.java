package io.github.scambon.cliwrapper4j.test.example;

import java.nio.file.Path;

import io.github.scambon.cliwrapper4j.Command;
import io.github.scambon.cliwrapper4j.Converter;
import io.github.scambon.cliwrapper4j.Executable;
import io.github.scambon.cliwrapper4j.ICommandLineWrapper;
import io.github.scambon.cliwrapper4j.Option;
import io.github.scambon.cliwrapper4j.converters.FilesWithSpaceSeparatorConverter;

@Executable("git")
public interface IGitCommandLine extends ICommandLineWrapper {

	@Command(value="commit", outType=int.class)
	public IGitCommandLine commit();

	@Option("-m")
	public IGitCommandLine message(String message);
	
	@Option("")
	public IGitCommandLine files(@Converter(FilesWithSpaceSeparatorConverter.class) Path... paths);
}