package com.hfjy.framework.net.socket;

public interface SockeServer {

	Class<?> getSocketSessionClass();

	SocketClient getSocketClient();

	void start() throws Exception;

	void initControllers(String[] paths);

	<T extends DestructionFilter> void addDestructionFilter(Class<T> destructionFilter) throws InstantiationException, IllegalAccessException;
}
