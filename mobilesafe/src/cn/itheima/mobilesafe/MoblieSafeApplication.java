package cn.itheima.mobilesafe;

import cn.itheima.mobilesafe.domain.BlackNumberInfo;
import android.app.Application;

public class MoblieSafeApplication extends Application {
	public BlackNumberInfo info;
	@Override
	public void onCreate() {
		super.onCreate();
	}
}
