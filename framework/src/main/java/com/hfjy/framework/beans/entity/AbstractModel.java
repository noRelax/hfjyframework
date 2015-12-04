package com.hfjy.framework.beans.entity;

import com.hfjy.framework.database.nosql.DataAccessObject;
import com.hfjy.framework.init.Initial;

public abstract class AbstractModel {

	public String getdbName() {
		return Initial.DB_CONFIG_DEFAULT_KEY;
	}

	public String getTableName() {
		return getClass().getSimpleName();
	}

	@SuppressWarnings("unchecked")
	public <T> DataAccessObject<T> dao() {
		Class<T> clazz = (Class<T>) getClass();
		return new DataAccessObject<T>(getdbName(), getTableName(), clazz);
	}
}