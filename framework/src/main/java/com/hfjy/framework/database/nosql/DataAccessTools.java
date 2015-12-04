package com.hfjy.framework.database.nosql;

import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.init.Initial;

public class DataAccessTools {

	public static DataAccess getDataAccess(String dbName, String tableName) {
		if (StringUtils.isEmpty(Initial.SYSTEM_ACHIEVE_DATA_ACCESS)) {
			return null;
		} else {
			Class<DataAccess> dataAccessClass = ClassUtil.forName(Initial.SYSTEM_ACHIEVE_DATA_ACCESS);
			return getDataAccess(dataAccessClass, dbName, tableName);
		}
	}

	public static DataAccess getDataAccess(Class<? extends DataAccess> clazz, String dbName, String tableName) {
		return ClassUtil.newInstance(clazz, dbName, tableName);
	}
}