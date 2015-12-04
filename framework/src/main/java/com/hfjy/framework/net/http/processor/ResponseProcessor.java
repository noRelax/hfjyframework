package com.hfjy.framework.net.http.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.exception.AjaxException;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.HttpBaseController;
import com.hfjy.framework.net.http.annotation.Ajax;
import com.hfjy.framework.net.http.entity.ControllerConfig;
import com.hfjy.framework.net.http.entity.ControllerContext;
import com.hfjy.framework.net.http.entity.ExecuteResult;

public class ResponseProcessor {
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ResponseProcessor.class);
	private final Map<String, byte[]> uriMap = new HashMap<>();
	private final ControllerConfig config;

	public ResponseProcessor(ControllerConfig config) {
		this.config = config;
	}

	public void requestResponse(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ExecuteResult executeResult) throws IOException, ServletException {
		ControllerContext context = HttpBaseController.getControllerContext();
		if (executeResult.getException() != null) {
			PrintWriter print = httpResponse.getWriter();
			if (executeResult.getException() instanceof AjaxException) {
				AjaxException ajaxException = (AjaxException) executeResult.getException();
				print.write(ajaxException.getJsonData());
				print.close();
			} else {
				if (executeResult.getException().getMessage() != null) {
					print.write(executeResult.getException().getMessage());
					print.close();
				} else {
					throw new ServletException(executeResult.getException());
				}
			}
		} else {
			if (executeResult.getResult() != null) {
				PrintWriter print = httpResponse.getWriter();
				if (context.getExecuteMethod() == null || context.getExecuteMethod().getAnnotation(Ajax.class) != null) {
					httpResponse.setContentType("application/json;charset=UTF-8");
					print.write(JsonUtil.toJson(executeResult.getResult()));
					print.close();
				} else {
					Iterator<String> keyIterator = context.getModel().keySet().iterator();
					while (keyIterator.hasNext()) {
						String key = keyIterator.next();
						httpRequest.setAttribute(key, context.getModel().get(key));
					}
					if (executeResult.getResult() != null && executeResult.getResult() instanceof String) {
						StringBuilder sb = new StringBuilder();
						sb.append(config.getPrefix());
						sb.append(executeResult.getResult());
						sb.append(config.getSuffix());
						RequestDispatcher dispatcher = httpRequest.getRequestDispatcher(sb.toString());
						dispatcher.forward(httpRequest, httpResponse);
						logger.debug("response = " + sb.toString());
					}
				}
			} else {
				String uri = httpRequest.getRequestURI();
				byte[] bf = uriMap.get(uri);
				if (bf == null) {
					String name = httpRequest.getContextPath();
					String path = uri;
					if (StringUtils.isNotEmpty(name)) {
						path = path.replaceFirst(name, "");
					}
					try {
						InputStream is = httpRequest.getServletContext().getResourceAsStream(path);
						if (is != null) {
							bf = new byte[is.available()];
							is.read(bf);
							is.close();
							uriMap.put(uri, bf);
						}
					} catch (Exception e) {
						logger.error(e.getMessage(), e);
					}
				}
				if (bf != null) {
					if (uri != null && (uri.endsWith(".html"))) {
						httpResponse.getWriter().write(new String(bf, config.getCharacterEncoding()));
					} else {
						if (uri.endsWith(".js")) {
							httpResponse.setHeader("Content-Type", "application/javascript");
						} else if (uri.endsWith(".css")) {
							httpResponse.setHeader("Content-Type", "text/css");
						}
						httpResponse.getOutputStream().write(bf);
					}
				}
			}
		}
	}
}
