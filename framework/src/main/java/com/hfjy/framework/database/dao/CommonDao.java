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

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.hfjy.framework.beans.entity.AbstractEntity;
import com.hfjy.framework.beans.entity.Entity;
import com.hfjy.framework.common.entity.generate.ColumnInfo;
import com.hfjy.framework.database.base.DBSession;
import com.hfjy.framework.database.base.DatabaseTools;
import com.hfjy.framework.database.base.SQLProvider;

/**
 * @ClassName: Dao
 * @Description: 数据库操作Dao接口
 * @author cloud
 * @date 2014年5月5日-上午11:47:39
 * 
 */
public abstract class CommonDao<T> extends SQLProvider implements Dao<T> {
	/**
	 * 
	 * @Title: getSession
	 * @Description: 获取Session对象
	 * @return Session
	 * @throws SQLException
	 * @since 1.0
	 */
	protected DBSession getSession() {
		return DatabaseTools.getDBSession(getDBName());
	}

	/**
	 * @Title 返回对象集合(无参数)
	 * @param sql
	 * @param c
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SQLException
	 */
	public <C> List<C> getList(String sql, Class<C> c) throws SQLException {
		return getList(sql, null, c);
	}

	/**
	 * @Title 返回查询的对象(无参数)
	 * @param sql
	 * @param c
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public <C> C getEntity(String sql, Class<C> c) throws SQLException {
		return getEntity(sql, null, c);
	}

	/**
	 * @Title 返回Map集合(无参数)
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> getMaps(String sql) throws SQLException {
		return getMaps(sql, null);
	}

	/**
	 * @Title 返回map(一行数据)
	 * @key cloumnname
	 * @value cloumvalue
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public Map<String, Object> getMap(String sql) throws SQLException {
		return getMap(sql, null);
	}

	/**
	 * @Title 返回List(无参数)
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public List<Object[]> getList(String sql) throws SQLException {
		return getList(sql, new Object[0]);
	}

	/**
	 * @Title 返回Object[](无参数)
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public Object[] getValues(String sql) throws SQLException {
		return getValues(sql, null);
	}

	/**
	 * @Title 返回单个结果(无参数)
	 * @param sql
	 * @return Object
	 * @throws SQLException
	 */
	public Object getSingleValue(String sql) throws SQLException {
		return getSingleValue(sql, null);
	}

	/**
	 * @Title 增、删、改(返回受影响的行数)
	 * @param sql
	 * @throws SQLException
	 */
	public int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, null);
	}

	/**
	 * @Title 执行（增、删、改）
	 * @param sql
	 * @throws SQLException
	 */
	public void execute(String sql) throws SQLException {
		execute(sql, null);
	}

	/**
	 * @Title 调用存储过程，执行(增、删、改)
	 * @param sql
	 * @param params
	 * @throws SQLException
	 */
	public void executeCall(String callSql) throws SQLException {
		executeCall(callSql, null);
	}

	public List<Object> getFristColumn(String sql) throws SQLException {
		return getFristColumn(sql, null);
	}

	/**
	 * 创建一条更新sql
	 * 
	 * @param entity
	 * @param condition
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public String createUpdateSql(Object entity, Map<String, Object> condition, List<Object> params) throws SQLException {
		StringBuilder sql = new StringBuilder();
		sql.append(" UPDATE ");
		sql.append(fieldToTableColumn(entity.getClass().getSimpleName()));
		sql.append(" SET ");
		Map<ColumnInfo, Object> valids = getValidData(entity);
		Iterator<ColumnInfo> iterator = valids.keySet().iterator();
		while (iterator.hasNext()) {
			ColumnInfo columnInfo = iterator.next();
			if (!condition.containsKey(columnInfo.getFieldName())) {
				Object fieldValue = valids.get(columnInfo);
				sql.append(columnInfo.getColumnName());
				sql.append(" = ?");
				sql.append(",");
				params.add(fieldValue);
			}
		}
		sql.delete(sql.length() - 1, sql.length());
		if (!condition.isEmpty()) {
			sql.append(" WHERE ");
			Iterator<String> tmp = condition.keySet().iterator();
			while (tmp.hasNext()) {
				String paramName = tmp.next();
				params.add(condition.get(paramName));
				paramName = fieldToTableColumn(paramName);
				sql.append(" ");
				sql.append(paramName);
				sql.append(" = ? ");
				sql.append("and");
			}
			sql.delete(sql.length() - 3, sql.length());
		}
		return sql.toString();
	}

	/**
	 * 创建一条插入sql
	 * 
	 * @param tmpEntity
	 * @param params
	 * @return
	 * @throws SQLException
	 * @throws Exception
	 */
	public String createInsertSql(Object tmpEntity, List<Object> params) throws SQLException {
		StringBuilder sql = new StringBuilder();
		StringBuilder columns = new StringBuilder();
		StringBuilder values = new StringBuilder();
		Map<ColumnInfo, Object> valids = getValidData(tmpEntity);
		Iterator<ColumnInfo> iterator = valids.keySet().iterator();
		while (iterator.hasNext()) {
			ColumnInfo columninfo = iterator.next();
			columns.append(columninfo.getColumnName());
			values.append("?");
			params.add(valids.get(columninfo));
			if (iterator.hasNext()) {
				columns.append(",");
				values.append(",");
			}
		}
		sql.append(" INSERT INTO ");
		sql.append(fieldToTableColumn(tmpEntity.getClass().getSimpleName()));
		sql.append("(");
		sql.append(columns.toString());
		sql.append(") VALUES(");
		sql.append(values.toString());
		sql.append(")");
		return sql.toString();
	}

	private Map<ColumnInfo, Object> getValidData(Object object) throws SQLException {
		Map<ColumnInfo, Object> tmpMap = new HashMap<>();
		Field[] tmpFields = AbstractEntity.getEntityFields(object.getClass());
		if (tmpFields != null) {
			for (int i = 0; i < tmpFields.length; i++) {
				tmpFields[i].setAccessible(true);
				Entity annotation = tmpFields[i].getAnnotation(Entity.class);
				try {
					Object tmpEntity = tmpFields[i].get(object);
					if (annotation != null && tmpEntity != null) {
						ColumnInfo ci = new ColumnInfo();
						ci.setColumnName(annotation.columnName());
						ci.setFieldName(tmpFields[i].getName());
						tmpMap.put(ci, tmpEntity);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new SQLException(e.getMessage(), e.getCause());
				}
				tmpFields[i].setAccessible(false);
			}
		}
		return tmpMap;
	}

	/**
	 * 属性字段名转数据库字段名
	 * 
	 * @param str
	 * @return
	 */
	private String fieldToTableColumn(String filed) {
		StringBuffer column = new StringBuffer();
		int len = filed.length();
		int index = 0;
		while (index < len) {
			char ch = filed.charAt(index);
			if (index > 0 && Character.isUpperCase(ch))
				column.append("_");
			column.append(ch);
			index++;
		}
		return column.toString().toLowerCase();
	}

	public String getTableName(String className) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < className.length(); i++) {
			if (i != 0) {
				if (className.charAt(i) > 64 && className.charAt(i) < 91)
					sb.append('_');
			}
			sb.append(className.charAt(i));
		}
		return sb.toString().toUpperCase();
	}

	public String getClassName(String tableName) {
		StringBuilder sb = new StringBuilder();
		boolean isSmall = false;
		for (int i = 0; i < tableName.length(); i++) {
			char tmp = tableName.charAt(i);
			if (isSmall) {
				if (tableName.charAt(i) > 64 && tableName.charAt(i) < 91) {
					tmp += 'z';
					tmp -= 'Z';
				}
			}
			if (tableName.charAt(i) == 95) {
				isSmall = false;
				continue;
			} else {
				isSmall = true;
			}
			sb.append(tmp);
		}
		return sb.toString();
	}

	protected <C> C InitObject(Map<String, Object> rowdata, Class<C> classInfo) throws SQLException {
		C entity = null;
		try {
			entity = classInfo.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SQLException(e.getMessage(), e.getCause());
		}
		if (entity instanceof AbstractEntity) {
			((AbstractEntity) entity).init(rowdata);
		}
		return entity;
	}
}
