package cn.itheima.mobilesafe;

import cn.itheima.mobilesafe.service.IService;
import cn.itheima.mobilesafe.service.WatchDogService;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterPasswrodActivity extends Activity {
	private String packname;
	private EditText et_password;
	private MyConn conn;
	private IService iService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_password);
		et_password = (EditText) findViewById(R.id.et_password);
		Intent intent = getIntent();
		packname = intent.getStringExtra("packname");
		
		Intent service = new Intent(this,WatchDogService.class);
		conn = new MyConn();
		bindService(service, conn, BIND_AUTO_CREATE);
		
		try {
			PackageManager pm = getPackageManager();
			ApplicationInfo info = pm.getApplicationInfo(packname, 0);
			Drawable icon = info.loadIcon(pm);
			CharSequence name = info.loadLabel(pm);
			TextView tv = (TextView) findViewById(R.id.tv_name);
			tv.setText(name);
			ImageView iv = (ImageView) findViewById(R.id.iv_icon);
			iv.setImageDrawable(icon);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	private class MyConn implements ServiceConnection{

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iService = (IService) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
		}
		
	}
	
	public void enter(View view){
		String password = et_password.getText().toString().trim();
		// 123 
		if("123".equals(password)){
			finish(); //告诉看门狗 ,密码正确了 ,这个是熟人 放行.
			//发送一个自定义的广播...,告诉看门狗.
		/*	Intent intent = new Intent();
			intent.setAction("cn.itheima.stopprotect");
			intent.putExtra("stoppackname", packname);
			sendBroadcast(intent);*/
			//调用了服务的方法. 停止保护
			iService.callMethodInService(packname);
			
		}else{
			Toast.makeText(this, "密码错误", 0).show();
		}
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.KEYCODE_BACK){
			Intent intent = new Intent();
			intent.setAction("android.intent.action.MAIN");
			intent.addCategory("android.intent.category.HOME");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addCategory("android.intent.category.MONKEY");
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			finish();
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		unbindService(conn);
		super.onDestroy();
	}
}
