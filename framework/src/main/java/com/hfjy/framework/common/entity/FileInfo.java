package com.hfjy.framework.common.entity;

import java.io.Serializable;

public class FileInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	private String path;
	private String name;
	private byte[] data;
	private String checksum;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

	@Override
	protected FileInfo clone() throws CloneNotSupportedException {
		FileInfo fileInfo = (FileInfo) super.clone();
		fileInfo.data = data.clone();
		return fileInfo;
	}
}
