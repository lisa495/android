package cn.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import cn.itheima.mobilesafe.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfoProvider {

	/**
	 * 获取手机上所有应用程序的信息
	 * @param context
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {
		PackageManager pm = context.getPackageManager();// 理解成windows下的程序管理器
		List<AppInfo> appinfos = new ArrayList<AppInfo>();
		List<PackageInfo> packinfos = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		for(PackageInfo packinfo : packinfos){
			AppInfo appinfo = new AppInfo();
			
			String[] permissions = packinfo.requestedPermissions;
			if(permissions!=null&&permissions.length>0){
				for(String permission : permissions){
					if("android.permission.INTERNET".equals(permission)){
						appinfo.setUsenet(true);
					}else if("android.permission.READ_CONTACTS".equals(permission)){
						appinfo.setUsecontact(true);
					}else if("android.permission.ACCESS_FINE_LOCATION".equals(permission)){
						appinfo.setUsegps(true);
					}else if("android.permission.SEND_SMS".equals(permission)){
						appinfo.setUsesms(true);
					}
					
				}
			}

			String version = packinfo.versionName;
			appinfo.setVersion(version);
			
			String packname = packinfo.packageName;
			appinfo.setPackname(packname);
			
			String appname = packinfo.applicationInfo.loadLabel(pm).toString();
			Drawable appicon = packinfo.applicationInfo.loadIcon(pm);
			
			appinfo.setAppicon(appicon);
			appinfo.setAppname(appname);
			
			if((packinfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)==0){
				appinfo.setUserapp(true);//用户应用
			}else{
				appinfo.setUserapp(false);//系统应用
			}
			
			if((packinfo.applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0){
				appinfo.setInRom(true);
			}else{
				appinfo.setInRom(false);
			}
			
			appinfos.add(appinfo);
		}
		
		return appinfos;

	}
}
