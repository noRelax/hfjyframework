package com.hfjy.framework.message;

import java.io.Serializable;

public class MessageData implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;
	String name;
	byte[] data;

	public MessageData(String name, byte[] data) {
		this.name = name;
		this.data = data;
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

	@Override
	protected MessageData clone() throws CloneNotSupportedException {
		return (MessageData) super.clone();
	}
}
