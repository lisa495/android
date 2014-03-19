package cn.itheima.mobilesafe;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import cn.itheima.mobilesafe.engine.SmsUtils;
import cn.itheima.mobilesafe.engine.SmsUtils.BackUpProcessListener;
import cn.itheima.mobilesafe.utils.ActivityUtils;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

public class AtoolsActivity extends Activity {
	private ProgressDialog pd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atools);
	}
	
	/**
	 * ��������ز�ѯ�ĵ���¼�
	 * @param view
	 */
	public void numberAddressQuery(View view){
		ActivityUtils.startActivity(this, NumberQueryActivity.class);
	}
	/**
	 * ���ú����ѯ
	 */
	
	public void commonNumberQuery(View view){
		ActivityUtils.startActivity(this, CommonNumberActivity.class);
		
		
	}
	
	/**
	 * ������
	 * @param view
	 */
	public void appLock(View view){
		ActivityUtils.startActivity(this, AppLockActivity.class);
	}
	
	public void backUpSms(View view){
		try {
			File file = new File(Environment.getExternalStorageDirectory(),"smsbackup.xml");
			FileOutputStream fos = new FileOutputStream(file);
			new AsyncTask<OutputStream, Integer, Boolean>() {

				@Override
				protected Boolean doInBackground(OutputStream... params) {
					try {
						SmsUtils smsUtils = new SmsUtils(getApplicationContext());
						smsUtils.backUpSms(params[0],new SmsUtils.BackUpProcessListener() {
							
							@Override
							public void onProcessUpdate(int process) {
								pd.setProgress(process);
								
							}
							
							@Override
							public void beforeBackup(int max) {
								pd.setMax(max);
								
							}
						});
						return true;
					} catch (Exception e) {
						e.printStackTrace();
						return false;
					}
					
				}

				@Override
				protected void onPreExecute() {
					pd = new ProgressDialog(AtoolsActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
					pd.setTitle("��ʾ:");
					pd.setMessage("���ڱ��ݶ���....");
					pd.show();
					super.onPreExecute();
				}

				@Override
				protected void onPostExecute(Boolean result) {
					pd.dismiss();
					if(result){
						Toast.makeText(getApplicationContext(), "���ݳɹ�", 0).show();
					}else{
						Toast.makeText(getApplicationContext(), "����ʧ��", 0).show();
					}
					super.onPostExecute(result);
				}

				@Override
				protected void onProgressUpdate(Integer... values) {
					// TODO Auto-generated method stub
					super.onProgressUpdate(values);
				}
				
				
			}.execute(fos);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "����ʧ��..", 1).show();
		}
		
		
	}
}
