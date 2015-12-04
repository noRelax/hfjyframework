package com.hfjy.framework.net.http.entity;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.Part;

import com.hfjy.framework.net.http.util.ServletUril;

public class MultipartFile {

	public MultipartFile() {
	}

	public MultipartFile(Part part) throws IOException {
		contentType = part.getContentType();
		inputStream = part.getInputStream();
		size = part.getSize();
		name = part.getName();
		originalFilename = ServletUril.getPartFileName(part);
	}

	private String name;

	private String originalFilename;

	private String contentType;

	private byte[] bytes;

	private long size;

	private InputStream inputStream;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte[] getBytes() throws IOException {
		if (bytes == null && inputStream != null && inputStream.available() > 0) {
			bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
		}
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public boolean isEmpty() {
		return bytes == null || bytes.length == 0;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public void setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
	}
}
