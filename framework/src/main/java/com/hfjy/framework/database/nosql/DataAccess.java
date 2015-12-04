package com.hfjy.framework.database.nosql;

import java.util.List;

import com.google.gson.JsonObject;

public interface DataAccess {

	boolean save(JsonObject data);

	boolean saveList(List<JsonObject> data);

	long wipe(Condition condition);

	JsonObject find();

	JsonObject find(Condition condition);

	List<JsonObject> findList();

	List<JsonObject> findList(Condition condition);

	List<JsonObject> findPage(int page, int size);

	List<JsonObject> findPage(Condition condition, int page, int size);

	long swop(Condition condition, JsonObject newData);

	long size();

	long size(Condition condition);

	void lose();
}