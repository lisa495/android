package cn.itheima.mobilesafe.domain;

import java.io.Serializable;

public class BlackNumberInfo implements Serializable{
	private String number;
	private String mode;

	public BlackNumberInfo() {

	}
	public BlackNumberInfo(String number, String mode) {
		this.number = number;
		setMode(mode);
	}

	@Override
	public String toString() {
		return "BlackNumberInfo [number=" + number + ", mode=" + mode + "]";
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		if ("1".equals(mode) || "2".equals(mode) || "3".equals(mode)) {
			this.mode = mode;
		}else{
			this.mode = "1";
		}
	}

}
