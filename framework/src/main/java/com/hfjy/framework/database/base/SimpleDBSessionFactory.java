package com.hfjy.framework.database.base;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

public class SimpleDBSessionFactory implements DBSessionFactory {
	private static Logger logger = LoggerFactory.getLogger(SimpleDBSessionFactory.class);
	private static final ThreadLocal<Boolean> isAutoCommitMap = new ThreadLocal<>();
	private static final ThreadLocal<Map<DBConnectionPool, DBSession>> threadSession = new ThreadLocal<>();
	private static final Map<DBConnectionPool, DBSession> pureDBSessionMap = new ConcurrentHashMap<>();

	@Override
	public void setDBSessionAutoCommit(boolean isAutoCommit) {
		if (isAutoCommitMap.get() != null) {
			isAutoCommitMap.set(isAutoCommit);
		}
	}

	@Override
	public DBSession getDBSession(DBConnectionPool connectionPool) {
		if (pureDBSessionMap.get(connectionPool) == null) {
			getPureDBSession(connectionPool);
		}
		if (threadSession.get() == null) {
			Map<DBConnectionPool, DBSession> tmpMap = new HashMap<>();
			if (isAutoCommitMap.get() == null) {
				isAutoCommitMap.set(false);
			}
			threadSession.set(tmpMap);
		}
		if (isAutoCommitMap.get()) {
			return getPureDBSession(connectionPool);
		} else {
			if (threadSession.get().get(connectionPool) == null) {
				threadSession.get().put(connectionPool, new SimpleDBSession(connectionPool.getConnection()));
			}
			return threadSession.get().get(connectionPool);
		}
	}

	@Override
	public boolean destroyDBSession(DBConnectionPool connectionPool) {
		Map<DBConnectionPool, DBSession> dbSessionMap = threadSession.get();
		try {
			connectionPool.destroyConnection(dbSessionMap.get(connectionPool).getConnection());
			dbSessionMap.remove(connectionPool);
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		}
		if (dbSessionMap.size() == 0) {
			threadSession.remove();
			return threadSession.get() == null;
		} else {
			return dbSessionMap.get(connectionPool) == null;
		}
	}

	@Override
	public synchronized DBSession getPureDBSession(DBConnectionPool connectionPool) {
		if (pureDBSessionMap.get(connectionPool) == null) {
			pureDBSessionMap.put(connectionPool, new SimpleDBSession(connectionPool.getConnection()));
		}
		pureDBSessionMap.get(connectionPool).setAutoCommit();
		return pureDBSessionMap.get(connectionPool);
	}
}
