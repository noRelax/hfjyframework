package com.hfjy.framework.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.logging.LoggerFactory;

public abstract class CacheProcessor<K, V> implements CacheProcess<K, V> {
	protected static final Logger logger = LoggerFactory.getLogger(CacheProcessor.class);
	private CacheAccess<K, V> cacheAccess;
	private int valueReinitializeNum = getReinitializeNum();
	private int mapReinitializeNum = getReinitializeNum();

	protected <T extends CacheAccess<K, V>> CacheProcessor() {
		cacheAccess = CacheUtil.getCacheAccess();
	}

	protected <T extends CacheAccess<K, V>> CacheProcessor(Class<T> classInfo, Class<K> key, Class<V> value) {
		cacheAccess = CacheUtil.getCacheAccess(classInfo, key, value);
	}

	@Override
	public V getCache(K key) {
		if (valueReinitializeNum > 1) {
			valueReinitializeNum--;
		} else if (valueReinitializeNum == 1) {
			initCacheSingle(key);
		}
		V tmp = cacheAccess.getMapValue(getCode(), key);
		if (tmp == null) {
			if (initCacheSingle(key)) {
				tmp = cacheAccess.getMapValue(getCode(), key);
			}
		}
		if (tmp == null) {
			tmp = getAbsolute(key);
		}
		if (tmp == null) {
			Map<K, V> tmpMap = getCacheMap();
			if (tmpMap != null) {
				tmp = tmpMap.get(key);
			}
		}
		return tmp;
	}

	@Override
	public Map<K, V> getCacheMap() {
		if (mapReinitializeNum > 1) {
			mapReinitializeNum--;
		} else if (mapReinitializeNum == 1) {
			initCache();
		}
		Map<K, V> tmpMap = cacheAccess.getMap(getCode());
		if (tmpMap == null) {
			if (initCache()) {
				tmpMap = cacheAccess.getMap(getCode());
			}
		}
		if (tmpMap == null) {
			tmpMap = getMapAbsolute();
		}
		return tmpMap;
	}

	@Override
	public List<V> getCacheList() {
		if (mapReinitializeNum > 1) {
			mapReinitializeNum--;
		} else if (mapReinitializeNum == 1) {
			initCache();
		}
		List<V> tmpList = cacheAccess.getList(getCode());
		if (tmpList == null) {
			if (initCache()) {
				tmpList = cacheAccess.getList(getCode());
			}
		}
		if (tmpList == null) {
			tmpList = getListAbsolute();
		}
		if (tmpList == null) {
			Map<K, V> tmpMap = getCacheMap();
			if (tmpMap != null) {
				tmpList = new ArrayList<>();
				tmpList.addAll(tmpMap.values());
			}
		}
		return tmpList;
	}

	@Override
	public boolean initCacheSingle(K key) {
		if (valueReinitializeNum == 1) {
			valueReinitializeNum = getReinitializeNum();
		}
		V v = getAbsolute(key);
		if (v != null) {
			return cacheAccess.setMapValue(getCode(), key, v);
		}
		return false;
	}

	@Override
	public boolean initCache() {
		if (mapReinitializeNum == 1) {
			mapReinitializeNum = getReinitializeNum();
		}
		try {
			cacheAccess.removeMap(getCode());
			cacheAccess.removeList(getCode());
			if (!cacheAccess.existsMap(getCode())) {
				Map<K, V> tmpMap = getMapAbsolute();
				if (tmpMap != null) {
					cacheAccess.setMap(getCode(), tmpMap);
				}
			}
			if (!cacheAccess.existsMap(getCode())) {
				List<V> rmpList = getListAbsolute();
				if (rmpList != null) {
					cacheAccess.setList(getCode(), rmpList);
				}
			}
			boolean bl = cacheAccess.existsMap(getCode());
			bl = cacheAccess.existsMap(getCode()) ? true : bl;
			return bl;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	protected int getReinitializeNum() {
		return 0;
	}

	public abstract String getCode();

	public abstract Map<K, V> getMapAbsolute();

	public abstract List<V> getListAbsolute();

	public abstract V getAbsolute(K key);
}
