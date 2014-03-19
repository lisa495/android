package cn.itheima.mobilesafe.ui;

import cn.itheima.mobilesafe.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingView extends RelativeLayout {
	private View view;
	private TextView tv_settingview_title;
	private TextView tv_settingview_content;
	private CheckBox cb_status;
	private String checked_text;
	private String unchecked_text;
	
	public SettingView(Context context) {
		super(context);
		initView(context);
	}
	public SettingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public SettingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
		
		//把属性集 和我们自己定义的属性集合建立映射关系
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.setting_view_style);
		String title = a.getString(R.styleable.setting_view_style_title);
		checked_text = a.getString(R.styleable.setting_view_style_checked_text);
		unchecked_text = a.getString(R.styleable.setting_view_style_unchecked_text);
		tv_settingview_content.setText(unchecked_text);
		tv_settingview_title.setText(title);
		a.recycle();//释放资源.
	}

	private void initView(Context context) {
		view = View.inflate(context, R.layout.ui_setting_view, this);
		tv_settingview_title = (TextView) view.findViewById(R.id.tv_settingview_title);
		tv_settingview_content = (TextView) view.findViewById(R.id.tv_settingview_content);
		cb_status = (CheckBox) view.findViewById(R.id.checkBox1);
	}
	/**
	 * 返回当前自定义控件的选中状态.
	 * @return
	 */
	public boolean isChecked(){
		return cb_status.isChecked();
	}
	/**
	 * 设置自定义控件的勾选状态.
	 * @param checked
	 */
	public void setChecked(boolean checked){
		cb_status.setChecked(checked);
		if(checked){
			setContent(checked_text);
		}else{
			setContent(unchecked_text);
		}
	}
	/**
	 * 设置里面的文本
	 * @param text
	 */
	public void setContent(String text){
		tv_settingview_content.setText(text);
	}
}
