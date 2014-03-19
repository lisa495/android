package cn.itheima.mobilesafe;


import cn.itheima.mobilesafe.db.dao.AddressDao;
import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class NumberQueryActivity extends Activity {
	private EditText et_query_number;
	private TextView tv_query_address;
	private Vibrator vibrator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_number_query);
		et_query_number = (EditText) findViewById(R.id.et_query_number);
		tv_query_address = (TextView) findViewById(R.id.tv_query_address);
		
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
	
		//���һ�����ݱ仯�ļ�����
		et_query_number.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String number = s.toString();
				String address = AddressDao.getAddress(number);
				tv_query_address.setText("������Ϊ:"+address);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
			}
		});
		
	}
	
	public void query(View view){
		String number = et_query_number.getText().toString().trim();
		if(TextUtils.isEmpty(number)){
			Toast.makeText(this, "���벻��Ϊ��", 1).show();
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_query_number.startAnimation(shake);
			vibrator.vibrate(200);
			return;
		}
		String address = AddressDao.getAddress(number);
		tv_query_address.setText("������Ϊ:"+address);
	}
}
