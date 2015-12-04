package com.hfjy.framework.common.entity.generate;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import com.hfjy.framework.beans.entity.Entity;
import com.hfjy.framework.common.util.ClassUtil;

public class TableGenerateTool {
	public static void main(String[] args) throws Exception {
		StringBuilder sql = new StringBuilder();

		List<String> ls = ClassUtil.getClassListInPackage("com.hfjy.baselql.entity");
		for (int i = 0; i < ls.size(); i++) {
			Class<?> classInfo = Class.forName(ls.get(i));
			sql.delete(0, sql.length());
			sql.append("CREATE TABLE `");
			sql.append(getTableName(classInfo.getSimpleName()));
			sql.append("` (");
			Field[] fs = classInfo.getDeclaredFields();
			boolean isP = false;
			for (int c = 0; c < fs.length; c++) {
				String colName = getColName(fs[c]);
				if (colName != null) {
					sql.append("`");
					sql.append(colName);
					sql.append("` ");
					sql.append(getType(fs[c].getType()));
					if (colName.equals("id")) {
						isP = true;
						sql.append(" NOT NULL AUTO_INCREMENT ");
					} else {
						sql.append(" DEFAULT NULL ");
					}
					sql.append(" COMMENT '");
					sql.append(getCon(classInfo.getSimpleName(), colName));
					sql.append("'");
					if (c < fs.length - 1) {
						sql.append(",");
					}
				}
			}
			if (isP) {
				sql.append(", PRIMARY KEY (`id`) ");
			}
			sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			System.out.println(sql.toString());
		}

	}

	public static String getType(Class<?> classInfo) {
		if (classInfo == Integer.class || classInfo == Long.class) {
			return "int(11)";
		} else if (classInfo == Date.class) {
			return "datetime";
		} else {
			return "varchar(255)";
		}
	}

	public static String getColName(Field field) {
		if (field.getAnnotation(Entity.class) != null) {
			return field.getAnnotation(Entity.class).columnName();
		}
		return null;
	}

	public static String getTableName(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < name.length(); i++) {
			if (i != 0 && name.charAt(i) > 64 && name.charAt(i) < 91) {
				sb.append('_');
			}
			sb.append(name.charAt(i));
		}
		return sb.toString().toLowerCase();
	}

	public static String getCon(String className, String colName) throws IOException {
		FileInputStream fis = new FileInputStream("E:\\workspace\\baselql\\src\\main\\java\\com\\hfjy\\baselql\\entity\\" + className + ".java");
		byte[] bf = new byte[fis.available()];
		fis.read(bf);
		fis.close();
		String text = new String(bf);
		String con = text.substring(text.indexOf(colName));
		con = con.substring(con.indexOf("//") + 2);
		return con.substring(0, con.indexOf("\r\n"));
	}
}
