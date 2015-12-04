package com.hfjy.framework.database.nosql;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.database.base.DBSession;
import com.hfjy.framework.database.base.DatabaseTools;
import com.hfjy.framework.database.base.SQLProvider;
import com.hfjy.framework.logging.LoggerFactory;

public class JDBCDataAccessObject extends SQLProvider implements DataAccess {
	private final Logger logger = LoggerFactory.getLogger(JDBCDataAccessObject.class);
	private final DBSession dbs;
	private final String tableName;

	public JDBCDataAccessObject(String dbName, String collectionName) {
		dbs = DatabaseTools.getDBSession(dbName);
		tableName = collectionName;
	}

	@Override
	public boolean save(JsonObject data) {
		try {
			List<String> list = getColumnNames(data);
			append("INSERT INTO ");
			append(tableName);
			append("(");
			for (int i = 0; i < list.size(); i++) {
				append(fieldToTableColumn(list.get(i)));
				if (i < list.size() - 1) {
					append(",");
				}
			}
			append(") VALUES(");
			for (int i = 0; i < list.size(); i++) {
				append("?", getValue(data.get(list.get(i))));
				if (i < list.size() - 1) {
					append(",");
				}
			}
			append(")");
			return dbs.execute(sql(), parameters());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return false;
	}

	@Override
	public boolean saveList(List<JsonObject> data) {
		try {
			for (int i = 0; i < data.size(); i++) {
				save(data.get(i));
			}
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return false;
	}

	@Override
	public long wipe(Condition condition) {
		try {
			append("DELETE FROM ");
			append(tableName);
			append(" ");
			conditionToSql(condition);
			return dbs.executeUpdate(sql(), parameters());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return 0;
	}

	@Override
	public JsonObject find() {
		try {
			select("*");
			from(tableName);
			if (dbs.executeQuery(sql(), null).next()) {
				return JsonUtil.toJsonObject(dbs.getMapFromResultSet());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public JsonObject find(Condition condition) {
		try {
			select("*");
			from(tableName);
			conditionToSql(condition);
			if (dbs.executeQuery(sql(), parameters()).next()) {
				return JsonUtil.toJsonObject(dbs.getMapFromResultSet());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public List<JsonObject> findList() {
		try {
			List<JsonObject> jsonList = new ArrayList<>();
			select("*");
			from(tableName);
			ResultSet rs = dbs.executeQuery(sql(), parameters());
			while (rs.next()) {
				jsonList.add(JsonUtil.toJsonObject(dbs.getMapFromResultSet()));
			}
			return jsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public List<JsonObject> findList(Condition condition) {
		try {
			List<JsonObject> jsonList = new ArrayList<>();
			select("*");
			from(tableName);
			conditionToSql(condition);
			ResultSet rs = dbs.executeQuery(sql(), parameters());
			while (rs.next()) {
				jsonList.add(JsonUtil.toJsonObject(dbs.getMapFromResultSet()));
			}
			return jsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public List<JsonObject> findPage(int page, int size) {
		try {
			List<JsonObject> jsonList = new ArrayList<>();
			select("*");
			from(tableName);
			ResultSet rs = dbs.executeQuery(sqlPagination(page, size), parameters());
			while (rs.next()) {
				jsonList.add(JsonUtil.toJsonObject(dbs.getMapFromResultSet()));
			}
			return jsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public List<JsonObject> findPage(Condition condition, int page, int size) {
		try {
			List<JsonObject> jsonList = new ArrayList<>();
			select("*");
			from(tableName);
			conditionToSql(condition);
			ResultSet rs = dbs.executeQuery(sqlPagination(page, size), parameters());
			while (rs.next()) {
				jsonList.add(JsonUtil.toJsonObject(dbs.getMapFromResultSet()));
			}
			return jsonList;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return null;
	}

	@Override
	public long swop(Condition condition, JsonObject newData) {
		try {
			append("UPDATE ");
			append(tableName);
			append(" SET ");
			List<String> list = getColumnNames(newData);
			Map<String, Object> tmpMap = new HashMap<>();
			for (int i = 0; i < list.size(); i++) {
				String name = list.get(i);
				Object value = getValue(newData.get(name));
				if (value != null) {
					tmpMap.put(fieldToTableColumn(name), value);
				}
			}
			Iterator<String> iterator = tmpMap.keySet().iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				if (iterator.hasNext()) {
					append(name + "=?, ", tmpMap.get(name));
				} else {
					append(name + "=? ", tmpMap.get(name));
				}
			}
			conditionToSql(condition);
			return dbs.executeUpdate(sql(), parameters());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return 0;
	}

	@Override
	public long size() {
		try {
			select("*");
			from(tableName);
			if (dbs.executeQuery(sqlCount(), null).next()) {
				return (long) dbs.getArrayFromResultSet()[0];
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return 0;
	}

	@Override
	public long size(Condition condition) {
		try {
			select("*");
			from(tableName);
			conditionToSql(condition);
			if (dbs.executeQuery(sqlCount(), parameters()).next()) {
				return (long) dbs.getArrayFromResultSet()[0];
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
		return 0;
	}

	@Override
	public void lose() {
		try {
			append("DROP TABLE ");
			append(tableName);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			dbs.clear();
		}
	}

	private void conditionToSql(Condition condition) {
		List<Condition> conditionList = condition.getConditions();
		for (int i = 0; i < conditionList.size(); i++) {
			Condition c = conditionList.get(i);
			if (c.getFactor() == FactorType.OR) {
				or("(");
				conditionToSql(c);
				append(") ");
			} else if (c.getFactor() == FactorType.NOT) {
				not("(");
				conditionToSql(c);
				append(") ");
			} else {
				if (condition.getFactor() == FactorType.NOT || condition.getFactor() == FactorType.OR) {
					if (i == 0) {
						if (c.getFactor() == FactorType.IN || c.getFactor() == FactorType.NOT_IN) {
							append(getFactor(c), getInParameters(c.getValue()));
							append(" ");
						} else {
							append(getFactor(c), c.getValue());
							append(" ");
						}
						continue;
					}
				}
				if (c.getFactor() == FactorType.IN || c.getFactor() == FactorType.NOT_IN) {
					and(getFactor(c), getInParameters(c.getValue()));
				} else {
					and(getFactor(c), c.getValue());
				}
			}
		}
	}

	private String getFactor(Condition condition) {
		StringBuilder sql = new StringBuilder("(");
		sql.append(condition.getElement());
		switch (condition.getFactor()) {
		case EQUAL:
			sql.append(" = ?");
			break;
		case NOT_EQUAL:
			sql.append(" != ?");
			break;
		case GREATER_THAN:
			sql.append(" > ?");
			break;
		case GREATER_THAN_EQUAL:
			sql.append(" >= ?");
			break;
		case LESS_THAN:
			sql.append(" < ?");
			break;
		case LESS_THAN_EQUAL:
			sql.append(" <= ?");
			break;
		case LIKE:
			sql.append(" LIKE ?");
			break;
		case IN:
			sql.append(" IN(");
			if (condition.getValue() instanceof Iterable<?>) {
				IterableToString(sql, (Iterable<?>) condition.getValue());
			} else {
				objectsToString(sql, (Object[]) condition.getValue());
			}
			sql.append(")");
			break;
		case NOT_IN:
			sql.append(" NOT IN(");
			if (condition.getValue() instanceof Iterable<?>) {
				IterableToString(sql, (Iterable<?>) condition.getValue());
			} else {
				objectsToString(sql, (Object[]) condition.getValue());
			}
			sql.append(")");
			break;
		default:
			break;
		}
		sql.append(")");
		return sql.toString();
	}

	private void IterableToString(StringBuilder sql, Iterable<?> iterable) {
		Iterator<?> iterator = iterable.iterator();
		while (iterator.hasNext()) {
			sql.append("?");
			if (iterator.hasNext()) {
				sql.append(",");
			}
		}
	}

	private void objectsToString(StringBuilder sql, Object[] objects) {
		for (int i = 0; i < objects.length; i++) {
			sql.append("?");
			if (i < objects.length - 1) {
				sql.append(",");
			}
		}
	}

	private Object[] getInParameters(Object parameters) {
		if (parameters instanceof Iterable<?>) {
			List<Object> list = new ArrayList<>();
			Iterable<?> Iterable = (Iterable<?>) parameters;
			Iterator<?> iterator = Iterable.iterator();
			while (iterator.hasNext()) {
				list.add(iterator.next());
			}
			return list.toArray();
		} else {
			return (Object[]) parameters;
		}
	}

	private List<String> getColumnNames(JsonObject data) {
		List<String> columnList = new ArrayList<>();
		Iterator<Map.Entry<String, JsonElement>> iterator = data.entrySet().iterator();
		while (iterator.hasNext()) {
			columnList.add(iterator.next().getKey());
		}
		return columnList;
	}

	private Object getValue(JsonElement data) {
		if (data != null && data.isJsonPrimitive()) {
			JsonPrimitive jp = data.getAsJsonPrimitive();
			if (jp.isBoolean()) {
				return jp.getAsBoolean();
			} else if (jp.isString()) {
				return jp.getAsString();
			} else if (jp.isNumber()) {
				return jp.getAsNumber();
			}
		}
		return null;
	}

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
}
