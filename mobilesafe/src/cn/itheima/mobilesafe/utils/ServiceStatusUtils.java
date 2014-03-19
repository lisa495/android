package cn.itheima.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceStatusUtils {

	/**
	 * �жϷ����Ƿ�������״̬.
	 * @param servicename
	 * @param context
	 * @return
	 */
	public static boolean isServiceRunning(String servicename,Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo>  infos = am.getRunningServices(100);
		for(RunningServiceInfo info: infos){
			if(servicename.equals(info.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}
