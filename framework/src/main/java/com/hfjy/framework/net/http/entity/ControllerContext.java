package com.hfjy.framework.net.http.entity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerContext {
	private HttpServletRequest request;
	private HttpServletResponse response;
	private Map<String, MultipartFile> multipartFileMap;
	private Model model;
	private Method executeMethod;

	public ControllerContext(final HttpServletRequest request, final HttpServletResponse response) {
		this.request = request;
		this.response = response;
		this.model = new Model();
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}

	public MultipartFile getMultipartFile(String key) {
		if (multipartFileMap == null) {
			return null;
		} else {
			return multipartFileMap.get(key);
		}
	}

	public void putMultipartFile(String key, MultipartFile multipartFile) {
		if (multipartFileMap == null) {
			multipartFileMap = new HashMap<>();
		}
		this.multipartFileMap.put(key, multipartFile);
	}

	public MultipartFile[] getMultipartFiles() {
		if (multipartFileMap == null) {
			return null;
		} else {
			MultipartFile[] array = new MultipartFile[0];
			array = multipartFileMap.values().toArray(array);
			return array;
		}
	}

	public Method getExecuteMethod() {
		return executeMethod;
	}

	public void setExecuteMethod(Method executeMethod) {
		this.executeMethod = executeMethod;
	}

	public Model getModel() {
		return model;
	}
}
