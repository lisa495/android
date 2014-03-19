package cn.itheima.mobilesafe;

import cn.itheima.mobilesafe.utils.ActivityUtils;
import android.content.Intent;

public class Setup1Activity extends BaseSetupActivity {

	@Override
	public void initView() {
		setContentView(R.layout.activity_setup1);
	}

	@Override
	public void setupView() {

	}

	@Override
	public void showNext() {
		ActivityUtils.startActivityAndFinish(this, Setup2Activity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {

	}

}
