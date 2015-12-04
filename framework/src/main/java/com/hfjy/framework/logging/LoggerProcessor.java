package com.hfjy.framework.logging;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.Marker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.hfjy.framework.common.util.HttpUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.nosql.DataAccess;
import com.hfjy.framework.database.nosql.DataAccessTools;

public class LoggerProcessor implements Logger {
	private final Logger logger;
	private static final Gson gson = new Gson();

	private static int logInitStatus = -1;

	public LoggerProcessor(Logger logger) {
		this.logger = logger;
	}

	@Override
	public String getName() {
		return logger.getName();
	}

	@Override
	public boolean isTraceEnabled() {
		return logger.isTraceEnabled();
	}

	@Override
	public boolean isTraceEnabled(Marker marker) {
		return logger.isTraceEnabled(marker);
	}

	@Override
	public boolean isDebugEnabled() {
		return logger.isDebugEnabled();
	}

	@Override
	public boolean isDebugEnabled(Marker marker) {
		return logger.isDebugEnabled(marker);
	}

	@Override
	public boolean isInfoEnabled() {
		return logger.isInfoEnabled();
	}

	@Override
	public boolean isInfoEnabled(Marker marker) {
		return logger.isInfoEnabled(marker);
	}

	@Override
	public boolean isWarnEnabled() {
		return logger.isWarnEnabled();
	}

	@Override
	public boolean isWarnEnabled(Marker marker) {
		return logger.isWarnEnabled(marker);
	}

	@Override
	public boolean isErrorEnabled() {
		return logger.isErrorEnabled();
	}

	@Override
	public boolean isErrorEnabled(Marker marker) {
		return logger.isErrorEnabled(marker);
	}

	@Override
	public void trace(String msg) {
		action(msg, "trace");
		logger.trace(msg);
	}

	@Override
	public void trace(String format, Object arg) {
		action(format, arg, "trace");
		logger.trace(format, arg);
	}

	@Override
	public void trace(String format, Object arg1, Object arg2) {
		action(format, arg1, arg2, "trace");
		logger.trace(format, arg1, arg2);
	}

	@Override
	public void trace(String format, Object... argArray) {
		action(format, argArray, "trace");
		logger.trace(format, argArray);
	}

	@Override
	public void trace(String msg, Throwable t) {
		action(msg, t, "trace");
		logger.trace(msg, t);
	}

	@Override
	public void trace(Marker marker, String msg) {
		action(marker, msg, "trace");
		logger.trace(marker, msg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg) {
		action(marker, format, arg, "trace");
		logger.trace(marker, format, arg);
	}

	@Override
	public void trace(Marker marker, String format, Object arg1, Object arg2) {
		action(marker, format, arg1, arg2, "trace");
		logger.trace(marker, format, arg1, arg2);
	}

	@Override
	public void trace(Marker marker, String format, Object... argArray) {
		action(marker, format, argArray, "trace");
		logger.trace(marker, format, argArray);
	}

	@Override
	public void trace(Marker marker, String msg, Throwable t) {
		action(marker, msg, t, "trace");
		logger.trace(marker, msg, t);
	}

	@Override
	public void debug(String msg) {
		action(msg, "debug");
		logger.debug(msg);
	}

	@Override
	public void debug(String format, Object arg) {
		action(format, arg, "debug");
		logger.debug(format, arg);
	}

	@Override
	public void debug(String format, Object arg1, Object arg2) {
		action(format, arg1, arg2, "debug");
		logger.debug(format, arg1, arg2);
	}

	@Override
	public void debug(String format, Object... argArray) {
		action(format, argArray, "debug");
		logger.debug(format, argArray);
	}

	@Override
	public void debug(String msg, Throwable t) {
		action(msg, t, "debug");
		logger.debug(msg, t);
	}

	@Override
	public void debug(Marker marker, String msg) {
		action(marker, msg, "debug");
		logger.debug(marker, msg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg) {
		action(marker, format, arg, "debug");
		logger.debug(marker, format, arg);
	}

	@Override
	public void debug(Marker marker, String format, Object arg1, Object arg2) {
		action(marker, format, arg1, arg2, "debug");
		logger.debug(marker, format, arg1, arg2);
	}

	@Override
	public void debug(Marker marker, String format, Object... argArray) {
		action(marker, format, argArray, "debug");
		logger.debug(marker, format, argArray);
	}

	@Override
	public void debug(Marker marker, String msg, Throwable t) {
		action(marker, msg, t, "debug");
		logger.debug(marker, msg, t);
	}

	@Override
	public void info(String msg) {
		action(msg, "info");
		logger.info(msg);
	}

	@Override
	public void info(String format, Object arg) {
		action(format, arg, "info");
		logger.info(format, arg);
	}

	@Override
	public void info(String format, Object arg1, Object arg2) {
		action(format, arg1, arg2, "info");
		logger.info(format, arg1, arg2);
	}

	@Override
	public void info(String format, Object... argArray) {
		action(format, argArray, "info");
		logger.info(format, argArray);
	}

	@Override
	public void info(String msg, Throwable t) {
		action(msg, t, "info");
		logger.info(msg, t);
	}

	@Override
	public void info(Marker marker, String msg) {
		action(marker, msg, "info");
		logger.info(marker, msg);
	}

	@Override
	public void info(Marker marker, String format, Object arg) {
		action(marker, format, arg, "info");
		logger.info(marker, format, arg);
	}

	@Override
	public void info(Marker marker, String format, Object arg1, Object arg2) {
		action(marker, format, arg1, arg2, "info");
		logger.info(marker, format, arg1, arg2);
	}

	@Override
	public void info(Marker marker, String format, Object... argArray) {
		action(marker, format, argArray, "info");
		logger.info(marker, format, argArray);
	}

	@Override
	public void info(Marker marker, String msg, Throwable t) {
		action(marker, msg, t, "info");
		logger.info(marker, msg, t);
	}

	@Override
	public void warn(String msg) {
		action(msg, "warn");
		logger.warn(msg);
	}

	@Override
	public void warn(String format, Object arg) {
		action(format, arg, "warn");
		logger.warn(format, arg);
	}

	@Override
	public void warn(String format, Object... argArray) {
		action(format, argArray, "warn");
		logger.warn(format, argArray);
	}

	@Override
	public void warn(String format, Object arg1, Object arg2) {
		action(format, arg1, arg2, "warn");
		logger.warn(format, arg1, arg2);
	}

	@Override
	public void warn(String msg, Throwable t) {
		action(msg, t, "warn");
		logger.warn(msg, t);
	}

	@Override
	public void warn(Marker marker, String msg) {
		action(marker, msg, "warn");
		logger.warn(marker, msg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg) {
		action(marker, format, arg, "warn");
		logger.warn(marker, format, arg);
	}

	@Override
	public void warn(Marker marker, String format, Object arg1, Object arg2) {
		action(marker, format, arg1, arg2, "warn");
		logger.warn(marker, format, arg1, arg2);
	}

	@Override
	public void warn(Marker marker, String format, Object... argArray) {
		action(marker, format, argArray, "warn");
		logger.warn(marker, format, argArray);
	}

	@Override
	public void warn(Marker marker, String msg, Throwable t) {
		action(marker, msg, t, "warn");
		logger.warn(marker, msg, t);
	}

	@Override
	public void error(String msg) {
		action(msg, "error");
		logger.error(msg);
	}

	@Override
	public void error(String format, Object arg) {
		action(format, arg, "error");
		logger.error(format, arg);
	}

	@Override
	public void error(String format, Object arg1, Object arg2) {
		action(format, arg1, arg2, "error");
		logger.error(format, arg1, arg2);
	}

	@Override
	public void error(String format, Object... argArray) {
		action(format, argArray, "error");
		logger.error(format, argArray);
	}

	@Override
	public void error(String msg, Throwable t) {
		action(msg, t, "error");
		logger.error(msg, t);
	}

	@Override
	public void error(Marker marker, String msg) {
		action(marker, msg, "error");
		logger.error(marker, msg);
	}

	@Override
	public void error(Marker marker, String format, Object arg) {
		action(marker, format, arg, "error");
		logger.error(marker, format, arg);
	}

	@Override
	public void error(Marker marker, String format, Object arg1, Object arg2) {
		action(marker, format, arg1, arg2, "error");
		logger.error(marker, format, arg1, arg2);
	}

	@Override
	public void error(Marker marker, String format, Object... argArray) {
		action(marker, format, argArray, "error");
		logger.error(marker, format, argArray);
	}

	@Override
	public void error(Marker marker, String msg, Throwable t) {
		action(marker, msg, t, "error");
		logger.error(marker, msg, t);
	}

	private void action(String msg, String type) {
		record(type, msg);
	}

	private void action(Marker marker, String msg, String type) {
		record(type, marker, msg);
	}

	private void action(String format, Object arg, String type) {
		record(type, format, arg);
	}

	private void action(String format, Object arg1, Object arg2, String type) {
		record(type, format, arg1, arg2);
	}

	private void action(Marker marker, String format, Object arg, String type) {
		record(type, marker, format, arg);
	}

	private void action(Marker marker, String format, Object arg1, Object arg2, String type) {
		record(type, marker, format, arg1, arg2);
	}

	private void record(String type, Object... message) {
		if (logInitStatus > -1) {
			JsonObject messageJson = new JsonObject();
			messageJson.add("DateTime", gson.toJsonTree(new Date()));
			messageJson.addProperty("type", type);
			messageJson.addProperty("Class", logger.getName());
			if (message != null) {
				for (int i = 0; i < message.length; i++) {
					messageJson.add("msg" + i, gson.toJsonTree(message[i]));
				}
			}
			saveLog(messageJson, "AllLog");
			saveLog(messageJson, logger.getName());
		}
	}

	private void saveLog(JsonObject messageJson, String table) {
		if (logInitStatus == 0) {
			logInitStatus = -1;
		}
		String dbName = HttpUtil.getLocalAddress();
		dbName = StringUtils.unite(dbName, "logs");
		dbName = dbName.replaceAll("[(]", "-").replaceAll("[)]", "-").replaceAll("[.]", "_");
		DataAccess dataAccess = DataAccessTools.getDataAccess(dbName, table);
		if (dataAccess != null) {
			dataAccess.save(messageJson);
			if (logInitStatus == -1) {
				logInitStatus = 1;
			}
		}
	}
}
