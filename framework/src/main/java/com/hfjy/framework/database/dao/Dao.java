/**
 * 海风在线学习平台
 * @Title: Dao.java 
 * @Package: com.hyphen.framework.base.dao
 * @author: cloud
 * @date: 2014年5月5日-上午11:47:39
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.database.dao;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: Dao
 * @Description: 数据库操作Dao接口
 * @author cloud
 * @date 2014年5月5日-上午11:47:39
 *
 */
public interface Dao<T> {

	String getDBName();

	/**
	 * @Title 返回List(无参数)
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	List<Object[]> getList(String sql) throws SQLException;

	/**
	 * @Title 返回List
	 * @param sql
	 * @param prams
	 * @return
	 * @throws SQLException
	 */
	List<Object[]> getList(String sql, Object[] prams) throws SQLException;

	/**
	 * @Title 返回对象集合(无参数)
	 * @param sql
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	<C> List<C> getList(String sql, Class<C> c) throws SQLException;

	/**
	 * @Title 返回对象集合
	 * @param sql
	 * @param params
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	<C> List<C> getList(String sql, Object[] params, Class<C> c) throws SQLException;

	/**
	 * @Title 返回查询的对象(无参数)
	 * @param sql
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	<C> C getEntity(String sql, Class<C> c) throws SQLException;

	/**
	 * @Title 返回查询的对象
	 * @param sql
	 * @param params
	 * @param c
	 * @return
	 * @throws SQLException
	 */
	<C> C getEntity(String sql, Object[] params, Class<C> c) throws SQLException;

	/**
	 * @Title 返回Map集合
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getMaps(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 返回Map集合(无参数)
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	List<Map<String, Object>> getMaps(String sql) throws SQLException;

	/**
	 * @Title 返回map(一行数据)
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getMap(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 返回map(一行数据)
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	Map<String, Object> getMap(String sql) throws SQLException;

	/**
	 * @Title 返回Object[]
	 * @param sql
	 * @param prams
	 * @return
	 * @throws SQLException
	 */
	Object[] getValues(String sql, Object[] prams) throws SQLException;

	/**
	 * @Title 返回Object[](无参数)
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	Object[] getValues(String sql) throws SQLException;

	/**
	 * @Title 返回单个结果
	 * @param sql
	 * @param params
	 * @return Object
	 * @throws SQLException
	 */
	Object getSingleValue(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 返回单个结果(无参数)
	 * @param sql
	 * @return Object
	 * @throws SQLException
	 */
	Object getSingleValue(String sql) throws SQLException;

	/**
	 * @Title 增、删、改(返回受影响的行数)
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	int executeUpdate(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 增、删、改(返回受影响的行数)
	 * @param sql
	 * @throws SQLException
	 */
	int executeUpdate(String sql) throws SQLException;

	/**
	 * @Title 根据条件执行（增、删、改）
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	boolean execute(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 执行（增、删、改）
	 * @param sql
	 * @throws SQLException
	 */
	void execute(String sql) throws SQLException;

	/**
	 * @Title 批处理数据（增、删、改）
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	int[] executeBatch(String sql, List<Object[]> list) throws SQLException;

	/**
	 * @Title 批处理数据（增、删、改）
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	int[] executeBatchReturnId(String sql, List<Object[]> list) throws SQLException;

	/**
	 * @Title 调用存储过程，执行(增、删、改)
	 * @param sql
	 * @param prams
	 * @throws SQLException
	 */
	void executeCall(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 调用存储过程，执行(增、删、改)
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	void executeCall(String sql) throws SQLException;

	/**
	 * @Title 插入一条数据到数据库并且返回id
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	int executeAndReturnId(String sql, Object[] params) throws SQLException;

	/**
	 * @Title 插入一条数据到数据库并且返回id
	 * @param t
	 *            实体bean
	 * @throws SQLException
	 */
	int insertEntityAndReturnId(T t) throws SQLException;

	/**
	 * @Title 插入一条数据到数据库
	 * @param t
	 *            实体bean
	 * @throws SQLException
	 */
	int insertEntity(T t) throws SQLException;

	/**
	 * @Title 按条件更新数据库数据
	 * @param t
	 *            实体bean
	 * @param condition
	 *            更新条件
	 * @throws SQLException
	 */
	int updateEntity(T t, Map<String, Object> condition) throws SQLException;

	List<Object> getFristColumn(String sql) throws SQLException;

	List<Object> getFristColumn(String sql, Object[] params) throws SQLException;
}
