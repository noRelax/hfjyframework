package com.hfjy.framework.net.http;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.hfjy.framework.biz.BaseController;
import com.hfjy.framework.net.http.entity.ControllerContext;

public class HttpBaseController extends BaseController {
	private static final ThreadLocal<ControllerContext> localControllerContext = new ThreadLocal<ControllerContext>();

	public static ControllerContext getControllerContext() {
		return localControllerContext.get();
	}

	public static void setControllerContext(ControllerContext controllerContext) {
		localControllerContext.set(controllerContext);
	}

	public HttpServletRequest getRequest() {
		return localControllerContext.get().getRequest();
	}

	public HttpServletResponse getResponse() {
		return localControllerContext.get().getResponse();
	}

	public HttpSession getSession() {
		return localControllerContext.get().getRequest().getSession();
	}
}