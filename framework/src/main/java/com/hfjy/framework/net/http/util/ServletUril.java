package com.hfjy.framework.net.http.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import com.hfjy.framework.net.util.Constant;

public class ServletUril {

	public static boolean isMultiPart(HttpServletRequest httpRequest) {
		if (httpRequest.getContentType() != null) {
			if (httpRequest.getContentType().startsWith(Constant.CONTENT_TYPE_MULTI_PART)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAjaxRequest(HttpServletRequest request) {
		String header = request.getHeader("X-Requested-With");
		return "XMLHttpRequest".equals(header);
	}

	public static String getPartFileName(Part part) {
		String contentDisposition = part.getHeader(Constant.PART_HEADER_CONTENT_DISPOSITION);
		if (contentDisposition == null) {
			return null;
		}
		int begin = contentDisposition.indexOf("filename=");
		if (begin == -1) {
			return null;
		}
		String name = contentDisposition.substring(begin + "filename=".length());
		if (name.startsWith("\"")) {
			int end = name.indexOf("\"", 1);
			if (end != -1) {
				return name.substring(1, end);
			}
		} else {
			int end = name.indexOf(";");
			if (end != -1) {
				return name.substring(0, end);
			}
		}
		return name;
	}
}