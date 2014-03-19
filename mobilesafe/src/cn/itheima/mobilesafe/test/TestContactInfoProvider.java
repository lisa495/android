package cn.itheima.mobilesafe.test;

import java.util.List;

import android.test.AndroidTestCase;
import cn.itheima.mobilesafe.domain.ContactInfo;
import cn.itheima.mobilesafe.engine.ContactInfoProvider;

public class TestContactInfoProvider extends AndroidTestCase {
	public void testGetContacts() throws Exception{
		List<ContactInfo>  infos = ContactInfoProvider.getContactInfos(getContext());
		for(ContactInfo info : infos){
			System.out.println(info.toString());
		}
	}
}
