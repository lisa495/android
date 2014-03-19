package cn.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class CopyFileUtils {

	/**
	 * �����ļ���ϵͳ��ĳ��Ŀ¼
	 * 
	 * @param is
	 *            Դ�ļ�����
	 * @param destPath
	 *            Ŀ��·��
	 * @return
	 */
	public static File copyFile(InputStream is, String destPath) {
		try {
			File file = new File(destPath);
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = is.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.flush();
			fos.close();
			is.close();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}
}
