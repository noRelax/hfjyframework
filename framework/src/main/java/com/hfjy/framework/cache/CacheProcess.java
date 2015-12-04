package com.hfjy.framework.cache;

import java.util.List;
import java.util.Map;

public interface CacheProcess<K, V> {

	V getCache(K key);

	Map<K, V> getCacheMap();

	List<V> getCacheList();

	boolean initCacheSingle(K key);

	boolean initCache();
}
