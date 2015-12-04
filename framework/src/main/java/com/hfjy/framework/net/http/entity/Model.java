package com.hfjy.framework.net.http.entity;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class Model extends LinkedHashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public Model addAttribute(String attributeName, Object attributeValue) {
		this.put(attributeName, attributeValue);
		return this;
	}

	public Model addAttribute(Object attributeValue) {
		put(attributeValue + "", attributeValue);
		return this;
	}

	public Model addAllAttributes(Collection<?> attributeValues) {
		this.values().addAll(attributeValues);
		return this;
	}

	public Model addAllAttributes(Map<String, ?> attributes) {
		this.putAll(attributes);
		return this;
	}

	public Model mergeAttributes(Map<String, ?> attributes) {
		this.putAll(attributes);
		return this;
	}

	public boolean containsAttribute(String attributeName) {
		return this.get(attributeName) != null;
	}

	public Map<String, Object> asMap() {
		return this;
	}
}
