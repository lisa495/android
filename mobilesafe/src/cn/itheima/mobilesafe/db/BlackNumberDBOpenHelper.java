package cn.itheima.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class BlackNumberDBOpenHelper extends SQLiteOpenHelper {

	public BlackNumberDBOpenHelper(Context context) {
		super(context, "blacknumber.db", null,1);
	}

	/**
	 * ����ģʽ  mode 1 ȫ������
	 *              2. �绰����
	 *              3.��������
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		//���ݿ��һ�α�������ʱ����õķ���.
		db.execSQL("create table blacknumber (_id integer primary key autoincrement,number varchar(20),mode varchar(2))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
