package com.hfjy.framework.net.transport;

public class ResponseBody {

	private static final String SUCCESS = "操作成功!";
	private static final String FAILURE = "操作失败!";

	private String type;
	private int code;
	private String desc;
	private Object data;

	public static ResponseBody createBody(String type, int code) {
		ResponseBody body = new ResponseBody();
		body.setType(type);
		body.setCode(code);
		if (code == 1)
			body.setDesc(SUCCESS);
		if (code == 0)
			body.setDesc(FAILURE);
		return body;
	}

	public static ResponseBody createBody(String type, int code, String desc) {
		ResponseBody body = new ResponseBody();
		body.setType(type);
		body.setCode(code);
		body.setDesc(desc);
		return body;
	}

	public static ResponseBody createBody(String type, int code, Object data) {
		ResponseBody body = new ResponseBody();
		body.setType(type);
		body.setCode(code);
		body.setData(data);
		return body;
	}

	public static ResponseBody createBody(String type, int code, String desc, Object data) {
		ResponseBody body = new ResponseBody();
		body.setType(type);
		body.setCode(code);
		body.setDesc(desc);
		body.setData(data);
		return body;
	}

	public static ResponseBody createBody(int code) {
		ResponseBody body = new ResponseBody();
		body.setCode(code);
		if (code == 1)
			body.setDesc(SUCCESS);
		if (code == 0)
			body.setDesc(FAILURE);
		return body;
	}

	public static ResponseBody createBody(int code, String desc) {
		ResponseBody body = new ResponseBody();
		body.setCode(code);
		body.setDesc(desc);
		return body;
	}

	public static ResponseBody createBody(int code, Object data) {
		ResponseBody body = new ResponseBody();
		body.setCode(code);
		body.setData(data);
		return body;
	}

	public static ResponseBody createBody(int code, String desc, Object data) {
		ResponseBody body = new ResponseBody();
		body.setCode(code);
		body.setDesc(desc);
		body.setData(data);
		return body;
	}

	/**
	 * 状态码
	 */
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * 描述
	 */
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	/**
	 * 数据
	 */
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	/**
	 * 处理标示
	 */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
