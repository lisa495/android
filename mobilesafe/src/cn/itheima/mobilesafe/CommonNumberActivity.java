package cn.itheima.mobilesafe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import cn.itheima.mobilesafe.db.dao.CommonNumDao;

public class CommonNumberActivity extends Activity {
	private ExpandableListView elv;
	private List<String> groupNames;
	private Map<Integer, List<String>> childrenCacheInfos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		childrenCacheInfos = new HashMap<Integer, List<String>>();
		setContentView(R.layout.activity_common_num);
		elv = (ExpandableListView) findViewById(R.id.elv);
		elv.setAdapter(new MyAdapter());
		
		
		elv.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_DIAL);
				String number = childrenCacheInfos.get(groupPosition).get(childPosition).split("\n")[1];
				intent.setData(Uri.parse("tel:"+number));
				startActivity(intent);
				return false;
			}
		});
	}

	private class MyAdapter extends BaseExpandableListAdapter {

		// 返回多少个分组
		@Override
		public int getGroupCount() {
			// return CommonNumDao.getGroupCount();
			groupNames = CommonNumDao.getGroupInfos();
			return groupNames.size();
		}

		@Override
		public int getChildrenCount(int groupPosition) {// 0 开始
			List<String> childreninfos;
			if (childrenCacheInfos.containsKey(groupPosition)) {
				childreninfos = childrenCacheInfos.get(groupPosition);
			} else {
				childreninfos = CommonNumDao
						.getChildrenInfosByPosition(groupPosition);
				childrenCacheInfos.put(groupPosition, childreninfos);
			}
			return childreninfos.size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return null;
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			TextView tv;
			if(convertView!=null&&convertView instanceof TextView){
				tv = (TextView) convertView;
			}else{
				tv = new TextView(getApplicationContext());
			}
			
			tv.setTextSize(25);
			tv.setTextColor(Color.RED);
			// tv.setText("      "+CommonNumDao.getGroupName(groupPosition));
			tv.setText("      " + groupNames.get(groupPosition));
			return tv;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			TextView tv;
			if(convertView!=null&&convertView instanceof TextView){
				tv = (TextView) convertView;
			}else{
				tv = new TextView(getApplicationContext());
			}
			tv.setTextSize(18);
			tv.setTextColor(Color.BLUE);
//			tv.setText(CommonNumDao.getChildInfoByPosition(groupPosition,
//					childPosition));
			tv.setText(childrenCacheInfos.get(groupPosition).get(childPosition));
			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}
}
