package cn.itheima.mobilesafe;

import java.io.File;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.TextView;

public class CleanSDActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TextView tv = new TextView(this);
		tv.setTextSize(30);
		tv.setText("SD������");
		setContentView(tv);
		
		
		File file = Environment.getExternalStorageDirectory();
		
		File[] files = file.listFiles();
		
		for(File f: files){
			if(f.isDirectory()){
				String dirname = f.getName();
				//��ѯ���ݿ� ��ѯ���dirname�Ƿ������ݿ��������.
				//��ʾ�û�ɾ��sd���ϵ��ļ�.
			}
		}
	}
	
	public void deleteFile(File file){
		if(file.isDirectory()){
			File[] files = file.listFiles();
			for(File f : files){
				deleteFile(f);
			}
		}else{
			file.delete();
		}
	}
	
	
}
