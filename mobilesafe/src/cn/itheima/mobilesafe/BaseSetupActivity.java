package cn.itheima.mobilesafe;

import java.io.InputStream;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	protected static final String TAG = "BaseSetupActivity";
	protected SharedPreferences sp;
	//1.定义一个手势识别器
	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		//2. 初始化手势识别器
		mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			//当手指在屏幕上滑动的时候调用的方法
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(velocityX)<200){
					Log.i(TAG,"移动的太慢,动作不合法");
					return true;
				}
				
				if(Math.abs(e1.getRawY()- e2.getRawY())>100){
					Log.i(TAG,"竖直方向移动距离过大,动作不合法");
					return true;
				}
				
				if((e1.getRawX()- e2.getRawX())>200){
					showNext();
					return true;
				}
				if((e2.getRawX()- e1.getRawX())>200){
					showPre();
					return true;
				}
				
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		});
		
		initView();
		setupView();
		
		
		
	}
	
	//3.让手势识别器生效
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	
	/**
	 * 初始化view对象
	 */
	public abstract void initView();
	/**
	 * 设置view对象的响应事件
	 */
	public abstract void setupView();
	
	/**
	 * 显示下一个
	 */
	public abstract void showNext();
	/**
	 * 显示上一个
	 */
	public abstract void showPre();
	
	
	public void next(View view){
		 showNext();
	}
	public void pre(View view){
		showPre();
	}
}
