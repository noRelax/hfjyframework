package com.hfjy.framework.database.migration;

import com.hfjy.framework.common.entity.AccessResult;
import com.hfjy.framework.database.base.DBAccess;
import com.hfjy.framework.database.base.SimpleDBAccess;
import com.hfjy.framework.database.entity.DBConnectionInfo;
import com.hfjy.framework.database.entity.ResultInfo;

public abstract class DataMigration {

	public AccessResult<String> startMigration(DBConnectionInfo fromDBInfo, DBConnectionInfo toDBInfo, boolean tableExist) {
		AccessResult<String> accessResult = new AccessResult<>();
		DBAccess fromDB = new SimpleDBAccess(fromDBInfo);
		DBAccess toDB = new SimpleDBAccess(toDBInfo);
		String[] tableNames = getTableNames(fromDBInfo);
		for (int i = 0; i < tableNames.length; i++) {
			ResultInfo ri = getTableDate(fromDB, tableNames[i]);
			if (!tableExist) {
				addTable(toDB, tableNames[i], ri);
			}
			addTableDate(toDB, tableNames[i], ri);
		}
		accessResult.setSuccess(true);
		return accessResult;
	}

	protected abstract String[] getTableNames(DBConnectionInfo fromDBInfo);

	protected abstract ResultInfo getTableDate(DBAccess from, String tableName);

	protected abstract boolean addTable(DBAccess to, String tableName, ResultInfo resultInfo);

	protected abstract boolean addTableDate(DBAccess to, String tableName, ResultInfo resultInfo);
}