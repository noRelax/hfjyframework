package com.hfjy.framework.net.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.net.http.annotation.Path;
import com.hfjy.framework.net.http.entity.ControllerMapping;
import com.hfjy.framework.net.socket.annotation.Type;

public class ControllerUril {
	public static void initControllers(Map<String, ControllerMapping> controllers, String[] paths) {
		if (paths == null || paths.length == 0) {
			return;
		}
		Set<Class<?>> classSet = ClassUtil.getClassSet(paths);
		if (classSet != null && classSet.size() > 0) {
			Iterator<Class<?>> iterator = classSet.iterator();
			while (iterator.hasNext()) {
				Class<?> controller = iterator.next();
				if (controller.getName().indexOf("$") > -1) {
					continue;
				}
				String controllerPath = null;
				if (controller.isAnnotationPresent(Path.class)) {
					controllerPath = controller.getAnnotation(Path.class).value();
				} else if (controller.isAnnotationPresent(Type.class)) {
					controllerPath = controller.getAnnotation(Type.class).value();
				} else {
					controllerPath = controller.getSimpleName().toLowerCase();
				}
				controllerPath = controllerPath.replaceAll("/", "");
				ControllerMapping controllerMapping = new ControllerMapping(controller);
				controllers.put(controllerPath, controllerMapping);
			}
		}
	}
}