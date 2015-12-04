package com.hfjy.framework.message.entity;

import java.io.Serializable;
import java.util.List;
import java.util.Properties;

public class EmailInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String serverHost;
	private String serverPort = "25";
	private String userName;
	private String password;
	private String my;
	private String to; // 收件人
	private String title; // 邮件标题
	private String data; // 邮件正文
	private List<byte[]> attachment; // 附件
	private boolean verify = false;
	private boolean isHTML = false;

	public Properties getProperties() {
		Properties p = new Properties();
		p.put("mail.smtp.host", serverHost);
		p.put("mail.smtp.port", serverPort);
		p.put("mail.smtp.auth", verify ? "true" : "false");
		return p;
	}

	public EmailInfo(String to, String title, String data) {
		this.to = to;
		this.title = title;
		this.data = data;
	}

	public EmailInfo(String to, String title, String data, List<byte[]> attachment) {
		this.to = to;
		this.title = title;
		this.data = data;
		this.attachment = attachment;
	}

	public String getServerHost() {
		return serverHost;
	}

	public void setServerHost(String serverHost) {
		this.serverHost = serverHost;
	}

	public String getServerPort() {
		return serverPort;
	}

	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMy() {
		return my;
	}

	public void setMy(String my) {
		this.my = my;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public List<byte[]> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<byte[]> attachment) {
		this.attachment = attachment;
	}

	public boolean isVerify() {
		return verify;
	}

	public void setVerify(boolean verify) {
		this.verify = verify;
	}

	public boolean isHTML() {
		return isHTML;
	}

	public void setHTML(boolean isHTML) {
		this.isHTML = isHTML;
	}
}
