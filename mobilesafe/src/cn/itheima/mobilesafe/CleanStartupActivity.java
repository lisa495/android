package cn.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

public class CleanStartupActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		
		//查询手机里面的应用程序 看哪个应用程序里面有 android.intent.action.BOOT_COMPLETED
		
		PackageManager pm = getPackageManager();
		Intent intent = new Intent("android.intent.action.BOOT_COMPLETED");
		List<ResolveInfo> infos = pm.queryBroadcastReceivers(intent, PackageManager.GET_INTENT_FILTERS);
		for(ResolveInfo info : infos){
			String receivername = info.activityInfo.name;
			String packname = info.activityInfo.packageName;
			System.out.println("reciever :"+ receivername);
		}
	}
}

