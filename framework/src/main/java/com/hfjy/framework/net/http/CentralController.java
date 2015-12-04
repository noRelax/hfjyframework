package com.hfjy.framework.net.http;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hfjy.framework.common.util.ClassUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.logging.LoggerFactory;
import com.hfjy.framework.net.http.entity.ControllerConfig;
import com.hfjy.framework.net.http.entity.ControllerContext;
import com.hfjy.framework.net.http.entity.DefaultControllerConfig;
import com.hfjy.framework.net.http.entity.ControllerDataChecker;
import com.hfjy.framework.net.http.entity.RequestMethod;
import com.hfjy.framework.net.http.processor.ControllerProcessor;
import com.hfjy.framework.net.util.Constant;

@MultipartConfig
public class CentralController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CentralController.class);
	private ControllerProcessor controllerProcessor;
	private ControllerDataChecker httpDataCheckers;

	@Override
	public final void init(ServletConfig initConfig) throws ServletException {
		logger.info("init begin");
		String className = initConfig.getInitParameter(Constant.CONTROLLER_INIT_CLASS_KEY);
		if (className == null) {
			logger.warn(StringUtils.unite("init parameter ", Constant.CONTROLLER_INIT_CLASS_KEY, " no configuration"));
		} else {
			logger.info(StringUtils.unite("init parameter ", Constant.CONTROLLER_INIT_CLASS_KEY, " configure ", className));
		}
		ControllerConfig config = (ControllerConfig) ClassUtil.newInstance(className);
		if (config == null) {
			logger.warn(StringUtils.unite("controller config ", className, " init error use the default configuration"));
			config = new DefaultControllerConfig();
		}
		controllerProcessor = new ControllerProcessor(config);
		httpDataCheckers = config.getDataChecker();
		logger.info(StringUtils.unite("controller config is ", config.getClass()));
		logger.info(StringUtils.unite("controller data checker is ", httpDataCheckers == null ? null : httpDataCheckers.getClass()));
		logger.info(StringUtils.unite("controller encoding is ", config.getCharacterEncoding()));
		logger.info(StringUtils.unite("controller return string prefix is ", config.getPrefix()));
		logger.info(StringUtils.unite("controller return string suffix is ", config.getSuffix()));
		logger.info(StringUtils.unite("controller packages is ", Arrays.asList(config.getControllersPackages())));
		logger.info("init end");
	}

	@Override
	protected final void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		HttpBaseController.setControllerContext(new ControllerContext(httpRequest, httpResponse));
		if (httpDataCheckers == null || httpDataCheckers.inCheck(httpRequest, httpResponse, null)) {
			super.service(httpRequest, httpResponse);
		}
	}

	@Override
	protected final void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		controllerProcessor.requestExecute(httpRequest, httpResponse, RequestMethod.GET);
	}

	@Override
	protected final void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
		controllerProcessor.requestExecute(httpRequest, httpResponse, RequestMethod.POST);
	}
}