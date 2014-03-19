package cn.itheima.mobilesafe.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.R;

public class MyToast {
	public static void show(int icon, String msg, Context context){
		Toast toast = new Toast(context);
		View view = View.inflate(context, R.layout.my_toast, null);
		TextView tv = (TextView) view.findViewById(R.id.tv_toast_msg);
		tv.setText(msg);
		ImageView iv = (ImageView) view.findViewById(R.id.iv_toast_icon);
		iv.setImageResource(icon);
		toast.setView(view);
		//toast.setGravity(Gravity.LEFT|Gravity.TOP, xOffset, yOffset);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}
