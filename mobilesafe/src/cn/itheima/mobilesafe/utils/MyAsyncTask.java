package cn.itheima.mobilesafe.utils;

import android.os.Handler;

public abstract class MyAsyncTask {
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			onPostExecute();
		};
	};
	/**
	 * 1.��ʱ������֮ǰ���õķ���.
	 */
	public abstract void onPreExecute();

	/**
	 * 3. ��ʱ����ִ��֮����õķ���
	 */
	public abstract void onPostExecute();
	
	/**
	 * �ں�ִ̨�еĺ�ʱ������ ���������߳�.
	 */
	public abstract void doInBackground();

	/**
	 * ִ��һ���첽����.
	 */
	public void execute() {
		onPreExecute();
		new Thread() {
			public void run() {
				doInBackground();
				handler.sendEmptyMessage(0);
			};
		}.start();

	}
}
