package cn.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import cn.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.itheima.mobilesafe.domain.BlackNumberInfo;

public class BlackNumberDao {
	private BlackNumberDBOpenHelper helper;

	public BlackNumberDao(Context context) {
		helper = new BlackNumberDBOpenHelper(context);
	}

	/**
	 * ���� һ������������
	 * 
	 * @param number
	 * @return
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber where number=?",
				new String[] { number });
		if (cursor.moveToNext()) {
			result = true;
		}
		cursor.close();
		db.close();
		return result;
	}

	/**
	 * ����һ�����������ģʽ.
	 * @param number 
	 * @return �������-1 ����Ĳ��Ǻ���������.
	 */
	public String findMode(String number) {
		String result = "-1";
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select mode from blacknumber where number=?",
				new String[] { number });
		if (cursor.moveToNext()) {
			result = cursor.getString(0);
		}
		cursor.close();
		db.close();
		return result;
	}
	
	/**
	 * ��ѯȫ��������
	 * @return
	 */
	public List<BlackNumberInfo> findAll() {
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc",
				null);
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
			info = null;
		}
		cursor.close();
		db.close();
		return infos;
	}
	
	/**
	 * ��ѯһ���ж������ļ�¼
	 */
	public int getMaxNumber() {
		int maxnumber = 0;
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor cursor = db.rawQuery("select * from blacknumber",
				null);
		maxnumber = cursor.getCount();
		cursor.close();
		db.close();
		return maxnumber;
	}
	/**
	 * ��ҳ��ѯ����
	 * @param maxnumber ����ȡ��������
	 * @param offset �ӵڼ�����ʼ��ȡ
	 * @return
	 */
	public List<BlackNumberInfo> findByPage(int maxnumber , int offset){
		//select * from blacknumber order by _id desc limit 5 offset  0
		SQLiteDatabase db = helper.getReadableDatabase();
		List<BlackNumberInfo> infos = new ArrayList<BlackNumberInfo>();
		Cursor cursor = db.rawQuery("select number,mode from blacknumber order by _id desc limit ? offset  ?",
				new String[]{maxnumber+"",offset+""});
		while (cursor.moveToNext()) {
			BlackNumberInfo info = new BlackNumberInfo();
			info.setNumber(cursor.getString(0));
			info.setMode(cursor.getString(1));
			infos.add(info);
			info = null;
		}
		cursor.close();
		db.close();
		return infos;
	}
	

	/**
	 * ��Ӻ���������
	 * 
	 * @param number
	 *            ����������
	 * @param mode
	 *            ����ģʽ
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into blacknumber (number,mode) values (?,?)",
				new Object[] { number, mode });
		db.close();
	}

	/**
	 * �޸ĺ���������
	 * 
	 * @param number
	 *            ����������
	 * @param newmode
	 *            �µ�����ģʽ
	 */
	public void update(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update blacknumber set mode =? where number=?",
				new Object[] { newmode, number });
		db.close();
	}

	/**
	 * ɾ������������
	 * 
	 * @param number
	 */
	public boolean delete(String number) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int result = db.delete("blacknumber", "number=?",
				new String[] { number });
		db.close();
		if (result == 0) {
			return false;
		} else {
			return true;
		}
	}
}
