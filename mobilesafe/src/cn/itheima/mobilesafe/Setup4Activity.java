package cn.itheima.mobilesafe;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import cn.itheima.mobilesafe.receiver.MyAdmin;
import cn.itheima.mobilesafe.utils.ActivityUtils;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_setup4_satus;
	@Override
	public void initView() {
		setContentView(R.layout.activity_setup4);
		cb_setup4_satus = (CheckBox) findViewById(R.id.cb_setup4_satus);
		
	}

	@Override
	public void setupView() {
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			cb_setup4_satus.setChecked(true);
			cb_setup4_satus.setText("防盗保护已经开启");
		}else{
			cb_setup4_satus.setChecked(false);
			cb_setup4_satus.setText("防盗保护没有开启");
		}
		
		cb_setup4_satus.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				if(isChecked){
					cb_setup4_satus.setText("防盗保护已经开启");
				}else{
					cb_setup4_satus.setText("防盗保护没有开启");
				}
				editor.putBoolean("protecting", isChecked);
				editor.commit();
			}
		});
	}

	@Override
	public void showNext() {
		Editor editor = sp.edit();
		editor.putBoolean("setup",true);
		editor.commit();
		ActivityUtils.startActivityAndFinish(this, LostFindActivity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		ActivityUtils.startActivityAndFinish(this, Setup3Activity.class);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	public void activeAdmin(View view){
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
    	ComponentName mDeviceAdminSample = new ComponentName(this, MyAdmin.class);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
               mDeviceAdminSample);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
               "激活我可以远程锁屏,清除数据....");
        startActivity(intent);
	}
}
