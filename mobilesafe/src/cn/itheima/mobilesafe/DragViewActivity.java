package cn.itheima.mobilesafe;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

public class DragViewActivity extends Activity {
	protected static final String TAG = "DragViewActivity";
	private ImageView iv_drag_view;
	private TextView tv_drag_view;
	private Display display;
	private SharedPreferences sp;
	private long firstClickTime;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Have the system blur any windows behind this one.
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

		setContentView(R.layout.activity_drag_view);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		iv_drag_view = (ImageView) findViewById(R.id.iv_drag_view);
		tv_drag_view = (TextView) findViewById(R.id.tv_drag_view);
		
		int lastx = sp.getInt("lastx", 0);
		int lasty = sp.getInt("lasty", 0);
		
		//iv_drag_view.layout(iv_drag_view.getLeft()+lastx, iv_drag_view.getTop()+lasty, iv_drag_view.getRight()+lastx, iv_drag_view.getBottom()+lasty);
		RelativeLayout.LayoutParams params = (LayoutParams) iv_drag_view.getLayoutParams();
		params.leftMargin = lastx;
		params.topMargin = lasty;
		iv_drag_view.setLayoutParams(params);
		
		WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
		display = wm.getDefaultDisplay();

		iv_drag_view.setOnTouchListener(new OnTouchListener() {
			int startX;
			int startY;
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					Log.i(TAG,"按下");
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					Log.i(TAG,"移动");
					int newX = (int) event.getRawX();
					int newY = (int) event.getRawY();
					int dx = newX - startX;
					int dy = newY - startY;
					int l = iv_drag_view.getLeft();
					int t = iv_drag_view.getTop();
					int r = iv_drag_view.getRight();
					int b = iv_drag_view.getBottom();
					int newl = l + dx;
					int newr = r + dx;
					int newt = t + dy;
					int newb = b + dy;
					
					if(newl<0||newr >display.getWidth() || newt<0||newb>display.getHeight()){
						break;
					}
					
					int tv_height = tv_drag_view.getBottom() - tv_drag_view.getTop();
					
					if(newt>display.getHeight()/2){
						//图片在下面 ,文本设置在上面
						tv_drag_view.layout(tv_drag_view.getLeft(), 0, tv_drag_view.getRight(), tv_height);
					}else{
						//文本设置在下面
						tv_drag_view.layout(tv_drag_view.getLeft(), display.getHeight()-tv_height-30, tv_drag_view.getRight(), display.getHeight()-30);
					}
					
					
					iv_drag_view.layout(newl, newt, newr, newb);
					
					//重新初始化手指的位置.
					startX = (int) event.getRawX();
									
					startY = (int) event.getRawY();
					
					break;
				case MotionEvent.ACTION_UP:
					Log.i(TAG,"松手");
					Editor editor = sp.edit();
					editor.putInt("lastx", iv_drag_view.getLeft());
					editor.putInt("lasty", iv_drag_view.getTop());
					editor.commit();
					break;
				}
				return false;
			}
		});
		iv_drag_view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i(TAG,"被点击了.....");
				if(firstClickTime>0){
					//第二次点击.
					long secondTime =  System.currentTimeMillis();
					if((secondTime - firstClickTime) <500){
						//双击事件.
						Log.i(TAG,"被双击....了.....");
						int iv_width = iv_drag_view.getRight() - iv_drag_view.getLeft();
						int l = display.getWidth()/2 - iv_width/2;
						int r = display.getWidth()/2 + iv_width/2;
						iv_drag_view.layout(l, iv_drag_view.getTop(), r, iv_drag_view.getBottom());
						
						Editor editor = sp.edit();
						editor.putInt("lastx", iv_drag_view.getLeft());
						editor.putInt("lasty", iv_drag_view.getTop());
						editor.commit();
						
					}
					firstClickTime = 0;
				}
				//判断 是否是第一次点击 , 记录点击时间
				firstClickTime = System.currentTimeMillis();
				new Thread(){
					public void run() {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						firstClickTime = 0;
					};
				}.start();
			}
		});
	}
}
