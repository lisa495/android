package cn.itheima.mobilesafe;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.domain.UpdateInfo;
import cn.itheima.mobilesafe.engine.UpdateInfoParser;
import cn.itheima.mobilesafe.utils.CopyFileUtils;
import cn.itheima.mobilesafe.utils.DownLoadUtil;

public class SplashActivity extends Activity {
	public static final int PARSE_XML_ERROR = 10;
	public static final int PARSE_XML_SUCCESS = 11;
	public static final int SERVER_ERROR = 12;
	public static final int URL_ERROR = 13;
	public static final int NETWORK_ERROR = 14;
	private static final int DOWNLOAD_SUCCESS = 15;
	private static final int DOWNLOAD_ERROR = 16;
	protected static final String TAG = "SplashActivity";
	protected static final int COPY_ERROR = 17;
	private TextView tv_splash_version;
	private UpdateInfo updateInfo;

	private ProgressDialog pd;// ���ؽ��ȵĶԻ���

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case PARSE_XML_ERROR:
				Toast.makeText(getApplicationContext(), "����xmlʧ��", 0).show();
				// �������������
				loadMainUI();
				break;
			case SERVER_ERROR:
				Toast.makeText(getApplicationContext(), "�������쳣", 0).show();
				// �������������
				loadMainUI();
				break;
			case URL_ERROR:
				Toast.makeText(getApplicationContext(), "��������ַ�쳣", 0).show();
				// �������������
				loadMainUI();
				break;
			case NETWORK_ERROR:
				Toast.makeText(getApplicationContext(), "�����쳣", 0).show();
				// �������������
				loadMainUI();
				break;
			case PARSE_XML_SUCCESS:
				if (getAppVersion().equals(updateInfo.getVersion())) {
					// �������������
					Log.i(TAG, "�汾����ͬ,����������");
					loadMainUI();
				} else {
					Log.i(TAG, "�汾�Ų���ͬ,������������ʾ�Ի���");
					showUpdateDialog();
				}
				break;
			case DOWNLOAD_ERROR:
				Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
				// �������������
				loadMainUI();
				break;
			case DOWNLOAD_SUCCESS:
				File file = (File) msg.obj;
				Log.i(TAG, "��װapk" + file.getAbsolutePath());
				// ��װapk
				installApk(file);
				finish();
				break;
			case COPY_ERROR:
				Toast.makeText(getApplicationContext(), "�������ݿ��쳣", 0).show();
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		Intent intent = new Intent();
//		intent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//		Intent shortCutIntent = new Intent();
//		shortCutIntent.setAction("cn.itheima.xxx");
//		shortCutIntent.addCategory(Intent.CATEGORY_DEFAULT);
//		
//		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
//    	intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "������ʿ");
//    	intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.atools));
//    	sendBroadcast(intent);
		
		setContentView(R.layout.activity_splash);
		tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
		tv_splash_version.setText("�汾:" + getAppVersion());//version

		// ���ӷ����� ���汾����.
		new Thread(new CheckVersionTask()).start();

		AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
		aa.setDuration(2000);
		findViewById(R.id.rl_splash).startAnimation(aa);

		// �����ؼ��ļ���ϵͳĿ¼.
		copyAddressDB();

		copyCommonNumDB();
	}

	/**
	 * �ͷ� ���ݿ��ļ���ϵͳĿ¼
	 */
	private void copyAddressDB() {
		final File file = new File(getFilesDir(), "address.db");
		if (file.exists() && file.length() > 0) {
			//do nothing
		} else {
			new Thread() {
				public void run() {
					Message msg = Message.obtain();
					try {
						InputStream is = getAssets().open("address.db");
						File f = CopyFileUtils.copyFile(is, file.getAbsolutePath());
						if(f!=null){
							// �����ɹ�.
						}else{
							msg.what = COPY_ERROR;
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = COPY_ERROR;
					}finally{
						handler.sendMessage(msg);
					}
				};
			}.start();
		}

	}
	/**
	 * �ͷ� ���ú������ݿ⵽ϵͳĿ¼
	 */
	private void copyCommonNumDB() {
		final File file = new File(getFilesDir(), "commonnum.db");
		if (file.exists() && file.length() > 0) {
			//do nothing
		} else {
			new Thread() {
				public void run() {
					Message msg = Message.obtain();
					try {
						InputStream is = getAssets().open("commonnum.db");
						File f = CopyFileUtils.copyFile(is, file.getAbsolutePath());
						if(f!=null){
							// �����ɹ�.
						}else{
							msg.what = COPY_ERROR;
						}
					} catch (Exception e) {
						e.printStackTrace();
						msg.what = COPY_ERROR;
					}finally{
						handler.sendMessage(msg);
					}
				};
			}.start();
		}

	}
	/**
	 * ��װһ��apk�ļ�
	 * 
	 * @param file
	 */
	protected void installApk(File file) {
		// <action android:name="android.intent.action.VIEW" />
		// <category android:name="android.intent.category.DEFAULT" />
		// <data android:scheme="content" />
		// <data android:scheme="file" />
		// <data android:mimeType="application/vnd.android.package-archive" />
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		// intent.setType("application/vnd.android.package-archive");
		// intent.setData(Uri.fromFile(file));
		intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		startActivity(intent);
	}

	/**
	 * �Զ���������ʾ�Ի���
	 */
	protected void showUpdateDialog() {

		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("��������");
		builder.setMessage(updateInfo.getDescription());
		builder.setPositiveButton("ȷ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String apkurl = updateInfo.getApkurl();
				pd = new ProgressDialog(SplashActivity.this);
				pd.setTitle("��������");
				pd.setMessage("��������...");
				pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				pd.show();
				Log.i(TAG, "���غ�װ:" + apkurl);
				final File file = new File(Environment
						.getExternalStorageDirectory(), DownLoadUtil
						.getFileName(apkurl));
				// �ж�sd���Ƿ����,ֻ�п���״̬.
				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					new Thread() {
						public void run() {
							File savedFile = DownLoadUtil.download(
									updateInfo.getApkurl(),
									file.getAbsolutePath(), pd);
							Message msg = Message.obtain();
							if (savedFile != null) {
								// ���سɹ�
								msg.what = DOWNLOAD_SUCCESS;
								msg.obj = savedFile;
							} else {
								// ����ʧ��
								msg.what = DOWNLOAD_ERROR;

							}
							handler.sendMessage(msg);
							pd.dismiss();
						};
					}.start();
				} else {
					Toast.makeText(getApplicationContext(), "sd��������", 0).show();
					loadMainUI();
				}

			}
		});
		builder.setNegativeButton("ȡ��", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				loadMainUI();
			}
		});
		builder.create().show();
	    //builder.show();
	}

	/**
	 * ����������
	 */
	private void loadMainUI() {
		Intent intent = new Intent(this, HomeActivity.class);
		startActivity(intent);
		finish();
	}

	private class CheckVersionTask implements Runnable {
		@Override
		public void run() {
			SharedPreferences sp = getSharedPreferences("config", MODE_PRIVATE);
			boolean isupdate = sp.getBoolean("update", true);
			if (!isupdate) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				loadMainUI();
				return;
			}
			long startTime = System.currentTimeMillis();
			Message msg = Message.obtain();
			try {
				URL url = new URL(getResources().getString(R.string.serverurl));
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(1500);
				int code = conn.getResponseCode();
				if (code == 200) {
					InputStream is = conn.getInputStream();
					updateInfo = UpdateInfoParser.getUpdateInfo(is);
					if (updateInfo == null) {
						// TODO:����xmlʧ��
						msg.what = PARSE_XML_ERROR;
					} else {
						// �����ɹ�
						msg.what = PARSE_XML_SUCCESS;
					}
				} else {
					// TODO:�������ڲ�����.
					msg.what = SERVER_ERROR;
				}
			} catch (MalformedURLException e) {
				msg.what = URL_ERROR; // http://
				e.printStackTrace();
			} catch (NotFoundException e) {
				msg.what = URL_ERROR; //
				e.printStackTrace();
			} catch (IOException e) {
				msg.what = NETWORK_ERROR;
				e.printStackTrace();
			} finally {
				long endTime = System.currentTimeMillis();
				long dTime = endTime - startTime;
				if (dTime < 2000) {
					try {
						Thread.sleep(2000 - dTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				handler.sendMessage(msg);
			}

		}
	}

	/**
	 * ��ȡӦ�ó���İ汾��
	 * 
	 * @return
	 */
	private String getAppVersion() {
		// �õ�ϵͳ�İ�������
		PackageManager pm = getPackageManager();
		try {
			PackageInfo packinfo = pm.getPackageInfo(getPackageName(), 0);
			return packinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			// can't reach
			return "";
		}

	}

}