package com.sdjzu.parent;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidclient.R;
import com.sdjzu.adapter.TabFragmentPagerAdapter;
import com.sdjzu.fragment.KqChatFrag;
import com.sdjzu.fragment.KqCheckFrag;
import com.sdjzu.fragment.KqInformFrag;

/**
 * 主页界面
 * 
 * @author Administrator
 *
 */
public class ParentIndexAct extends FragmentActivity {
	public int jno = -1;
	public int rno = -1;
	private String userName = "";
	protected static float currentIndicatorLeft = 0;
	private TabFragmentPagerAdapter framPageAdapter;
	private ViewPager mViewPage;
	private LinearLayout radioGroupTab;
	private ImageView tabIndictor;
	private int tabIndictorWidth = 0;
	private int tabIndictorCurrentLeft = 0;
	private String[] tabTitle = { "考勤", "通知", "消息" };
	private List<Fragment> listFrag = new ArrayList<Fragment>();
	private List<TextView> listTab = new ArrayList<TextView>();
	private Intent intent;
	private Handler mHandler;
	private final int BACK_SUCCESS = 0;
	private int backTimes = 0;
	private final int backOffTime = 4000;// 按两次返回键之间的时间间隔

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.act_manager_index);
		userName = LoginAct.userName;
		findView();
		initView();
		setListener();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case BACK_SUCCESS:
					backTimes = 0;
					break;
				}
			}
		};
	}

	private void findView() {
		mViewPage = (ViewPager) findViewById(R.id.mViewPager);
		radioGroupTab = (LinearLayout) findViewById(R.id.group_tab);
		tabIndictor = (ImageView) findViewById(R.id.tab_indictor);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Log.i("chen", "keyback");
			++backTimes;
			if (backTimes == 1) {
				Toast.makeText(ParentIndexAct.this, getString(R.string.back_tip1), 1000).show();
				mHandler.sendEmptyMessageDelayed(BACK_SUCCESS, backOffTime);
			} else {
				finish();
			}
		}
		return true;
	}

	@SuppressLint("NewApi")
	private void initView() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		tabIndictorWidth = dm.widthPixels / tabTitle.length;
		LayoutParams cursor_Params = tabIndictor.getLayoutParams();
		cursor_Params.width = tabIndictorWidth;
		tabIndictor.setLayoutParams(cursor_Params);
		LayoutInflater mInflater = LayoutInflater.from(this);
		for (int i = 0; i < tabTitle.length; i++) {
			TextView rb = (TextView) mInflater.inflate(R.layout.radiogroup_item, null);
			rb.setTag(i);
			rb.setId(i);
			rb.setText(tabTitle[i]);
			rb.setClickable(true);
			rb.setLayoutParams(new LayoutParams(tabIndictorWidth, LayoutParams.MATCH_PARENT));
			rb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					int index = (Integer) v.getTag();
					TranslateAnimation animation = new TranslateAnimation(currentIndicatorLeft, v.getLeft(), 0f, 0f);
					animation.setInterpolator(new LinearInterpolator());
					animation.setDuration(100);
					animation.setFillAfter(true);
					// 执行位移动画
					tabIndictor.startAnimation(animation);
					mViewPage.setCurrentItem(index); // ViewPager 跟随一起 切换
					// 记录当前 下标的距最左侧的 距离
					currentIndicatorLeft = v.getLeft();

				}
			});
			listTab.add(rb);
			radioGroupTab.addView(rb);
		}
		listFrag.add(new KqCheckFrag());
		listFrag.add(new KqInformFrag());
		listFrag.add(new KqChatFrag());
		framPageAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), userName, listFrag);
		mViewPage.setAdapter(framPageAdapter);
		mViewPage.setCurrentItem(0);
	}

	private void setListener() {
		mViewPage.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				listTab.get(arg0).performClick();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

		// radioGroupTab.setOnCheckedChangeListener(new
		// OnCheckedChangeListener() {
		//
		// @Override
		// public void onCheckedChanged(RadioGroup group, int checkedId) {
		// if (radioGroupTab.getChildAt(checkedId) != null) {
		//
		// TranslateAnimation animation = new
		// TranslateAnimation(currentIndicatorLeft,
		// ((RadioButton) radioGroupTab.getChildAt(checkedId)).getLeft(), 0f,
		// 0f);
		// animation.setInterpolator(new LinearInterpolator());
		// animation.setDuration(100);
		// animation.setFillAfter(true);
		// // 执行位移动画
		// tabIndictor.startAnimation(animation);
		// mViewPage.setCurrentItem(checkedId); // ViewPager 跟随一起 切换
		// // 记录当前 下标的距最左侧的 距离
		// currentIndicatorLeft = ((RadioButton)
		// radioGroupTab.getChildAt(checkedId)).getLeft();
		// }
		// }
		// });
	}

}
