package com.hfjy.framework.net.socket;

import java.io.IOException;
import java.util.Iterator;

import com.hfjy.framework.biz.BaseController;

public class BaseSocketController extends BaseController {

	protected static SocketSession getWebSocketSession(String remote) {
		return BaseSockeServer.getWebSocketSession(remote);
	}

	protected static void sendMessageRemote(String remote, Object data) throws IOException {
		SocketSession wss = BaseSockeServer.getWebSocketSession(remote);
		if (wss != null) {
			wss.sendMessage(data);
		}
	}

	protected static void sendMessageToAll(Object data) throws IOException {
		Iterator<String> iterator = BaseSockeServer.getAllRemoteUser().iterator();
		while (iterator.hasNext()) {
			sendMessageRemote(iterator.next(), data);
		}
	}

	protected static void sendMessageToAllNotOwn(SocketSession session, Object data) throws IOException {
		Iterator<String> iterator = BaseSockeServer.getAllRemoteUser().iterator();
		while (iterator.hasNext()) {
			String remote = iterator.next();
			if (!remote.equals(session.getRemoteUser())) {
				sendMessageRemote(remote, data);
			}
		}
	}

	protected static void sendMessageServer(SocketSession session, String uri, DataMessage data) throws Exception {
		SocketSession newSession = BaseSockeServer.getWebSocketSession(uri);
		if (newSession == null) {
			SocketClient sc = BaseSockeServer.getSocketClient(session.getClass());
			newSession = sc.getSocketSession(uri);
		}
		newSession.sendMessage(data);
	}
}