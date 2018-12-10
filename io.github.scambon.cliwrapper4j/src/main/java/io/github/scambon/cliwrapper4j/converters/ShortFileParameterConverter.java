package io.github.scambon.cliwrapper4j.converters;

import java.io.File;
import java.nio.file.Path;

public final class ShortFileParameterConverter extends CompositeConverter<Object, String>{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ShortFileParameterConverter() {
		super(
			(IConverter) new LambdaConverter<File, String>(File.class, String.class, File::getPath),
			(IConverter) new LambdaConverter<Path, String>(Path.class, String.class, path -> path.toFile().getPath()),
			new StringQuotedIfNeededConverter());
	}
}