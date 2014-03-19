package cn.itheima.mobilesafe.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.ProgressDialog;

public class DownLoadUtil {

	/**
	 * �����ļ�����
	 * 
	 * @param serverPath
	 *            �������ļ���·��
	 * @param savedPath
	 *            ���ر����·��
	 * @param pd �������Ի���
	 * @return ���سɹ� �����ļ����� ����ʧ�� ����null
	 */
	public static File download(String serverPath, String savedPath, ProgressDialog pd) {
		try {
			URL url = new URL(serverPath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			int code = conn.getResponseCode();
			if (code == 200) {
				pd.setMax(conn.getContentLength());
				InputStream is = conn.getInputStream();
				File file = new File(savedPath);
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = new byte[1024];
				int len = 0;
				int total = 0;
				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
					total +=len;
					pd.setProgress(total);
					Thread.sleep(20);
				}
				fos.flush();
				fos.close();
				is.close();
				return file;
			} else {
				return null;
			}

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ��ȡ�������ļ�������
	 * @param serverPath
	 * @return
	 */
	public static String getFileName(String serverPath){
		return serverPath.substring(serverPath.lastIndexOf("/")+1);
	}
}
