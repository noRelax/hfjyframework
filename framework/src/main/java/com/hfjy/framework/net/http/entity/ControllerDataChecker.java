package com.hfjy.framework.net.http.entity;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ControllerDataChecker {

	boolean inCheck(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ExecuteResult executeResult) throws ServletException, IOException;

	boolean outCheck(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ExecuteResult executeResult) throws ServletException, IOException;
}
