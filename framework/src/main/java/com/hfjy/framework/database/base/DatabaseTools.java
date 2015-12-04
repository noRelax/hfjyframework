package com.hfjy.framework.database.base;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.LocalResourcesUtil;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.logging.LoggerFactory;

public class DatabaseTools {
	private static Logger logger = LoggerFactory.getLogger(SimpleDBSession.class);
	private final static Map<String, DBConnectionInfo> dbinfos = new HashMap<>();
	private final static Map<String, DBConnectionPool> connectionPools = new HashMap<>();
	private final static DBSessionFactory sessionFactory;

	static {
		Properties dbs = LocalResourcesUtil.getProperties(Initial.DB_CONFIG_FILE);
		Iterator<Object> dbsKeys = dbs.keySet().iterator();
		while (dbsKeys.hasNext()) {
			try {
				String key = dbsKeys.next().toString().trim();
				String value = dbs.getProperty(key).trim();
				Properties dbConfig = LocalResourcesUtil.getProperties(Initial.DB_CONFIG_PATH + value);
				DBConnectionInfo dbinfo = new DBConnectionInfo(key, dbConfig);
				dbinfos.put(key, dbinfo);
				dbinfos.put(value, dbinfo);
				DBConnectionPool cp = ClassUtil.newInstance(Initial.SYSTEM_ACHIEVE_DB_CONNECTION_POOL, dbinfo);
				connectionPools.put(key, cp);
				connectionPools.put(value, cp);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		sessionFactory = ClassUtil.newInstance(Initial.SYSTEM_ACHIEVE_DB_SESSION_FACTORY);
	}

	public static void setMySessionAutoCommit(boolean isAutoCommit) {
		sessionFactory.setDBSessionAutoCommit(isAutoCommit);
	}

	public static DBSession getPureDBSession() {
		return getPureDBSession(Initial.DB_CONFIG_DEFAULT_KEY);
	}

	public static DBSession getPureDBSession(String dbName) {
		return sessionFactory.getPureDBSession(connectionPools.get(dbName));
	}

	public static DBSession getDBSession() {
		return getDBSession(Initial.DB_CONFIG_DEFAULT_KEY);
	}

	public static DBSession getDBSession(String dbName) {
		return sessionFactory.getDBSession(connectionPools.get(dbName));
	}

	public static boolean destroyDBSession() {
		return destroyDBSession(Initial.DB_CONFIG_DEFAULT_KEY);
	}

	public static boolean destroyDBSession(String dbName) {
		return sessionFactory.destroyDBSession(connectionPools.get(dbName));
	}

	public static DBAccess getDBAccess() {
		return getDBAccess(Initial.DB_CONFIG_DEFAULT_KEY);
	}

	public static DBAccess getDBAccess(String dbName) {
		return new SimpleDBAccess(dbinfos.get(dbName));
	}
}