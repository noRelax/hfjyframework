package com.hfjy.framework.common.util;

import java.util.ArrayList;

public class StringUtils {

	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() < 1;
	}

	public static boolean isEmpty(Object object) {
		return null == object || isEmpty(object + "");
	}

	public static boolean isNotEmpty(String string) {
		return string != null && string.trim().length() > 0;
	}

	public static boolean isNotEmpty(Object object) {
		return object != null && object.toString().length() > 0;
	}

	public static boolean isNotEmpty(Object[] object) {
		return object != null && object.length > 0;
	}

	public static String unite(Object... values) {
		if (values == null) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < values.length; i++) {
			sb.append(values[i]);
		}
		return sb.toString();
	}

	/**
	 * @author chumingqiang
	 * @date 2014年5月6日 下午1:58:03
	 * @version V1.0
	 * @param strValue
	 * @return 字符串长度
	 */
	public static int length(String strValue) {
		if (strValue == null) {
			return 0;
		}
		return strValue.length();
	}

	/**
	 * @author chumingqiang
	 * @date 2014年5月6日 下午1:58:35
	 * @version V1.0
	 * @param strValue1
	 * @param strValue2
	 * @param bIgnoreCase
	 *            是否忽略大小写
	 * @return
	 */
	public static int compare(String strValue1, String strValue2, boolean bIgnoreCase) {
		if (strValue1 == null || strValue2 == null) {
			return -1;
		}
		if (bIgnoreCase) {
			return strValue1.compareToIgnoreCase(strValue2);
		} else {
			return strValue1.compareTo(strValue2);
		}
	}

	/**
	 * 通过字符拆分字符串
	 * 
	 * @author chumingqiang
	 * @date 2014年5月6日 下午2:00:32
	 * @version V1.0
	 * @param strValue
	 * @param chSeperator
	 * @return
	 */
	public static String[] split(String strValue, char chSeperator) {
		if (length(strValue) == 0)
			return null;

		ArrayList<String> arrList = new ArrayList<String>();
		while (true) {
			int nPos = strValue.indexOf(chSeperator);
			if (nPos != -1) {
				String strPartA = strValue.substring(0, nPos);
				arrList.add(strPartA);
				strValue = strValue.substring(nPos + 1);
			} else {
				arrList.add(strValue);
				break;
			}
		}

		String[] strList = new String[arrList.size()];
		arrList.toArray(strList);
		return strList;

	}

	/**
	 * 取左边空格
	 * 
	 * @author chumingqiang
	 * @date 2014年5月6日 下午2:01:05
	 * @version V1.0
	 * @param strValue
	 * @return
	 */
	public static String trimLeft(String strValue) {
		return trimLeft(strValue, ' ');
	}

	/**
	 * 去右边指定字符
	 * 
	 * @author chumingqiang
	 * @date 2014年5月6日 下午2:01:19
	 * @version V1.0
	 * @param strValue
	 * @param ch
	 * @return
	 */
	public static String trimLeft(String strValue, char ch) {
		while (strValue.length() > 0) {
			if (strValue.charAt(0) == ch) {
				strValue = strValue.substring(1);
			} else
				break;
		}
		return strValue;
	}

	/**
	 * 把第一个字母变成大写
	 * 
	 * @author chumingqiang
	 * @date 2014年5月6日 下午2:06:20
	 * @version V1.0
	 * @param strValue
	 * @return
	 */
	public static String makeStrFirstToUp(String strValue) {
		char[] tempArray = strValue.toCharArray();
		tempArray[0] = Character.toUpperCase(tempArray[0]);
		return new String(tempArray);
	}

	/**
	 * 把第一个字母变成小写
	 * 
	 * @author chumingqiang
	 * @date 2014年5月6日 下午2:06:50
	 * @version V1.0
	 * @param strValue
	 * @return
	 */
	public static String makeStrFirstToLower(String strValue) {
		char[] tempArray = strValue.toCharArray();
		tempArray[0] = Character.toLowerCase(tempArray[0]);
		return new String(tempArray);
	}

	/**
	 * 截取字符串前段部分
	 * 
	 * @author cmq
	 * @date 2014年12月30日 下午4:54:59
	 * @throws
	 * @param strValue
	 * @param ch
	 */
	public static String substringBefore(String strValue, String ch) {
		return strValue.substring(0, strValue.indexOf(ch));
	}

	/**
	 * 截取字符串后段部分
	 * 
	 * @author cmq
	 * @date 2014年12月30日 下午4:55:23
	 * @throws
	 * @param strValue
	 * @param ch
	 * @return
	 */
	public static String substringAfter(String strValue, String ch) {
		return strValue.substring(strValue.indexOf(ch) + 1);
	}

	/**
	 * 比较字符串忽略大小写
	 * 
	 * @author cmq
	 * @date 2014年12月30日 下午4:59:09
	 * @throws
	 * @param strValue1
	 * @param strValue2
	 * @return
	 */
	public static boolean equalsIgnoreCase(String strValue1, String strValue2) {
		if (strValue1 == null || strValue2 == null) {
			return false;
		} else {
			return strValue1.equalsIgnoreCase(strValue2);
		}
	}

	/**
	 * 把中文转成Unicode码
	 * 
	 * @param str
	 * @return
	 */
	public static String chinaToUnicode(String str) {
		String result = "";
		for (int i = 0; i < str.length(); i++) {
			int chr1 = (char) str.charAt(i);
			if (chr1 >= 19968 && chr1 <= 171941) {// 汉字范围 \u4e00-\u9fa5 (中文)
				result += "\\u" + Integer.toHexString(chr1);
			} else {
				result += str.charAt(i);
			}
		}
		return result;
	}

	/**
	 * 判断是否为中文字符
	 * 
	 * @param c
	 * @return
	 */
	public static boolean isChinese(char c) {
		Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
		if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
			return true;
		}
		return false;
	}

	public static float getSimilarityRatio(String thisString, String thatString) {
		return 1 - (float) compare(thisString, thatString) / Math.max(thisString.length(), thatString.length());
	}

	private static int compare(String thisString, String thatString) {
		int n = thisString.length();
		int m = thatString.length();
		if (n == 0) {
			return m;
		}
		if (m == 0) {
			return n;
		}
		int[][] ints = new int[n + 1][m + 1];
		int i;
		for (i = 0; i <= n; i++) {
			ints[i][0] = i;
		}
		int j;
		for (j = 0; j <= m; j++) {
			ints[0][j] = j;
		}
		int temp;
		for (i = 1; i <= n; i++) {
			char a = thisString.charAt(i - 1);
			for (j = 1; j <= m; j++) {
				char b = thatString.charAt(j - 1);
				if (a == b) {
					temp = 0;
				} else {
					temp = 1;
				}
				int one = ints[i - 1][j] + 1;
				int two = ints[i][j - 1] + 1;
				int three = ints[i - 1][j - 1] + temp;
				ints[i][j] = (one = one < two ? one : two) < three ? one : three;
			}
		}
		return ints[n][m];
	}
	
	/**
	 * @param thisString 当前的字符串，中间以regex规则分割  不支持有些特殊的分隔符
	 * @param regex 字符串截取的规则
	 * @param extraItem 要去除的多余重复的元素
	 * @return
	 */
	public static String removeStrExtraItems(String thisString,String regex,String extraItem){
		StringBuffer versionNum=new StringBuffer();
		String[] strs;
		if (regex.endsWith(".")) {
			strs = thisString.split("\\"+regex);
		}else {
			strs = thisString.split(regex);
		}
		for (String str : strs) {
			if (str.startsWith(extraItem)) {
				char ch = str.charAt(1);
				versionNum.append(regex).append(ch);
			}else {
				versionNum.append(regex).append(str);
			}
		}
		return versionNum.toString().substring(1);
	}
}
