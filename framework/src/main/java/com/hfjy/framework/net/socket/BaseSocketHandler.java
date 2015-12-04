package com.hfjy.framework.net.socket;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.google.gson.JsonObject;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.entity.ControllerMapping;
import com.hfjy.framework.net.transport.ResponseBody;
import com.hfjy.framework.net.util.ControllerUril;

public class BaseSocketHandler {
	private final static Logger logger = LoggerFactory.getLogger(BaseSocketHandler.class);
	private final Map<String, SocketSession> serverSockets = new ConcurrentHashMap<>();
	private final Map<String, ControllerMapping> controllers = new HashMap<String, ControllerMapping>();
	private final List<DestructionFilter> destructionFilterList = new ArrayList<>();

	protected void initControllers(String[] paths) {
		ControllerUril.initControllers(controllers, paths);
	}

	protected <T extends DestructionFilter> void addDestructionFilter(Class<T> destructionFilter) throws InstantiationException, IllegalAccessException {
		destructionFilterList.add((DestructionFilter) destructionFilter.newInstance());
	}

	protected Map<String, SocketSession> getServerSockets() {
		return serverSockets;
	}

	protected void putSocketSession(String remote, SocketSession socketSession) {
		if (!serverSockets.containsValue(socketSession)) {
			serverSockets.put(remote, socketSession);
		}
	}

	protected void removeSocketSession(String remote) {
		for (int i = 0; i < destructionFilterList.size(); i++) {
			try {
				destructionFilterList.get(i).onClose(remote);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		serverSockets.remove(remote);
	}

	protected Object handleController(SocketSession socketSession, DataMessage data) {
		try {
			String name = data.getType();
			String type = data.getCode();
			ControllerMapping cm = controllers.get(name);
			if (cm != null && type != null) {
				Object execObject = cm.getObject();
				Method method = cm.getControllerMethod(type);
				if (method != null) {
					Class<?>[] classInfos = cm.getMethodParameters(method);
					Object[] objects = classInfos == null ? null : new Object[classInfos.length];
					if (classInfos != null && classInfos.length > 0) {
						for (int i = 0; i < classInfos.length; i++) {
							if (classInfos[i] == JsonObject.class) {
								if (data.getData() instanceof JsonObject) {
									objects[i] = data.getData();
								} else {
									objects[i] = data.getData() == null ? null : JsonUtil.toJsonObject(data.getData());
								}
							} else if (classInfos[i] == String.class) {
								objects[i] = data.getData() == null ? null : data.getData().toString();
							} else if (classInfos[i] == SocketSession.class) {
								objects[i] = socketSession;
							} else {
								objects[i] = data.getData() == null ? null : JsonUtil.toObject(JsonUtil.toJsonElement(data.getData()), classInfos[i]);
							}
						}
					}
					long begin = System.currentTimeMillis();
					logger.debug(cm.getClassInfo().getName() + " the " + method.getName() + " method begin ");
					Object reObject = null;
					if (objects == null) {
						reObject = method.invoke(execObject);
					} else {
						reObject = method.invoke(execObject, objects);
					}
					logger.debug(cm.getClassInfo().getName() + " the " + method.getName() + " method end " + " with time " + (System.currentTimeMillis() - begin) + " ms");
					return reObject;
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return ResponseBody.createBody(0, e.getMessage());
		}
		return ResponseBody.createBody(0, "the controller you requested could not be found");
	}
}