package io.github.scambon.cliwrapper4j.converters;

public final class Result {
	
	private final String output;
	private final String error;
	private final int returnCode;

	public Result(String output, String error, int returnCode) {
		this.output = output;
		this.error = error;
		this.returnCode = returnCode;
	}

	public String getOutput() {
		return output;
	}
	
	public String getError() {
		return error;
	}

	public int getReturnCode() {
		return returnCode;
	}
}