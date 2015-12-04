/**
 * 海风在线学习平台
 * @Title: ResponseJson.java 
 * @Package: com.hyphen.framework.response
 * @author: cloud
 * @date: 2014年5月14日-上午11:52:54
 * @version: V1.0
 * @copyright: 2014上海风创信息咨询有限公司-版权所有
 * 
 */
package com.hfjy.framework.net.transport;

import java.util.Map;

import com.alibaba.fastjson.JSONObject;

/**
 * @ClassName: ResponseJson
 * @Description: TODO(返回数据格式)
 * @author cloud
 * @date 2014年5月14日 上午11:52:54
 * 
 */
public class ResponseJson {
	/************ 状态码 **********/
	private static final String CODE = "code";
	/************ 描述 **********/
	private static final String DESC = "desc";
	/************ 数据 **********/
	private static final String DATA = "data";
	/************ 回调参数 **********/
	public static final String REBACK_PARAM = "reback_param";
	/************ NTS返回的 状态码 **********/
	private static final String STATUS = "status";

	public static JSONObject createJson(Map<String, Object> rebackParam) {
		JSONObject json = new JSONObject();
		json.put(REBACK_PARAM, rebackParam);
		return json;
	}

	public static JSONObject createJson(String status) {
		JSONObject json = new JSONObject();
		json.put(STATUS, status);
		return json;
	}

	public static JSONObject createJson(int code) {
		JSONObject json = new JSONObject();
		json.put(CODE, code);
		return json;
	}

	public static JSONObject createJson(int code, String desc) {
		JSONObject json = new JSONObject();
		json.put(CODE, code);
		json.put(DESC, desc);
		return json;
	}

	public static JSONObject createJson(int code, Object data) {
		JSONObject json = new JSONObject();
		json.put(CODE, code);
		json.put(DATA, data);
		return json;
	}

	public static JSONObject createJson(int code, String desc, Object data) {
		JSONObject json = new JSONObject();
		json.put(CODE, code);
		json.put(DESC, desc);
		json.put(DATA, data);
		return json;
	}

}