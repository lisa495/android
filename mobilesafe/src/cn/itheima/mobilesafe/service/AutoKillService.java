package cn.itheima.mobilesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class AutoKillService extends Service {

	public static final String TAG = "AutoKillService";
	private LockScreenReceiver receiver;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		receiver = new LockScreenReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(1000);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}
	private class LockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG,"∆¡ƒªÀ¯∆¡¡À....");
			ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
			List<RunningAppProcessInfo>  infos = am.getRunningAppProcesses();
			for(RunningAppProcessInfo info : infos){
				am.killBackgroundProcesses(info.processName);
			}
		}
	}
}
