package com.hfjy.framework.cache.base;

import java.util.List;
import java.util.Set;

public interface CacheListAccess<K, V> extends CacheDataAccess<K, V> {

	Set<String> getListCodes();

	boolean existsList(String code);

	boolean setList(String code, List<V> list);

	List<V> getList(String code);

	boolean addListValue(String code, V value);

	boolean setListValue(String code, Integer index, V value);

	V getListValue(String code, Integer index);

	List<V> getListValues(String code, Integer begin, Integer end);

	V removeListFirst(String code);

	Integer getListSize(String code);

	boolean removeList(String code);
}
