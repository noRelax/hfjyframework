package com.hfjy.framework.database.base;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DBSession {

	/**
	 * 
	 * @Title: isClose
	 * @Description: 连接是否关闭
	 * @return boolean
	 * @throws SQLException
	 * @since 1.0
	 */
	boolean isClosed() throws SQLException;

	/**
	 * @Title getConnection
	 * @Description 创建数据库连接(默认手动提交事务)
	 * @throws SQLException
	 */
	Connection getConnection() throws SQLException;

	/**
	 * @title closeResultSet
	 * @Description 关闭结果集
	 * @param result
	 *            要关闭的结果集对象
	 */
	void closeResultSet();

	/**
	 * @title closeCallStatement
	 * @Description 关闭CallableStatement
	 */
	void closeCallStatement();

	/**
	 * @title closePreparedStatement
	 * @Description 关闭PreparedStatement对象
	 */
	void closePreparedStatement();

	/**
	 * @title setAutoCommit
	 * @description 更改当前conn事务为自动提交（谨慎使用）
	 */
	void setAutoCommit();

	/**
	 * @title setAutoCommit
	 * @description 更改当前conn事务为手动提交（谨慎使用）
	 */
	void setManCommit();

	/**
	 * @title commit
	 * @description 提交事务
	 * @throws Exception
	 */
	void commit();

	/**
	 * @title rollback
	 * @description 回滚事务
	 */
	void rollback();

	/**
	 * @title clear
	 * @description 释放资源
	 */
	void clear();

	boolean execute(String sql, Object[] params) throws SQLException;

	/**
	 * @Title createPreparedStatement
	 * @Description 创建PreparedStatement对象
	 * @param sql
	 * @param params
	 * @return pstm
	 * @throws SQLException
	 */
	int executeUpdate(String sql, Object[] params) throws SQLException;

	/**
	 * @Title getGeneratedKey
	 * @Description 插入一条数据到数据库并返回id
	 * @param sql
	 * @param params
	 * @return id
	 * @throws SQLException
	 */
	int executeUpdateReturnKey(String sql, Object[] params) throws SQLException;

	int[] executeBatch(String sql, List<Object[]> list) throws SQLException;

	ResultSet executeBatchReturnKeys(String sql, List<Object[]> list) throws SQLException;

	/**
	 * @title createCallStam
	 * @Description 调用存储过程，创建CallableStatement对象
	 * @param callSql
	 * @param params
	 * @return call
	 * @throws SQLException
	 */
	CallableStatement createCallStam(String callSql, Object[] params) throws SQLException;

	/**
	 * @title getResultSet
	 * @Description 创建ResultSet对象
	 * @param sql
	 * @param params
	 * @return ResultSet
	 * @throws SQLException
	 */
	ResultSet executeQuery(String sql, Object[] params) throws SQLException;

	/**
	 * @title prepareCall
	 * @Description 创建ResultSet对象
	 * @param callSql
	 * @param params
	 * @return ResultSet
	 * @throws SQLException
	 */
	ResultSet prepareCall(String callSql, Object[] params) throws SQLException;

	Map<String, Object> getMapFromResultSet() throws SQLException;

	Object[] getArrayFromResultSet() throws SQLException;
}
