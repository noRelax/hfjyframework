package com.hfjy.framework.net.socket;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class BaseSockeServer implements SockeServer {
	private final static Map<Class<?>, BaseSocketHandler> baseSocketHandlerMap = new HashMap<>();
	private final static Map<Class<?>, SocketClient> socketClientMap = new HashMap<>();
	private BaseSocketHandler socketHandler;

	public BaseSockeServer() {
		socketHandler = new BaseSocketHandler();
		baseSocketHandlerMap.put(getSocketSessionClass(), socketHandler);
		socketClientMap.put(getSocketSessionClass(), getSocketClient());
	}

	protected static SocketClient getSocketClient(Class<?> sessionClass) {
		return socketClientMap.get(sessionClass);
	}

	protected static BaseSocketHandler getBaseSocketHandler(Class<?> sessionClass) {
		return baseSocketHandlerMap.get(sessionClass);
	}

	protected static SocketSession getWebSocketSession(String remoteUser) {
		Iterator<BaseSocketHandler> iterator = baseSocketHandlerMap.values().iterator();
		while (iterator.hasNext()) {
			SocketSession tmp = iterator.next().getServerSockets().get(remoteUser);
			if (tmp != null) {
				return tmp;
			}
		}
		return null;
	}

	protected static Set<String> getAllRemoteUser() {
		Set<String> set = new HashSet<>();
		Iterator<BaseSocketHandler> iterator = baseSocketHandlerMap.values().iterator();
		while (iterator.hasNext()) {
			set.addAll(iterator.next().getServerSockets().keySet());
		}
		return set;
	}

	@Override
	public void initControllers(String[] paths) {
		socketHandler.initControllers(paths);
	}

	@Override
	public <T extends DestructionFilter> void addDestructionFilter(Class<T> destructionFilter) throws InstantiationException, IllegalAccessException {
		socketHandler.addDestructionFilter(destructionFilter);
	}
}
