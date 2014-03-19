package cn.itheima.mobilesafe.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPSInfoProvider {
	
	private static GPSInfoProvider mGpsInfoProvider;
	private static SharedPreferences sp;
	private static LocationManager lm;
	private static MyListener listener;
	private GPSInfoProvider(){}
	
	public static synchronized GPSInfoProvider getInstance(Context context){
		if(mGpsInfoProvider==null){
			lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			  Criteria criteria = new Criteria();
		        criteria.setAccuracy(Criteria.ACCURACY_FINE);//获取精确的位置.
		        criteria.setAltitudeRequired(true);
		        criteria.setBearingRequired(true);
		        criteria.setCostAllowed(true);
		        criteria.setPowerRequirement(Criteria.POWER_HIGH);
		        criteria.setSpeedRequired(true);
		        String provider = lm.getBestProvider(criteria, true);
		        mGpsInfoProvider = new GPSInfoProvider();
		        listener =  mGpsInfoProvider.new MyListener();
		        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		        lm.requestLocationUpdates(provider, 0, 0,listener);
		        
		     
		}
		
		return mGpsInfoProvider;
	}
	/**
	 * 获取手机最后一次更新到的位置.
	 * @return
	 */
	public String getLastLocation(){
		return sp.getString("lastlocation", "");
	}
	
	 private class MyListener implements LocationListener{

	    	/**
	    	 * 当位置发生改变的时候调用.
	    	 */
			@Override
			public void onLocationChanged(Location location) {
				String longitude = "j: "+ location.getLongitude();
				String latitude = "w: "+ location.getLatitude();
				String dx = "dx: "+location.getAccuracy();
				Editor editor = sp.edit();
				editor.putString("lastlocation", latitude+longitude+dx);
				editor.commit();
				
			}

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				
			}

			@Override
			public void onProviderDisabled(String provider) {
				
			}
	    	
	    }
}
