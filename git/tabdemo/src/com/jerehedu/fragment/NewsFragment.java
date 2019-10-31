package com.jerehedu.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Gallery;
import android.widget.TextView;

public class NewsFragment extends Fragment {
	private String weburl;
	private String channelName;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	private View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){//�Ż�View����View�Ĵ�������
			
			//�ò��ֿ�ͨ��xml�ļ����Fragment���棬��ͨ��LayoutInflaterת��ΪView���
			//����ͨ������Ϊfragment���һ��TextView
			TextView tvTitle=new TextView(getActivity());
			tvTitle.setText(channelName);
			tvTitle.setTextSize(16);
			tvTitle.setGravity(Gravity.CENTER);
			tvTitle.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			view=tvTitle;
		}
		ViewGroup parent=(ViewGroup)view.getParent();
		if(parent!=null){//���View�Ѿ���ӵ������У�Ҫ����ɾ��������ᱨ��
			parent.removeView(view);
		}
		return view;
	}
	@Override
	public void setArguments(Bundle bundle) {
		// TODO Auto-generated method stub
		weburl=bundle.getString("weburl");
		channelName=bundle.getString("name");
		
	}
	
}
