package com.hfjy.framework.database.migration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.database.base.DBAccess;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.ResultInfo;
import com.hfjy.framework.logging.LoggerFactory;

public class DMCommonProcessor extends DataMigration {
	private static final Logger logger = LoggerFactory.getLogger(DMCommonProcessor.class);

	@Override
	protected String[] getTableNames(DBConnectionInfo fromDBInfo) {
		List<String> list = new ArrayList<>();
		try (Connection conn = getConnection(fromDBInfo)) {
			DatabaseMetaData dmd = conn.getMetaData();
			ResultSet rs = dmd.getTables("", "", "%", new String[] { "TABLE" });
			while (rs.next()) {
				list.add(rs.getString("TABLE_NAME"));
			}
			rs.close();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return list.toArray(new String[] {});
	}

	@Override
	protected ResultInfo getTableDate(DBAccess from, String tableName) {
		return from.executeQuery(StringUtils.unite("select * from ", tableName));
	}

	@Override
	protected boolean addTable(DBAccess to, String tableName, ResultInfo resultInfo) {
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ");
		sql.append(tableName);
		sql.append(" (");
		for (int c = 0; c < resultInfo.getColNames().length; c++) {
			sql.append(resultInfo.getColInfo(resultInfo.getColNames()[c]).getColumnName());
			sql.append(" ");
			sql.append(resultInfo.getColInfo(resultInfo.getColNames()[c]).getColumnTypeName());
			if (c < resultInfo.getColNames().length - 1) {
				sql.append(",");
			}
		}
		sql.append(")");
		return to.execute(sql.toString());
	}

	@Override
	protected boolean addTableDate(DBAccess to, String tableName, ResultInfo resultInfo) {
		List<Object[]> valuesList = new ArrayList<>();
		StringBuilder sql = new StringBuilder();
		String[] names = resultInfo.getColNames();
		StringBuilder columnsSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		for (int c = 0; c < names.length; c++) {
			columnsSql.append(names[c]);
			valuesSql.append("?");
			for (int r = 0; r < resultInfo.getRowNum(); r++) {
				if (c == 0) {
					valuesList.add(new Object[resultInfo.getColNum()]);
				}
				valuesList.get(r)[c] = resultInfo.getData(r, names[c]);
			}
			if (c < names.length - 1) {
				columnsSql.append(",");
				valuesSql.append(",");
			}
		}
		sql.append("insert into ");
		sql.append(tableName);
		sql.append("(");
		sql.append(columnsSql.toString());
		sql.append(") values(");
		sql.append(valuesSql.toString());
		sql.append(")");
		to.executeBatchSql(sql.toString(), valuesList);
		return true;
	}

	private Connection getConnection(DBConnectionInfo fromDBInfo) {
		Connection conn = null;
		try {
			Class.forName(fromDBInfo.getDriveClass());
			if (StringUtils.isEmpty(fromDBInfo.getDbUsername())) {
				conn = DriverManager.getConnection(fromDBInfo.getDbConnectionString());
			} else {
				conn = DriverManager.getConnection(fromDBInfo.getDbConnectionString(), fromDBInfo.getDbUsername(), fromDBInfo.getDbPassword());
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return conn;
	}
}
