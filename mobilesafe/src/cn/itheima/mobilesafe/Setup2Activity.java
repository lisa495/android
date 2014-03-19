package cn.itheima.mobilesafe;

import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import cn.itheima.mobilesafe.utils.ActivityUtils;

public class Setup2Activity extends BaseSetupActivity {
	private TelephonyManager tm;//��ȡ�ֻ�������绰��ص�����
	private ImageView iv_setup2_bind;
	
	@Override
	public void initView() {
		setContentView(R.layout.activity_setup2);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	}

	@Override
	public void setupView() {
		iv_setup2_bind = (ImageView) findViewById(R.id.iv_setup2_bind);
		//�������õ���Ϣ ��ʼ��һ��״̬.
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			iv_setup2_bind.setImageResource(R.drawable.unlock);
		}else{
			iv_setup2_bind.setImageResource(R.drawable.lock);
		}
	}

	@Override
	public void showNext() {
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			Toast.makeText(this, "���Ȱ�sim��", 0).show();
			return;
			
		}

		ActivityUtils.startActivityAndFinish(this, Setup3Activity.class);
		overridePendingTransition(R.anim.tran_next_in, R.anim.tran_next_out);
	}

	@Override
	public void showPre() {
		ActivityUtils.startActivityAndFinish(this, Setup1Activity.class);
		overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
	}
	/**
	 * ��sim���ĵ���¼�
	 */
	public void bindSim(View view){
		//�ж�sp�����Ƿ񱣴���sim���Ĵ���
		String sim = sp.getString("sim", "");
		if(TextUtils.isEmpty(sim)){
			String simnumber = tm.getSimSerialNumber();
			Editor editor = sp.edit();
			editor.putString("sim", simnumber);
			editor.commit();
			iv_setup2_bind.setImageResource(R.drawable.lock);
		}else{
			Editor editor = sp.edit();
			editor.putString("sim", null);
			editor.commit();
			iv_setup2_bind.setImageResource(R.drawable.unlock);
		}
	}
}
