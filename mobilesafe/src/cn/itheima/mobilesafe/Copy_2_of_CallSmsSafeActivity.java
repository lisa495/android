package cn.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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

public class Copy_2_of_CallSmsSafeActivity extends Activity implements OnClickListener {
	public static final String TAG = "CallSmsSafeActivity";
	private ListView lv_call_sms;
	private View loading;
	private BlackNumberDao dao;
	private List<BlackNumberInfo> infos;
	private CallSmsAdapter adapter;
	private int maxnumber = 20;
	private int offset = 0;
	
	private int totalNumber;//总共有多少条记录

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_sms_safe);
		loading = findViewById(R.id.loading);
		dao = new BlackNumberDao(this);
		totalNumber = dao.getMaxNumber();
		lv_call_sms = (ListView) findViewById(R.id.lv_call_sms);

		lv_call_sms.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					int position = lv_call_sms.getLastVisiblePosition(); // 19
					int total = infos.size(); // 20
					if (position == (total - 1)) {
						Log.i(TAG, "移动到了最后面,加载更多的数据");
						offset += maxnumber;
						if(offset>totalNumber){
							Toast.makeText(getApplicationContext(), "没有更多数据了.", 1).show();
							return;
						}
						
						filldata();
					}
					break;
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});

		filldata();
	}

	/**
	 * 填充数据
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
				}else{
					adapter.notifyDataSetChanged();
				}
			}

			@Override
			public void doInBackground() {
				if (infos == null) {
					infos = dao.findByPage(maxnumber, offset);
				} else {
					infos.addAll(dao.findByPage(maxnumber, offset));
				}
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
				view = convertView; // 复用历史缓存的view对象
				// Log.i(TAG,"使用缓存view"+position);
				hodler = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_callsms_item, null);
				// Log.i(TAG,"创建新的view"+position);
				// 寻找到孩子引用 把引用存起来.
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
				hodler.tv_mode.setText("全部拦截");
			} else if ("2".equals(info.getMode())) {
				hodler.tv_mode.setText("电话拦截");
			} else if ("3".equals(info.getMode())) {
				hodler.tv_mode.setText("短信拦截");
			}
			hodler.iv_callsms_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String number = info.getNumber();
					boolean result = dao.delete(number);
					if (result) {
						infos.remove(info);
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(getApplicationContext(), "删除失败", 0)
								.show();
					}

				}
			});
			return view;
		}

	}

	/**
	 * view对象孩子引用的容器
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
	 * 添加黑名单号码
	 * 
	 * @param view
	 */
	public void addBlackNumber(View view) {
		AlertDialog.Builder builder = new Builder(this);
		dialog = builder.create();
		View dialogView = View.inflate(this, R.layout.dialog_add_blacknumber,
				null);
		dialog.setView(dialogView, 0, 0, 0, 0);
		et_blacknumber = (EditText) dialogView
				.findViewById(R.id.et_blacknumber);
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
				Toast.makeText(this, "号码或者拦截模式不能为空", 1).show();
				return;
			}
			dao.add(number, mode);// 数据被存到数据库.
			// 界面没有更新.
			infos.add(0, new BlackNumberInfo(number, mode));
			// 刷新listview
			adapter.notifyDataSetChanged();
			dialog.dismiss();
			break;
		}

	}

}
