package com.hfjy.framework.common.entity.generate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.hfjy.framework.database.base.DatabaseTools;

public class MySqlGenerateTool extends EntityGenerateTool {
	String dbCode = null;
	String[] types = { "TABLE" };

	public MySqlGenerateTool() {
	}

	public MySqlGenerateTool(String dbCode) {
		this.dbCode = dbCode;
	}

	Connection getConnection() throws SQLException {
		if (dbCode == null) {
			return DatabaseTools.getDBSession().getConnection();
		} else {
			return DatabaseTools.getDBSession(dbCode).getConnection();
		}
	}

	@Override
	List<TableInfo> getTableInfos() throws SQLException {
		List<TableInfo> tableInfoList = new ArrayList<TableInfo>();
		Connection conn = getConnection();
		DatabaseMetaData dmd = conn.getMetaData();
		ResultSet rs = dmd.getTables("", "", "%", types);
		while (rs.next()) {
			TableInfo tableInfo = new TableInfo();
			tableInfo.setTableName(rs.getString("TABLE_NAME"));
			tableInfo.setClassName(getClassName(rs.getString("TABLE_NAME")));
			tableInfo.setComment(rs.getString("REMARKS"));
			List<ColumnInfo> columnInfoList = new ArrayList<ColumnInfo>();
			ResultSet tmpRs = conn.createStatement().executeQuery("select * from " + tableInfo.getTableName() + " limit 1,1");
			ResultSetMetaData rsmd = tmpRs.getMetaData();
			int cSize = rsmd.getColumnCount();
			for (int i = 1; i <= cSize; i++) {
				ColumnInfo columnInfo = new ColumnInfo();
				columnInfo.setColumnName(rsmd.getColumnName(i));
				columnInfo.setFieldName(getFieldName(rsmd.getColumnName(i)));
				columnInfo.setJavaType(rsmd.getColumnClassName(i));
				columnInfo.setDataType(rsmd.getColumnTypeName(i));
				ResultSet columnsRs = dmd.getColumns("", "%", tableInfo.getTableName(), rsmd.getColumnName(i));
				while (columnsRs.next()) {
					columnInfo.setComment(columnsRs.getString("REMARKS"));
					break;
				}
				columnsRs.close();
				columnInfoList.add(columnInfo);
			}
			tmpRs.close();
			tableInfo.setColumnInfoList(columnInfoList);
			tableInfoList.add(tableInfo);
		}
		rs.close();
		conn.close();
		return tableInfoList;
	}

	private String getFieldName(String columnName) {
		StringBuilder sb = new StringBuilder();
		char[] tmpChars = columnName.toLowerCase().toCharArray();
		for (int i = 0; i < tmpChars.length; i++) {
			if (tmpChars[i] == '_') {
				if (i + 1 < tmpChars.length) {
					tmpChars[i + 1] = toUpper(tmpChars[i + 1]);
				}
				continue;
			}
			sb.append(tmpChars[i]);
		}
		return sb.toString();
	}

	private String getClassName(String tableName) {
		StringBuilder sb = new StringBuilder();
		char[] tmpChars = tableName.toLowerCase().toCharArray();
		for (int i = 0; i < tmpChars.length; i++) {
			if (i == 0) {
				sb.append(toUpper(tmpChars[i]));
				continue;
			}
			if (tmpChars[i] == '_') {
				if (i + 1 < tmpChars.length) {
					tmpChars[i + 1] = toUpper(tmpChars[i + 1]);
				}
				continue;
			}
			sb.append(tmpChars[i]);
		}
		return sb.toString();
	}
}
