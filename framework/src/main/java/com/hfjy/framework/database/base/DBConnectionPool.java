package com.hfjy.framework.database.base;

import java.sql.Connection;

import javax.sql.DataSource;

public interface DBConnectionPool {

	Connection getConnection();

	DataSource getDataSource();

	boolean destroyConnection(Connection conn);
}
