package com.jerehedu.db;

import java.util.ArrayList;
import java.util.List;

import com.jerehedu.entity.Channel;

public class ChannelDb {
	
	private static List<Channel>   selectedChannel=new ArrayList<Channel>();
	static{
		selectedChannel.add(new Channel("","ͷ��",0,"",""));
		selectedChannel.add(new Channel("","����",0,"",""));
		selectedChannel.add(new Channel("","����",0,"",""));
		selectedChannel.add(new Channel("","�ƾ�",0,"",""));
		selectedChannel.add(new Channel("","�ȵ�",0,"",""));
		selectedChannel.add(new Channel("","�Ƽ�",0,"",""));
		selectedChannel.add(new Channel("","ͼƬ",0,"",""));
		selectedChannel.add(new Channel("","����",0,"",""));
		selectedChannel.add(new Channel("","ʱ��",0,"",""));
	}
	public static  List<Channel> getSelectedChannel(){
		 return selectedChannel;
	}
}
