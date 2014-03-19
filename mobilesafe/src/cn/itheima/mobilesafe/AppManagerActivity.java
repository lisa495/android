package cn.itheima.mobilesafe;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.domain.AppInfo;
import cn.itheima.mobilesafe.engine.AppInfoProvider;
import cn.itheima.mobilesafe.utils.DensityUtil;
import cn.itheima.mobilesafe.utils.MyAsyncTask;

public class AppManagerActivity extends Activity implements OnClickListener {
	protected static final String TAG = "AppManagerActivity";
	private TextView tv_free_mem;
	private TextView tv_free_sd;
	private View loading;
	private ListView lv_app_manager;
	private List<AppInfo> appInfos;
	private List<AppInfo> userAppInfos;
	private List<AppInfo> systemAppInfos;
	private TextView tv_appmanger_status;
	private PopupWindow popwindow;
	private LinearLayout ll_uninstall;
	private LinearLayout ll_start;
	private LinearLayout ll_share;
	private AppInfo selectedAppInfo;// ���������Ŀ��Ӧ��appinfo����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_manager);
		tv_free_mem = (TextView) findViewById(R.id.tv_free_mem);
		tv_free_sd = (TextView) findViewById(R.id.tv_free_sd);
		tv_free_mem.setText("�ڴ����:" + getAvailRom());
		tv_free_sd.setText("SD������:" + getAvailSD());
		lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
		loading = findViewById(R.id.loading);
		tv_appmanger_status = (TextView) findViewById(R.id.tv_appmanger_status);
		lv_app_manager.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			// ��listview������ʱ�� ����onscroll�ķ���.
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				dismissPopupWindow();

				if (userAppInfos != null && systemAppInfos != null) {
					if (firstVisibleItem >= (userAppInfos.size())) {// ��ǰ��ϵͳ����
						tv_appmanger_status.setText("ϵͳ���� ("
								+ systemAppInfos.size() + ")");
					} else {
						tv_appmanger_status.setText("�û����� ("
								+ userAppInfos.size() + ")");
					}

				}
				int[] location = new int[2];
				View topview = lv_app_manager.getChildAt(0);
				// if(topview!=null){
				// topview.getLocationInWindow(location);
				// System.out.println("y="+location[1]);
				// }
			}
		});

		lv_app_manager.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// �ڵ�����������ж� �����ǰ�����Ѿ������˵������� ��ȥ�ر���.
				dismissPopupWindow();

				// ����positionȥ��ȡ�������Ŀ��Ӧ�Ķ���.

				Object obj = lv_app_manager.getItemAtPosition(position);
				if (obj instanceof AppInfo) {
					selectedAppInfo = (AppInfo) obj;
					Log.i(TAG, "packname:" + selectedAppInfo.getPackname());
					View contentView = View.inflate(getApplicationContext(),
							R.layout.ui_popupwindow_app, null);
					ll_share = (LinearLayout) contentView
							.findViewById(R.id.ll_share);
					ll_start = (LinearLayout) contentView
							.findViewById(R.id.ll_start);
					ll_uninstall = (LinearLayout) contentView
							.findViewById(R.id.ll_uninstall);

					ll_share.setOnClickListener(AppManagerActivity.this);
					ll_start.setOnClickListener(AppManagerActivity.this);
					ll_uninstall.setOnClickListener(AppManagerActivity.this);

					popwindow = new PopupWindow(contentView,
							ViewGroup.LayoutParams.WRAP_CONTENT,
							ViewGroup.LayoutParams.WRAP_CONTENT);
					popwindow.setBackgroundDrawable(new ColorDrawable(
							Color.LTGRAY));
					int[] location = new int[2];
					view.getLocationInWindow(location);
					popwindow.showAtLocation(parent,
							Gravity.TOP | Gravity.LEFT, location[0] + 60,
							location[1]);
					
					ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f,
							1.0f, 0.5f, 0.5f);
					sa.setDuration(600);

					TranslateAnimation ta = new TranslateAnimation(
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0.1f,
							Animation.RELATIVE_TO_SELF, 0,
							Animation.RELATIVE_TO_SELF, 0);
					ta.setDuration(800);

					AnimationSet set = new AnimationSet(false);
					set.addAnimation(sa);
					set.addAnimation(ta);
					contentView.startAnimation(set);
				}

			}
		});

		fillData();

	}

	private void fillData() {
		new MyAsyncTask() {

			@Override
			public void onPreExecute() {
				loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExecute() {
				loading.setVisibility(View.INVISIBLE);
				lv_app_manager.setAdapter(new AppInfoAdapter());

			}

			@Override
			public void doInBackground() {
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				userAppInfos = new ArrayList<AppInfo>();
				systemAppInfos = new ArrayList<AppInfo>();
				for (AppInfo appinfo : appInfos) {
					if (appinfo.isUserapp()) {
						userAppInfos.add(appinfo);
					} else {
						systemAppInfos.add(appinfo);
					}
				}
			}
		}.execute();
	}

	public String getAvailSD() {
		File path = Environment.getExternalStorageDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(this, blockSize * availableBlocks);
	}

	public String getAvailRom() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return Formatter.formatFileSize(this, blockSize * availableBlocks);
	}

	private void dismissPopupWindow() {
		if (popwindow != null && popwindow.isShowing()) {
			popwindow.dismiss();
			popwindow = null;
		}
	}

	private class AppInfoAdapter extends BaseAdapter {

		@Override
		public boolean isEnabled(int position) {
			if (position == 0) {
				return false;
			} else if (position == (userAppInfos.size() + 1)) {
				return false;
			}

			return super.isEnabled(position);
		}

		@Override
		public int getCount() {
			return userAppInfos.size() + systemAppInfos.size() + 2;
		}

		@Override
		public Object getItem(int position) {
			if (position == 0) {
				return position;
			} else if (position == (userAppInfos.size() + 1)) {
				return position;
			} else if (position <= userAppInfos.size()) {// �û�����
				int newposition = position - 1;
				return userAppInfos.get(newposition);
			} else {
				int newposition = position - 1 - userAppInfos.size() - 1;
				return systemAppInfos.get(newposition);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			AppInfo appinfo;
			if (position == 0) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLUE);
				tv.setBackgroundResource(R.color.gray);
				tv.setText("�û����� (" + userAppInfos.size() + ")");
				return tv;
			} else if (position == (userAppInfos.size() + 1)) {
				TextView tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.BLUE);
				tv.setBackgroundResource(R.color.gray);
				tv.setText("ϵͳ���� (" + systemAppInfos.size() + ")");
				return tv;
			} else if (position <= userAppInfos.size()) {// �û�����
				int newposition = position - 1;
				appinfo = userAppInfos.get(newposition);
			} else {
				int newposition = position - 1 - userAppInfos.size() - 1;
				appinfo = systemAppInfos.get(newposition);
			}

			// AppInfo appinfo = appInfos.get(position);

			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				view = View.inflate(getApplicationContext(),
						R.layout.list_app_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) view.findViewById(R.id.tv_app_name);
				holder.tv_location = (TextView) view
						.findViewById(R.id.tv_app_location);
				holder.tv_version = (TextView) view
						.findViewById(R.id.tv_app_version);
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_app_icon);
				holder.ll_container = (LinearLayout) view.findViewById(R.id.ll_appstatus_container);
				view.setTag(holder);
			}

			holder.iv_icon.setImageDrawable(appinfo.getAppicon());
			holder.tv_name.setText(appinfo.getAppname());
			holder.tv_version.setText(appinfo.getVersion());
			if (appinfo.isInRom()) {
				holder.tv_location.setText("�ֻ��ڴ�" + appinfo.isUserapp());
			} else {
				holder.tv_location.setText("�ⲿ�洢" + appinfo.isUserapp());
			}
			holder.ll_container.removeAllViews();
			if(appinfo.isUsecontact()){
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.contact);
				holder.ll_container.addView(iv, DensityUtil.dip2px(getApplicationContext(), 25), DensityUtil.dip2px(getApplicationContext(), 25));
			}if(appinfo.isUsegps()){
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.gps);
				holder.ll_container.addView(iv, DensityUtil.dip2px(getApplicationContext(), 25), DensityUtil.dip2px(getApplicationContext(), 25));
			}if(appinfo.isUsesms()){
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.sms);
				holder.ll_container.addView(iv, DensityUtil.dip2px(getApplicationContext(), 25), DensityUtil.dip2px(getApplicationContext(), 25));
			}if(appinfo.isUsenet()){
				ImageView iv = new ImageView(getApplicationContext());
				iv.setImageResource(R.drawable.net);
				holder.ll_container.addView(iv, DensityUtil.dip2px(getApplicationContext(), 25), DensityUtil.dip2px(getApplicationContext(), 25));
			}
			
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		TextView tv_location;
		TextView tv_version;
		ImageView iv_icon;
		LinearLayout ll_container;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_share:
			Log.i(TAG, "����:" + selectedAppInfo.getAppname());
			shareApk();
			break;

		case R.id.ll_start:
			Log.i(TAG, "����:" + selectedAppInfo.getAppname());
			startApk();
			break;
		case R.id.ll_uninstall:
			Log.i(TAG, "ж��:" + selectedAppInfo.getAppname());
			// ���Ӧ�ó�����ϵͳ�����. ��ʾ�û�ж�ز���.
			if (selectedAppInfo.isUserapp()) {
				uninstallApk();
			} else {
				Toast.makeText(this, "ϵͳӦ����ҪrootȨ�޲���ж��.", 1).show();
			}
			break;
		}
		dismissPopupWindow();

	}

	/**
	 * ����һ��Ӧ�ó���
	 */
	private void startApk() {
		// �������Ӧ�ó�������ĵ�1��activity.
		String packname = selectedAppInfo.getPackname();
		try {
			PackageInfo packinfo = getPackageManager().getPackageInfo(packname,
					PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityinfos = packinfo.activities;
			if(activityinfos!=null&&activityinfos.length>0){
				ActivityInfo activityinfo = activityinfos[0];
				String className = activityinfo.name;
				Intent intent = new Intent();
				intent.setClassName(selectedAppInfo.getPackname(), className);
				startActivity(intent);
			}else{
				Toast.makeText(this, "�޷�����Ӧ�ó���!", 0).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "�޷�����Ӧ�ó���", 0).show();
		}

	}

	/**
	 * ����Ӧ��.
	 */
	private void shareApk() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.SEND");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				"�Ƽ���ʹ��һ�����.����Ϊ:" + selectedAppInfo.getAppname() + ",�汾:"
						+ selectedAppInfo.getVersion());
		startActivity(intent);
	}

	/**
	 * ж��һ��Ӧ�ó���
	 */
	private void uninstallApk() {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.setAction("android.intent.action.DELETE");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setData(Uri.parse("package:" + selectedAppInfo.getPackname()));
		startActivityForResult(intent, 10);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 10) {
			// ˢ������.
			fillData();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
