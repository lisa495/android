package cn.itheima.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import cn.itheima.mobilesafe.R;
import cn.itheima.mobilesafe.receiver.MyWidget;
import cn.itheima.mobilesafe.utils.TaskUtils;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private Timer timer;
	private TimerTask task;
	private AppWidgetManager awm ;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		awm = AppWidgetManager.getInstance(getApplicationContext());	
		// ��������ִ�е�����.
		timer = new Timer();
		task = new TimerTask() {
			
			@Override
			public void run() {
				//����widget
				ComponentName provider = new ComponentName(getApplicationContext(), MyWidget.class);
				RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
				views.setTextViewText(R.id.process_count, "�������е����:"+TaskUtils.getRunningProcessCount(getApplicationContext())+"��");
				views.setTextViewText(R.id.process_memory, "�����ڴ�:"+Formatter.formatFileSize(getApplicationContext(), TaskUtils.getAvailRam(getApplicationContext())));
				Intent intent = new Intent();//�Զ���Ĺ㲥�¼�. 
				intent.setAction("cn.itheima.killbgprocess");
				PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);
				awm.updateAppWidget(provider, views);
			}
		};
		timer.schedule(task, 1000, 2000);
		
		super.onCreate();
	}

	public void onDestroy() {
		timer.cancel();
		task = null;
		timer = null;
	};

}
