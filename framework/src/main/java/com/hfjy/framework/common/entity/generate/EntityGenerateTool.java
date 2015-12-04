package com.hfjy.framework.common.entity.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

public abstract class EntityGenerateTool {
	private final StringBuilder sb = new StringBuilder();
	private int depth = 0;

	public void start(String path, String packageName) throws Exception {
		File f = new File(path);
		f.mkdirs();
		List<TableInfo> tables = getTableInfos();
		for (int i = 0; i < tables.size(); i++) {
			TableInfo ti = tables.get(i);
			initTableInfo(ti);
			addLine("package ", packageName, ";");
			rn();
			addLine("import java.io.Serializable;");
			// initImports(ti);
			rn();
			addLine("import com.hfjy.framework.beans.entity.AbstractEntity;");
			addLine("import com.hfjy.framework.beans.entity.Entity;");
			addLine("/**");
			addLine(" * ", ti.getTableName());
			addLine(" * ", ti.getComment());
			addLine(" * @author EntityGenerateTool");
			addLine(" */");
			addLine("public class ", ti.getClassName(), " extends AbstractEntity implements Cloneable,Serializable{");
			rope();
			addLine("private static final long serialVersionUID = 1L;");
			for (int c = 0; c < ti.getColumnInfoList().size(); c++) {
				ColumnInfo ci = ti.getColumnInfoList().get(c);
				addLine("/** ", ci.getColumnName(), " ", ci.getDataType(), " */");
				addLine("@Entity(columnName = \"", ci.getColumnName(), "\")");
				addLine("private ", ci.getJavaType(), " ", ci.getFieldName(), ";//", ci.getComment());
			}
			rn();
			for (int c = 0; c < ti.getColumnInfoList().size(); c++) {
				ColumnInfo ci = ti.getColumnInfoList().get(c);
				addLine("/**");
				addLine(" * @Title: ", ci.getColumnName());
				addLine(" * @Description: ", ci.getComment());
				addLine(" * @return ", ci.getJavaType());
				addLine(" */");
				addLine("public ", ci.getJavaType(), " get", toFirstBig(ci.getFieldName()), "() {");
				rope();
				addLine("return ", ci.getFieldName(), ";");
				shift();
				addLine("}");
				rn();
				addLine("/**");
				addLine(" * @Title: ", ci.getColumnName());
				addLine(" * @Description: ", ci.getComment());
				addLine(" * @param ", ci.getJavaType());
				addLine(" */");
				addLine("public void set", toFirstBig(ci.getFieldName()), "(", ci.getJavaType(), " ", ci.getFieldName(), ") {");
				rope();
				addLine("this.", ci.getFieldName(), " = ", ci.getFieldName(), ";");
				shift();
				addLine("}");
			}
			rn();
			addLine("/**");
			addLine(" * @Title: 克隆");
			addLine(" * @Description: JAVA对象的克隆");
			addLine(" * @param com.hfjy.base.entity.", ti.getClassName());
			addLine(" */");
			addLine("@Override");
			addLine("public ", ti.getClassName(), " clone() throws CloneNotSupportedException {");
			rope();
			addLine("return (", ti.getClassName(), ")super.clone();");
			shift();
			addLine("}");
			shift();
			addLine("}");
			FileOutputStream fos = new FileOutputStream(path + ti.getClassName() + ".java");
			fos.write(getClassText().getBytes("UTF-8"));
			fos.close();
		}
	}

	private void rn() {
		sb.append("\r\n");
	}

	private void rope() {
		depth++;
	}

	private void shift() {
		depth--;
	}

	private String getClassText() {
		return sb.toString();
	}

	// private void initImports(TableInfo ti) {
	// for (int i = 0; i < ti.getColumnInfoList().size(); i++) {
	// if (sb.indexOf(ti.getColumnInfoList().get(i).getJavaType()) == -1) {
	// if (ti.getColumnInfoList().get(i).getJavaType().indexOf("java.lang.") ==
	// -1) {
	// sb.append("import ");
	// sb.append(ti.getColumnInfoList().get(i).getJavaType());
	// sb.append(";\r\n");
	// }
	// }
	// }
	// }

	private void initTableInfo(TableInfo ti) {
		sb.delete(0, sb.length());
		for (int i = 0; i < ti.getColumnInfoList().size(); i++) {
			if (ti.getColumnInfoList().get(i).getJavaType().equals(Timestamp.class.getName())) {
				ti.getColumnInfoList().get(i).setJavaType(Date.class.getName());
			} else if (ti.getColumnInfoList().get(i).getJavaType().equals(java.sql.Date.class.getName())) {
				ti.getColumnInfoList().get(i).setJavaType(Date.class.getName());
			}
		}
	}

	private void addLine(String... text) {
		if (text != null && text.length > 0) {
			for (int i = 0; i < depth; i++) {
				sb.append("\t");
			}
			for (int i = 0; i < text.length; i++) {
				sb.append(text[i]);
			}
			sb.append("\r\n");
		}

	}

	private String toFirstBig(String text) {
		char[] tmp = text.toCharArray();
		tmp[0] = toUpper(tmp[0]);
		return new String(tmp);
	}

	protected char toUpper(char ch) {
		if (ch >= 97 && ch <= 122) {
			ch += 'Z';
			ch -= 'z';
		}
		return ch;
	}

	protected String toBig(String text) {
		char[] tmp = text.toCharArray();
		if (tmp[0] > 96 && tmp[0] < 123) {
			tmp[0] -= 'z';
			tmp[0] += 'Z';
		}
		return String.valueOf(tmp);
	}

	protected String toSmall(String text) {
		char[] tmp = text.toCharArray();
		if (tmp[0] > 64 && tmp[0] < 91) {
			tmp[0] += 'z';
			tmp[0] -= 'Z';
		}
		return String.valueOf(tmp);
	}

	abstract List<TableInfo> getTableInfos() throws SQLException;
}
