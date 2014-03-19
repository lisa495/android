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
		tv.setText("SD卡清理");
		setContentView(tv);
		
		
		File file = Environment.getExternalStorageDirectory();
		
		File[] files = file.listFiles();
		
		for(File f: files){
			if(f.isDirectory()){
				String dirname = f.getName();
				//查询数据库 查询这个dirname是否在数据库里面存在.
				//提示用户删除sd卡上的文件.
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
