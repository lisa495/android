package cn.itheima.mobilesafe.test;

import cn.itheima.mobilesafe.db.dao.AddressDao;
import android.test.AndroidTestCase;

public class TestGetAddress extends AndroidTestCase {
	public void testAddress() throws Exception{
		String result = AddressDao.getAddress("110");
		System.out.println(result);
	}
}
