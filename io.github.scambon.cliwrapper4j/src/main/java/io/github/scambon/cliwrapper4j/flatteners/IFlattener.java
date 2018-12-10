package io.github.scambon.cliwrapper4j.flatteners;

import java.util.List;

public interface IFlattener {
	String flatten(List<String> parameters, String parameter);
}
