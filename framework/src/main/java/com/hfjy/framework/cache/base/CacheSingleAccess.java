package com.hfjy.framework.cache.base;

import java.util.Set;

public interface CacheSingleAccess<K, V> extends CacheDataAccess<K, V> {

	Set<String> getKeys();

	boolean exists(K key);

	boolean set(K key, V value);

	V get(K key);

	boolean remove(K key);
}
