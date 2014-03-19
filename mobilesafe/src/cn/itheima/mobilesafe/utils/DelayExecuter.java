package cn.itheima.mobilesafe.utils;

import android.os.Handler;

/**
 * 延时执行的工具类
 * @author Administrator
 *
 */
public abstract class DelayExecuter {
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			onPostExecute();
		};
	};
	
	public abstract void onPostExecute();
	/**
	 * 延时执行
	 * @param delayTime 毫秒值
	 */
	public void execute(final long delayTime){
		new Thread(){
			public void run() {
				try {
					Thread.sleep(delayTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			};
		}.start();
	}
}
