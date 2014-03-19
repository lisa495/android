package cn.itheima.mobilesafe.receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import cn.itheima.mobilesafe.R;
import cn.itheima.mobilesafe.engine.GPSInfoProvider;

public class SmsReceiver extends BroadcastReceiver {

	private static final String TAG = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");

		for (Object pdu : pdus) {
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
			String sender = smsMessage.getOriginatingAddress();
			String body = smsMessage.getMessageBody();
			if ("#*location*#".equals(body)) {
				Log.i(TAG, "�����ֻ���λ��...");
				String location = GPSInfoProvider.getInstance(context)
						.getLastLocation();
				if (!TextUtils.isEmpty(location)) {
					SmsManager sm = SmsManager.getDefault();
					sm.sendTextMessage(sender, null, location, null, null);
				}
				abortBroadcast();
			} else if ("#*alarm*#".equals(body)) {
				Log.i(TAG, "���ű�������...");
				MediaPlayer mp = MediaPlayer.create(context, R.raw.ylzs);
				mp.setVolume(1.0f, 1.0f);
				mp.start();
				abortBroadcast();
			} else if ("#*wipedata*#".equals(body)) {
				Log.i(TAG, "�������...");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context, MyAdmin.class);
				if (dpm.isAdminActive(who)) {
					dpm.wipeData(0);
				}
				abortBroadcast();
			} else if ("#*lockscreen*#".equals(body)) {
				Log.i(TAG, "Զ������...");
				DevicePolicyManager dpm = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				ComponentName who = new ComponentName(context, MyAdmin.class);
				if (dpm.isAdminActive(who)) {
					dpm.resetPassword("321", 0);
					dpm.lockNow();
				}
				abortBroadcast();
			}
		}
	}
}
