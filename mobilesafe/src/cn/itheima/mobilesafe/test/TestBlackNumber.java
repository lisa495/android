package cn.itheima.mobilesafe.test;

import java.util.Random;

import cn.itheima.mobilesafe.db.BlackNumberDBOpenHelper;
import cn.itheima.mobilesafe.db.dao.BlackNumberDao;
import android.test.AndroidTestCase;

public class TestBlackNumber extends AndroidTestCase {
	public void testCreateDB() throws Exception {
		BlackNumberDBOpenHelper helper = new BlackNumberDBOpenHelper(
				getContext());
		helper.getWritableDatabase();
	}

	public void testAdd() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		Random random = new Random();
		
		for (int i = 0; i < 200; i++) {
			long number = 13500000000l+i;
			dao.add(""+number, String.valueOf((random.nextInt(3)+1)));
		}
	}

	public void testFind() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.find("110");
		assertEquals(true, result);
	}

	public void testUpdate() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		dao.update("110", "2");
	}

	public void testDelete() throws Exception {
		BlackNumberDao dao = new BlackNumberDao(getContext());
		boolean result = dao.delete("110");
		assertEquals(true, result);
	}
}
