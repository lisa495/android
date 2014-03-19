package cn.itheima.mobilesafe.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import cn.itheima.mobilesafe.domain.UpdateInfo;

public class UpdateInfoParser {
	
	/**
	 * 解析服务器返回的更新信息
	 * @param is 服务器返回的流
	 * @return 如果发生异常 返回null;
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(is, "utf-8");
			int type = parser.getEventType();
			UpdateInfo info = null;
			while(type!=XmlPullParser.END_DOCUMENT){
				switch (type) {
				case XmlPullParser.START_TAG:
					if("info".equals(parser.getName())){
						info = new UpdateInfo();
					}else if("version".equals(parser.getName())){
						info.setVersion(parser.nextText());
					}else if("description".equals(parser.getName())){
						info.setDescription(parser.nextText());
					}else if("apkurl".equals(parser.getName())){
						info.setApkurl(parser.nextText());
					}
					break;
				}
				type = parser.next();
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
}
