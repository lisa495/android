package cn.itheima.mobilesafe.engine;

import java.io.OutputStream;

import org.xmlpull.v1.XmlSerializer;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsUtils {
	private Context context;

	public SmsUtils(Context context) {
		this.context = context;
	}
	
	public interface BackUpProcessListener{
		void beforeBackup(int max);
		void onProcessUpdate(int process);
	}
	
	
	public void backUpSms(OutputStream os, BackUpProcessListener listener) throws Exception{
		Uri uri = Uri.parse("content://sms/");
		XmlSerializer  serializer = Xml.newSerializer();
		serializer.setOutput(os, "utf-8");
		serializer.startDocument("utf-8", true);
		serializer.startTag(null, "smss");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address","date","type","body"} , null, null, null);
		listener.beforeBackup(cursor.getCount());
		int total = 0;
		while(cursor.moveToNext()){
			String address = cursor.getString(0);
			String date  =cursor.getString(1);
			String type  =cursor.getString(2);
			String body  =cursor.getString(3);
			serializer.startTag(null, "sms");
			
			serializer.startTag(null, "address");
			serializer.text(address);
			serializer.endTag(null, "address");

			serializer.startTag(null, "date");
			serializer.text(date);
			serializer.endTag(null, "date");
			
			serializer.startTag(null, "type");
			serializer.text(type);
			serializer.endTag(null, "type");
			
			serializer.startTag(null, "body");
			serializer.text(body);
			serializer.endTag(null, "body");
			
			serializer.endTag(null, "sms");
			
			os.flush();
			total++;
			listener.onProcessUpdate(total);
			Thread.sleep(1000);
		}
		cursor.close();
		serializer.endTag(null, "smss");
		serializer.endDocument();
		os.flush();
		os.close();
		
	}
	
	public void restoreSms(){
		//读取xml文件. 把每一条短信的数据获取出来,插入到系统的数据库
	}
	
}
