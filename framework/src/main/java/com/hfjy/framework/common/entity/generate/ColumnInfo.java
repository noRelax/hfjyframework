package com.hfjy.framework.common.entity.generate;

public class ColumnInfo {
	private String columnName;
	private String fieldName;
	private String dataType;
	private String javaType;
	private String comment;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getJavaType() {
		return javaType;
	}

	public void setJavaType(String javaType) {
		this.javaType = javaType;
	}

	public String getComment() {
		if (comment != null) {
			return comment.replaceAll("\r|\n", " ");
		}
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
}
