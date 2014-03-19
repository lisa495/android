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
	//1.����һ������ʶ����
	private GestureDetector mGestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config",MODE_PRIVATE);
		//2. ��ʼ������ʶ����
		mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener(){
			//����ָ����Ļ�ϻ�����ʱ����õķ���
			
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				if(Math.abs(velocityX)<200){
					Log.i(TAG,"�ƶ���̫��,�������Ϸ�");
					return true;
				}
				
				if(Math.abs(e1.getRawY()- e2.getRawY())>100){
					Log.i(TAG,"��ֱ�����ƶ��������,�������Ϸ�");
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
	
	//3.������ʶ������Ч
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
	
	
	/**
	 * ��ʼ��view����
	 */
	public abstract void initView();
	/**
	 * ����view�������Ӧ�¼�
	 */
	public abstract void setupView();
	
	/**
	 * ��ʾ��һ��
	 */
	public abstract void showNext();
	/**
	 * ��ʾ��һ��
	 */
	public abstract void showPre();
	
	
	public void next(View view){
		 showNext();
	}
	public void pre(View view){
		showPre();
	}
}
