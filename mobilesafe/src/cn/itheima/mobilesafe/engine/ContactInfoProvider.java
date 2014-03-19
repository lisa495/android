package cn.itheima.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import cn.itheima.mobilesafe.domain.ContactInfo;

public class ContactInfoProvider {

	/**
	 * 获取系统里面所有的联系人信息.
	 * 
	 * @return 所有联系人信息的集合
	 */
	public static List<ContactInfo> getContactInfos(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri datauri = Uri.parse("content://com.android.contacts/data");
		Cursor cursor = resolver.query(uri, new String[] { "contact_id" },
				null, null, null);
		List<ContactInfo> infos = new ArrayList<ContactInfo>();
		while (cursor.moveToNext()) {
			String id = cursor.getString(0);
			if (id != null) {
				ContactInfo info = new ContactInfo();
				Cursor dataCursor = resolver.query(datauri, new String[] {
						"mimetype", "data1" }, "raw_contact_id=?",
						new String[] { id }, null);
				while (dataCursor.moveToNext()) {
					String mime =  dataCursor.getString(0);
					String data1 = dataCursor.getString(1);
					if("vnd.android.cursor.item/name".equals(mime)){
						info.setName(data1);
					}else if("vnd.android.cursor.item/phone_v2".equals(mime)){
						info.setPhone(data1);
					}
					
				}
				infos.add(info);
				dataCursor.close();
			}
		}
		cursor.close();

		return infos;
	}
}
