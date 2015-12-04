package com.hfjy.framework.cache.base;

import java.util.Date;

public interface CacheDataAccess<K, V> {

	boolean setTimeOut(K key, int seconds);

	boolean setTimeOut(K key, Date date);

	boolean cancelTimeOut(K key);

	boolean clear();
}
