package com.hfjy.framework.exception;

import com.alibaba.fastjson.JSONObject;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.exception.base.BaseException;
import com.hfjy.framework.net.transport.ResponseBody;

/**
 * @author cloud
 *
 */
public class AjaxException extends BaseException {

	private static final long serialVersionUID = 1L;

	private String jsonData;

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}
	
	public AjaxException(ResponseBody body,Throwable cause) {
		super(cause);
		this.jsonData = JsonUtil.toJson(body);
	}
	
	public AjaxException(JSONObject json,Throwable cause) {
		super(cause);
		this.jsonData = json.toJSONString();
	}
	
	public AjaxException(ResponseBody body) {
		super(null);
		this.jsonData = JsonUtil.toJson(body);
	}
	
	public AjaxException(JSONObject json) {
		super(null);
		this.jsonData = json.toJSONString();
	}
}
