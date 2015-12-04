package com.hfjy.framework.database.base;

import java.util.List;

import com.hfjy.framework.database.entity.ResultInfo;

public interface DBAccess {

	boolean testExecute();

	boolean execute(String sql);

	boolean execute(String sql, Object... param);

	int executeSql(String sql);

	int executeSql(String sql, Object... param);

	int[] executeBatchSql(String sql, List<Object[]> paramList);

	int executeSqlGetId(String sql);

	int executeSqlGetId(String sql, Object... param);

	List<ResultInfo> executeListQuery(String sql);

	List<ResultInfo> executeListQuery(String sql, Object... param);

	ResultInfo executeQuery(String sql);

	ResultInfo executeQuery(String sql, Object... param);

	List<ResultInfo> executeProcedure(String spName);

	List<ResultInfo> executeProcedure(String spName, Object... in);

	List<ResultInfo> executeProcedure(String spName, Object[] in, Object[] out);
}