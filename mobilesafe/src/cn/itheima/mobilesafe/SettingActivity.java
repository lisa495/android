package cn.itheima.mobilesafe;

import cn.itheima.mobilesafe.service.CallSmsFireWallService;
import cn.itheima.mobilesafe.service.ShowLocationService;
import cn.itheima.mobilesafe.service.WatchDogService;
import cn.itheima.mobilesafe.ui.SettingView;
import cn.itheima.mobilesafe.utils.ActivityUtils;
import cn.itheima.mobilesafe.utils.ServiceStatusUtils;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingActivity extends Activity implements OnClickListener {
	private SettingView sv_setting_update;
	private SettingView sv_setting_showaddress;
	private Intent showAddressServiceIntent;
	private SharedPreferences sp;
	
	private SettingView sv_setting_callsmsfirewall;
	private Intent callSmsFirewallIntent;
	
	
	private SettingView sv_setting_applock;
	private Intent appLockServiceIntent;
	
	private RelativeLayout rl_setting_changebg;
	private TextView tv_setting_addressbg_color;
	private static String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	
	
	private RelativeLayout rl_setting_change_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);

		setContentView(R.layout.activity_setting);
		
		//自动更新设置
		sv_setting_update = (SettingView) findViewById(R.id.sv_setting_update);
		boolean isupdate = sp.getBoolean("update", true);
		sv_setting_update.setChecked(isupdate);
		sv_setting_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Editor editor = sp.edit();
				// 判断 cb是否被勾选.
				if (sv_setting_update.isChecked()) {
					sv_setting_update.setChecked(false);
					editor.putBoolean("update", false);
				}else{
					sv_setting_update.setChecked(true);
					editor.putBoolean("update", true);
				}
				editor.commit();
			}
		});
		//号码归属地设置
		sv_setting_showaddress= (SettingView)findViewById(R.id.sv_setting_showaddress);
		showAddressServiceIntent = new Intent(this,ShowLocationService.class);
		

		sv_setting_showaddress.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sv_setting_showaddress.isChecked()){
					sv_setting_showaddress.setChecked(false);
					stopService(showAddressServiceIntent);
				}else{
					sv_setting_showaddress.setChecked(true);
					startService(showAddressServiceIntent);
				}
				
			}
		});
		
		//更改背景颜色的控件初始化
		rl_setting_changebg = (RelativeLayout) findViewById(R.id.rl_setting_changebg);
		rl_setting_changebg.setOnClickListener(this);
		tv_setting_addressbg_color = (TextView) findViewById(R.id.tv_setting_addressbg_color);
		
		int which = sp.getInt("which", 0);
		tv_setting_addressbg_color.setText(items[which]);
		
		
		//更改归属地位置
		rl_setting_change_location = (RelativeLayout) findViewById(R.id.rl_setting_change_location);
		rl_setting_change_location.setOnClickListener(this);
		
		//短信电话防火墙初始化
		sv_setting_callsmsfirewall = (SettingView)findViewById(R.id.sv_setting_callsmsfirewall);
		callSmsFirewallIntent = new Intent(this,CallSmsFireWallService.class);
		sv_setting_callsmsfirewall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(sv_setting_callsmsfirewall.isChecked()){
					sv_setting_callsmsfirewall.setChecked(false);
					stopService(callSmsFirewallIntent);
				}else{
					sv_setting_callsmsfirewall.setChecked(true);
					startService(callSmsFirewallIntent);
				}
				
			}
		});
		
		//程序锁初始化
		sv_setting_applock = (SettingView)findViewById(R.id.sv_setting_applock);
		appLockServiceIntent = new Intent(this,WatchDogService.class);
		sv_setting_applock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(sv_setting_applock.isChecked()){
					sv_setting_applock.setChecked(false);
					stopService(appLockServiceIntent);
				}else{
					sv_setting_applock.setChecked(true);
					startService(appLockServiceIntent);
				}
				
			}
		});
	}
	
	//当界面重新用户可见的时候
	@Override
	protected void onStart() {
		//重新初始化服务的状态.
		if(ServiceStatusUtils.isServiceRunning("cn.itheima.mobilesafe.service.ShowLocationService", this)){
			sv_setting_showaddress.setChecked(true);
		}else{
			sv_setting_showaddress.setChecked(false);
		}
		if(ServiceStatusUtils.isServiceRunning("cn.itheima.mobilesafe.service.CallSmsFireWallService", this)){
			sv_setting_callsmsfirewall.setChecked(true);
		}else{
			sv_setting_callsmsfirewall.setChecked(false);
		}
		if(ServiceStatusUtils.isServiceRunning("cn.itheima.mobilesafe.service.WatchDogService", this)){
			sv_setting_applock.setChecked(true);
		}else{
			sv_setting_applock.setChecked(false);
		}
		
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_setting_changebg:
			showChangeBgDialog();
			break;
		case R.id.rl_setting_change_location:
			ActivityUtils.startActivity(this, DragViewActivity.class);
			break;
		}
		
	}

	private void showChangeBgDialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(R.drawable.notification);
		builder.setTitle("归属地提示框风格");
		int which = sp.getInt("which", 0);
		builder.setSingleChoiceItems(items, which, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Editor editor = sp.edit();
				editor.putInt("which", which);
				editor.commit();
				dialog.dismiss();
				tv_setting_addressbg_color.setText(items[which]);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		builder.show();
	}

}
