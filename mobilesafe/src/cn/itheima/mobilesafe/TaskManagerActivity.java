package cn.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.domain.TaskInfo;
import cn.itheima.mobilesafe.engine.TaskInfoProvider;
import cn.itheima.mobilesafe.ui.MyToast;
import cn.itheima.mobilesafe.utils.ActivityUtils;
import cn.itheima.mobilesafe.utils.MyAsyncTask;
import cn.itheima.mobilesafe.utils.TaskUtils;

public class TaskManagerActivity extends Activity {
	private TextView tv_process_count;
	private TextView tv_mem_info;

	private ListView lv_task_manager;
	private View loading;

	private List<TaskInfo> taskInfos;
	private List<TaskInfo> userTaskInfos;
	private List<TaskInfo> systemTaskInfos;

	private TaskAdapter adapter;

	private int runningProcessCount;
	private long availRam;
	private long totalRam;

	private boolean selectAll;
	private Button bt_select_all;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager);
		tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		bt_select_all = (Button) findViewById(R.id.bt_select_all);
		runningProcessCount = TaskUtils.getRunningProcessCount(this);
		availRam = TaskUtils.getAvailRam(this);
		totalRam = TaskUtils.getTotalRam();
		tv_process_count.setText("�����н���:" + runningProcessCount + "��");
		tv_mem_info.setText("����/���ڴ�:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
		loading = findViewById(R.id.loading);
		lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);

		lv_task_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Object obj = lv_task_manager.getItemAtPosition(position);
				if (obj != null) {
					TaskInfo taskInfo = (TaskInfo) obj;
					if (getPackageName().equals(taskInfo.getPackName())) {// ���Լ�.
						return;
					}
					if (taskInfo.isChecked()) {
						taskInfo.setChecked(false);
					} else {
						taskInfo.setChecked(true);
					}
					adapter.notifyDataSetChanged();// ˢ�½���.
				}
			}
		});

		new MyAsyncTask() {

			@Override
			public void onPreExecute() {
				loading.setVisibility(View.VISIBLE);

			}

			@Override
			public void onPostExecute() {
				loading.setVisibility(View.INVISIBLE);
				adapter = new TaskAdapter();
				lv_task_manager.setAdapter(adapter);
			}

			@Override
			public void doInBackground() {
				taskInfos = TaskInfoProvider
						.getTaskInfos(getApplicationContext());
				userTaskInfos = new ArrayList<TaskInfo>();
				systemTaskInfos = new ArrayList<TaskInfo>();
				for (TaskInfo taskinfo : taskInfos) {
					if (taskinfo.isUserTask()) {
						userTaskInfos.add(taskinfo);
					} else {
						systemTaskInfos.add(taskinfo);
					}
				}

			}
		}.execute();
	}

	private class TaskAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean showsystem = sp.getBoolean("showsystem", false);
			if (showsystem) {
				return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
			}else{
				return userTaskInfos.size() + 1;
			}
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return null;
			} else if (position == (userTaskInfos.size() + 1)) {
				return null;
			} else if (position <= userTaskInfos.size()) {
				int newpositon = position - 1;
				return userTaskInfos.get(newpositon);
			} else {
				int newpositon = position - 1 - userTaskInfos.size() - 1;
				return systemTaskInfos.get(newpositon);
			}

		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TaskInfo taskinfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(R.color.gray);
				tv.setTextSize(18);
				tv.setText("�û�����:" + userTaskInfos.size() + "��");
				return tv;
			} else if (position == (userTaskInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setBackgroundColor(R.color.gray);
				tv.setTextSize(18);
				tv.setText("ϵͳ����:" + systemTaskInfos.size() + "��");
				return tv;
			} else if (position <= userTaskInfos.size()) {
				int newpositon = position - 1;
				taskinfo = userTaskInfos.get(newpositon);
			} else {
				int newpositon = position - 1 - userTaskInfos.size() - 1;
				taskinfo = systemTaskInfos.get(newpositon);
			}

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_task_item, null);
				holder = new ViewHolder();
				holder.tv_mem = (TextView) view.findViewById(R.id.tv_task_mem);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_task_name);
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_task_icon);
				holder.cb = (CheckBox) view.findViewById(R.id.cb_task_status);
				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(taskinfo.getTaskIcon());
			holder.tv_name.setText(taskinfo.getTaskName());
			holder.tv_mem.setText("�ڴ�ռ��:"
					+ Formatter.formatFileSize(getApplicationContext(),
							taskinfo.getMemsize()));
			holder.cb.setChecked(taskinfo.isChecked());
			if (getPackageName().equals(taskinfo.getPackName())) {// ���Լ�.
				holder.cb.setVisibility(View.INVISIBLE);
			} else {
				holder.cb.setVisibility(View.VISIBLE);
			}

			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_mem;
		ImageView iv_icon;
		CheckBox cb;
	}

	/**
	 * ȫѡ
	 * 
	 * @param view
	 */
	public void selectAll(View view) {
		if (selectAll) {// �Ѿ�ȫѡ�� ,����Ϊȫ��ѡ
			for (TaskInfo info : userTaskInfos) {
				info.setChecked(false);
			}
			for (TaskInfo info : systemTaskInfos) {
				info.setChecked(false);
			}
			adapter.notifyDataSetChanged();
			selectAll = false;
			bt_select_all.setText("ȫѡ");
		} else { // û��ȫ��ѡ��,����ȫѡ
			for (TaskInfo info : userTaskInfos) {
				info.setChecked(true);
				if (getPackageName().equals(info.getPackName())) {// ���Լ�.
					info.setChecked(false);
				}
			}
			for (TaskInfo info : systemTaskInfos) {
				info.setChecked(true);
			}
			adapter.notifyDataSetChanged();
			selectAll = true;
			bt_select_all.setText("ȫ��ѡ");
		}
	}

	/**
	 * һ���������
	 */
	public void killAll(View view) {
		ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		int count = 0;
		long savedMem = 0;
		List<TaskInfo> killedTasks = new ArrayList<TaskInfo>();
		for (TaskInfo info : userTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
				count++;
				savedMem += info.getMemsize();
				killedTasks.add(info);
			}
		}
		for (TaskInfo info : systemTaskInfos) {
			if (info.isChecked()) {
				am.killBackgroundProcesses(info.getPackName());
				count++;
				savedMem += info.getMemsize();
				killedTasks.add(info);
			}
		}
		String memStr = Formatter.formatFileSize(this, savedMem);
		// Toast.makeText(this, "ɱ����"+count+"������,�ͷ���"+memStr+"���ڴ�", 1).show();

		MyToast.show(R.drawable.notification, "ɱ����" + count + "������,�ͷ���"
				+ memStr + "���ڴ�", this);
		// ѡ����ɱ���ĸ���Ŀ �������Ŀ�ӽ������Ƴ�.
		for (TaskInfo info : killedTasks) {
			if (info.isUserTask()) {
				userTaskInfos.remove(info);
			} else {
				systemTaskInfos.remove(info);
			}
		}
		adapter.notifyDataSetChanged();
		runningProcessCount -= count;
		availRam += savedMem;
		tv_process_count.setText("�����н���:" + runningProcessCount + "��");
		tv_mem_info.setText("����/���ڴ�:"
				+ Formatter.formatFileSize(this, availRam) + "/"
				+ Formatter.formatFileSize(this, totalRam));
	}

	public void setting(View view) {
		Intent intent = new Intent(this,TaskSettingActivity.class);
		startActivityForResult(intent, 0);

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode==200){
			adapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
