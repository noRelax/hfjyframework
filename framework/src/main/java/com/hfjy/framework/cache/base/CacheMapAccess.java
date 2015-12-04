package com.hfjy.framework.cache.base;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CacheMapAccess<K, V> extends CacheDataAccess<K, V> {

	Set<String> getMapCodes();

	boolean existsMap(String code);

	boolean existsMapKey(String code, K key);

	boolean setMap(String code, Map<K, V> map);

	Map<K, V> getMap(String code);

	boolean setMapValue(String code, K key, V value);

	V getMapValue(String code, K key);

	Map<K, V> getMapValues(String code, K[] keys);

	List<V> getMapValueToList(String code, K[] keys);

	boolean removeMapKey(String code, K key);

	boolean removeMapKeys(String code, K[] key);

	Integer getMapSize(String code);

	boolean removeMap(String code);
}
