package cn.itheima.mobilesafe.utils;

import android.app.Activity;
import android.content.Intent;

public class ActivityUtils {
	/**
	 * �����µ�activity ���ҹرյ��Լ�
	 */
	public static void startActivityAndFinish(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
		context.finish();
	}
	
	
	/**
	 * �����µ�activity 
	 */
	public static void startActivity(Activity context, Class<?> cls){
		Intent intent = new Intent(context,cls);
		context.startActivity(intent);
	}
}
