package cn.itheima.mobilesafe.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AddressDao {

	public static final String path = "/data/data/cn.itheima.mobilesafe/files/address.db";

	/**
	 * ��ȡĳ���绰����Ĺ�������Ϣ
	 * 
	 * @param number
	 *            �绰����
	 * @return
	 */
	public static String getAddress(String number) {
		String address = number;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		// �жϺ����Ƿ����ֻ�����. //11λ 13x 15x 18x 14x
		if (number.matches("^1[3458]\\d{9}$")) {
			Cursor cursor = db
					.rawQuery(
							"select location from data2 where id=(select outkey from data1 where id=?)",
							new String[] { number.substring(0, 7) });
			if (cursor.moveToNext()) {
				address = cursor.getString(0);
			}
			cursor.close();
		}else{//���ֻ�����. 
			switch (number.length()) {
			case 3:
				address = "�������";
				break;
			case 4:
				address = "ģ����";
				break;
			case 5:
				address = "�������";
				break;
			case 7:
				address = "���غ���";
				break;
			case 8:
				address = "���غ���";
				break;
			default:
				if(number.length()>=10&&number.startsWith("0")){
					//ȥ��ǰ��λ
					Cursor cursor = db.rawQuery("select location from data2 where area =?", new String[]{number.substring(1, 3)});
					if(cursor.moveToNext()){
						String text = cursor.getString(0);
						address = text.substring(0,text.length()-2);
					}
					cursor.close();
					//ȥ��ǰ��λ
					cursor = db.rawQuery("select location from data2 where area =?", new String[]{number.substring(1, 4)});
					if(cursor.moveToNext()){
						String text = cursor.getString(0);
						address = text.substring(0,text.length()-2);
					}
					cursor.close();
				}
				break;
			}
			
			
		}
		db.close();
		return address;
	}

}







