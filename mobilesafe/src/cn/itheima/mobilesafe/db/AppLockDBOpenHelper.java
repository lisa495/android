package cn.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AppLockDBOpenHelper extends SQLiteOpenHelper {

	public AppLockDBOpenHelper(Context context) {
		super(context, "applock.db", null,1);
	}

	/**
	 * 包名  packname 
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//数据库第一次被创建的时候调用的方法.
		db.execSQL("create table applock (_id integer primary key autoincrement,packname varchar(20))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
