package cn.itheima.mobilesafe.service;

import cn.itheima.mobilesafe.R;
import cn.itheima.mobilesafe.db.dao.AddressDao;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class ShowLocationService extends Service {
	public static final String TAG = "ShowLocationService";
	private TelephonyManager tm;
	private MyPhoneListener listener;
	private InnerOutcallReceiver receiver;
	private WindowManager wm;
	private View view;
	private SharedPreferences sp;
	// "��͸��","������","��ʿ��","������","ƻ����"
	private static final int[] bgs = { R.drawable.call_locate_white,
			R.drawable.call_locate_orange, R.drawable.call_locate_blue,
			R.drawable.call_locate_gray, R.drawable.call_locate_green };

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class InnerOutcallReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "�����Ⲧ�绰");
			// Toast.makeText(context,
			// AddressDao.getAddress(getResultData()),1).show();
			showAddressInfo(getResultData());
		}
	}

	@Override
	public void onCreate() {
		sp = getSharedPreferences("config", MODE_PRIVATE);
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		receiver = new InnerOutcallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		// �ڴ������涯̬��ע��㲥������.
		registerReceiver(receiver, filter);

		// �����绰״̬�ĸı���.
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		listener = new MyPhoneListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
		// ȡ��ע��㲥������
		unregisterReceiver(receiver);
		receiver = null;
		super.onDestroy();
	}

	private class MyPhoneListener extends PhoneStateListener {
		private static final String TAG = "MyPhoneListener";

		/**
		 * ���绰����״̬�����ı��ʱ����õķ���.
		 */
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // ����״̬.
				if (view != null) {
					wm.removeView(view);
					view = null;
				}
				break;

			case TelephonyManager.CALL_STATE_RINGING: // ����״̬.
				// Log.i(TAG,"incomingNumber:"+AddressDao.getAddress(incomingNumber));
				// Toast.makeText(getApplicationContext(),
				// AddressDao.getAddress(incomingNumber), 1).show();
				showAddressInfo(incomingNumber);

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK: // ͨ��״̬

				break;
			}

			super.onCallStateChanged(state, incomingNumber);
		}

	}

	private final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

	/**
	 * ��ʾ��������Ϣ
	 * 
	 * @param incomingNumber
	 *            �绰����
	 */

	public void showAddressInfo(String incomingNumber) {
		view = View.inflate(this, R.layout.ui_toast, null);
		view.setOnTouchListener(new OnTouchListener() {
			int startX = 0;
			int startY = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;

				case MotionEvent.ACTION_MOVE:
					int newX = (int) event.getRawX();
					int newY  = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					params.x += dx;
					params.y += dy;
					wm.updateViewLayout(view, params);
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				}

				return true;
			}
		});
		int which = sp.getInt("which", 0);
		view.setBackgroundResource(bgs[which]);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_address);
		tv.setText(AddressDao.getAddress(incomingNumber));
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.gravity = Gravity.TOP | Gravity.LEFT;
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);
		params.x = lastx;
		params.y = lasty;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;
		params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		params.setTitle("Toast");
		wm.addView(view, params);

	}
}
