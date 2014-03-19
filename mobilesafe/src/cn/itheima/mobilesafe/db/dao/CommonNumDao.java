package cn.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumDao {

	public static final String path = "/data/data/cn.itheima.mobilesafe/files/commonnum.db";

	/**
	 * ����һ���ж��ٸ�����
	 * 
	 * @return
	 */
	public static int getGroupCount() {
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor curosr = db.rawQuery("select * from classlist", null);
		count = curosr.getCount();
		curosr.close();
		db.close();
		return count;
	}

	/**
	 * �������еķ�����Ϣ
	 * 
	 * @return
	 */
	public static List<String> getGroupInfos() {
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		Cursor curosr = db.rawQuery("select name from classlist", null);
		List<String> names = new ArrayList<String>();
		while (curosr.moveToNext()) {
			String name = curosr.getString(0);
			names.add(name);
		}
		curosr.close();
		db.close();
		return names;
	}

	/**
	 * ����ĳ�����������ж��ٸ�����
	 * 
	 * @return
	 */
	public static int getChildrenCount(int groupPosition) {
		int count = 0;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		String sql = "select * from table" + newPosition;
		Cursor curosr = db.rawQuery(sql, null);
		count = curosr.getCount();
		curosr.close();
		db.close();
		return count;
	}

	/**
	 * ����ĳ�����������ж��ٸ�����
	 * 
	 * @return
	 */
	public static List<String> getChildrenInfosByPosition(int groupPosition) {
		List<String> childrenInfos =new ArrayList<String>();
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		String sql = "select name,number from table" + newPosition;
		Cursor curosr = db.rawQuery(sql, null);
		while(curosr.moveToNext()){
			String name = curosr.getString(0);
			String number = curosr.getString(1);
			childrenInfos.add(name+"\n"+number);
		}
		curosr.close();
		db.close();
		return childrenInfos;
	}
	/**
	 * ��ȡĳ�����������
	 * 
	 * @param groupPosition
	 *            �����λ��
	 * @return
	 */
	public static String getGroupName(int groupPosition) {
		String name = "";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		Cursor curosr = db.rawQuery("select name from classlist where idx =?",
				new String[] { newPosition + "" });
		if (curosr.moveToNext()) {
			name = curosr.getString(0);
		}
		curosr.close();
		db.close();
		return name;
	}

	/**
	 * ��ȡĳ����������ĳ�����ӵ���Ϣ
	 * 
	 * @param groupPosition
	 *            �����λ��
	 * @param childPosition
	 *            ���ӵ�λ��
	 * @return
	 */
	public static String getChildInfoByPosition(int groupPosition,
			int childPosition) {
		String result = "";
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
		int newPosition = groupPosition + 1;
		int newChildPostion = childPosition + 1;
		String sql = "select name,number from table" + newPosition
				+ " where _id =?";
		Cursor curosr = db.rawQuery(sql, new String[] { newChildPostion + "" });
		if (curosr.moveToNext()) {
			String name = curosr.getString(0);
			String number = curosr.getString(1);
			result = name + "\n" + number;
		}
		curosr.close();
		db.close();
		return result;
	}
}
