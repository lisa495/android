package cn.itheima.mobilesafe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.itheima.mobilesafe.db.dao.AppLockDao;
import cn.itheima.mobilesafe.domain.AppInfo;
import cn.itheima.mobilesafe.engine.AppInfoProvider;
import cn.itheima.mobilesafe.utils.DelayExecuter;
import cn.itheima.mobilesafe.utils.MyAsyncTask;

public class AppLockActivity extends Activity implements OnClickListener {

	private TextView tv_unlock, tv_locked;
	private LinearLayout ll_unlock, ll_locked;
	private ListView lv_unlock, lv_locked;
	private ProgressBar pb_loading;
	private List<AppInfo> appInfos;
	
	private List<AppInfo> appLockedInfos;//����Ѿ������������Ϣ.
	private List<AppInfo> appunLockInfos;//���û�м����������Ϣ.
	
	private TextView tv_locked_status,tv_unlock_status;
	
	private AppInfoAdapter unlockAdapter;
	private AppInfoAdapter lockedAdapter;
	
	
	private AppLockDao dao;
	
	
	private boolean removingFlag;
	
	private PopupWindow popwindow ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		popwindow = new PopupWindow(new View(this), getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight());
		appLockedInfos = new ArrayList<AppInfo>();
		appunLockInfos = new ArrayList<AppInfo>();
		setContentView(R.layout.activity_applock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_unlock.setOnClickListener(this);
		tv_locked = (TextView) findViewById(R.id.tv_locked);
		tv_locked.setOnClickListener(this);
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_locked = (LinearLayout) findViewById(R.id.ll_locked);
		lv_locked = (ListView) findViewById(R.id.lv_locked);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		dao = new AppLockDao(this);
		
		//δ����������Ŀ�ĵ���¼�
		lv_unlock.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(removingFlag){
					System.out.println("removingFlag"+removingFlag);
					return;
				}
				removingFlag = true;
				popwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				popwindow.showAtLocation(parent, Gravity.TOP|Gravity.LEFT, 0, 0);
				AppInfo appinfo = (AppInfo) lv_unlock.getItemAtPosition(position);
				String packname = appinfo.getPackname();
				dao.add(packname);//��������ĳ�����������ݿ�.
				appLockedInfos.add(appinfo);//�����Щ�������ĳ�����Ϣ���ڴ漯��
				appunLockInfos.remove(appinfo);//�ӵ�ǰ���������Ƴ��Ǹ��������ĳ���
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(800);
				view.startAnimation(ta);
				new DelayExecuter() {
					@Override
					public void onPostExecute() {
						unlockAdapter.notifyDataSetChanged();
						lockedAdapter.notifyDataSetChanged();
						removingFlag = false;
						popwindow.dismiss();
					}
				}.execute(800);
			}
		});
		
		//�Ѽ���������Ŀ�ĵ���¼�
		lv_locked.setOnItemClickListener(new  OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(removingFlag){
					System.out.println("removingFlag"+removingFlag);
					return;
				}
				removingFlag = true;
				popwindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
				popwindow.showAtLocation(parent, Gravity.TOP|Gravity.LEFT, 0, 0);
				AppInfo appinfo = (AppInfo) lv_locked.getItemAtPosition(position);
				String packname = appinfo.getPackname();
				dao.delete(packname);//ɾ�������ĳ�����������ݿ�.
				appLockedInfos.remove(appinfo);//���ڴ漯���Ƴ������Ŀ
				appunLockInfos.add(appinfo);//��ӵ�δ�����ĳ��򼯺�
				TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
				ta.setDuration(800);
				view.startAnimation(ta);
				new DelayExecuter() {
					@Override
					public void onPostExecute() {//800�����Ĳ���.
						unlockAdapter.notifyDataSetChanged();
						lockedAdapter.notifyDataSetChanged();
						removingFlag = false;
						popwindow.dismiss();
					}
				}.execute(800);
				
			}
		});
		
		
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		tv_locked_status = (TextView) findViewById(R.id.tv_locked_status);
		tv_unlock_status = (TextView) findViewById(R.id.tv_unlock_status);
		new MyAsyncTask() {

			@Override
			public void onPreExecute() {
				pb_loading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPostExecute() {
				pb_loading.setVisibility(View.INVISIBLE);
				unlockAdapter = new AppInfoAdapter(appunLockInfos, true);
				lv_unlock.setAdapter(unlockAdapter);
				
				lockedAdapter = new AppInfoAdapter(appLockedInfos, false);
				lv_locked.setAdapter(lockedAdapter);
			}

			@Override
			public void doInBackground() {
				appInfos = AppInfoProvider.getAppInfos(getApplicationContext());
				for(AppInfo info : appInfos){
					if(dao.find(info.getPackname())){
						appLockedInfos.add(info);
					}else{
						appunLockInfos.add(info);
					}
				}
				
			}
		}.execute();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_unlock:// δ����.
			tv_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
			tv_locked.setBackgroundResource(R.drawable.tab_right_default);
			ll_unlock.setVisibility(View.VISIBLE);
			ll_locked.setVisibility(View.INVISIBLE);

			break;

		case R.id.tv_locked:// �Ѽ���
			tv_unlock.setBackgroundResource(R.drawable.tab_left_default);
			tv_locked.setBackgroundResource(R.drawable.tab_right_pressed);
			ll_unlock.setVisibility(View.INVISIBLE);
			ll_locked.setVisibility(View.VISIBLE);
			break;

		}

	}

	private class AppInfoAdapter extends BaseAdapter {

		private List<AppInfo> showAppInfos;
		private boolean unlockflag;

		public AppInfoAdapter(List<AppInfo> showAppInfos, boolean unlockflag) {
			this.showAppInfos = showAppInfos;
			this.unlockflag = unlockflag;
		}

		@Override
		public int getCount() {
			if(unlockflag){
				tv_unlock_status.setText("δ�������("+showAppInfos.size()+")��");
			}else{
				tv_locked_status.setText("�Ѽ������("+showAppInfos.size()+")��");
			}
			return showAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			return showAppInfos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			ViewHolder holder;
			if (convertView != null && convertView instanceof RelativeLayout) {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			} else {
				if (unlockflag) {
					view = View.inflate(getApplicationContext(),
							R.layout.list_appunlock_item, null);
				}else{
					view = View.inflate(getApplicationContext(),
							R.layout.list_applocked_item, null);
				}
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) view
						.findViewById(R.id.iv_applock_icon);
				holder.tv_name = (TextView) view
						.findViewById(R.id.tv_applock_name);
				view.setTag(holder);
			}
			AppInfo appinfo = showAppInfos.get(position);
			holder.iv_icon.setImageDrawable(appinfo.getAppicon());
			holder.tv_name.setText(appinfo.getAppname());
			return view;
		}

	}

	static class ViewHolder {
		TextView tv_name;
		ImageView iv_icon;
	}
}
