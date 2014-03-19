package cn.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.itheima.mobilesafe.service.AutoKillService;
import cn.itheima.mobilesafe.ui.SettingView;
import cn.itheima.mobilesafe.utils.ServiceStatusUtils;

public class TaskSettingActivity extends Activity {
	private CheckBox cb_task_setting_showsystem;
	private SharedPreferences sp;
	private SettingView sv_task_setting_autokill;
	private Intent autoKillIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_setting);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		cb_task_setting_showsystem = (CheckBox) findViewById(R.id.cb_task_setting_showsystem);
		sv_task_setting_autokill = (SettingView) findViewById(R.id.sv_task_setting_autokill);
		boolean showsystem = sp.getBoolean("showsystem", false);
		autoKillIntent = new Intent(this,AutoKillService.class);
		
//		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//		
//		alarmManager.setRepeating(type, triggerAtTime, interval, operation)
		
		cb_task_setting_showsystem.setChecked(showsystem);
		cb_task_setting_showsystem
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						Editor editor = sp.edit();
						editor.putBoolean("showsystem", isChecked);
						editor.commit();

						setResult(200);
					}
				});
		
		sv_task_setting_autokill.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//如果开启 锁屏的时候清理进程.
				if(sv_task_setting_autokill.isChecked()){
					sv_task_setting_autokill.setChecked(false);
					stopService(autoKillIntent);
				}else{
					sv_task_setting_autokill.setChecked(true);
					startService(autoKillIntent);
				}
			}
		});
	}
	
	
	@Override
	protected void onStart() {
		if(ServiceStatusUtils.isServiceRunning("cn.itheima.mobilesafe.service.AutoKillService", this)){
			sv_task_setting_autokill.setChecked(true);
		}else{
			sv_task_setting_autokill.setChecked(false);
		}
		super.onStart();
	}
}
