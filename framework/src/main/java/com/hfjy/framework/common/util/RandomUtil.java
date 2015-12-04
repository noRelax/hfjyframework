package com.hfjy.framework.common.util;

import java.util.List;
import java.util.Random;

public class RandomUtil {
	private final static Random RANDOM = new Random();
	private final static char[] abcChars = new char[52];

	static {
		for (int i = 0; i < abcChars.length; i++) {
			int index = i + 65;
			if (index > 90) {
				index = index + 6;
			}
			abcChars[i] = (char) index;
		}
	}

	public static int getInt(int max) {
		return RANDOM.nextInt(max);
	}

	/**
	 * 产生随机数
	 * 
	 * @return
	 */
	public static String getRanText(int len) {
		StringBuffer text = new StringBuffer();
		for (int i = 0; i < len; i++) {
			text.append(RANDOM.nextInt(10));
		}
		return text.toString();
	}

	/**
	 * 产生随机小写英文字符
	 * 
	 * @return
	 */
	public static String getAbcLowerText(int len) {
		return getABCText(len).toLowerCase();
	}

	/**
	 * 产生随机大写英文字符
	 * 
	 * @return
	 */
	public static String getAbcUpperText(int len) {
		return getABCText(len).toUpperCase();
	}

	/**
	 * 产生随机英文字符
	 * 
	 * @return
	 */
	public static String getABCText(int len) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(abcChars[RANDOM.nextInt(abcChars.length)]);
		}
		return sb.toString();
	}

	public static <T> T getRandomItem(List<T> list) {
		T item = list.get(RANDOM.nextInt(list.size()));
		list.remove(item);
		return item;
	}
}
