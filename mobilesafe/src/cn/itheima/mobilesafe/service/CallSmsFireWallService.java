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
		Log.i(TAG,"防火墙服务开启了");
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
				//判断sender是否在黑名单数据库里面, 判断拦截模式 是否是1, 3
				String mode = dao.findMode(sender);
				if("1".equals(mode)||"3".equals(mode)){
					//TODO: 把黑名单短信内容存储起来
					Log.i(TAG,"拦截到黑名单短信");
					abortBroadcast();
				}
				String body = smsMessage.getMessageBody();
				if(body.contains("fapiao")){
					abortBroadcast();
					Log.i(TAG,"拦截到fapiao短信");
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
					Log.i(TAG,"挂断电话");
					endCall(incomingNumber);
					//当呼叫记录产生后 再去移除.
					getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI, true, new MyObserver(new Handler(),incomingNumber));
					
					//deleteCallLog(incomingNumber);
				}
				break;
			case TelephonyManager.CALL_STATE_IDLE: 
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				if(dTime<3000){
					Log.i(TAG,"发现响一声号码");
					//判断一下当前号码是否已经在黑名单数据库
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
			telephony.endCall();//挂断电话.
		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	/**
	 * 显示来电一声响的提醒.
	 * @param incomingNumber
	 */
	public void showNotification(String incomingNumber) {
		//1.获取系统notification的管理器
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		
		//2.实例化notification
		Notification notification = new  Notification(R.drawable.notification, "发现响一声号码:"+incomingNumber, System.currentTimeMillis());
		
		//3.设置notification的具体参数
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(this,CallSmsSafeActivity.class);
		intent.putExtra("blacknumber", incomingNumber);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, "黑马卫士提醒您", "拦截到了一个响一声号码", contentIntent);
		//4. 把notification显示出来.
		nm.notify(0, notification);
		
		
	}


	/**
	 * 删除呼叫记录
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
		//当观察到内容发生变化的时候 调用.
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			deleteCallLog(incomingNumber);
			getContentResolver().unregisterContentObserver(this);
			//取消内容观察者的注册
		}
		
	}
}
