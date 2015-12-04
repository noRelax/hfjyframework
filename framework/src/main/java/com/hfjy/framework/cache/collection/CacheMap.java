package com.hfjy.framework.cache.collection;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.hfjy.framework.cache.CacheAccess;
import com.hfjy.framework.cache.CacheUtil;
import com.hfjy.framework.cache.base.CacheMapAccess;

@SuppressWarnings("unchecked")
public class CacheMap<K, V> implements Map<K, V> {
	private final CacheMapAccess<K, V> cacheAccess;
	private final String code;

	public CacheMap(String code) {
		this.code = code;
		this.cacheAccess = CacheUtil.getCacheAccess();
	}

	public <T extends CacheAccess<?, ?>> CacheMap(Class<T> clazz, String code) {
		this.code = code;
		this.cacheAccess = CacheUtil.getCacheAccess(clazz);
	}

	@Override
	public int size() {
		return cacheAccess.getMapSize(code);
	}

	@Override
	public boolean isEmpty() {
		return cacheAccess.existsMap(code);
	}

	@Override
	public boolean containsKey(Object key) {
		return cacheAccess.existsMapKey(code, (K) key);
	}

	@Override
	public boolean containsValue(Object value) {
		return getMap().containsValue(value);
	}

	@Override
	public V get(Object key) {
		return cacheAccess.getMapValue(code, (K) key);
	}

	@Override
	public V put(K key, V value) {
		cacheAccess.setMapValue(code, key, value);
		return value;
	}

	@Override
	public V remove(Object key) {
		V tmp = get(key);
		cacheAccess.removeMapKey(code, (K) key);
		return tmp;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		Map<K, V> map = getMap();
		map.putAll(m);
		setMap(map);
	}

	@Override
	public void clear() {
		cacheAccess.removeMap(code);
	}

	@Override
	public Set<K> keySet() {
		return getMap().keySet();
	}

	@Override
	public Collection<V> values() {
		return getMap().values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return getMap().entrySet();
	}

	private Map<K, V> getMap() {
		Map<K, V> tmpMap = cacheAccess.getMap(code);
		if (tmpMap == null) {
			tmpMap = new HashMap<K, V>();
		}
		return tmpMap;
	}

	private boolean setMap(Map<K, V> map) {
		if (map != null && map.size() > 0) {
			return cacheAccess.setMap(code, map);
		}
		return false;
	}

	@Override
	public String toString() {
		Map<K, V> map = new HashMap<>();
		map.putAll(this);
		return map.toString();
	}
}