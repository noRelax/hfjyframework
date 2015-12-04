package com.hfjy.framework.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.logging.LoggerFactory;

@SuppressWarnings("unchecked")
public class CacheUtil {
	protected static final Logger logger = LoggerFactory.getLogger(CacheProcessor.class);
	private final static Map<String, CacheAccess<?, ?>> cacheAccessMap = new HashMap<>();

	public static <K, V, T extends CacheAccess<?, ?>> CacheAccess<K, V> getCacheAccess(Class<T> cache, Class<K> key, Class<V> value) {
		String code = StringUtils.unite(cache, "#", key, "#", value);
		try {
			if (cacheAccessMap.get(code) == null) {
				cacheAccessMap.put(code, cache.newInstance());
			}
		} catch (InstantiationException | IllegalAccessException e) {
			logger.error(e.getMessage(), e);
		}
		return (CacheAccess<K, V>) cacheAccessMap.get(code);
	}

	public static <K, V, T extends CacheAccess<?, ?>> CacheAccess<K, V> getCacheAccess(Class<T> cache) {
		return (CacheAccess<K, V>) getCacheAccess(cache, Serializable.class, Serializable.class);
	}

	public static <K, V> CacheAccess<K, V> getCacheAccess() {
		CacheAccess<K, V> ca = null;
		try {
			Class<CacheAccess<K, V>> caClass = (Class<CacheAccess<K, V>>) Class.forName(Initial.SYSTEM_ACHIEVE_CACHE_ACCESS);
			ca = (CacheAccess<K, V>) getCacheAccess(caClass, Serializable.class, Serializable.class);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return ca;
	}
}