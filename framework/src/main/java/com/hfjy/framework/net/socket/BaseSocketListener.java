package com.hfjy.framework.net.socket;

import org.slf4j.Logger;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.logging.LoggerFactory;

public class BaseSocketListener {
	private final static Logger logger = LoggerFactory.getLogger(BaseSocketListener.class);

	protected void addSocketSession(SocketSession socketSession) {
		logger.debug("addSocketSession:" + socketSession.getRemoteUser());
		BaseSocketHandler socketHandler = BaseSockeServer.getBaseSocketHandler(socketSession.getClass());
		if (socketHandler != null) {
			socketHandler.putSocketSession(socketSession.getRemoteUser(), socketSession);
		}
	}

	protected void removeSocketSession(SocketSession socketSession) {
		BaseSocketHandler socketHandler = BaseSockeServer.getBaseSocketHandler(socketSession.getClass());
		if (socketHandler != null) {
			socketHandler.removeSocketSession(socketSession.getRemoteUser());
		}
	}

	protected void handleTextMessage(SocketSession socketSession, DataMessage message) {
		logger.debug("handleTextMessage:" + JsonUtil.toJson(message));
		BaseSocketHandler socketHandler = BaseSockeServer.getBaseSocketHandler(socketSession.getClass());
		if (socketHandler != null) {
			try {
				SocketSession session = socketHandler.getServerSockets().get(socketSession.getRemoteUser());
				Object reObj = socketHandler.handleController(session, message);
				if (reObj != null) {
					if (reObj instanceof String) {
						session.sendMessage(reObj.toString());
					} else if (reObj instanceof AccessResult) {
						AccessResult<?> ar = (AccessResult<?>) reObj;
						if (StringUtils.isEmpty(ar.getType())) {
							ar.setType(message.getType());
						}
						if (StringUtils.isEmpty(ar.getCode())) {
							ar.setCode(message.getCode());
						}
						session.sendMessage(ar);
					} else {
						session.sendMessage(reObj);
					}
				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

	protected void handleBinaryMessage(SocketSession socketSession, byte[] data, int offset, int length) {

	}

	protected boolean execHeartbeat(SocketSession session, String message) {
		try {
			if (message != null && message.equals("ping")) {
				session.sendMessage("pong");
				return true;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}
}