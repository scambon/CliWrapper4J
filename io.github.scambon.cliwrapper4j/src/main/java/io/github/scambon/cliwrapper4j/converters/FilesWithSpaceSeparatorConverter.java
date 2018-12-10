package io.github.scambon.cliwrapper4j.converters;

import io.github.scambon.cliwrapper4j.flatteners.JoiningOnDelimiterFlattener;

public final class FilesWithSpaceSeparatorConverter extends MultipleParameterConverter {

	public FilesWithSpaceSeparatorConverter() {
		super(Object.class, new ShortFileParameterConverter(), new JoiningOnDelimiterFlattener(), " ");
	}
}