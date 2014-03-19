package cn.itheima.mobilesafe.utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.test.suitebuilder.annotation.Smoke;

public class TaskUtils {

	/**
	 * 获取正在运行的进程的个数
	 * 
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// 得到系统的任务管理器.
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}

	/**
	 * 获取手机的可用内存
	 * 
	 * @param context
	 * @return long类型数据 总的可用内存
	 */
	public static long getAvailRam(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * 获取手机的总内存
	 * 
	 * @return
	 */
	public static long getTotalRam() {
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			// MemTotal: 253604 kB
			String result = br.readLine();
			StringBuffer sb = new StringBuffer();
			char[] chars = result.toCharArray();
			for (char c : chars) {
				if (c >= '0' && c <= '9') {
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString()) * 1024;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return 0;

		}
	}
}
