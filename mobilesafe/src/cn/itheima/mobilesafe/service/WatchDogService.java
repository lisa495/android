package cn.itheima.mobilesafe.service;

import java.util.ArrayList;
import java.util.List;

import cn.itheima.mobilesafe.EnterPasswrodActivity;
import cn.itheima.mobilesafe.db.dao.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

public class WatchDogService extends Service {
	protected static final String TAG = "WatchDogService";
	private ActivityManager am;
	private AppLockDao dao;
	private Intent intent;
	private boolean flag;
	private List<String> tempStopProtectPacknames;
	private InnerLockScreenReceiver lockScreenReceiver;
	private InnerUnLockScreenReceiver unlockScreenReceiver;

	private List<String> lockedPackNames;

	private MyObserver observer;

	@Override
	public IBinder onBind(Intent intent) {

		return new MyBinder();
	}

	private class MyBinder extends Binder implements IService {

		@Override
		public void callMethodInService(String packname) {
			tempStopProtecet(packname);
		}

	}

	private void tempStopProtecet(String packname) {
		tempStopProtectPacknames.add(packname);
	}

	private class InnerLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			tempStopProtectPacknames.clear();
			flag = false;
		}

	}

	private class InnerUnLockScreenReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (!flag) {// 保险
				startWatchDog();
			}
		}

	}

	@Override
	public void onCreate() {
		tempStopProtectPacknames = new ArrayList<String>();

		lockScreenReceiver = new InnerLockScreenReceiver();
		IntentFilter lockFilter = new IntentFilter();
		lockFilter.addAction(Intent.ACTION_SCREEN_OFF);
		lockFilter.setPriority(1000);
		registerReceiver(lockScreenReceiver, lockFilter);

		unlockScreenReceiver = new InnerUnLockScreenReceiver();
		IntentFilter unlockFilter = new IntentFilter();
		unlockFilter.addAction(Intent.ACTION_SCREEN_ON);
		unlockFilter.setPriority(1000);
		registerReceiver(unlockScreenReceiver, unlockFilter);

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		lockedPackNames = dao.findAll();
		intent = new Intent(this, EnterPasswrodActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		observer = new MyObserver(new Handler());
		getContentResolver().registerContentObserver(AppLockDao.uri, true,
				observer);

		super.onCreate();
		// 开启看门狗 监视 当前系统的程序运行信息.
		startWatchDog();

	}

	private void startWatchDog() {
		new Thread() {
			public void run() {
				flag = true;
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					RunningTaskInfo taskinfo = infos.get(0);// 最新打开的任务栈.
					ComponentName topActivity = taskinfo.topActivity;// 得到栈顶的activity
																		// 用户可见的activity
					String packname = topActivity.getPackageName();
					Log.i(TAG, "packname=" + packname);
					// if (dao.find(packname)) {// 需要保护这个应用 查询数据库 慢!
					if (lockedPackNames.contains(packname)) { // 查询内存
						// 判断当前包名是否要临时的停止保护.
						if (tempStopProtectPacknames.contains(packname)) {

						} else {
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					}
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();
	}

	@Override
	public void onDestroy() {
		flag = false;
		unregisterReceiver(lockScreenReceiver);
		lockScreenReceiver = null;
		getContentResolver().unregisterContentObserver(observer);
		observer = null;
		unregisterReceiver(unlockScreenReceiver);
		unlockScreenReceiver = null;
		super.onDestroy();
	}

	private class MyObserver extends ContentObserver {

		public MyObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onChange(boolean selfChange) {
			Log.i(TAG, "观察到数据变化了..........");
			lockedPackNames = dao.findAll();
			super.onChange(selfChange);
		}

	}
}
