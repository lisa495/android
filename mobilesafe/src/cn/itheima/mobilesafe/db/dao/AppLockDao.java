package cn.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import cn.itheima.mobilesafe.db.AppLockDBOpenHelper;

public class AppLockDao {
	private AppLockDBOpenHelper helper;
	private Context context;
	public static  Uri uri = Uri.parse("content://cn.itheima.xxx");//�Զ������Ϣ����ĵ�ַ.
	

	public AppLockDao(Context context) {
		helper = new AppLockDBOpenHelper(context);
		this.context = context;
	}

	/**
	 * ���� �����Ƿ�����
	 * 
	 * @param packname
	 * @return
	 */
	public boolean find(String packname) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from applock where packname=?",
				new String[] { packname });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}
	/**
	 * ��ѯ���е������İ�����Ϣ.
	 * @return
	 */
	public List<String> findAll(){
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select packname from applock ",
				null);
		List<String> packnames = new ArrayList<String>();
		while (cursor.moveToNext()) {
			packnames.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return packnames;
	}

	
	
	

	/**
	 * �����������
	 * 
	 * @param packname
	 *            ����
	 */
	public void add(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into applock (packname) values (?)",
				new Object[] { packname });
		context.getContentResolver().notifyChange(uri, null);
		db.close();
	}

	

	/**
	 * ɾ�������İ���
	 * 
	 * @param number
	 */
	public boolean delete(String packname) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("applock", "packname=?",
				new String[] { packname });
		db.close();
		if (result == 0) {
			return false;
		} else {
			context.getContentResolver().notifyChange(uri, null);
			return true;
		}
	}
}
