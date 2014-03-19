package cn.itheima.mobilesafe.receiver;

import cn.itheima.mobilesafe.service.UpdateWidgetService;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

public class MyWidget extends AppWidgetProvider {

	@Override
	public void onEnabled(Context context) {
		//手工的开启服务 ,定期的在后台更新界面.
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent iintent = new Intent(context,UpdateWidgetService.class);
		context.startService(iintent);
		super.onReceive(context, intent);
		System.out.println("onreceive");
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.startService(intent);
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		System.out.println("onupdate");
	}

	@Override
	public void onDisabled(Context context) {
		super.onDisabled(context);
		Intent intent = new Intent(context,UpdateWidgetService.class);
		context.stopService(intent);
	}

	
	
	
}
