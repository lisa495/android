package cn.itheima.mobilesafe.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String appname;
	private String packname;
	private String version;
	private Drawable appicon;
	private boolean inRom;
	private boolean userapp;
	
	private boolean usesms;
	private boolean usegps;
	private boolean usecontact;
	private boolean usenet;
	
	
	public boolean isUsesms() {
		return usesms;
	}
	public void setUsesms(boolean usesms) {
		this.usesms = usesms;
	}
	public boolean isUsegps() {
		return usegps;
	}
	public void setUsegps(boolean usegps) {
		this.usegps = usegps;
	}
	public boolean isUsecontact() {
		return usecontact;
	}
	public void setUsecontact(boolean usecontact) {
		this.usecontact = usecontact;
	}
	public boolean isUsenet() {
		return usenet;
	}
	public void setUsenet(boolean usenet) {
		this.usenet = usenet;
	}
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	public String getPackname() {
		return packname;
	}
	public void setPackname(String packname) {
		this.packname = packname;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public Drawable getAppicon() {
		return appicon;
	}
	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}
	public boolean isInRom() {
		return inRom;
	}
	public void setInRom(boolean inRom) {
		this.inRom = inRom;
	}
	public boolean isUserapp() {
		return userapp;
	}
	public void setUserapp(boolean userapp) {
		this.userapp = userapp;
	}
	
	
}
