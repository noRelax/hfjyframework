package com.hfjy.framework.beans;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

public class BeanManager {
	private static Logger logger = LoggerFactory.getLogger(BeanManager.class);
	protected static Map<String, BeanInfo> beans = new HashMap<>();

	public static void registrationClass(Object object) {
		if (object == null) {
			return;
		}
		new BeanInfo(object).initFields();
	}

	@SuppressWarnings("unchecked")
	public static <T> T registrationClass(Class<T> beanClass) {
		if (beanClass == null) {
			return null;
		}
		Object object = null;
		try {
			BeanInfo beanInfo = beans.get(beanClass.getName());
			if (beanInfo == null) {
				beanInfo = new BeanInfo(beanClass);
				beans.put(beanClass.getName(), beanInfo);
			}
			object = beanInfo.getProxyObject();
		} catch (Exception e) {
			logger.error(e.toString(), e);
		}
		return (T) object;
	}
}