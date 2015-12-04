package com.hfjy.framework.net.socket;

import java.io.IOException;

public interface SocketSession {

	void sendMessage(String data) throws IOException;

	void sendMessage(Object data) throws IOException;

	void sendMessage(byte[] data, int offset, int length) throws IOException;

	void close();

	String getRemoteUser();

	String getLocalServerUrl();

	void put(Object key, Object value);

	Object get(Object key);
}
