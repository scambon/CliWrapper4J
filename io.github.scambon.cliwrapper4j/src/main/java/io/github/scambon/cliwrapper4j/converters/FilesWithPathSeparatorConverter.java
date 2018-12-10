package io.github.scambon.cliwrapper4j.converters;

import java.io.File;

import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

public final class FilesWithPathSeparatorConverter extends MultipleParameterConverter {

	public FilesWithPathSeparatorConverter() {
		super(Object.class, new ShortFileParameterConverter(), new JoiningOnDelimiterFlattener(), File.pathSeparator);
	}
}