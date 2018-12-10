package io.github.scambon.cliwrapper4j.converters;

public final class ResultConverter implements IConverter<Result, Object> {

	@Override
	public boolean canConvert(Class<Result> inClass, Class<Object> outClass) {
		return Result.class.equals(inClass)
			&& (Result.class.equals(outClass)
				|| Result.class.equals(outClass)
				|| String.class.equals(outClass)
				|| Integer.class.equals(outClass)
				|| int.class.equals(outClass));
	}

	@Override
	public Object convert(Result in, Class<Object> outClass) {
		if(Result.class.equals(outClass)){
			return in;
		} else if(String.class.equals(outClass)) {
			return in.getOutput();
		} else if(Integer.class.equals(outClass) || int.class.equals(outClass)) {
			return in.getReturnCode();
		}
		throw new IllegalArgumentException("Unsupported out type");
	}
}