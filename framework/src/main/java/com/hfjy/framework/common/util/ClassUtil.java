package com.hfjy.framework.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.CentralController;

public class ClassUtil {
	private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

	@SuppressWarnings("unchecked")
	public static <T> Class<T> forName(String name) {
		Class<T> clazz = null;
		try {
			clazz = (Class<T>) Class.forName(name);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return clazz;
	}

	public static <T> T newInstance(String className, Object... parameters) {
		Class<T> clazz = null;
		try {
			if (StringUtils.isNotEmpty(className)) {
				clazz = forName(className);
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return newInstance(clazz, parameters);
	}

	public static <T> T newInstance(Class<T> clazz, Object... parameters) {
		T objetc = null;
		try {
			if (StringUtils.isNotEmpty(clazz)) {
				if (parameters != null && parameters.length > 0) {
					Class<?>[] classs = new Class<?>[parameters.length];
					for (int i = 0; i < classs.length; i++) {
						classs[i] = parameters[i].getClass();
					}
					Constructor<T> constructor = clazz.getConstructor(classs);
					objetc = constructor.newInstance(parameters);
				} else {
					objetc = clazz.newInstance();
				}

			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return objetc;
	}

	public static String getMethodKey(Method method) {
		StringBuilder sb = new StringBuilder(method.getReturnType().getName());
		sb.append(" ");
		sb.append(method.getName());
		sb.append("(");
		Class<?>[] parameterTypes = method.getParameterTypes();
		for (int i = 0; i < parameterTypes.length; i++) {
			sb.append(parameterTypes[i].getName());
			sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}

	public static boolean isJavaType(Class<?> classInfo) {
		boolean isJavaType = false;
		isJavaType = classInfo == Object.class || classInfo == Object[].class ? true : isJavaType;
		isJavaType = classInfo == Byte.class || classInfo == Byte[].class ? true : isJavaType;
		isJavaType = classInfo == byte.class || classInfo == byte[].class ? true : isJavaType;
		isJavaType = classInfo == Short.class || classInfo == Short[].class ? true : isJavaType;
		isJavaType = classInfo == short.class || classInfo == short[].class ? true : isJavaType;
		isJavaType = classInfo == Integer.class || classInfo == Integer[].class ? true : isJavaType;
		isJavaType = classInfo == int.class || classInfo == int[].class ? true : isJavaType;
		isJavaType = classInfo == Long.class || classInfo == Long[].class ? true : isJavaType;
		isJavaType = classInfo == long.class || classInfo == long[].class ? true : isJavaType;
		isJavaType = classInfo == Float.class || classInfo == Float[].class ? true : isJavaType;
		isJavaType = classInfo == float.class || classInfo == float[].class ? true : isJavaType;
		isJavaType = classInfo == Double.class || classInfo == Double[].class ? true : isJavaType;
		isJavaType = classInfo == double.class || classInfo == double[].class ? true : isJavaType;
		isJavaType = classInfo == String.class || classInfo == String[].class ? true : isJavaType;
		return isJavaType;
	}

	public static Class<?> getSingleGenerics(Object object) {
		Class<?>[] clazzs = getGenerics(object);
		if (clazzs != null && clazzs.length > 0) {
			return clazzs[0];
		}
		return null;
	}

	public static Class<?>[] getGenerics(Object object) {
		Type[] types = object.getClass().getGenericInterfaces();
		if (types != null && types.length > 0) {
			Type type = types[0];
			if (type instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) type;
				types = pt.getActualTypeArguments();
				Class<?>[] clazzs = new Class<?>[types.length];
				for (int i = 0; i < types.length; i++) {
					if (types[i] instanceof Class<?>) {
						clazzs[i] = (Class<?>) types[i];
					} else {
						clazzs[i] = types[i].getClass();
					}
				}
				return clazzs;
			}
		}
		return null;
	}

	public static Annotation getAnnotation(Annotation[] annotations, Class<?> annotationClass) {
		for (int i = 0; i < annotations.length; i++) {
			if (annotations[i].annotationType() == annotationClass) {
				return annotations[i];
			}
		}
		return null;
	}

	public static Set<Class<?>> getClassSet(String[] paths) {
		Set<Class<?>> classSet = new HashSet<>();
		for (int i = 0; i < paths.length; i++) {
			List<String> tmpList = getClassListInPackage(paths[i]);
			for (int t = 0; t < tmpList.size(); t++) {
				Class<?> tmpClass = forName(tmpList.get(t));
				if (tmpClass != null) {
					classSet.add(tmpClass);
				}
			}
		}
		return classSet;
	}

	public static List<String> getClassListInPackage(String packageName) {
		String packagePath = packageName.replace('.', '/') + "/";
		List<String> reClassList = new ArrayList<String>();
		Set<String> pathSet = new HashSet<>();
		URL resource = LocalResourcesUtil.class.getClassLoader().getResource(packagePath);
		if (resource != null) {
			String filePath = resource.getPath();
			if (filePath.indexOf("jar!") > -1) {
				filePath = filePath.replaceFirst("!/" + packagePath, "");
				pathSet.add(filePath);
			} else {
				addLocalClass(reClassList, packageName, filePath);
				if (reClassList.size() > 0) {
					return reClassList;
				}
			}
		} else {
			pathSet = getClassOrJarPaths();
		}
		Iterator<String> paths = pathSet.iterator();
		while (paths.hasNext()) {
			String path = paths.next();
			try {
				File classPath;
				if (path.startsWith("file:")) {
					classPath = new File(new URI(path));
				} else {
					classPath = new File(path);
				}
				if (!classPath.exists()) {
					continue;
				}
				if (classPath.isDirectory()) {
					File dir = new File(classPath, packagePath);
					if (!dir.exists()) {
						continue;
					}
					File[] files = dir.listFiles();
					for (int f = 0; f < files.length; f++) {
						if (files[f].isFile()) {
							String clsName = files[f].getName();
							clsName = packageName + "." + clsName.substring(0, clsName.length() - 6);
							reClassList.add(clsName);
						}
					}
				} else {
					FileInputStream fis = new FileInputStream(classPath);
					JarInputStream jis = new JarInputStream(fis, false);
					JarEntry entry = null;
					while ((entry = jis.getNextJarEntry()) != null) {
						String eName = entry.getName();
						if (eName.startsWith(packagePath) && !eName.endsWith("/")) {
							reClassList.add(eName.replace('/', '.').substring(0, eName.length() - 6));
						}
						jis.closeEntry();
					}
					jis.close();
				}
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} catch (URISyntaxException e) {
				logger.error(e.getMessage(), e);
			}
		}
		return reClassList;
	}

	public static Set<String> getClassOrJarPaths() {
		Set<String> pathSet = new HashSet<>();
		URL url = CentralController.class.getClassLoader().getResource("");
		if (url != null) {
			String path = url.getPath();
			if (path.endsWith("/")) {
				path = path.substring(0, path.lastIndexOf("/"));
			}
			path = path.substring(0, path.lastIndexOf("/") + 1);
			addJarPath(pathSet, new File(path));
		}
		String[] paths = new String[] { "java.class.path", "java.ext.dirs", "sun.boot.class.path" };
		String delim = ":";
		if (System.getProperty("os.name").indexOf("Windows") != -1) {
			delim = ";";
		}
		for (int p = 0; p < paths.length; p++) {
			String[] jarPaths = System.getProperty(paths[p]).split(delim);
			for (int i = 0; i < jarPaths.length; i++) {
				pathSet.add(jarPaths[i]);
			}
		}
		return pathSet;
	}

	public static Map<String, Field> getClassAllFields(Class<?> classInfo) {
		Class<?> tmpClass = classInfo;
		if (!tmpClass.isInterface() && !ClassUtil.isJavaType(tmpClass)) {
			Map<String, Field> fieldMap = new HashMap<>();
			do {
				Field[] fields = tmpClass.getDeclaredFields();
				for (int f = 0; f < fields.length; f++) {
					fieldMap.put(fields[f].getName(), fields[f]);
				}
			} while ((tmpClass = tmpClass.getSuperclass()) != Object.class);
			return fieldMap;
		}
		return null;
	}

	private static void addLocalClass(List<String> classList, String packageName, String path) {
		try {
			File file = new File(path);
			if (file.isDirectory()) {
				String[] paths = file.list();
				for (int i = 0; i < paths.length; i++) {
					if (path.endsWith("/")) {
						addLocalClass(classList, packageName + "." + paths[i], path + paths[i]);
					} else {
						addLocalClass(classList, packageName + "." + paths[i], path + "/" + paths[i]);
					}
				}
			} else {
				if (file.getName().endsWith(".class")) {
					if (file.getName().indexOf("$") == -1) {
						if (packageName.endsWith(".class")) {
							packageName = packageName.replaceFirst("." + file.getName(), "");
						}
						classList.add(packageName + "." + file.getName().substring(0, file.getName().length() - 6));
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static void addJarPath(Set<String> set, File file) {
		String[] names = file.list();
		if (names != null) {
			for (int i = 0; i < names.length; i++) {
				File f = new File(StringUtils.unite(file.getPath(), File.separator, names[i]));
				if (f.isFile() && f.getName().endsWith(".jar")) {
					set.add(f.getPath());
				} else {
					addJarPath(set, f);
				}
			}
		}
	}
}