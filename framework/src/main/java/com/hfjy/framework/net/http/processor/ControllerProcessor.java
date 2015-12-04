package com.hfjy.framework.net.http.processor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hfjy.framework.beans.BeanManager;
import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.HttpBaseController;
import com.hfjy.framework.net.http.annotation.Path;
import com.hfjy.framework.net.http.entity.ControllerConfig;
import com.hfjy.framework.net.http.entity.ControllerDataChecker;
import com.hfjy.framework.net.http.entity.ControllerMapping;
import com.hfjy.framework.net.http.entity.ExecuteResult;
import com.hfjy.framework.net.http.entity.RequestMethod;
import com.hfjy.framework.net.util.ControllerUril;

public class ControllerProcessor {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ControllerProcessor.class);
	private final Map<String, ControllerMapping> controllers = new HashMap<String, ControllerMapping>();
	private final Map<Class<?>, ControllerDataChecker> dataCheckerMap = new HashMap<>();
	private final ParameterProcessor parameterProcessor;
	private final ResponseProcessor responseProcessor;
	private final ControllerConfig config;

	public ControllerProcessor(ControllerConfig config) {
		this.config = config;
		parameterProcessor = new ParameterProcessor(config);
		responseProcessor = new ResponseProcessor(config);
		ControllerUril.initControllers(controllers, config.getControllersPackages());
		Iterator<ControllerMapping> iterator = controllers.values().iterator();
		while (iterator.hasNext()) {
			ControllerMapping cm = iterator.next();
			BeanManager.registrationClass(cm.getObject());
		}
	}

	public void requestExecute(HttpServletRequest httpRequest, HttpServletResponse httpResponse, RequestMethod method) throws IOException, ServletException {
		httpRequest.setCharacterEncoding(config.getCharacterEncoding());
		httpResponse.setCharacterEncoding(config.getCharacterEncoding());
		logger.debug(StringUtils.unite("request = ", httpRequest.getRequestURL(), " method = ", method));
		ExecuteResult executeResult = new ExecuteResult();
		ControllerDataChecker[] dataCheckers = null;
		try {
			ControllerMapping controllerMapping = findControllerMapping(httpRequest, method);
			if (controllerMapping != null) {
				Method executeMethod = HttpBaseController.getControllerContext().getExecuteMethod();
				if (executeMethod.isAnnotationPresent(Path.class)) {
					dataCheckers = getControllerDataCheckers(executeMethod);
				}
				executeResult.setParameter(parameterProcessor.getParameters(httpRequest, controllerMapping));
				if (dataCheckers == null || dataCheckers != null && checkersInCheck(dataCheckers, httpRequest, httpResponse, executeResult)) {
					if (executeResult.getParameter() != null && executeResult.getParameter().length > 0) {
						executeResult.setResult(executeMethod.invoke(controllerMapping.getObject(), executeResult.getParameter()));
					} else {
						executeResult.setResult(executeMethod.invoke(controllerMapping.getObject()));
					}
				}
			}
		} catch (Exception e) {
			executeResult.setException(e);
			logger.error(e.getMessage(), e);
		} finally {
			if (config.getDataChecker() == null || config.getDataChecker() != null && config.getDataChecker().outCheck(httpRequest, httpResponse, executeResult)) {
				if (dataCheckers == null || dataCheckers != null && checkersOutCheck(dataCheckers, httpRequest, httpResponse, executeResult)) {
					responseProcessor.requestResponse(httpRequest, httpResponse, executeResult);
				}
			}
		}
	}

	private ControllerMapping findControllerMapping(HttpServletRequest httpRequest, RequestMethod method) {
		String[] paths = handleUrl(httpRequest);
		if (paths != null && paths.length > 0) {
			ControllerMapping controllerMapping = controllers.get(paths[0]);
			if (controllerMapping != null) {
				Method tmpMethod = null;
				if (paths.length > 1) {
					tmpMethod = controllerMapping.getControllerMethod(paths[1]);
				} else {
					tmpMethod = controllerMapping.getDefaultMethod();
				}
				if (tmpMethod != null) {
					RequestMethod requestMethod = null;
					if (tmpMethod.isAnnotationPresent(Path.class)) {
						requestMethod = tmpMethod.getAnnotation(Path.class).type();
					} else {
						if (controllerMapping.getClassInfo().isAnnotationPresent(Path.class)) {
							requestMethod = controllerMapping.getClassInfo().getAnnotation(Path.class).type();
						}
					}
					HttpBaseController.getControllerContext().setExecuteMethod(tmpMethod);
					if (requestMethod == null) {
						return controllerMapping;
					} else if (requestMethod.name().equals(RequestMethod.ALL.name())) {
						return controllerMapping;
					} else if (method.equals(requestMethod)) {
						return controllerMapping;
					}
				}
			}
		}
		return null;
	}

	private String[] handleUrl(HttpServletRequest request) {
		String path = getUri(request);
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		if (path.length() == 0) {
			return new String[0];
		}
		return path.split("/");
	}

	private String getUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute("javax.servlet.include.servlet_path");
		if (uri != null) {
			return uri;
		}
		uri = getServletPath(request);
		if (uri != null && !"".equals(uri)) {
			return uri;
		}
		uri = request.getRequestURI();
		return uri.substring(request.getContextPath().length());
	}

	private String getServletPath(HttpServletRequest request) {
		String requestUri = null;
		try {
			requestUri = URLDecoder.decode(request.getRequestURI(), config.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			logger.error(e.getMessage());
		}
		int startIndex = request.getContextPath().length() < 1 ? 0 : request.getContextPath().length();
		int endIndex = request.getPathInfo() == null ? requestUri.length() : requestUri.lastIndexOf(request.getPathInfo());
		if (startIndex > endIndex) {
			endIndex = startIndex;
		}
		return requestUri.substring(startIndex, endIndex);
	}

	private ControllerDataChecker[] getControllerDataCheckers(Method method) {
		ControllerDataChecker[] controllerDataCheckers = null;
		Class<? extends ControllerDataChecker>[] checkerClassInfos = method.getAnnotation(Path.class).checker();
		if (checkerClassInfos.length > 0 && checkerClassInfos[0] != ControllerDataChecker.class) {
			controllerDataCheckers = new ControllerDataChecker[checkerClassInfos.length];
			for (int i = 0; i < checkerClassInfos.length; i++) {
				controllerDataCheckers[i] = dataCheckerMap.get(checkerClassInfos[i]);
				if (controllerDataCheckers[i] == null) {
					controllerDataCheckers[i] = ClassUtil.newInstance(checkerClassInfos[i]);
					dataCheckerMap.put(checkerClassInfos[i], controllerDataCheckers[i]);
				}
			}
		}
		return controllerDataCheckers;
	}

	private boolean checkersInCheck(ControllerDataChecker[] checkers, HttpServletRequest req, HttpServletResponse res, ExecuteResult result) throws ServletException, IOException {
		boolean isInCheck = true;
		for (int i = 0; i < checkers.length; i++) {
			isInCheck = checkers[i].inCheck(req, res, result) ? isInCheck : false;
		}
		return isInCheck;
	}

	private boolean checkersOutCheck(ControllerDataChecker[] checkers, HttpServletRequest req, HttpServletResponse res, ExecuteResult result) throws ServletException, IOException {
		boolean isInCheck = true;
		for (int i = 0; i < checkers.length; i++) {
			isInCheck = checkers[i].outCheck(req, res, result) ? isInCheck : false;
		}
		return isInCheck;
	}
}