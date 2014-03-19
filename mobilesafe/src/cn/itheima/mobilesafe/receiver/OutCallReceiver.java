package cn.itheima.mobilesafe.receiver;

import cn.itheima.mobilesafe.LostFindActivity;
import cn.itheima.mobilesafe.db.dao.AddressDao;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class OutCallReceiver extends BroadcastReceiver {

	private static final String TAG = "OutCallReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"外拨电话了...");
		//获取外拨电话的号码
		String number = getResultData();
		if("20182018".equals(number)){
			Intent lostIntent = new Intent(context,LostFindActivity.class);
			lostIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostIntent);
			setResultData(null);
		}
		
	}

}
