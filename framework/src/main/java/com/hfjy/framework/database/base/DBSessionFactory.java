package com.hfjy.framework.database.base;

public interface DBSessionFactory {

	void setDBSessionAutoCommit(boolean isAutoCommit);

	/**
	 * @Title: createSession
	 * @Description: 构建一个Session对象
	 * @param dataSource
	 * @return session
	 * @since 1.0
	 */
	DBSession getDBSession(DBConnectionPool connectionPool);

	DBSession getPureDBSession(DBConnectionPool connectionPool);

	boolean destroyDBSession(DBConnectionPool connectionPool);
}
