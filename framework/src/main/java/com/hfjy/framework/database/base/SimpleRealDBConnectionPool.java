package com.hfjy.framework.database.base;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.slf4j.Logger;

import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.logging.LoggerFactory;

public class SimpleRealDBConnectionPool implements DBConnectionPool {
	private static Logger logger = LoggerFactory.getLogger(SimpleRealDBConnectionPool.class);
	private final DBConnectionInfo dbInfo;
	private Lock poolLock = new ReentrantLock();
	private Lock connectionLock = new ReentrantLock();
	private Queue<Connection> connectionQueue;

	public SimpleRealDBConnectionPool(DBConnectionInfo dbInfo) {
		this.dbInfo = dbInfo;
		try {
			createPool();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	public void createPool() {
		if (connectionQueue != null) {
			return;
		}
		try {
			poolLock.lock();
			Driver driver = (Driver) (Class.forName(this.dbInfo.getDriveClass()).newInstance());
			DriverManager.registerDriver(driver);
			connectionQueue = new ConcurrentLinkedQueue<>();
			createConnections(dbInfo.getMinConnectionNum());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			poolLock.unlock();
		}
	}

	public int getConnectionNum() {
		if (connectionQueue == null) {
			return 0;
		}
		return this.connectionQueue.size();
	}

	private void createConnections(long numConnections) {
		try {
			connectionLock.lock();
			for (int i = 0; i < numConnections; i++) {
				if (this.dbInfo.getMaxConnectionNum() > 0 && this.connectionQueue.size() >= this.dbInfo.getMaxConnectionNum()) {
					break;
				}
				Connection newConnection = newConnection();
				connectionQueue.offer(newConnection);
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
		if (connectionQueue.size() == 0) {
			DatabaseMetaData metaData = conn.getMetaData();
			int driverMaxConnections = metaData.getMaxConnections();
			if (driverMaxConnections > 0 && this.dbInfo.getMaxConnectionNum() > driverMaxConnections) {
				dbInfo.setMaxConnectionNum(driverMaxConnections);
			}
		}
		return conn;
	}

	public Connection getSingleConnection() throws SQLException {
		if (connectionQueue == null) {
			return null;
		}
		Connection conn = findFreeConnection();
		while (conn == null) {
			wait(250);
			conn = findFreeConnection();
		}
		return conn;
	}

	private Connection findFreeConnection() throws SQLException {
		if (connectionQueue.isEmpty()) {
			createConnections(dbInfo.getMinConnectionNum());
		}
		Connection conn = connectionQueue.poll();
		if (!testConnection(conn)) {
			conn = null;
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
				return true;
			} else {
				return conn.isValid(0);
			}
		} catch (SQLException e) {
			closeConnection(conn);
			return false;
		}
	}

	public void returnConnection(Connection conn) throws SQLException {
		if (connectionQueue == null) {
			return;
		}
		if (testConnection(conn)) {
			connectionQueue.offer(conn);
		} else {
			connectionQueue.offer(newConnection());
		}
	}

	public void refreshConnectionPool() throws SQLException {
		poolLock.lock();
		if (connectionQueue == null) {
			return;
		}
		Iterator<Connection> iterator = connectionQueue.iterator();
		while (iterator.hasNext()) {
			Connection conn = iterator.next();
			if (!testConnection(conn)) {
				connectionQueue.remove(conn);
				connectionQueue.offer(newConnection());
			}
		}
		poolLock.unlock();
	}

	public void closeConnectionPool() {
		poolLock.lock();
		if (connectionQueue == null) {
			return;
		}
		while (connectionQueue.size() > 0) {
			Connection conn = connectionQueue.poll();
			if (testConnection(conn)) {
				closeConnection(conn);
			}
		}
		connectionQueue = null;
		poolLock.unlock();
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