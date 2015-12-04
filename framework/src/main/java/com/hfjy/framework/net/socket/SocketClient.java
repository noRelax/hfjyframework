package com.hfjy.framework.net.socket;

public interface SocketClient {

	SocketSession getSocketSession(String uri) throws Exception;

	void close() throws Exception;
}
