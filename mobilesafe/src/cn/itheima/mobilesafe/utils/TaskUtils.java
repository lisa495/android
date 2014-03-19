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
	 * ��ȡ�������еĽ��̵ĸ���
	 * 
	 * @param context
	 * @return
	 */
	public static int getRunningProcessCount(Context context) {
		// �õ�ϵͳ�����������.
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		return am.getRunningAppProcesses().size();
	}

	/**
	 * ��ȡ�ֻ��Ŀ����ڴ�
	 * 
	 * @param context
	 * @return long�������� �ܵĿ����ڴ�
	 */
	public static long getAvailRam(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}

	/**
	 * ��ȡ�ֻ������ڴ�
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
