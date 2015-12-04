package com.hfjy.framework.database.nosql;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hfjy.framework.beans.entity.Entity;
import com.hfjy.framework.common.util.JsonUtil;

public class DataAccessObject<T> {
	private final Class<T> clazz;
	private final DataAccess da;

	public DataAccessObject(String dbName, String tableName, Class<T> clazz) {
		this.da = DataAccessTools.getDataAccess(JDBCDataAccessObject.class, dbName, tableName);
		this.clazz = clazz;
	}

	public boolean insert(T data) {
		return da.save(JsonUtil.toJsonObject(data));
	}

	public boolean insertList(List<T> data) {
		List<JsonObject> jsonObjectList = new ArrayList<>();
		for (int i = 0; i < data.size(); i++) {
			jsonObjectList.add(JsonUtil.toJsonObject(data.get(i)));
		}
		return da.saveList(jsonObjectList);
	}

	public long delete(Condition condition) {
		return da.wipe(condition);
	}

	public long update(Condition condition, T data) {
		return da.swop(condition, JsonUtil.toJsonObject(data));
	}

	public T selectFirst() {
		JsonObject jo = da.find();
		if (jo != null) {
			return getValue(jo);
		} else {
			return null;
		}
	}

	public T selectFirst(Condition condition) {
		JsonObject jo = da.find(condition);
		if (jo != null) {
			return getValue(jo);
		} else {
			return null;
		}
	}

	public List<T> selectList() {
		List<T> dataList = new ArrayList<>();
		List<JsonObject> jsonObjectList = da.findList();
		for (int i = 0; i < jsonObjectList.size(); i++) {
			dataList.add(getValue(jsonObjectList.get(i)));
		}
		return dataList;
	}

	public List<T> selectList(Condition condition) {
		List<T> dataList = new ArrayList<>();
		List<JsonObject> jsonObjectList = da.findList(condition);
		for (int i = 0; i < jsonObjectList.size(); i++) {
			dataList.add(getValue(jsonObjectList.get(i)));
		}
		return dataList;
	}

	public List<T> selectPage(int page, int size) {
		List<T> dataList = new ArrayList<>();
		List<JsonObject> jsonObjectList = da.findPage(page, size);
		for (int i = 0; i < jsonObjectList.size(); i++) {
			dataList.add(getValue(jsonObjectList.get(i)));
		}
		return dataList;
	}

	public List<T> selectPage(Condition condition, int page, int size) {
		List<T> dataList = new ArrayList<>();
		List<JsonObject> jsonObjectList = da.findPage(condition, page, size);
		for (int i = 0; i < jsonObjectList.size(); i++) {
			dataList.add(getValue(jsonObjectList.get(i)));
		}
		return dataList;
	}

	public long count() {
		return da.size();
	}

	public long count(Condition condition) {
		return da.size(condition);
	}

	public void truncate() {
		da.lose();
	}

	private T getValue(JsonObject jsonObject) {
		Field[] fields = clazz.getDeclaredFields();
		T exec = null;
		for (int i = 0; i < fields.length; i++) {
			Field f = fields[i];
			try {
				if (exec == null) {
					exec = clazz.newInstance();
				}
				String name = f.getName();
				if (f.getAnnotation(Entity.class) != null) {
					name = f.getAnnotation(Entity.class).columnName();
				}
				JsonElement je = jsonObject.get(name);
				if (je != null && !je.isJsonNull()) {
					f.setAccessible(true);
					f.set(exec, JsonUtil.toObject(je, f.getType()));
				}
			} catch (IllegalArgumentException | IllegalAccessException | InstantiationException e) {
			}
		}
		return exec;
	}
}