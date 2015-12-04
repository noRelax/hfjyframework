package com.hfjy.framework.beans;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.hfjy.framework.cache.CacheProcess;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.dao.Dao;

public class BeanInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	private transient Lock initLock = new ReentrantLock();
	private Class<?> classInfo;
	private Object object;
	private Object transactionProxyObject;
	private boolean fieldsIsOk = false;
	private List<Field> fields = new ArrayList<>();
	private Map<String, Method> methodMap = new HashMap<>();
	private Set<String> dbNames = new HashSet<>();

	@Override
	protected BeanInfo clone() throws CloneNotSupportedException {
		return (BeanInfo) super.clone();
	}

	public BeanInfo(Class<?> classInfo) throws InstantiationException, IllegalAccessException {
		if (object == null) {
			this.object = classInfo.newInstance();
		}
		initBeanInfo(object);
	}

	public BeanInfo(Object object) {
		initBeanInfo(object);
	}

	private void initBeanInfo(Object object) {
		this.object = object;
		this.classInfo = object.getClass();
		Method[] methods = classInfo.getMethods();
		for (int i = 0; i < methods.length; i++) {
			methodMap.put(ClassUtil.getMethodKey(methods[i]), methods[i]);
		}
		Class<?> tmpClass = classInfo;
		do {
			Field[] tmpFields = tmpClass.getDeclaredFields();
			for (int i = 0; i < tmpFields.length; i++) {
				this.fields.add(tmpFields[i]);
			}
		} while ((tmpClass = tmpClass.getSuperclass()) != Object.class);
	}

	public Class<?> getClassInfo() {
		return classInfo;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public Object getObject() {
		return object;
	}

	public String[] getDbNames() {
		return dbNames.toArray(new String[] {});
	}

	public Method getMethod(String str) {
		return methodMap.get(str);
	}

	public Object getProxyObject() {
		if (transactionProxyObject == null) {
			transactionProxyObject = Proxy.newProxyInstance(object.getClass().getClassLoader(), object.getClass().getInterfaces(), new BeanCentralHandler(this));
		}
		return transactionProxyObject;
	}

	public void initFields() {
		if (fieldsIsOk) {
			return;
		}
		int errorNum = 0;
		try {
			initLock.lock();
			for (int i = 0; i < fields.size(); i++) {
				Field field = fields.get(i);
				if (field.isAnnotationPresent(Bean.class)) {
					try {
						field.setAccessible(true);
						Class<?> tmpClass = field.getAnnotation(Bean.class).thisClass();
						BeanInfo fieldBeanInfo = BeanManager.beans.get(StringUtils.unite(field.getType(), "#", tmpClass.getName()));
						if (fieldBeanInfo == null) {
							fieldBeanInfo = new BeanInfo(tmpClass);
							BeanManager.beans.put(StringUtils.unite(field.getType(), "#", tmpClass.getName()), fieldBeanInfo);
						}
						if (fieldBeanInfo.getObject() instanceof Dao<?>) {
							Dao<?> dao = (Dao<?>) fieldBeanInfo.getObject();
							dbNames.add(dao.getDBName());
						}
						if (field.getType() == CacheProcess.class) {
							field.set(getObject(), fieldBeanInfo.getObject());
						} else if (field.getType().isInterface()) {
							field.set(getObject(), fieldBeanInfo.getProxyObject());
						} else {
							field.set(getObject(), fieldBeanInfo.getObject());
						}
						field.setAccessible(false);
					} catch (Exception e) {
						e.printStackTrace();
						errorNum++;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (errorNum == 0) {
				fieldsIsOk = true;
			}
			initLock.unlock();
		}
	}
}