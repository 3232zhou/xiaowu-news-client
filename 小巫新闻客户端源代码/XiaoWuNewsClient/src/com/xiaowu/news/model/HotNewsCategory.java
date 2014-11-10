package com.xiaowu.news.model;

public class HotNewsCategory {
	//�Ȱ����ͱ��
	private int hotNewsCid;
	//�Ȱ����
	private String hotNewsTitle;
	
	public HotNewsCategory() {
		super();
	}

	public HotNewsCategory(int hotNewsCid, String hotNewsTitle) {
		super();
		this.hotNewsCid = hotNewsCid;
		this.hotNewsTitle = hotNewsTitle;
	}

	public int getHotNewsCid() {
		return hotNewsCid;
	}

	public void setHotNewsCid(int hotNewsCid) {
		this.hotNewsCid = hotNewsCid;
	}

	public String getHotNewsTitle() {
		return hotNewsTitle;
	}

	public void setHotNewsTitle(String hotNewsTitle) {
		this.hotNewsTitle = hotNewsTitle;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return hotNewsTitle;
	}
}
