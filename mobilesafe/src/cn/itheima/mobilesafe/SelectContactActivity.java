package cn.itheima.mobilesafe;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.itheima.mobilesafe.domain.ContactInfo;
import cn.itheima.mobilesafe.engine.ContactInfoProvider;
import cn.itheima.mobilesafe.utils.MyAsyncTask;

public class SelectContactActivity extends Activity {
	private ListView lv_select_contact;
	private List<ContactInfo>  infos;
	private View loading;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_contact);
		loading = findViewById(R.id.loading);
		lv_select_contact = (ListView) findViewById(R.id.lv_select_contact);
	
		//第一个参数 是异步任务执行时候 需要的参数..
		//第二个参数  执行异步任务的进度.
		//第三个参数 是异步任务执行完毕的返回值
		new AsyncTask<Context, Integer, List<ContactInfo>>() {

			@Override
			protected List<ContactInfo> doInBackground(Context... params) {
				try {
//					publishProgress(1);
//					Thread.sleep(1000);
//					publishProgress(2);
//					Thread.sleep(1000);
					infos = ContactInfoProvider.getContactInfos(params[0]);
//					publishProgress(100);
//					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return infos;
			}
	
			
			@Override
			protected void onProgressUpdate(Integer... values) {
				// TODO Auto-generated method stub
				int number = values[0];
				Toast.makeText(getApplicationContext(), "当前进度"+number, 0).show();
				super.onProgressUpdate(values);
			}


			@Override
			protected void onPreExecute() {
				loading.setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(List<ContactInfo> result) {
				System.out.println(result.size());
				loading.setVisibility(View.INVISIBLE);
				lv_select_contact.setAdapter(new ContactAdapter());
				super.onPostExecute(result);
			}
			
		}.execute(this);
		
		
		
		
		//lv_select_contact.setAdapter(new ContactAdapter());
		
		lv_select_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactInfo info = infos.get(position);
				String phone = info.getPhone();
				Intent data = new Intent();
				data.putExtra("phone", phone);
				setResult(0, data);
				finish();
			}
		});
		
	}
	
	private class ContactAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return infos.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.list_contact_item, null);
			TextView tv_name = (TextView) view.findViewById(R.id.tv_contact_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_contact_phone);
			ContactInfo info = infos.get(position);
			tv_name.setText(info.getName());
			tv_phone.setText(info.getPhone());
			return view;
		}
		
	}
	
}
