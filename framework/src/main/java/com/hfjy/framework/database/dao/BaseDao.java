/**
 * 海风在线学习平台
 * @Title: BaseDao.java 
 * @Package: com.hyphen.framework.base.dao
 * @author: cloud
 * @date: 2014年5月5日-下午3:15:38
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.database.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hfjy.framework.database.base.DBSession;
import com.hfjy.framework.init.Initial;

/**
 * @ClassName: BaseDao
 * @Description: 数据库jdbc基本操作dao类
 * @author cloud
 * @date 2014年5月5日-下午3:15:38
 * 
 */
public class BaseDao<T> extends CommonDao<T> {

	@Override
	public String getDBName() {
		return Initial.DB_CONFIG_DEFAULT_KEY;
	}

	public <C> List<C> getList(String sql, Object[] params, Class<C> c) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		List<C> list = new ArrayList<C>();
		while (rs.next()) {
			list.add(InitObject(session.getMapFromResultSet(), c));
		}
		session.clear();
		return list;
	}

	public List<Object[]> getList(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		List<Object[]> list = new ArrayList<Object[]>();
		while (rs.next()) {
			list.add(session.getArrayFromResultSet());
		}
		session.clear();
		return list;
	}

	public <C> C getEntity(String sql, Object[] params, Class<C> c) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		C entity = null;
		if (rs.next()) {
			entity = InitObject(session.getMapFromResultSet(), c);
		}
		session.clear();
		return (C) entity;
	}

	public List<Map<String, Object>> getMaps(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			result.add(session.getMapFromResultSet());
		}
		session.clear();
		return result;
	}

	public Map<String, Object> getMap(String sql, Object[] params) throws SQLException {
		Map<String, Object> rowdata = new HashMap<String, Object>(0);
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		if (rs.next()) {
			rowdata = session.getMapFromResultSet();
		}
		session.clear();
		return rowdata;
	}

	public Object[] getValues(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		Object[] values = null;
		if (rs.next()) {
			values = session.getArrayFromResultSet();
		}
		session.clear();
		return values;
	}

	public Object getSingleValue(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		Object value = null;
		if (rs.next()) {
			value = rs.getObject(1);
		}
		session.clear();
		return value;
	}

	public boolean execute(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		boolean bl = session.execute(sql, params);
		session.clear();
		return bl;
	}

	public int executeUpdate(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		int count = session.executeUpdate(sql, params);
		session.clear();
		return count;
	}

	public int[] executeBatch(String sql, List<Object[]> list) throws SQLException {
		DBSession session = getSession();
		int[] rs = session.executeBatch(sql, list);
		session.clear();
		return rs;
	}

	public int[] executeBatchReturnId(String sql, List<Object[]> list) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeBatchReturnKeys(sql, list);
		int[] ids = new int[list.size()];
		for (int i = 0; rs.next(); i++) {
			ids[i] = rs.getInt(1);
		}
		session.clear();
		return ids;
	}

	public int executeAndReturnId(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		int result = session.executeUpdateReturnKey(sql, params);
		session.clear();
		return result;
	}

	public int insertEntityAndReturnId(T t) throws SQLException {
		List<Object> params = new ArrayList<>();
		String sql = createInsertSql(t, params);
		int result = executeAndReturnId(sql, params.toArray());
		if (result == -1)
			throw new SQLException("插入数据不成功");
		return result;
	}

	public int insertEntity(T t) throws SQLException {
		List<Object> params = new ArrayList<>();
		String sql = createInsertSql(t, params);
		int result = executeUpdate(sql, params.toArray());
		if (result != 1)
			throw new SQLException("插入数据不成功");
		return result;
	}

	public int updateEntity(T t, Map<String, Object> condition) throws SQLException {
		List<Object> params = new ArrayList<>();
		String sql = createUpdateSql(t, condition, params);
		int result = executeUpdate(sql, params.toArray());
		return result;
	}

	@Override
	public List<Object> getFristColumn(String sql, Object[] params) throws SQLException {
		DBSession session = getSession();
		ResultSet rs = session.executeQuery(sql, params);
		List<Object> list = new ArrayList<Object>();
		while (rs.next()) {
			list.add(rs.getObject(1));
		}
		session.clear();
		return list;
	}

	public void executeCall(String callSql, Object[] params) throws SQLException {
		DBSession session = getSession();
		session.createCallStam(callSql, params).execute();
		session.clear();
	}
}
