package cn.itheima.mobilesafe.db.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CommonNumDao {

	public static final String path = "/data/data/cn.itheima.mobilesafe/files/commonnum.db";

	/**
	 * 返回一共有多少个分组
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
	 * 返回所有的分组信息
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
	 * 返回某个分组里面有多少个孩子
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
	 * 返回某个分组里面有多少个孩子
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
	 * 获取某个分组的名称
	 * 
	 * @param groupPosition
	 *            分组的位置
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
	 * 获取某个分组里面某个孩子的信息
	 * 
	 * @param groupPosition
	 *            分组的位置
	 * @param childPosition
	 *            孩子的位置
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
