package com.hfjy.framework.cache.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.hfjy.framework.cache.CacheAccess;
import com.hfjy.framework.cache.CacheUtil;
import com.hfjy.framework.cache.base.CacheListAccess;

public class CacheList<V> implements List<V> {
	protected final CacheListAccess<String, V> cacheListAccess;
	protected final String code;

	public CacheList(String code) {
		this.code = code;
		this.cacheListAccess = CacheUtil.getCacheAccess();
	}

	public <T extends CacheAccess<?, ?>> CacheList(Class<T> clazz, String code) {
		this.code = code;
		this.cacheListAccess = CacheUtil.getCacheAccess(clazz);
	}

	@Override
	public int size() {
		return cacheListAccess.getListSize(code);
	}

	@Override
	public boolean isEmpty() {
		return cacheListAccess.existsList(code);
	}

	@Override
	public boolean contains(Object o) {
		return getList().contains(o);
	}

	@Override
	public Iterator<V> iterator() {
		return getList().iterator();
	}

	@Override
	public Object[] toArray() {
		return getList().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return getList().toArray(a);
	}

	@Override
	public boolean add(V e) {
		return cacheListAccess.addListValue(code, e);
	}

	@Override
	public boolean remove(Object o) {
		List<V> list = getList();
		list.remove(o);
		return setList(list);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		List<V> list = getList();
		list.containsAll(c);
		return setList(list);
	}

	@Override
	public boolean addAll(Collection<? extends V> c) {
		List<V> list = getList();
		list.addAll(c);
		return setList(list);
	}

	@Override
	public boolean addAll(int index, Collection<? extends V> c) {
		List<V> list = getList();
		list.addAll(index, c);
		return setList(list);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		List<V> list = getList();
		list.removeAll(c);
		return setList(list);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		List<V> list = getList();
		list.retainAll(c);
		return setList(list);
	}

	@Override
	public void clear() {
		cacheListAccess.removeList(code);
	}

	@Override
	public V get(int index) {
		return cacheListAccess.getListValue(code, index);
	}

	@Override
	public V set(int index, V element) {
		cacheListAccess.setListValue(code, index, element);
		return element;
	}

	@Override
	public void add(int index, V element) {
		List<V> list = getList();
		list.add(index, element);
		setList(list);
	}

	@Override
	public V remove(int index) {
		List<V> list = getList();
		V tmp = list.remove(index);
		setList(list);
		return tmp;
	}

	@Override
	public int indexOf(Object o) {
		return getList().indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return getList().lastIndexOf(o);
	}

	@Override
	public ListIterator<V> listIterator() {
		return getList().listIterator();
	}

	@Override
	public ListIterator<V> listIterator(int index) {
		return getList().listIterator(index);
	}

	@Override
	public List<V> subList(int fromIndex, int toIndex) {
		List<V> list = getList();
		list = list.subList(fromIndex, toIndex);
		setList(list);
		return list;
	}

	protected List<V> getList() {
		List<V> tmpList = cacheListAccess.getList(code);
		if (tmpList == null) {
			tmpList = new ArrayList<V>();
		}
		return tmpList;
	}

	protected boolean setList(List<V> list) {
		if (list != null && list.size() > 0) {
			return cacheListAccess.setList(code, list);
		}
		return false;
	}

	@Override
	public String toString() {
		List<V> list = new ArrayList<V>();
		list.addAll(this);
		return list.toString();
	}
}