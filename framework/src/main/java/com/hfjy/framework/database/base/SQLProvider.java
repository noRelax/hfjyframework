package com.hfjy.framework.database.base;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.JsonObject;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.database.nosql.Condition;
import com.hfjy.framework.database.nosql.DataAccess;
import com.hfjy.framework.database.nosql.DataAccessTools;
import com.hfjy.framework.init.Initial;

public class SQLProvider {
	private static DataAccess dataAccess;

	private static final ThreadLocal<StringBuilder> localSql = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder();
		};
	};
	private static final ThreadLocal<Queue<Object>> localParameters = new ThreadLocal<Queue<Object>>() {
		@Override
		protected Queue<Object> initialValue() {
			return new LinkedList<>();
		};
	};

	private boolean isNull(String sql) {
		return sql == null || sql.length() < 1;
	}

	private StringBuilder getSqlBuilder() {
		return localSql.get();
	}

	private Queue<Object> getParameters() {
		return localParameters.get();
	}

	private boolean isAppend(boolean... bls) {
		boolean bl = true;
		for (int i = 0; i < bls.length; i++) {
			bl = bls[i] ? bl : bls[i];
		}
		return bl;
	}

	private boolean[] getConditionBls(Object... field) {
		boolean[] bls = new boolean[field.length];
		for (int i = 0; i < field.length; i++) {
			bls[i] = field[i] != null;
		}
		if (isAppend(bls)) {
			getParameters().addAll(Arrays.asList(field));
		}
		return bls;
	}

	protected void initProvider() {
		localSql.set(new StringBuilder());
	}

	protected void append(String sql) {
		getSqlBuilder().append(sql);
	}

	protected void append(String sql, Object... field) {
		appendIt(sql, getConditionBls(field));
	}

	protected void appendIt(String sql, boolean... bls) {
		if (isAppend(bls)) {
			getSqlBuilder().append(sql);
		}
	}

	protected String select(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("SELECT") > -1) {
				getSqlBuilder().append(", ");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("SELECT ");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String from(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("FROM") > -1) {
				getSqlBuilder().append(", ");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("FROM ");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String leftJoin(String sql) {
		if (!isNull(sql)) {
			getSqlBuilder().append("LEFT JOIN ");
			getSqlBuilder().append(sql);
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String and(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("WHERE") > -1) {
				getSqlBuilder().append("AND ");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("WHERE ");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String and(String sql, Object... field) {
		return andIt(sql, getConditionBls(field));
	}

	protected String andIt(String sql, boolean... bls) {
		if (isAppend(bls)) {
			return and(sql);
		} else {
			return "";
		}
	}

	protected String or(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("WHERE") > -1) {
				getSqlBuilder().append("OR ");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("WHERE ");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String or(String sql, Object... field) {
		return orIt(sql, getConditionBls(field));
	}

	protected String notIt(String sql, boolean... bls) {
		if (isAppend(bls)) {
			return or(sql);
		} else {
			return "";
		}
	}

	protected String not(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("WHERE") > -1) {
				getSqlBuilder().append("AND NOT ");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("WHERE NOT");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(" ");
		}
		return getSqlBuilder().toString();
	}

	protected String not(String sql, Object... field) {
		return orIt(sql, getConditionBls(field));
	}

	protected String orIt(String sql, boolean... bls) {
		if (isAppend(bls)) {
			return or(sql);
		} else {
			return "";
		}
	}

	protected String in(String sql) {
		if (!isNull(sql)) {
			if (getSqlBuilder().toString().toUpperCase().indexOf("WHERE") > -1) {
				getSqlBuilder().append("IN(");
				getSqlBuilder().append(sql);
			} else {
				getSqlBuilder().append("WHERE ");
				getSqlBuilder().append("IN(");
				getSqlBuilder().append(sql);
			}
			getSqlBuilder().append(") ");
		}
		return getSqlBuilder().toString();
	}

	protected String in(String sql, Object... field) {
		return inIt(sql, getConditionBls(field));
	}

	protected String inIt(String sql, boolean... bls) {
		if (isAppend(bls)) {
			return in(sql);
		} else {
			return "";
		}
	}

	protected String sqlFromDB(String code, Object... parameters) {
		if (dataAccess == null) {
			Class<DataAccess> classInfo = ClassUtil.forName(Initial.SQL_DEFAULT_EXTERNAL_DB_ACHIEVE);
			dataAccess = DataAccessTools.getDataAccess(classInfo, Initial.DB_CONFIG_DEFAULT_KEY, Initial.SQL_DEFAULT_EXTERNAL_TABLE_NAME);
		}
		JsonObject jo = dataAccess.find(Condition.init().is("code", code));
		String sql = jo.get("sql").getAsString();
		if (parameters != null && parameters.length > 0) {
			sql = initSql(sql, JsonUtil.toJsonObject(parameters[0]));
		} else {
			sql = initSql(sql, null);
		}
		return sql;
	}

	protected String sql() {
		String sql = getSqlBuilder().toString();
		getSqlBuilder().delete(0, getSqlBuilder().length());
		return sql;
	}

	protected String sqlPagination(int pageNum, int pageSize) {
		return sqlPagination(null, pageNum, pageSize);
	}

	protected String sqlPagination(String sortOrders, int pageNum, int pageSize) {
		return sqlPaginationFromMySql(pageNum, pageSize);
	}

	protected String sqlPaginationFromMySql(int pageNum, int pageSize) {
		StringBuffer sql = new StringBuffer(getSqlBuilder());
		sql.append(" limit ");
		sql.append((pageNum - 1) * pageSize);
		sql.append(",");
		sql.append(pageSize);
		getSqlBuilder().delete(0, getSqlBuilder().length());
		return sql.toString();
	}

	protected String sqlPaginationFromOracle(String sortOrders, int pageNum, int pageSize) {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM ( SELECT a.*,ROW_NUMBER() OVER (ORDER BY ");
		if (sortOrders != null && sortOrders.trim().length() > 0) {
			sql.append(sortOrders);
		} else {
			sql.append(" 1 ");
		}
		sql.append(" ) row_num FROM ( ");
		sql.append(getSqlBuilder());
		sql.append(" )a ) WHERE row_num BETWEEN ");
		sql.append(((pageNum - 1) * pageSize + 1));
		sql.append(" AND ");
		sql.append(pageNum * pageSize);
		if (sortOrders != null && sortOrders.trim().length() > 0) {
			sql.append(" ORDER BY ").append(sortOrders);
		}
		getSqlBuilder().delete(0, getSqlBuilder().length());
		return sql.toString();
	}

	protected String sqlCount() {
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT COUNT(1) AS COUNTNUM FROM (");
		sql.append(getSqlBuilder());
		sql.append(") t");
		getSqlBuilder().delete(0, getSqlBuilder().length());
		return sql.toString();
	}

	protected Object[] parameters() {
		Object[] reArror = getParameters().toArray();
		getParameters().clear();
		return reArror;
	}

	private String initSql(String sql, JsonObject josnObject) {
		if (josnObject != null) {
			if (sql.indexOf("#") > -1) {
				int begin = sql.indexOf("#{");
				int end = sql.indexOf("}");
				String name = sql.substring(begin + 2, end);
				String key = sql.substring(begin, end + 1);
				key = key.replaceFirst("[{]", "[{]").replaceFirst("[}]", "[}]");
				sql = sql.replaceFirst(key, "?");
				if (josnObject.get(name) != null && !josnObject.get(name).isJsonNull()) {
					getParameters().add(JsonUtil.toObject(josnObject.get(name), Object.class));
				} else {
					getParameters().add(null);
				}
				return initSql(sql, josnObject);
			}
		}
		return sql;
	}
}