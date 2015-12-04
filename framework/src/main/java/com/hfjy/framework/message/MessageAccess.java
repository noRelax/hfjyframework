package com.hfjy.framework.message;

import org.slf4j.Logger;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.logging.LoggerFactory;

public interface MessageAccess<T> {
	final Logger log = LoggerFactory.getLogger(MessageProcess.class);

	AccessResult<?> send(T message, String code) throws Exception;

	AccessResult<?> send(T message, String ip, int port) throws Exception;
}
