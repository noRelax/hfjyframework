/**
 * 海风在线学习平台
 * @Title: AbstractEntity.java 
 * @Package: com.hyphen.bean
 * @author: cloud
 * @date: 2014年5月5日-上午11:47:39
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.beans.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.logging.LoggerFactory;

/**
 * @ClassName: AbstractEntity
 * @Description: 实体抽象基类
 * @author cloud
 * @date 2014年5月5日-上午11:47:39
 * 
 */
public abstract class AbstractEntity extends AbstractModel {
	private static Logger logger = LoggerFactory.getLogger(AbstractEntity.class);
	private static final Map<String, Field> fieldMap = new HashMap<>();
	private static final Map<Class<?>, Field[]> fieldsMap = new HashMap<>();

	@Override
	public String getTableName() {
		return getTableName(getClass().getSimpleName());
	}

	public static Field getEntityField(String name) {
		return fieldMap.get(name);
	}

	public static void setEntityField(String name, Field fields) {
		fieldMap.put(name, fields);
	}

	public static Field[] getEntityFields(Class<?> classInfo) {
		if (fieldsMap.get(classInfo) == null) {
			setEntityFields(classInfo, classInfo.getDeclaredFields());
		}
		return fieldsMap.get(classInfo);
	}

	public static void setEntityFields(Class<?> classInfo, Field[] fields) {
		fieldsMap.put(classInfo, fields);
	}

	/**
	 * 默认的初始化对象属性方法
	 * 
	 * @throws Exception
	 */
	public void init(Map<String, Object> data) {
		if (data != null && data.size() > 0) {
			Iterator<String> tmpIterator = data.keySet().iterator();
			while (tmpIterator.hasNext()) {
				String columnName = tmpIterator.next();
				Object columnData = data.get(columnName);
				if (columnData != null) {
					try {
						String name = StringUtils.unite(getClass().getName(), ".", columnName);
						Field field = getEntityField(name);
						if (field == null) {
							Class<?> tmpClass = getClass();
							do {
								Field[] fields = getEntityFields(tmpClass);
								field = findField(columnName, fields);
							} while (field == null && (tmpClass = tmpClass.getSuperclass()) != Object.class);
							setEntityField(name, field);
						}
						if (field != null) {
							field.setAccessible(true);
							field.set(this, columnData);
							field.setAccessible(false);
						}
					} catch (Exception e) {
						logger.error(columnName + "属性赋值出错: ", e);
					}
				}
			}
		}
	}

	private Field findField(String columnName, Field[] fields) {
		for (int i = 0; i < fields.length; i++) {
			Entity annotation = fields[i].getAnnotation(Entity.class);
			if (annotation != null) {
				if (annotation.columnName().equalsIgnoreCase(columnName)) {
					return fields[i];
				}
			}
			if (columnName.replaceAll("_", "").equalsIgnoreCase(fields[i].getName())) {
				return fields[i];
			}
		}
		return null;
	}

	public static String getTableName(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			if (i != 0 && name.charAt(i) > 64 && name.charAt(i) < 91) {
				sb.append('_');
			}
			sb.append(name.charAt(i));
		}
		return sb.toString().toLowerCase();
	}
}
