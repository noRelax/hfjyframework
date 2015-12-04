package com.hfjy.framework.cache;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;

import com.hfjy.framework.cache.collection.CacheMap;
import com.hfjy.framework.common.util.DateUtils;
import com.hfjy.framework.common.util.EncryptUtil;
import com.hfjy.framework.common.util.JsonUtil;
import com.hfjy.framework.common.util.RandomUtil;
import com.hfjy.framework.common.util.StringUtils;
import com.hfjy.framework.init.Initial;
import com.hfjy.framework.logging.LoggerFactory;

public class CacheHandleResultManager {
	private static final Logger logger = LoggerFactory.getLogger(CacheHandleResultManager.class);
	private static final Map<Integer, Map<String, Object>> localMethodResultMap = new HashMap<>();
	private static final Map<Integer, Map<String, Integer>> localMethodResultCountMap = new HashMap<>();
	private static final Map<Integer, Map<String, Long>> localMethodResultOutdateMap = new HashMap<>();

	public Object getHandleResult(Method method, Object[] args) {
		if (Initial.SYSTEM_IS_DEBUG) {
			return null;
		}
		Object result = null;
		Cache cache = method.getAnnotation(Cache.class);
		if (cache != null || Initial.SYSTEM_IS_ALL_STATIC) {
			boolean local;
			boolean refresh;
			if (Initial.SYSTEM_IS_ALL_STATIC) {
				local = Initial.SYSTEM_IS_ALL_STATIC_LOCAL;
				refresh = false;
			} else {
				local = cache.local();
				refresh = cache.refresh();
			}
			if (local || !Initial.CACHE_ACCESS_INIT_OK) {
				if (refresh) {
					if (cache.count() > 0) {
						addLocalCount(method, args);
						checkLocalCount(method, args, cache.count());
					}
					if (cache.outdate() > 0) {
						checkLocalOutdate(method, args);
					}
				}
				result = getLocalCache(method, args);
			} else {
				if (refresh) {
					if (cache.count() > 0) {
						addRemoteCount(method, args);
						checkRemoteCount(method, args, cache.count());
					}
					if (cache.outdate() > 0) {
						checkRemoteOutdate(method, args);
					}
				}
				result = getRemoteCache(method, args);
			}
		}
		return result;
	}

	public void setHandleResult(Method method, Object[] args, Object result) {
		if (Initial.SYSTEM_IS_DEBUG) {
			return;
		}
		Cache cache = method.getAnnotation(Cache.class);
		if (cache != null || Initial.SYSTEM_IS_ALL_STATIC) {
			boolean local;
			boolean refresh;
			if (Initial.SYSTEM_IS_ALL_STATIC) {
				local = Initial.SYSTEM_IS_ALL_STATIC_LOCAL;
				refresh = false;
			} else {
				local = cache.local();
				refresh = cache.refresh();
			}
			if (local || !Initial.CACHE_ACCESS_INIT_OK) {
				setLocalCache(method, args, result);
				if (refresh) {
					if (cache.max() > 0) {
						checkLocalMax(method, cache.max());
					}
					if (cache.count() > 0) {
						setLocalCount(method, args);
					}
					if (cache.outdate() > 0) {
						Date nextDate = DateUtils.nextDate(new Date(), cache.dateType(), (int) cache.outdate());
						setLocalOutdate(method, args, nextDate);
					}
				}
			} else {
				setRemoteCache(method, args, result);
				if (refresh) {
					if (cache.max() > 0) {
						checkRemoteMax(method, cache.max());
					}
					if (cache.count() > 0) {
						setRemoteCount(method, args);
					}
					if (cache.outdate() > 0) {
						Date nextDate = DateUtils.nextDate(new Date(), cache.dateType(), (int) cache.outdate());
						setRemoteOutdate(method, args, nextDate);
					}
				}
			}
		}
	}

	private Object getLocalCache(Method method, Object[] args) {
		Map<String, Object> resultMap = localMethodResultMap.get(method.hashCode());
		if (resultMap != null) {
			return resultMap.get(getKey(args));
		} else {
			return null;
		}
	}

	private Object getRemoteCache(Method method, Object[] args) {
		Map<String, Object> resultMap = new CacheMap<>(getCode(method));
		return resultMap.get(getKey(args));
	}

	private void setLocalCache(Method method, Object[] args, Object result) {
		Map<String, Object> resultMap = localMethodResultMap.get(method.hashCode());
		if (resultMap == null) {
			resultMap = new LinkedHashMap<>();
		}
		resultMap.put(getKey(args), result);
		localMethodResultMap.put(method.hashCode(), resultMap);
	}

	private void setRemoteCache(Method method, Object[] args, Object result) {
		Map<String, Object> resultMap = new CacheMap<>(getCode(method));
		resultMap.put(getKey(args), result);
	}

	private void checkLocalMax(Method method, long max) {
		Map<String, Object> resultMap = localMethodResultMap.get(method.hashCode());
		if (resultMap.size() > max) {
			for (int i = 0; i < resultMap.size() - max; i++) {
				delLocalCache(method);
			}
		}
	}

	private void checkRemoteMax(Method method, long max) {
		Map<String, Object> resultMap = new CacheMap<>(getCode(method));
		if (resultMap.size() > max) {
			for (int i = 0; i < resultMap.size() - max; i++) {
				delRemoteCache(method);
			}
		}
	}

	private void checkLocalCount(Method method, Object[] args, long count) {
		Map<String, Integer> countMap = localMethodResultCountMap.get(method.hashCode());
		if (countMap != null) {
			Integer nowCount = countMap.get(getKey(args));
			if (nowCount != null && nowCount >= count) {
				delLocalCache(method, args);
			}
		}
	}

	private void checkRemoteCount(Method method, Object[] args, long count) {
		Map<String, Integer> countMap = new CacheMap<>(getCountCode(method));
		if (countMap != null) {
			Integer nowCount = countMap.get(getKey(args));
			if (nowCount != null && nowCount >= count) {
				delRemoteCache(method, args);
			}
		}
	}

	private void checkLocalOutdate(Method method, Object[] args) {
		Map<String, Long> outdateMap = localMethodResultOutdateMap.get(method.hashCode());
		if (outdateMap != null) {
			Long endDate = outdateMap.get(getKey(args));
			if (endDate != null) {
				if (System.currentTimeMillis() > endDate) {
					delLocalCache(method, args);
				}
			}

		}
	}

	private void checkRemoteOutdate(Method method, Object[] args) {
		Map<String, Long> outdateMap = new CacheMap<>(getOutdateCode(method));
		if (outdateMap != null) {
			Long endDate = outdateMap.get(getKey(args));
			if (endDate != null) {
				if (System.currentTimeMillis() > endDate) {
					delLocalCache(method, args);
				}
			}
		}
	}

	private void delLocalCache(Method method) {
		Map<String, Object> resultMap = localMethodResultMap.get(method.hashCode());
		if (resultMap != null) {
			String key = resultMap.keySet().iterator().next();
			resultMap.remove(key);
		}
	}

	private void delLocalCache(Method method, Object[] args) {
		Map<String, Object> resultMap = localMethodResultMap.get(method.hashCode());
		if (resultMap != null) {
			resultMap.remove(getKey(args));
		}
	}

	private void delRemoteCache(Method method) {
		Map<String, Object> resultMap = new CacheMap<>(getCode(method));
		Iterator<String> keys = resultMap.keySet().iterator();
		for (int i = RandomUtil.getInt(resultMap.size()); keys.hasNext(); i--) {
			String key = keys.next();
			if (i == 0) {
				resultMap.remove(key);
				break;
			}
		}
	}

	private void delRemoteCache(Method method, Object[] args) {
		Map<String, Object> resultMap = new CacheMap<>(getCode(method));
		resultMap.remove(getKey(args));
	}

	private void setLocalCount(Method method, Object[] args) {
		Map<String, Integer> countMap = localMethodResultCountMap.get(method.hashCode());
		if (countMap == null) {
			countMap = new LinkedHashMap<>();
		}
		countMap.put(getKey(args), 0);
		localMethodResultCountMap.put(method.hashCode(), countMap);
	}

	private void setRemoteCount(Method method, Object[] args) {
		Map<String, Integer> countMap = new CacheMap<>(getCountCode(method));
		countMap.put(getKey(args), 0);
	}

	private void addLocalCount(Method method, Object[] args) {
		Map<String, Integer> countMap = localMethodResultCountMap.get(method.hashCode());
		if (countMap != null) {
			Integer count = countMap.get(getKey(args));
			if (count != null) {
				count = count + 1;
				countMap.put(getKey(args), count);
			}
		}
	}

	private void addRemoteCount(Method method, Object[] args) {
		Map<String, Integer> countMap = new CacheMap<>(getCountCode(method));
		if (countMap != null) {
			Integer count = countMap.get(getKey(args));
			if (count != null) {
				count = count + 1;
				countMap.put(getKey(args), count);
			}
		}
	}

	private void setLocalOutdate(Method method, Object[] args, Date date) {
		Map<String, Long> outdateMap = localMethodResultOutdateMap.get(method.hashCode());
		if (outdateMap == null) {
			outdateMap = new LinkedHashMap<>();
		}
		outdateMap.put(getKey(args), date.getTime());
		localMethodResultOutdateMap.put(method.hashCode(), outdateMap);
	}

	private void setRemoteOutdate(Method method, Object[] args, Date date) {
		Map<String, Long> outdateMap = new CacheMap<>(getOutdateCode(method));
		outdateMap.put(getKey(args), date.getTime());
	}

	private String getKey(Object[] args) {
		return md5(JsonUtil.toJson(args));
	}

	private String md5(String key) {
		String tmp = key;
		try {
			tmp = EncryptUtil.md5(tmp);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return tmp;
	}

	private String getCode(Method method) {
		return StringUtils.unite(CacheHandleResultManager.class, "#", md5(method.toGenericString()));
	}

	private String getCountCode(Method method) {
		return StringUtils.unite(CacheHandleResultManager.class, "#", md5(method.toGenericString()), "#count");
	}

	private String getOutdateCode(Method method) {
		return StringUtils.unite(CacheHandleResultManager.class, "#", md5(method.toGenericString()), "#outdate");
	}
}
