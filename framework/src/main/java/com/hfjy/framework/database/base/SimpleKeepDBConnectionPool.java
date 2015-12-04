package com.hfjy.framework.database.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.logging.LoggerFactory;

public class SimpleKeepDBConnectionPool implements DBConnectionPool {
	private static Logger logger = LoggerFactory.getLogger(SimpleKeepDBConnectionPool.class);
	private final DBConnectionInfo dbInfo;
	private Lock poolLock = new ReentrantLock();
	private Lock connectionLock = new ReentrantLock();
	private Map<Connection, Boolean> connectionMap;

	public SimpleKeepDBConnectionPool(DBConnectionInfo dbInfo) {
		this.dbInfo = dbInfo;
		try {
			createPool();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void createPool() throws Exception {
		if (connectionMap != null) {
			return;
		}
		try {
			poolLock.lock();
			Driver driver = (Driver) (Class.forName(this.dbInfo.getDriveClass()).newInstance());
			DriverManager.registerDriver(driver);
			connectionMap = new ConcurrentHashMap<>();
			createConnections(dbInfo.getMinConnectionNum());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			poolLock.unlock();
		}
	}

	public int getConnectionNum() {
		if (connectionMap == null) {
			return 0;
		}
		return this.connectionMap.size();
	}

	private void createConnections(long numConnections) {
		try {
			connectionLock.lock();
			for (int i = 0; i < numConnections; i++) {
				if (this.dbInfo.getMaxConnectionNum() > 0 && this.connectionMap.size() >= this.dbInfo.getMaxConnectionNum()) {
					break;
				}
				Connection newConnection = newConnection();
				connectionMap.put(newConnection, false);
			}
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			connectionLock.unlock();
		}

	}

	private Connection newConnection() throws SQLException {
		Connection conn = null;
		if (dbInfo.getDbUsername() == null || dbInfo.getDbUsername().length() < 1) {
			conn = DriverManager.getConnection(dbInfo.getDbConnectionString());
		} else {
			conn = DriverManager.getConnection(dbInfo.getDbConnectionString(), dbInfo.getDbUsername(), dbInfo.getDbPassword());
		}
		if (connectionMap.size() == 0) {
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			if (driverMaxConnections > 0 && this.dbInfo.getMaxConnectionNum() > driverMaxConnections) {
				dbInfo.setMaxConnectionNum(driverMaxConnections);
			}
		}
		return conn;
	}

	public Connection getSingleConnection() throws SQLException {
		if (connectionMap == null) {
			return null;
		}
		Connection conn = getFreeConnection();
		while (conn == null) {
			wait(250);
			conn = getFreeConnection();
		}
		return conn;
	}

	private Connection getFreeConnection() throws SQLException {
		Connection conn = findFreeConnection();
		if (conn == null) {
			createConnections(5);
			conn = findFreeConnection();
			if (conn == null) {
				return null;
			}
		}
		return conn;
	}

	private Connection findFreeConnection() {
		Connection conn = null;
		try {
			connectionLock.lock();
			Iterator<Connection> iterator = connectionMap.keySet().iterator();
			while (iterator.hasNext()) {
				conn = iterator.next();
				if (!connectionMap.get(conn)) {
					if (!testConnection(conn)) {
						conn = newConnection();
					}
					connectionMap.put(conn, true);
					break;
				} else {
					conn = null;
				}
			}
		} catch (SQLException e) {
			logger.error("Connection Error", e);
			conn = null;
		} finally {
			connectionLock.unlock();
		}
		return conn;
	}

	private boolean testConnection(Connection conn) {
		try {
			if (conn == null || conn.isClosed()) {
				return false;
			}
			if (dbInfo.getDbTestSQL() != null && dbInfo.getDbTestSQL().length() > 0) {
				conn.setAutoCommit(true);
				Statement stmt = conn.createStatement();
				stmt.execute(dbInfo.getDbTestSQL());
				stmt.close();
			} else {
				return conn.isValid(0);
			}
		} catch (SQLException e) {
			closeConnection(conn);
			return false;
		}
		return true;
	}

	public void returnConnection(Connection conn) {
		if (connectionMap == null) {
			return;
		}
		if (testConnection(conn)) {
			connectionMap.put(conn, false);
		} else {
			createConnections(1);
		}
	}

	public void refreshConnectionPool() throws SQLException {
		if (connectionMap == null) {
			return;
		}
		try {
			poolLock.lock();
			Iterator<Connection> iterator = connectionMap.keySet().iterator();
			while (iterator.hasNext()) {
				closeConnection(iterator.next());
			}
			createConnections(dbInfo.getMinConnectionNum());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			poolLock.unlock();
		}
	}

	public void closeConnectionPool() {
		if (connectionMap == null) {
			return;
		}
		try {
			poolLock.lock();
			Iterator<Connection> iterator = connectionMap.keySet().iterator();
			while (iterator.hasNext()) {
				closeConnection(iterator.next());
			}
			connectionMap = null;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			poolLock.unlock();
		}
	}

	private void closeConnection(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("Close Connection Error", e);
		}
	}

	private void wait(int mSeconds) {
		try {
			Thread.sleep(mSeconds);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public DataSource getDataSource() {
		return null;
	}

	@Override
	public Connection getConnection() {
		Connection Connection = null;
		try {
			Connection = getSingleConnection();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Connection = null;
		}
		return Connection;
	}

	@Override
	public boolean destroyConnection(Connection conn) {
		try {
			returnConnection(conn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
		return true;
	}
}