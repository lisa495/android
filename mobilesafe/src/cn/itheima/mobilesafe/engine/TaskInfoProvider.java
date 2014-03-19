package cn.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.itheima.mobilesafe.R;
import cn.itheima.mobilesafe.domain.TaskInfo;

public class TaskInfoProvider {
	/**
	 * 获取手机里面所有的运行的进程信息
	 * @param context
	 * @return
	 */
	public static List<TaskInfo> getTaskInfos(Context context){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();
		List<RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for(RunningAppProcessInfo processInfo: processInfos){
			String packName = processInfo.processName;
			TaskInfo taskInfo = new TaskInfo();
			taskInfo.setPackName(packName);
			
			try {
				ApplicationInfo  applicationInfo =	pm.getApplicationInfo(packName, 0);
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){
					taskInfo.setUserTask(true);
				}else{
					taskInfo.setUserTask(false);
				}
				taskInfo.setTaskIcon(applicationInfo.loadIcon(pm));
				taskInfo.setTaskName(applicationInfo.loadLabel(pm).toString());
			} catch (NameNotFoundException e) {
				e.printStackTrace();
				taskInfo.setTaskIcon(context.getResources().getDrawable(R.drawable.ic_launcher));
				taskInfo.setTaskName(packName);
			}
			long memsize = am.getProcessMemoryInfo(new int[]{processInfo.pid})[0].getTotalPrivateDirty()*1024;
			taskInfo.setMemsize(memsize);
			
			taskInfos.add(taskInfo);
			
		}
		return taskInfos;
	}
}
