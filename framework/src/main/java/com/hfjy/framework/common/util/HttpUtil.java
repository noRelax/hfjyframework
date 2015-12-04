package com.hfjy.framework.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Properties;

import org.slf4j.Logger;

import com.hfjy.framework.init.Initial;
import com.hfjy.framework.logging.LoggerFactory;

public class HttpUtil {
	private final static Logger logger = LoggerFactory.getLogger(HttpUtil.class);

	private static String javaSendText(byte[] mXmlStr, String url, Properties requestHeaderProperty) throws IOException {
		URLConnection httpConn = new URL(url).openConnection();
		if (requestHeaderProperty != null) {
			Iterator<Object> keys = requestHeaderProperty.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = requestHeaderProperty.getProperty(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		httpConn.setConnectTimeout(Initial.SYSTEM_DEFAULT_WAIT_TIMEOUT);
		httpConn.setReadTimeout(Initial.SYSTEM_DEFAULT_WAIT_TIMEOUT);
		httpConn.setDoOutput(true);
		httpConn.getOutputStream().write(mXmlStr);
		httpConn.getOutputStream().close();
		InputStream in = httpConn.getInputStream();
		byte[] date = new byte[in.available()];
		in.read(date);
		in.close();
		return new String(date, Initial.SYSTEM_DEFAULT_CHARSET);
	}

	public static String sendText(byte[] mXmlStr, String url, Properties requestHeaderProperty) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(javaSendText(mXmlStr, url, requestHeaderProperty));
		logger.debug("Begins to transmit data");
		logger.debug("Call address is " + url);
		logger.debug("Send an Data is " + new String(mXmlStr, Initial.SYSTEM_DEFAULT_CHARSET));
		logger.debug("Return an Data is " + sb.toString());
		return sb.toString();
	}

	public static String httpRequest(String urlStr, String method, Properties requestProperty, Properties requestHeaderProperty) throws IOException {
		URL url = null;
		URLConnection httpConn = null;
		if (method != null && method.equals("post")) {
			url = new URL(urlStr);
			httpConn = url.openConnection();
		} else {
			urlStr = urlStr + "?" + getParameterStr(requestProperty);
			url = new URL(urlStr);
			httpConn = url.openConnection();
		}
		if (requestHeaderProperty != null) {
			Iterator<Object> keys = requestHeaderProperty.keySet().iterator();
			while (keys.hasNext()) {
				String key = keys.next().toString();
				String value = requestHeaderProperty.getProperty(key);
				httpConn.setRequestProperty(key, value);
			}
		}
		if (method != null && method.equals("post")) {
			httpConn.setDoOutput(true);
			OutputStreamWriter out = new OutputStreamWriter(httpConn.getOutputStream(), Initial.SYSTEM_DEFAULT_CHARSET);
			out.write(getParameterStr(requestProperty));
			out.flush();
			out.close();
		}
		InputStream in = httpConn.getInputStream();
		byte[] data = new byte[in.available()];
		in.read(data);
		in.close();
		return new String(data, Initial.SYSTEM_DEFAULT_CHARSET);
	}

	public static String getParameterStr(Properties requestProperty) throws IOException {
		if (requestProperty != null && requestProperty.size() > 0) {
			StringBuilder sb = new StringBuilder();
			Iterator<Object> iterator = requestProperty.keySet().iterator();
			while (iterator.hasNext()) {
				String key = iterator.next().toString();
				String value = requestProperty.getProperty(key);
				sb.append(key);
				sb.append("=");
				sb.append(URLEncoder.encode(value, Initial.SYSTEM_DEFAULT_CHARSET));
				if (iterator.hasNext()) {
					sb.append("&");
				}
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	public static boolean ping(String host, int port) {
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(host, port));
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public static String getLocalAddress() {
		String hostName = "127.0.0.1";
		try {
			InetAddress ia = InetAddress.getLocalHost();
			hostName = StringUtils.unite(ia.getHostName(), "(", ia.getHostAddress(), ")");
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return hostName;
	}
}
