package com.hfjy.framework.checkable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.ConvertUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.exception.CheckableDataException;

public class CheckableManager {

	public static void checkObjects(Object[] parameters, Class<?>[] outTypes, Annotation[][] annotations) throws CheckableDataException {
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				Annotation tmpAnnotation = ClassUtil.getAnnotation(annotations[i], Checkable.class);
				if (tmpAnnotation != null) {
					checkObject(parameters[i], outTypes[i], (Checkable) tmpAnnotation);
				}
			}
		}
	}

	public static void checkObject(Object data, Class<?> outType, Checkable checkable) throws CheckableDataException {
		checkIsNull(data, checkable);
		if (outType.isArray()) {
			checkArray((Object[]) data, outType, checkable);
		} else if (outType == Date.class || StringUtils.isNotEmpty(checkable.dateFormat())) {
			checkDate(ConvertUtil.toString(data), checkable);
		} else if (outType == String.class) {
			checkString(ConvertUtil.toString(data), checkable);
		} else if (outType == Double.class || outType == double.class) {
			checkDouble(ConvertUtil.toString(data), checkable);
		} else if (outType == Float.class || outType == float.class) {
			checkFloat(ConvertUtil.toString(data), checkable);
		} else if (outType == Integer.class || outType == int.class) {
			checkInteger(ConvertUtil.toString(data), checkable);
		} else if (!ClassUtil.isJavaType(outType)) {
			Field[] fields = outType.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].isAnnotationPresent(Checkable.class)) {
					Checkable tmpCheckable = fields[i].getAnnotation(Checkable.class);
					try {
						fields[i].setAccessible(true);
						Object fieldValue = fields[i].get(data);
						fields[i].setAccessible(false);
						checkObject(fieldValue, fields[i].getType(), tmpCheckable);
					} catch (Exception e) {
						throw new CheckableDataException(e.getMessage(), e);
					}
				}
			}
		}
	}

	public static void checkIsNull(Object data, Checkable checkable) throws CheckableDataException {
		if (data == null && !checkable.isNull()) {
			String message = StringUtils.unite("[", data, "] claim is not null. data check failed!");
			throw new CheckableDataException(message);
		}
	}

	public static void checkArray(Object[] data, Class<?> outType, Checkable checkable) throws CheckableDataException {
		checkIsNull(data, checkable);
		int length = data.length;
		if (checkable.maxLength() != -1 && length > checkable.maxLength()) {
			String message = StringUtils.unite("[", data, "] length is ", length, " claim array max length ", checkable.maxLength(), ". data check failed!");
			throw new CheckableDataException(message);
		}
		if (checkable.minLength() != -1 && length < checkable.minLength()) {
			String message = StringUtils.unite("[", data, "] length is ", length, " claim array min length ", checkable.minLength(), ". data check failed!");
			throw new CheckableDataException(message);
		}
		for (int i = 0; i < length; i++) {
			String tmp = ConvertUtil.toString(data[i]);
			if (outType == String[].class) {
				checkString(tmp, checkable);
			} else if (outType == Integer[].class || outType == int[].class) {
				checkInteger(tmp, checkable);
			} else if (outType == Double[].class || outType == double[].class) {
				checkDouble(tmp, checkable);
			} else if (outType == Float[].class || outType == float[].class) {
				checkFloat(tmp, checkable);
			} else if (outType == Date[].class) {
				checkDate(tmp, checkable);
			} else {
				checkObject(data[i], outType.getComponentType(), checkable);
			}
		}
	}

	public static void checkString(String data, Checkable checkable) throws CheckableDataException {
		checkIsNull(data, checkable);
		int length = data.length();
		if (checkable.maxLength() != -1 && length > checkable.maxLength()) {
			String message = StringUtils.unite("[", data, "] length is ", length, " claim max length ", checkable.maxLength(), ". data check failed!");
			throw new CheckableDataException(message);
		}
		if (checkable.minLength() != -1 && length < checkable.minLength()) {
			String message = StringUtils.unite("[", data, "] length is ", length, " claim min length ", checkable.minLength(), ". data check failed!");
			throw new CheckableDataException(message);
		}
		if (StringUtils.isNotEmpty(checkable.regex())) {
			Pattern pattern = Pattern.compile(checkable.regex());
			Matcher matcher = pattern.matcher(data);
			if (!matcher.matches()) {
				String message = StringUtils.unite("[", data, "] claim regex does not pass. data check failed!");
				throw new CheckableDataException(message);
			}
		}
	}

	public static void checkInteger(String data, Checkable checkable) throws CheckableDataException {
		checkString(data, checkable);
		try {
			Integer.parseInt(data);
		} catch (NumberFormatException e) {
			throw new CheckableDataException(e.getMessage(), e);
		}
	}

	public static void checkFloat(String data, Checkable checkable) throws CheckableDataException {
		checkString(data, checkable);
		try {
			Float.parseFloat(data);
			if (checkable.decimalLength() != -1) {
				Pattern pattern = Pattern.compile(StringUtils.unite("^\\d+\\.\\d{", checkable.decimalLength(), "}$"));
				Matcher matcher = pattern.matcher(data);
				if (!matcher.matches()) {
					String message = StringUtils.unite("[", data, "] decimal length is ", checkable.decimalLength(), " claim decimal length. data check failed!");
					throw new CheckableDataException(message);
				}
			}
		} catch (NumberFormatException e) {
			throw new CheckableDataException(e.getMessage(), e);
		}
	}

	public static void checkDouble(String data, Checkable checkable) throws CheckableDataException {
		checkString(data, checkable);
		try {
			Double.parseDouble(data);
			if (checkable.decimalLength() != -1) {
				Pattern pattern = Pattern.compile(StringUtils.unite("^\\d+\\.\\d{", checkable.decimalLength(), "}$"));
				Matcher matcher = pattern.matcher(data);
				if (!matcher.matches()) {
					String message = StringUtils.unite("[", data, "] decimal length is ", checkable.decimalLength(), " claim decimal length. data check failed!");
					throw new CheckableDataException(message);
				}
			}
		} catch (NumberFormatException e) {
			throw new CheckableDataException(e.getMessage(), e);
		}
	}

	public static void checkDate(String data, Checkable checkable) throws CheckableDataException {
		checkIsNull(data, checkable);
		try {
			DateFormat sf = new SimpleDateFormat(checkable.dateFormat());
			sf.parse(data);
		} catch (ParseException e) {
			String message = StringUtils.unite("[", data, "] claim date format does not pass. data check failed!");
			throw new CheckableDataException(message, e);
		}
	}
}
