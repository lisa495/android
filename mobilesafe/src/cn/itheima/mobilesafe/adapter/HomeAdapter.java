package cn.itheima.mobilesafe.adapter;

import cn.itheima.mobilesafe.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeAdapter extends BaseAdapter {

	private static final String[] names = { "手机防盗", "通讯卫士", "软件管理", "进程管理",
			"流量统计", "病毒查杀", "系统优化", "高级工具", "设置中心" };
	
	private static final int[] icons ={R.drawable.safe,R.drawable.callmsgsafe,R.drawable.app,
		R.drawable.taskmanager,R.drawable.netmanager,R.drawable.trojan,R.drawable.sysoptimize,
		R.drawable.atools,R.drawable.settings
	};
	private Context context;
	

	public HomeAdapter(Context context) {
		this.context = context;
	}

	// 返回有多少个条目
	@Override
	public int getCount() {
		return names.length;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	// 返回每一个位置对应的view对象.
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	    View view =	View.inflate(context, R.layout.grid_home_item, null);
		TextView tv_name = (TextView) view.findViewById(R.id.tv_home_name);
		ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_home_icon);
		tv_name.setText(names[position]);
		iv_icon.setImageResource(icons[position]);
		if(position==0){
			SharedPreferences sp = context.getSharedPreferences("config",Context.MODE_PRIVATE);
			String newname = sp.getString("newname", "");
			if(!TextUtils.isEmpty(newname)){
				tv_name.setText(newname);
			}
		}
		return view;
	}

}