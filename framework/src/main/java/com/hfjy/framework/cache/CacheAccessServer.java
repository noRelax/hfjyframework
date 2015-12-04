package com.hfjy.framework.cache;

public abstract class CacheAccessServer<T> {

	protected CacheAccessServer() {
		init();
	}

	protected abstract void init();

	public abstract T getClient();

	public abstract boolean destroy(T client);

	public abstract boolean closeAll();

	public abstract boolean clear();

	public abstract String serverInfo();
}
