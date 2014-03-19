package cn.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.db.dao.BlackNumberDao;
import cn.itheima.mobilesafe.domain.BlackNumberInfo;
import cn.itheima.mobilesafe.utils.MyAsyncTask;

public class CallSmsSafeActivity extends Activity implements OnClickListener {
	public static final String TAG = "CallSmsSafeActivity";
	private ListView lv_call_sms;
	private View loading;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos;
	private CallSmsAdapter adapter;
	private int maxnumber = 20;
	private int offset = 0;

	private int totalNumber;// �ܹ��ж�������¼
	private int currentPage = 1; // ��ǰҳ

	private EditText et_page_number;
	private TextView tv_page_status;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		et_page_number = (EditText) findViewById(R.id.et_page_number);
		loading = findViewById(R.id.loading);
		dao = new BlackNumberDao(this);
		totalNumber = dao.getMaxNumber();
		tv_page_status = (TextView) findViewById(R.id.tv_page_status);
		tv_page_status.setText("��ǰ/��:" + currentPage + "/"
				+ getTotalPagenumber(totalNumber) + "ҳ");
		lv_call_sms = (ListView) findViewById(R.id.lv_call_sms);
		
		lv_call_sms.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(CallSmsSafeActivity.this,EditBlacknumberDialog.class);
				//�����޸ĵĵ绰����.
				MoblieSafeApplication app = (MoblieSafeApplication) getApplication();
				app.info = infos.get(position);
				startActivityForResult(intent, 0);
				return false;
			}
		});
		
		filldata();
		
		
		Intent intent = getIntent();
		String blacknumber = intent.getStringExtra("blacknumber");
		if(!TextUtils.isEmpty(blacknumber)){
			showAddBlackNumberDialog(blacknumber);
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Log.i(TAG,"onnewintent");
		String blacknumber = intent.getStringExtra("blacknumber");
		if(!TextUtils.isEmpty(blacknumber)){
			showAddBlackNumberDialog(blacknumber);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==200){
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	public int getTotalPagenumber(int totalNumber) {
		if (totalNumber % maxnumber == 0) {
			return totalNumber / maxnumber;
		} else {
			return totalNumber / maxnumber + 1;
		}
	}

	/**
	 * ҳ����ת��Ӧ���¼�
	 * 
	 * @param view
	 */
	public void jump(View view) {
		String pagenumberStr = et_page_number.getText().toString().trim();
		int pagenumber = Integer.parseInt(pagenumberStr);
		if (pagenumber == currentPage) {
			Toast.makeText(this, "���ǵ�ǰҳ..", 0).show();
			return;
		}
		if (pagenumber > getTotalPagenumber(totalNumber)) {
			Toast.makeText(this, "����ҳ�뷶Χ..", 0).show();
			return;
		}

		// 1 -- 0(ҳ��-1)*maxnumber ~19
		// 2 --- 20~39
		offset = (pagenumber - 1) * maxnumber;
		currentPage = pagenumber;
		tv_page_status.setText("��ǰ/��:" + currentPage + "/"
				+ getTotalPagenumber(totalNumber) + "ҳ");
		filldata();
	}

	/**
	 * �������
	 */
	private void filldata() {
		MyAsyncTask task = new MyAsyncTask() {
			@Override
			public void onPreExecute() {
				loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExecute() {
				loading.setVisibility(View.INVISIBLE);
				if (adapter == null) {
					adapter = new CallSmsAdapter();
					lv_call_sms.setAdapter(adapter);
				} else {
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void doInBackground() {
				infos = dao.findByPage(maxnumber, offset);

			}
		};
		task.execute();
	}

	private class CallSmsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final BlackNumberInfo info = infos.get(position);
			View view;
			ViewHolder hodler;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView; // ������ʷ�����view����
				// Log.i(TAG,"ʹ�û���view"+position);
				hodler = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_callsms_item, null);
				// Log.i(TAG,"�����µ�view"+position);
				// Ѱ�ҵ��������� �����ô�����.
				hodler = new ViewHolder();
				hodler.tv_mode = (TextView) view
						.findViewById(R.id.tv_call_sms_mode);
				hodler.tv_number = (TextView) view
						.findViewById(R.id.tv_call_sms_number);
				hodler.iv_callsms_delete = (ImageView) view
						.findViewById(R.id.iv_callsms_delete);
				view.setTag(hodler);
			}

			hodler.tv_number.setText(info.getNumber());
			if ("1".equals(info.getMode())) {
				hodler.tv_mode.setText("ȫ������");
			} else if ("2".equals(info.getMode())) {
				hodler.tv_mode.setText("�绰����");
			} else if ("3".equals(info.getMode())) {
				hodler.tv_mode.setText("��������");
			}
			hodler.iv_callsms_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if (result) {
						infos.remove(info);
						adapter.notifyDataSetChanged();
						totalNumber--;
						tv_page_status.setText("��ǰ/��:" + currentPage + "/"
								+ getTotalPagenumber(totalNumber) + "ҳ");
					} else {
						Toast.makeText(getApplicationContext(), "ɾ��ʧ��", 0)
								.show();
					}
				}
			});
			return view;
		}

	}

	/**
	 * view���������õ�����
	 * 
	 * @author Administrator
	 * 
	 */
	static class ViewHolder {
		TextView tv_number;
		TextView tv_mode;
		ImageView iv_callsms_delete;
	}

	private EditText et_blacknumber;
	private RadioGroup rg_mode;
	private Button bt_ok, bt_cancle;
	private AlertDialog dialog;

	/**
	 * ��Ӻ���������
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		showAddBlackNumberDialog("");
	}

	private void showAddBlackNumberDialog(String number) {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		dialog.setView(dialogView, 0, 0, 0, 0);
		et_blacknumber = (EditText) dialogView
				.findViewById(R.id.et_blacknumber);
		et_blacknumber.setText(number);
		rg_mode = (RadioGroup) dialogView.findViewById(R.id.rg_mode);
		bt_cancle = (Button) dialogView.findViewById(R.id.bt_cancle);
		bt_ok = (Button) dialogView.findViewById(R.id.bt_ok);
		bt_cancle.setOnClickListener(this);
		bt_ok.setOnClickListener(this);

		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_cancle:
			dialog.dismiss();
			break;
		case R.id.bt_ok:
			String number = et_blacknumber.getText().toString().trim();
			int id = rg_mode.getCheckedRadioButtonId();
			String mode = "";
			switch (id) {
			case R.id.rb_all:
				mode = "1";
				break;

			case R.id.rb_phone:
				mode = "2";
				break;
			case R.id.rb_sms:
				mode = "3";
				break;
			}
			if (TextUtils.isEmpty(number) || TextUtils.isEmpty(mode)) {
				Toast.makeText(this, "�����������ģʽ����Ϊ��", 1).show();
				return;
			}
			dao.add(number, mode);// ���ݱ��浽���ݿ�.
			totalNumber++;
			tv_page_status.setText("��ǰ/��:" + currentPage + "/"
					+ getTotalPagenumber(totalNumber) + "ҳ");
			// ����û�и���.
			infos.add(0, new BlackNumberInfo(number, mode));
			// ˢ��listview
			adapter.notifyDataSetChanged();
			dialog.dismiss();
			break;
		}

	}

}
