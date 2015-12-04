package com.hfjy.framework.net.http.entity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.CentralController;
import com.hfjy.framework.net.http.annotation.Path;
import com.hfjy.framework.net.socket.annotation.Code;

public class ControllerMapping {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(CentralController.class);
	private Class<?> classInfo;
	private Object object;
	private String path;
	private Method defaultMethod;
	private Map<String, Method> controllerMethods = new HashMap<>();
	private Map<Method, Class<?>[]> methodParameters = new HashMap<>();

	public ControllerMapping(Class<?> classInfo) {
		try {
			this.classInfo = classInfo;
			this.object = classInfo.newInstance();
			Method[] methods = classInfo.getDeclaredMethods();
			for (int i = 0; i < methods.length; i++) {
				Method tmp = methods[i];
				if (tmp.getName().equals("index")) {
					defaultMethod = tmp;
				}
				String methodPath = null;
				if (tmp.isAnnotationPresent(Path.class)) {
					methodPath = tmp.getAnnotation(Path.class).value();
				} else if (tmp.isAnnotationPresent(Code.class)) {
					methodPath = tmp.getAnnotation(Code.class).value();
				} else {
					methodPath = tmp.getName();
				}
				methodPath = methodPath.replaceAll("/", "");
				controllerMethods.put(methodPath.toLowerCase(), tmp);
				controllerMethods.put(methodPath.toUpperCase(), tmp);
				controllerMethods.put(methodPath, tmp);
				Class<?>[] classInfos = tmp.getParameterTypes();
				methodParameters.put(tmp, classInfos);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public Class<?> getClassInfo() {
		return classInfo;
	}

	public void setClassInfo(Class<?> classInfo) {
		this.classInfo = classInfo;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Method getDefaultMethod() {
		return defaultMethod;
	}

	public void setDefaultMethod(Method defaultMethod) {
		this.defaultMethod = defaultMethod;
	}

	public Method getControllerMethod(String path) {
		return controllerMethods.get(path);
	}

	public Class<?>[] getMethodParameters(Method method) {
		return methodParameters.get(method);
	}
}