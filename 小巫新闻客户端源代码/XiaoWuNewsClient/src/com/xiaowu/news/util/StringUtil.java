package com.xiaowu.news.util;

public class StringUtil {
	/**
	 * ���ַ���ת��Ϊ����
	 * @param str	
	 * @return
	 */
	public static int string2Int(String str) {
		try {
			int value = Integer.valueOf(str);
			return value;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return 0;
		}
	}
}	
