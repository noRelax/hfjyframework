package com.hfjy.framework.cache.collection;

import java.util.Queue;

import com.hfjy.framework.cache.CacheAccess;

public class CacheQueue<V> extends CacheList<V> implements Queue<V> {

	public CacheQueue(String code) {
		super(code);
	}

	public <T extends CacheAccess<?, ?>> CacheQueue(Class<T> clazz, String code) {
		super(clazz, code);
	}

	@Override
	public boolean offer(V e) {
		return add(e);
	}

	@Override
	public V remove() {
		return poll();
	}

	@Override
	public V poll() {
		return cacheListAccess.removeListFirst(code);
	}

	@Override
	public V element() {
		return peek();
	}

	@Override
	public V peek() {
		return cacheListAccess.getListValue(code, 0);
	}
}
