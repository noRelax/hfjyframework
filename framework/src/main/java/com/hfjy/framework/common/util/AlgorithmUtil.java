package com.hfjy.framework.common.util;

import java.util.Stack;

public class AlgorithmUtil {

	public void bubbleSort(int[] array) {
		for (int i = 0; i < array.length - 1; i++) {
			for (int t = 0; t < array.length - i - 1; t++) {
				if (array[t] >= array[t + 1]) {
					int tmp = array[t];
					array[t] = array[t + 1];
					array[t + 1] = tmp;
				}
			}
		}
	}

	public void quickSort(int[] array) {
		if (array == null || array.length < 0) {
			return;
		}
		Stack<Integer> startStack = new Stack<Integer>();
		Stack<Integer> endStack = new Stack<Integer>();
		int start = 0;
		int end = array.length - 1;
		int pivotPos;
		startStack.push(start);
		endStack.push(end);
		while (!startStack.isEmpty()) {
			start = startStack.pop();
			end = endStack.pop();
			pivotPos = partition(array, start, end);
			if (start < pivotPos - 1) {
				startStack.push(start);
				endStack.push(pivotPos - 1);
			}
			if (end > pivotPos + 1) {
				startStack.push(pivotPos + 1);
				endStack.push(end);
			}
		}
	}

	private int partition(int[] a, int start, int end) {
		int pivot = a[start];
		while (start < end) {
			while (start < end && a[end] >= pivot)
				end--;
			a[start] = a[end];
			while (start < end && a[start] <= pivot)
				start++;
			a[end] = a[start];
		}
		a[start] = pivot;
		return start;
	}

	public static String to_2(long numType, long num) {
		String t = "";
		long tmp = to_10(numType, num);
		long i = 0;
		while (tmp / 2 != 0) {
			t = tmp % 2 + t;
			tmp = tmp / 2;
			t = tmp / 2 == 0 ? tmp + t : t;
			i++;
		}
		t = i == 0 ? tmp + t : t;
		return t;
	}

	public static String to_8(long numType, long num) {
		String t = "";
		long tmp = to_10(numType, num);
		long i = 0;
		while (tmp / 8 != 0) {
			t = tmp % 8 + t;
			tmp = tmp / 8;
			t = tmp / 8 == 0 ? tmp + t : t;
			i++;
		}
		t = i == 0 ? tmp + t : t;
		return t;
	}

	public static long to_10(long numType, long num) {
		if (numType == 16) {
			return Long.valueOf(num);
		}
		char[] tmp = (num + "").toCharArray();
		int newNum = 0;
		for (int i = 0; i < tmp.length; i++) {
			if (i == 0) {
				continue;
			}
			try {
				int t = Integer.valueOf(tmp[i]);
				for (int q = 0; q < tmp.length - i - 1; q++) {
					t *= numType;
				}
				newNum += t;
			} catch (Exception e) {
				return 0;
			}
		}
		return newNum;
	}

	public static String to_16(long numType, long num) {
		String t = "";
		long tmp = to_10(numType, num);
		long i = 0;
		while (tmp / 16 != 0) {
			t = tmp % 16 == 10 ? "A" + t : t;
			t = tmp % 16 == 11 ? "B" + t : t;
			t = tmp % 16 == 12 ? "C" + t : t;
			t = tmp % 16 == 13 ? "D" + t : t;
			t = tmp % 16 == 14 ? "E" + t : t;
			t = tmp % 16 == 15 ? "F" + t : t;
			t = tmp % 16 < 10 ? tmp % 16 + t : t;
			tmp = tmp / 16;
			t = tmp / 16 == 0 ? tmp + t : t;
			i++;
		}
		t = i == 0 ? tmp + t : t;
		return "0x" + t;
	}

	public static String to_36(long num) {
		StringBuilder temp = new StringBuilder();
		do {
			long tmp = num % 36;
			temp = tmp > 9 ? temp.insert(0, (char) (tmp + 55)) : temp.insert(0, tmp);
			num = num / 36;
			temp = num != 0 && num / 36 == 0 ? temp.insert(0, to_36(num)) : temp;
		} while (num / 36 != 0);
		return temp.toString();
	}
}
