package com.hfjy.framework.logging;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import com.hfjy.framework.init.Initial;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;

public class LoggerFactory {
	private static final Logger logger = LoggerFactory.getLogger(LoggerFactory.class);

	static {
		try {
			LoggerContext loggerContext = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
			loggerContext.reset();
			JoranConfigurator joranConfigurator = new JoranConfigurator();
			joranConfigurator.setContext(loggerContext);
			joranConfigurator.doConfigure(Initial.LOG_CONFIG_FILE);
			logger.debug("loaded slf4j configure file ok!");
		} catch (JoranException e) {
			logger.error("can loading slf4j configure file error", e);
		}
	}

	public static Logger getLogger(String name) {
		return new LoggerProcessor(org.slf4j.LoggerFactory.getLogger(name));
	}

	/**
	 * Return a logger named corresponding to the class passed as parameter,
	 * using the statically bound {@link ILoggerFactory} instance.
	 * 
	 * @param clazz
	 *            the returned logger will be named after clazz
	 * @return logger
	 */
	public static Logger getLogger(Class<?> clazz) {
		return new LoggerProcessor(org.slf4j.LoggerFactory.getLogger(clazz));
	}

	/**
	 * Return the {@link ILoggerFactory} instance in use.
	 * <p/>
	 * <p/>
	 * ILoggerFactory instance is bound with this class at compile time.
	 * 
	 * @return the ILoggerFactory instance in use
	 */
	public static ILoggerFactory getILoggerFactory() {
		return org.slf4j.LoggerFactory.getILoggerFactory();
	}
}
