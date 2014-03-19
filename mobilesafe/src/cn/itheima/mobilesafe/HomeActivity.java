package cn.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;
import cn.itheima.mobilesafe.adapter.HomeAdapter;

public class HomeActivity extends Activity implements OnClickListener {
	private GridView gv_home;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		gv_home = (GridView) findViewById(R.id.gv_home);

		gv_home.setAdapter(new HomeAdapter(this));

		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent;
				switch (position) {
				case 0:
					// 判断用户是否设置过密码.
					if (isSetupPwd()) {
						showNormalEntryDialog();
					} else {
						showFirstEntryDialog();
					}

					break;
				case 1:
					intent = new Intent(HomeActivity.this,
							CallSmsSafeActivity.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(HomeActivity.this,
							AppManagerActivity.class);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(HomeActivity.this,
							TaskManagerActivity.class);
					startActivity(intent);
					break;
				case 4:
					intent = new Intent(HomeActivity.this,
							TrafficManagerActivity.class);
					startActivity(intent);
					break;
				case 6:
					intent = new Intent(HomeActivity.this,
							SystemOptActivity.class);
					startActivity(intent);
					break;
				case 7:
					intent = new Intent(HomeActivity.this, AtoolsActivity.class);
					startActivity(intent);
					break;

				case 8:
					intent = new Intent(HomeActivity.this,
							SettingActivity.class);
					startActivity(intent);
					break;
				}

			}
		});
	}

	/**
	 * 第一次进入对话框
	 */
	private EditText et_first_entry_pwd;
	private EditText et_first_entry_pwd_confirm;
	private Button bt_first_entry_ok;
	private Button bt_first_entry_cancle;
	private AlertDialog dialog;

	protected void showFirstEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_first_entry, null);
		et_first_entry_pwd = (EditText) view
				.findViewById(R.id.et_first_entry_pwd);
		et_first_entry_pwd_confirm = (EditText) view
				.findViewById(R.id.et_first_entry_pwd_confirm);
		bt_first_entry_cancle = (Button) view
				.findViewById(R.id.bt_first_entry_cancle);
		bt_first_entry_ok = (Button) view.findViewById(R.id.bt_first_entry_ok);
		bt_first_entry_ok.setOnClickListener(this);
		bt_first_entry_cancle.setOnClickListener(this);
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

	}

	/**
	 * 正常进入对话框
	 */
	private EditText et_normal_entry_pwd;
	private Button bt_normal_entry_ok;
	private Button bt_normal_entry_cancle;

	protected void showNormalEntryDialog() {
		AlertDialog.Builder builder = new Builder(this);
		View view = View.inflate(this, R.layout.dialog_normal_entry, null);
		et_normal_entry_pwd = (EditText) view
				.findViewById(R.id.et_normal_entry_pwd);
		bt_normal_entry_cancle = (Button) view
				.findViewById(R.id.bt_normal_entry_cancle);
		bt_normal_entry_ok = (Button) view
				.findViewById(R.id.bt_normal_entry_ok);
		bt_normal_entry_ok.setOnClickListener(this);
		bt_normal_entry_cancle.setOnClickListener(this);
		dialog = builder.create();
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();

	}

	/**
	 * 判断用户是否设置过密码
	 * 
	 * @return
	 */
	private boolean isSetupPwd() {
		String pwd = sp.getString("pwd", "");
		if (TextUtils.isEmpty(pwd)) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_first_entry_cancle:
			dialog.dismiss();
			break;

		case R.id.bt_first_entry_ok:
			String pwd = et_first_entry_pwd.getText().toString().trim();
			String pwd_confirm = et_first_entry_pwd_confirm.getText()
					.toString().trim();
			if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(pwd_confirm)) {
				Toast.makeText(this, "密码不能为空", 0).show();
				return;
			}
			if (!pwd.equals(pwd_confirm)) {
				Toast.makeText(this, "两次输入密码不相同", 0).show();
				return;
			}
			Editor editor = sp.edit();
			editor.putString("pwd", Md5Util.encode(pwd));
			editor.commit();
			dialog.dismiss();
			break;
		case R.id.bt_normal_entry_cancle:
			dialog.dismiss();
			break;
		case R.id.bt_normal_entry_ok:
			String enter_pwd = et_normal_entry_pwd.getText().toString().trim();
			if (TextUtils.isEmpty(enter_pwd)) {
				Toast.makeText(this, "密码不能为空", 0).show();
				return;
			}
			String saved_pwd = sp.getString("pwd", "");
			if (saved_pwd.equals(Md5Util.encode(enter_pwd))) {
				Intent intent = new Intent(this, LostFindActivity.class);
				startActivity(intent);
				dialog.dismiss();
			} else {
				Toast.makeText(this, "密码不正确", 0).show();
				return;
			}
			break;

		}

	}

}
