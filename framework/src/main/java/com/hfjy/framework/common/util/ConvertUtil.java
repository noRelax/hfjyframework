package com.hfjy.framework.common.util;

import java.lang.reflect.Array;
import java.util.Date;

import com.hfjy.framework.logging.LoggerFactory;

public class ConvertUtil {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ConvertUtil.class);

	@SuppressWarnings("unchecked")
	public static <T> T[] toIts(Object[] datas, Class<T> classInfo) {
		T[] array = (T[]) Array.newInstance(classInfo, datas.length);
		for (int i = 0; i < datas.length; i++) {
			array[i] = toIt(datas[i], classInfo);
		}
		return array;
	}

	public static <T> T toIt(Object data, Class<T> classInfo) {
		T object = null;
		String inData = null;
		if (data instanceof String) {
			inData = (String) data;
		} else {
			inData = toString(data);
		}
		if (StringUtils.isNotEmpty(inData)) {
			object = stringToIt(inData, classInfo);
		}
		return object;
	}

	public static String toString(Object object) {
		if (object instanceof Boolean) {
			return String.valueOf((boolean) object);
		} else if (object instanceof Character) {
			return String.valueOf((char) object);
		} else if (object instanceof char[]) {
			return String.valueOf((char[]) object);
		} else if (object instanceof Double) {
			return String.valueOf((double) object);
		} else if (object instanceof Float) {
			return String.valueOf((float) object);
		} else if (object instanceof Integer) {
			return String.valueOf((int) object);
		} else if (object instanceof Long) {
			return String.valueOf((long) object);
		} else if (object instanceof Date) {
			return DateUtils.formatDate((Date) object, "yyyy-MM-dd HH:mm:ss");
		} else {
			return String.valueOf(object);
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T stringToIt(String string, Class<T> classInfo) {
		Object object = null;
		try {
			if (classInfo == byte.class || classInfo == Byte.class) {
				object = Byte.parseByte(string);
			} else if (classInfo == short.class || classInfo == Short.class) {
				object = Short.parseShort(string);
			} else if (classInfo == int.class || classInfo == Integer.class) {
				object = Integer.parseInt(string);
			} else if (classInfo == long.class || classInfo == Long.class) {
				object = Long.parseLong(string);
			} else if (classInfo == float.class || classInfo == Float.class) {
				object = Float.parseFloat(string);
			} else if (classInfo == double.class || classInfo == Double.class) {
				object = Double.parseDouble(string);
			} else if (classInfo == Date.class || classInfo == java.sql.Date.class) {
				object = DateUtils.stringToDate(string, "yyyy-MM-dd HH:mm:ss");
			} else {
				object = string;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return (T) object;
	}
}