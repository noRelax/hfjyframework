/**
 * 海风在线学习平台
 * @Title: Session.java 
 * @Package: com.hyphen.framework.db
 * @author: cloud
 * @date: 2014年5月4日-下午2:47:38
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.database.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

/**
 * @ClassName: Session
 * @Description: 数据库会话类
 * @author cloud
 * @date 2014年5月5日 下午2:47:38
 * 
 */
public class SimpleDBSession implements DBSession {

	private static final Logger logger = LoggerFactory.getLogger(SimpleDBSession.class);

	private Connection dbConnection = null;

	private PreparedStatement pstm = null;

	private ResultSet rs = null;

	private CallableStatement call = null;

	private boolean autoCommit = false;

	public SimpleDBSession(Connection connection) {
		dbConnection = connection;
	}

	/**
	 * 
	 * @Title: isClose
	 * @Description: 连接是否关闭
	 * @return boolean
	 * @throws SQLException
	 * @since 1.0
	 */
	public boolean isClosed() throws SQLException {
		if (dbConnection == null || dbConnection.isClosed())
			return true;
		else
			return false;
	}

	/**
	 * @Title getConnection
	 * @Description 创建数据库连接(默认手动提交事务)
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException {
		clear();
		if (!isClosed()) {
			if (dbConnection.getAutoCommit() != autoCommit) {
				dbConnection.setAutoCommit(autoCommit);
			}
		}
		return dbConnection;
	}

	/**
	 * @title closeResultSet
	 * @Description 关闭结果集
	 * @param result
	 *            要关闭的结果集对象
	 */
	public void closeResultSet() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (Exception e) {
			logger.error("Close the ResultSet error···", e);
		}
	}

	/**
	 * @title closeCallStatement
	 * @Description 关闭CallableStatement
	 */
	public void closeCallStatement() {
		try {
			if (call != null) {
				call.close();
				call = null;
			}
		} catch (Exception e) {
			logger.error("Close the CallableStatement error···", e);
		}
	}

	/**
	 * @title closePreparedStatement
	 * @Description 关闭PreparedStatement对象
	 */
	public void closePreparedStatement() {
		try {
			if (pstm != null) {
				pstm.close();
				pstm = null;
			}
		} catch (Exception e) {
			logger.error("Close the PreparedStatement error···", e);
		}
	}

	/**
	 * @title setAutoCommit
	 * @description 更改当前conn事务为自动提交（谨慎使用）
	 */
	public void setAutoCommit() {
		autoCommit = true;
	}

	/**
	 * @title setAutoCommit
	 * @description 更改当前conn事务为手动提交（谨慎使用）
	 */
	public void setManCommit() {
		autoCommit = false;
	}

	/**
	 * @title commit
	 * @description 提交事务
	 * @throws Exception
	 */
	public void commit() {
		try {
			if (!isClosed()) {
				dbConnection.commit();
			}
		} catch (SQLException e) {
			logger.error("Database error···", e);
		}
	}

	/**
	 * @title rollback
	 * @description 回滚事务
	 */
	public void rollback() {
		try {
			if (!isClosed()) {
				dbConnection.rollback();
			}
		} catch (SQLException e) {
			logger.error("Database error···", e);
		}
	}

	/**
	 * @title clear
	 * @description 释放资源
	 */
	public void clear() {
		try {
			closeResultSet();
			closePreparedStatement();
			closeCallStatement();
		} catch (Exception e) {
			logger.error("Clear the conneticon is error···", e);
		}
	}

	public boolean execute(String sql, Object[] params) throws SQLException {
		showSql(sql, params);
		pstm = getConnection().prepareStatement(sql);
		setParams(pstm, params);
		return pstm.execute();
	}

	/**
	 * @Title createPreparedStatement
	 * @Description 创建PreparedStatement对象
	 * @param sql
	 * @param params
	 * @return pstm
	 * @throws SQLException
	 */
	public int executeUpdate(String sql, Object[] params) throws SQLException {
		showSql(sql, params);
		pstm = getConnection().prepareStatement(sql);
		setParams(pstm, params);
		return pstm.executeUpdate();
	}

	/**
	 * @Title getGeneratedKey
	 * @Description 插入一条数据到数据库并返回id
	 * @param sql
	 * @param params
	 * @return id
	 * @throws SQLException
	 */
	public int executeUpdateReturnKey(String sql, Object[] params) throws SQLException {
		showSql(sql, params);
		pstm = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		setParams(pstm, params);
		pstm.executeUpdate();
		rs = pstm.getGeneratedKeys();
		int re = -1;
		if (rs.next()) {
			re = rs.getInt(1);
		}
		return re;
	}

	public int[] executeBatch(String sql, List<Object[]> list) throws SQLException {
		pstm = getConnection().prepareStatement(sql);
		for (int i = 0; i < list.size(); i++) {
			setParams(pstm, list.get(i));
			showSql(sql, list.get(i));
			pstm.addBatch();
		}
		return pstm.executeBatch();
	}

	public ResultSet executeBatchReturnKeys(String sql, List<Object[]> list) throws SQLException {
		pstm = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		for (int i = 0; i < list.size(); i++) {
			setParams(pstm, list.get(i));
			showSql(sql, list.get(i));
			pstm.addBatch();
		}
		pstm.executeBatch();
		rs = pstm.getGeneratedKeys();
		return rs;
	}

	/**
	 * @title getResultSet
	 * @Description 创建ResultSet对象
	 * @param sql
	 * @param params
	 * @return ResultSet
	 * @throws SQLException
	 */
	public ResultSet executeQuery(String sql, Object[] params) throws SQLException {
		showSql(sql, params);
		pstm = getConnection().prepareStatement(sql);
		setParams(pstm, params);
		rs = pstm.executeQuery();
		return rs;
	}

	/**
	 * @title createCallStam
	 * @Description 调用存储过程，创建CallableStatement对象
	 * @param callSql
	 * @param params
	 * @return call
	 * @throws SQLException
	 */
	public CallableStatement createCallStam(String callSql, Object[] params) throws SQLException {
		showSql(callSql, params);
		call = getConnection().prepareCall(callSql);
		setParams(call, params);
		return call;
	}

	/**
	 * @title prepareCall
	 * @Description 创建ResultSet对象
	 * @param callSql
	 * @param params
	 * @return ResultSet
	 * @throws SQLException
	 */
	public ResultSet prepareCall(String callSql, Object[] params) throws SQLException {
		showSql(callSql, params);
		createCallStam(callSql, params);
		rs = call.executeQuery();
		return rs;
	}

	@Override
	public Map<String, Object> getMapFromResultSet() throws SQLException {
		if (rs == null) {
			return null;
		}
		Map<String, Object> tmpMap = new HashMap<String, Object>();
		ResultSetMetaData rsmd = rs.getMetaData();
		for (int i = 1; i <= rsmd.getColumnCount(); i++) {
			String columnName = rsmd.getColumnLabel(i);
			tmpMap.put(columnName, rs.getObject(columnName));
		}
		return tmpMap;
	}

	@Override
	public Object[] getArrayFromResultSet() throws SQLException {
		if (rs == null) {
			return null;
		}
		ResultSetMetaData rsmd = rs.getMetaData();
		Object[] values = new Object[rsmd.getColumnCount()];
		for (int i = 0; i < rsmd.getColumnCount(); i++) {
			values[i] = rs.getObject(i + 1);
		}
		return values;
	}

	private void setParams(PreparedStatement preparedStatement, Object[] params) throws SQLException {
		if (params != null && params.length > 0) {
			for (int i = 0; i < params.length; i++) {
				preparedStatement.setObject(i + 1, params[i]);
			}
		}
	}

	private void showSql(String sql, Object[] params) {
		if (logger.isDebugEnabled()) {
			String tmpSql = sql;
			try {
				if (params != null && params.length > 0) {
					for (int i = 0; i < params.length; i++) {
						tmpSql = tmpSql.replaceFirst("[?]", params[i] == null ? "null" : "'" + params[i].toString() + "'");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.debug(tmpSql);
		}
	}
}