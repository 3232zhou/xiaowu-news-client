package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.xiaowu.news.model.Category;
import com.xiaowu.news.util.StringUtil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class AddSubscriptionActivity extends Activity{
	
	private ListView subList = null;			//��ע�б�
	private SimpleAdapter subListAdapter; 		//������ʾ��ע�б��Adaper
	private ArrayList<HashMap<String, Category>> subListData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_subscription_layout);
		
		
		//�ӽ��浱�л�ȡListView
		subList = (ListView) findViewById(R.id.subList);
		subListData = new ArrayList<HashMap<String,Category>>();
		// ��ȡ������Դ
		String[] categoryArray = getResources().getStringArray(
				R.array.categories);
		for(int i = 0; i < categoryArray.length; i++) {
			String temp[] = categoryArray[i].split("[|]");
			if(temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				Category type = new Category(cid, title);
				// ����һ��HashMap����������ż�ֵ��
				HashMap<String, Category> hashMap = new HashMap<String, Category>();	
				hashMap.put("category_title", type);
				subListData.add(hashMap);
			}
		}
		
		subListAdapter = new SimpleAdapter(this, subListData, R.layout.sublist_item_layout
				, new String[]{"category_title"}, new int[]{R.id.category_title});
		
		
		//��ʾ�б�
		subList.setAdapter(subListAdapter);
		
	}
}
