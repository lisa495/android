package cn.itheima.mobilesafe.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import cn.itheima.mobilesafe.CallSmsSafeActivity;
import cn.itheima.mobilesafe.R;
import cn.itheima.mobilesafe.db.dao.BlackNumberDao;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallSmsFireWallService extends Service {
	public static final String TAG = "CallSmsFireWallService";
	private BlackNumberDao dao;
	private InnerSmsReceiver receiver;
	private TelephonyManager tm;
	private InnerTeleStateListener listener;
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	@Override
	public void onCreate() {
		dao = new BlackNumberDao(this);
		Log.i(TAG,"����ǽ��������");
		tm  = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new InnerTeleStateListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		receiver = new InnerSmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(receiver, filter);
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		unregisterReceiver(receiver);
		receiver = null;
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		super.onDestroy();
	}
	
	private class InnerSmsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			Object[] objs = (Object[]) intent.getExtras().get("pdus");
			for(Object obj : objs ){
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
				String sender = smsMessage.getOriginatingAddress();
				//�ж�sender�Ƿ��ں��������ݿ�����, �ж�����ģʽ �Ƿ���1, 3
				String mode = dao.findMode(sender);
				if("1".equals(mode)||"3".equals(mode)){
					//TODO: �Ѻ������������ݴ洢����
					Log.i(TAG,"���ص�����������");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					abortBroadcast();
					Log.i(TAG,"���ص�fapiao����");
				}
			}
			
		}
		
	}
	private class InnerTeleStateListener extends PhoneStateListener{
		private long startTime;
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				startTime = System.currentTimeMillis();
				String mode = dao.findMode(incomingNumber);
				if("1".equals(mode)||"2".equals(mode)){
					Log.i(TAG,"�Ҷϵ绰");
					endCall(incomingNumber);
					//�����м�¼������ ��ȥ�Ƴ�.
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(),incomingNumber));
					
					//deleteCallLog(incomingNumber);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: 
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				if(dTime<3000){
					Log.i(TAG,"������һ������");
					//�ж�һ�µ�ǰ�����Ƿ��Ѿ��ں��������ݿ�
					String  blockmode = dao.findMode(incomingNumber);
					if("1".equals(blockmode)||"2".equals(blockmode)){
					
					}else{
						showNotification(incomingNumber);
					}
				}
				break;
			}
			
			super.onCallStateChanged(state, incomingNumber);
		}
		
	}
	
	public void endCall(String incomingNumber) {
		try {
			Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
			IBinder binder = (IBinder)method.invoke(null, new Object[]{TELEPHONY_SERVICE});
			ITelephony telephony = ITelephony.Stub.asInterface(binder);
			telephony.endCall();//�Ҷϵ绰.
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	/**
	 * ��ʾ����һ���������.
	 * @param incomingNumber
	 */
	public void showNotification(String incomingNumber) {
		//1.��ȡϵͳnotification�Ĺ�����
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//2.ʵ����notification
		Notification notification = new  Notification(R.drawable.notification, "������һ������:"+incomingNumber, System.currentTimeMillis());
		
		//3.����notification�ľ������
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(this,CallSmsSafeActivity.class);
		intent.putExtra("blacknumber", incomingNumber);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "������ʿ������", "���ص���һ����һ������", contentIntent);
		//4. ��notification��ʾ����.
		nm.notify(0, notification);
		
		
	}


	/**
	 * ɾ�����м�¼
	 * @param incomingNumber
	 */
	public void deleteCallLog(String incomingNumber) {
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().delete(uri, "number=?", new String[]{incomingNumber});
	}
	private class MyObserver extends ContentObserver{
		private String incomingNumber;
		public MyObserver(Handler handler,String incomingNumber) {
			super(handler);
			this.incomingNumber = incomingNumber;
		}
		//���۲쵽���ݷ����仯��ʱ�� ����.
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
			//ȡ�����ݹ۲��ߵ�ע��
		}
		
	}
}
