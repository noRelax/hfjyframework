package com.hfjy.framework.cache;

import com.hfjy.framework.cache.base.CacheMapAccess;
import com.hfjy.framework.cache.base.CacheListAccess;
import com.hfjy.framework.cache.base.CacheSingleAccess;

public interface CacheAccess<K, V> extends CacheSingleAccess<K, V>, CacheListAccess<K, V>, CacheMapAccess<K, V> {

}