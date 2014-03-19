package cn.itheima.mobilesafe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.itheima.mobilesafe.utils.ActivityUtils;

public class LostFindActivity extends Activity {
	private SharedPreferences sp;
	private TextView tv_lostfind_number;
	private ImageView iv_lostfind_status;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		setContentView(R.layout.activity_lost_find);
		tv_lostfind_number = (TextView) findViewById(R.id.tv_lostfind_number);
		tv_lostfind_number.setText(sp.getString("safenumber", ""));
		iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
		boolean protecting = sp.getBoolean("protecting", false);
		if(protecting){
			iv_lostfind_status.setImageResource(R.drawable.lock);
		}else{
			iv_lostfind_status.setImageResource(R.drawable.unlock);
		}
		
		//�ж��û��Ƿ���й�������.
		if(!isSetup()){
			//����������.
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			finish();
		}
		
	}
	
	public void reEntrySetup(View view){
		ActivityUtils.startActivityAndFinish(this, Setup1Activity.class);
	}
	
	/**
	 * �ж��û��Ƿ���й�������.
	 * @return
	 */
	private boolean isSetup(){
		return sp.getBoolean("setup", false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.lost_find_menu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.item_change_name:
	        //������ �������ƵĶԻ���.
	    	AlertDialog.Builder buidler = new Builder(this);
	    	buidler.setTitle("�������� ");
	    	buidler.setIcon(R.drawable.notification);
	    	final EditText et = new EditText(this);
	    	et.setHint("�������µ�����");
	    	buidler.setView(et);
	    	buidler.setPositiveButton("ȷ��", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newname = et.getText().toString().trim();
					Editor editor = sp.edit();
					editor.putString("newname", newname);
					editor.commit();
					//���̰�sp���������д��sd�� ����һ������.
				}
			});
	    	
	    	buidler.show();
	        return true;

	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
}
