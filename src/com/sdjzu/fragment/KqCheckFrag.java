package com.sdjzu.fragment;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import com.example.androidclient.R;
import com.sdjzu.adapter.KqAdater;
import com.sdjzu.parenttool.ManageTool;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import edu.sdjzu.attr.Attr;
import edu.sdjzu.model.KQStuPerson;

public class KqCheckFrag extends Fragment {
	private TextView tvQingjia, tvChidao, tvQueqing,tvPercentage;
	private ListView kqListView;
	private Activity act;
	private List<KQStuPerson> listKq = new ArrayList<KQStuPerson>();
	private Handler mHandler;
	private KqAdater kqAdapter;
	private static final int KQ_GET_SUCCESS = 0;
	private ManageTool manageTool;
	private String userName = "";
	private String kqCountJson="";
	private String textQueqing,textChidao,textQingjia,textPercentage;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		act = getActivity();
		userName = getArguments().getString(Attr.userKey);
		init();
	}

	private void init() {
		manageTool = new ManageTool(act);
		tvQingjia = (TextView) getView().findViewById(R.id.kq_qingjia);
		tvChidao = (TextView) getView().findViewById(R.id.kq_chidao);
		tvQueqing = (TextView) getView().findViewById(R.id.kq_queqing);
		tvPercentage=(TextView)getView().findViewById(R.id.kq_pertage);
		kqListView = (ListView) getView().findViewById(R.id.kq_lsv);
		kqAdapter = new KqAdater(act, listKq);
		kqListView.setAdapter(kqAdapter);
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case KQ_GET_SUCCESS:
					kqAdapter.setKQ(listKq);
					kqAdapter.notifyDataSetChanged();
					parseJson(kqCountJson);
					tvQingjia.setText(textQingjia+" 次");
					tvChidao.setText(textChidao+" 次");
					tvQueqing.setText(textQueqing+" 次");
					tvPercentage.setText("出勤率："+Integer.valueOf(textPercentage)*100+" %");
					break;
				}
			}
		};
		new Thread() {
			public void run() {
				if (null != userName && !userName.equals("")) {
					listKq = manageTool.getKQStuPerson(userName);
					kqCountJson=manageTool.getKqCount(userName);
					mHandler.sendEmptyMessage(KQ_GET_SUCCESS);
				}
			};
		}.start();

	}
	public void parseJson(String jsonData) {
		JsonReader reader = new JsonReader(new StringReader(jsonData));
		try {
			reader.beginArray();
			reader.beginObject();// 开始解析对象
			while (reader.hasNext()) {
				String tagName = reader.nextName();
				if (tagName.equals("qingJia")) {
					textQingjia=reader.nextString();
				} else if(tagName.equals("chiDao")){
					textChidao=reader.nextString();
				}else if(tagName.equals("queQing")){
					textQueqing=reader.nextString();
				}else if(tagName.equals("perCentage")){
					textPercentage=reader.nextString();
				}
			}
			reader.endObject();// 结束对象解析
			reader.endArray();
		} catch (IOException e) {
			e.printStackTrace();
			
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_kq_check, null);
	}

}
