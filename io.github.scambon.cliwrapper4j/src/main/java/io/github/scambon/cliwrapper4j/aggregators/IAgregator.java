package io.github.scambon.cliwrapper4j.aggregators;

public interface IAgregator {

	String aggregate(String optionOrCommand, String value, String parameter);
}