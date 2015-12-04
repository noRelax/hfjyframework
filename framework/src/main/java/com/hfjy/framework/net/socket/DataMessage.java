package com.hfjy.framework.net.socket;

import java.io.Serializable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.hfjy.framework.common.util.JsonUtil;

public class DataMessage implements Cloneable, Serializable {
	private transient static final long serialVersionUID = 1L;
	private String type;
	private String code;
	private Object data;

	public DataMessage() {
	}

	public DataMessage(String message) {
		JsonObject json = JsonUtil.toJsonObject(message);
		if (json.has("type")) {
			type = json.get("type").getAsString();
		}
		if (json.has("code")) {
			code = json.get("code").getAsString();
		}
		if (json.has("data")) {
			JsonElement je = json.get("data");
			if (je != null && !je.isJsonNull()) {
				if (je.isJsonArray()) {
					data = je.getAsJsonArray();
				} else if (je.isJsonObject()) {
					data = je.getAsJsonObject();
				} else {
					data = je.getAsString();
				}
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	protected DataMessage clone() throws CloneNotSupportedException {
		return (DataMessage) super.clone();
	}
}