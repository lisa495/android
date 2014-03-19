package cn.itheima.mobilesafe;

import cn.itheima.mobilesafe.db.dao.BlackNumberDao;
import cn.itheima.mobilesafe.domain.BlackNumberInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class EditBlacknumberDialog extends Activity {
	private RadioButton rb_all, rb_phone, rb_sms;
	private BlackNumberDao dao;
	private BlackNumberInfo info;
	private RadioGroup rg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_add_blacknumber);
		rg = (RadioGroup) findViewById(R.id.rg_mode);
		rb_all = (RadioButton) findViewById(R.id.rb_all);
		rb_phone = (RadioButton) findViewById(R.id.rb_phone);
		rb_sms = (RadioButton) findViewById(R.id.rb_sms);
		dao = new BlackNumberDao(this);
		TextView tv_title = (TextView) findViewById(R.id.tv_title_name);
		tv_title.setText("更该黑名单拦截模式");
		EditText et = (EditText) findViewById(R.id.et_blacknumber);
		et.setEnabled(false);
		MoblieSafeApplication app = (MoblieSafeApplication) getApplication();
		info = app.info;
		app.info = null;
		String blacknumber = info.getNumber();
		et.setText(blacknumber);
		String mode = info.getMode();
		System.out.println(mode);
		if ("1".equals(mode)) {
			rb_all.setChecked(true);
		} else if ("2".equals(mode)) {
			rb_phone.setChecked(true);
		} else if ("3".equals(mode)) {
			rb_sms.setChecked(true);
		}

		findViewById(R.id.bt_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				int id = rg.getCheckedRadioButtonId();
				String mode = "1";
				switch (id) {
				case R.id.rb_all:
					mode ="1";
					break;
				case R.id.rb_phone:
					mode ="2";
					break;
				case R.id.rb_sms:
					mode ="3";
					break;
				}
				dao.update(info.getNumber(), mode);
				//设置新的拦截模式到info对象.
				info.setMode(mode);
				setResult(200);
				finish();
				
			}
		});
		findViewById(R.id.bt_cancle).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(404);
				finish();
			}
		});
	}
}
