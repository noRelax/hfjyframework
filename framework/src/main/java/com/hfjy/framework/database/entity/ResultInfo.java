package com.hfjy.framework.database.entity;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hfjy.framework.common.util.StringUtils;

public class ResultInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<Object[]> dataList = new ArrayList<Object[]>();
	private String[] colNames = new String[0];
	private Map<String, ResultColumnInfo> resultColumnInfoMap = new HashMap<>();;
	private Integer colNum = 0;
	private Integer rowNum = 0;
	private Integer updateNum = 0;
	private Exception resultException;
	private transient static boolean IS_NEED_INFO = true;
	private transient static boolean IS_NEED_DATA = true;

	public static void setReadData() {
		IS_NEED_INFO = false;
		IS_NEED_DATA = true;
	}

	public static void setReadInfo() {
		IS_NEED_INFO = true;
		IS_NEED_DATA = false;
	}

	public static void setAllRead() {
		IS_NEED_INFO = true;
		IS_NEED_DATA = true;
	}

	public int getColNum() {
		return colNum;
	}

	public int getRowNum() {
		return rowNum;
	}

	public Exception getResultException() {
		return resultException;
	}

	public void setResultException(Exception resultException) {
		this.resultException = resultException;
	}

	public Integer getUpdateNum() {
		return updateNum;
	}

	public void setUpdateNum(Integer updateNum) {
		this.updateNum = updateNum;
	}

	public Object getData(int row, int col) {
		if (row > rowNum || col > colNum) {
			return null;
		}
		return dataList.get(row)[col];
	}

	public Object[] getData(int row) {
		if (row > rowNum) {
			return null;
		}
		return dataList.get(row);
	}

	public Object getData(int row, String colName) {
		if (StringUtils.isNotEmpty(colName)) {
			return getData(row, searchIndex(colNames, colName.toUpperCase()));
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public <T> T getDataValue(int row, int col) {
		return (T) getData(row, col);
	}

	@SuppressWarnings("unchecked")
	public <T> T getDataValue(int row, String colName) {
		return (T) getDataValue(row, searchIndex(colNames, colName.toUpperCase()));
	}

	public ResultColumnInfo getColInfo(String colName) {
		return resultColumnInfoMap.get(colName.toUpperCase());
	}

	public String[] getColNames() {
		return colNames;
	}

	public Object[][] toArray() {
		Object[][] tmp = new Object[0][];
		return dataList.toArray(tmp);
	}

	public List<Object[]> toList() {
		return dataList;
	}

	public List<Map<String, Object>> toMap() {
		List<Map<String, Object>> mapList = new ArrayList<>();
		for (int i = 0; i < dataList.size(); i++) {
			Map<String, Object> tmpMap = new HashMap<String, Object>();
			for (int c = 0; c < colNames.length; c++) {
				tmpMap.put(colNames[c], dataList.get(i)[c]);
			}
			mapList.add(tmpMap);
		}
		return mapList;
	}

	public void putResultSet(ResultSet rs) throws SQLException {
		if (rs != null) {
			ResultSetMetaData rsmd = rs.getMetaData();
			colNum = rsmd.getColumnCount();
			colNames = new String[colNum];
			setColumnInfo(rsmd);
			if (IS_NEED_DATA) {
				setDataInfo(rs);
			}
			rowNum = dataList.size();
			rs.close();
		}
	}

	private void setColumnInfo(ResultSetMetaData rsmd) throws SQLException {
		for (int i = 1; i <= colNum; i++) {
			if (IS_NEED_INFO) {
				ResultColumnInfo resultColumnInfo = new ResultColumnInfo();
				resultColumnInfo.setCatalogName(rsmd.getCatalogName(i));
				resultColumnInfo.setColumnClassName(rsmd.getColumnClassName(i));
				resultColumnInfo.setColumnCount(rsmd.getColumnCount());
				resultColumnInfo.setColumnDisplaySize(rsmd.getColumnDisplaySize(i));
				resultColumnInfo.setColumnLabel(rsmd.getColumnLabel(i));
				resultColumnInfo.setColumnName(rsmd.getColumnName(i));
				resultColumnInfo.setColumnType(rsmd.getColumnType(i));
				resultColumnInfo.setColumnTypeName(rsmd.getColumnTypeName(i));
				resultColumnInfo.setPrecision(rsmd.getPrecision(i));
				resultColumnInfo.setScale(rsmd.getScale(i));
				resultColumnInfo.setSchemaName(rsmd.getSchemaName(i));
				resultColumnInfo.setTableName(rsmd.getTableName(i));
				resultColumnInfoMap.put(rsmd.getColumnLabel(i).toUpperCase(), resultColumnInfo);
			}
			colNames[i - 1] = rsmd.getColumnLabel(i).toUpperCase();
		}
	}

	private void setDataInfo(ResultSet rs) throws SQLException {
		while (rs.next()) {
			Object[] tmp = new Object[colNum];
			for (int i = 1; i <= colNum; i++) {
				tmp[i - 1] = rs.getObject(i);
			}
			dataList.add(tmp);
		}
	}

	private <T> int searchIndex(T[] array, T value) {
		for (int i = 0; i < array.length; i++) {
			if (array[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}
}