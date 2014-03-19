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
	 * 查找 一条黑名单号码
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
	 * 查找一个号码的拦截模式.
	 * @param number 
	 * @return 如果返回-1 代表的不是黑名单号码.
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
	 * 查询全部的数据
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
	 * 查询一共有多少条的记录
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
	 * 分页查询数据
	 * @param maxnumber 最多获取多少数据
	 * @param offset 从第几条开始获取
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
	 * 添加黑名单号码
	 * 
	 * @param number
	 *            黑名单号码
	 * @param mode
	 *            拦截模式
	 */
	public void add(String number, String mode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("insert into blacknumber (number,mode) values (?,?)",
				new Object[] { number, mode });
		db.close();
	}

	/**
	 * 修改黑名单号码
	 * 
	 * @param number
	 *            黑名单号码
	 * @param newmode
	 *            新的拦截模式
	 */
	public void update(String number, String newmode) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("update blacknumber set mode =? where number=?",
				new Object[] { newmode, number });
		db.close();
	}

	/**
	 * 删除黑名单号码
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
