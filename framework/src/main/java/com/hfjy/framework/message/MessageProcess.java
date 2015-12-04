package com.hfjy.framework.message;

import org.slf4j.Logger;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.logging.LoggerFactory;

public interface MessageProcess {
	final Logger log = LoggerFactory.getLogger(MessageProcess.class);

	String getName();

	AccessResult<Object> process(byte[] message);
}
