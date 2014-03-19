package cn.itheima.mobilesafe.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BootCompleteRecevier extends BroadcastReceiver {

	private static final String TAG = "BootCompleteRecevier";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "�ֻ�������");
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean protecting = sp.getBoolean("protecting", false);
		if (protecting) {
			// 1.��ȡ��ǰ��sim������
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String realSim = tm.getSimSerialNumber();

			// 2.��ȡ���󶨵�sim������

			String savedSim = sp.getString("sim", "");
			
			//3.�ȶ��Ƿ���ͬ.
			
			if(!savedSim.equals(realSim)){
				SmsManager sm = SmsManager.getDefault();
				sm.sendTextMessage(sp.getString("safenumber", ""), null, "sim changed", null, null);
			}
		}
	}

}
