package cn.itheima.mobilesafe;

import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CleanCacheActivity extends Activity {
	public static final int SCANING = 70;
	protected static final int SCAN_FINISH = 71;
	private PackageManager pm;
	private LinearLayout ll;
	private TextView tv_clean_cache_status;
	private ProgressBar pb;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANING:
				try {
					final MyPackageInfo info = (MyPackageInfo) msg.obj;
					tv_clean_cache_status.setText("正在扫描:"
							+ pm.getApplicationInfo(info.packname, 0)
									.loadLabel(pm));
					if (info.cachesize > 0) {
						View view = View.inflate(getApplicationContext(),
								R.layout.list_item, null);
						view.setClickable(true);
						view.setBackgroundResource(R.drawable.home_selector);
						view.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								System.out.println("被点击了...");
								String packname = info.packname;
								if (Build.VERSION.SDK_INT >= 9) {
									// 开启设置界面.
									Intent intent = new Intent();
									intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
									intent.setData(Uri.parse("package:"
											+ packname));
									startActivity(intent);
								} else {// 2.2 一下版本呢
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									intent.addCategory("android.intent.category.DEFAULT");
									intent.addCategory("android.intent.category.VOICE_LAUNCH");
									intent.putExtra("pkg", packname);
									startActivity(intent);
								}
							}
						});
						ImageView iv = (ImageView) view
								.findViewById(R.id.imageView1);
						TextView tv_name = (TextView) view
								.findViewById(R.id.textView1);
						TextView tv_codesize = (TextView) view
								.findViewById(R.id.textView2);

						iv.setImageDrawable(pm.getApplicationInfo(
								info.packname, 0).loadIcon(pm));
						tv_name.setText(pm.getApplicationInfo(info.packname, 0)
								.loadLabel(pm));
						tv_codesize.setText("缓存大小:"
								+ Formatter
										.formatFileSize(
												getApplicationContext(),
												info.cachesize));

						ll.addView(view, 0);
					}
				} catch (NameNotFoundException e) {
					e.printStackTrace();
				}
				break;

			case SCAN_FINISH:
				tv_clean_cache_status.setText("扫描完毕.");
				break;
			}

		};
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_clean_cache);
		ll = (LinearLayout) findViewById(R.id.ll_container);
		pb = (ProgressBar) findViewById(R.id.progressBar1);
		tv_clean_cache_status = (TextView) findViewById(R.id.tv_clean_cache_status);
		pm = getPackageManager();
		new Thread() {
			public void run() {
				List<PackageInfo> infos = pm.getInstalledPackages(0);
				pb.setMax(infos.size());
				int total = 0;
				for (PackageInfo info : infos) {
					String packname = info.packageName;
					getPackageSize(packname);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					total++;
					pb.setProgress(total);
				}

				Message msg = Message.obtain();
				msg.what = SCAN_FINISH;
				handler.sendMessage(msg);
			};
		}.start();

	}

	private void getPackageSize(String packname) {
		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[] { String.class,
							IPackageStatsObserver.class });

			method.invoke(pm,
					new Object[] { packname, new MyObserver(packname) });
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class MyObserver extends IPackageStatsObserver.Stub {
		private String packname;

		public MyObserver(String packname) {
			this.packname = packname;
		}

		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cachesize = pStats.cacheSize;
			long codesize = pStats.codeSize;
			long datasize = pStats.dataSize;

			MyPackageInfo mypackinfo = new MyPackageInfo();
			mypackinfo.codesize = codesize;
			mypackinfo.cachesize = cachesize;
			mypackinfo.packname = packname;
			Message msg = Message.obtain();
			msg.what = SCANING;
			msg.obj = mypackinfo;
			handler.sendMessage(msg);
		}

	}

	class MyPackageInfo {
		long codesize;
		long cachesize;
		String packname;
	}
}
