package com.xiaowu.news.util;

import android.content.Context;

public class DensityUtil {
	
	/**
	 * �����ֻ��ֱ��ʰ�dipת��Ϊpx
	 * @param context
	 * @param dipValue
	 * @return
	 */
	public static int dip2px(Context context, float dipValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dipValue * scale + 0.5f);
	}
	
	/**
	 * ��dipת��Ϊpx
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(pxValue / scale + 0.5f);
	}
}
