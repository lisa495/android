package cn.itheima.mobilesafe.test;

import android.content.ContentValues;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestSmsUtils extends AndroidTestCase {
	public void testInsertSms() throws Exception{
		Uri uri = Uri.parse("content://sms/");
		ContentValues values = new ContentValues();
		values.put("address", "95533");
		values.put("date", "1356245022508");
		values.put("type", "1");
		getContext().getContentResolver().insert(uri, values);
	}
}
