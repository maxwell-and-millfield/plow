package plow.ui;

import org.eclipse.core.databinding.conversion.Converter;

public class IntegerToBooleanConverter extends Converter {

	public IntegerToBooleanConverter(Object fromType, Object toType) {
		super(fromType, toType);
	}

	public IntegerToBooleanConverter() {
		super(Integer.class, Boolean.class);
	}

	@Override
	public Object convert(Object fromObject) {
		System.out.println(fromObject);
		Integer from = (Integer) fromObject;
		Boolean res = from >= 0;
		System.out.println(res);
		return from >= 0 ? true : null;
	}

}
