package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaowu.news.custom.ConstomSimpleAdapter;
import com.xiaowu.news.model.FinancialCagory;
import com.xiaowu.news.util.DensityUtil;
import com.xiaowu.news.util.StringUtil;

public class FinancialActivity extends Activity {
	private final int COLUMNWIDTH_PX = 50; 				// GridViewÿ����Ԫ��Ŀ��(����)
	private int mColumnWidth_dip;
	private ListView financialList;
	private GridView category = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.financial_index_layout);
		
		// ����Ԫ��֮�����ؾ���ת��Ϊdip
		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);
		
		// ����һ�����񲼾�ʵ���Ȱ����ŵ���Ŀ����
		category = new GridView(this);
		//���GridView
		addGridView();
		
		category.setOnItemClickListener(new OnItemClickListener() {
			TextView financialTitle;
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				for(int i = 0; i < parent.getCount(); i++) {
					financialTitle = (TextView)parent.getChildAt(i);
					financialTitle.setTextColor(0XFFADB2AD);
					financialTitle.setBackgroundDrawable(null);
				}
				financialTitle = (TextView) view;
				financialTitle.setTextColor(0xFFFFFFFF);
				financialTitle
				.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
				Toast.makeText(FinancialActivity.this, financialTitle.getText(),
						Toast.LENGTH_SHORT).show();				
				
			}
		});
		
		
		//���ListView
		setListView();
		
		
		
	}
	
	
	private void addGridView() {
		// ��ȡ������Դ
		String[] categoryArray = getResources().getStringArray(
				R.array.financialCategories);

		// ����һ��List���飬�������HashMap����
		List<HashMap<String, FinancialCagory>> data = new ArrayList<HashMap<String, FinancialCagory>>();
		// �ָ������ַ���
		for (int i = 0; i < categoryArray.length; i++) {
			String temp[] = categoryArray[i].split("[|]");
			if (temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				FinancialCagory type = new FinancialCagory(cid, title);
				// ����һ��HashMap����������ż�ֵ��
				HashMap<String, FinancialCagory> hashMap = new HashMap<String, FinancialCagory>();
				hashMap.put("financial_title", type);
				data.add(hashMap);
			}
		}

		ConstomSimpleAdapter simpleAdapter = new ConstomSimpleAdapter(this,
				data, R.layout.financial_category_item_layout,
				new String[] { "financial_title" },
				new int[] { R.id.financial_title });


		// ���õ�Ԫ��ı���ɫΪ͸��������ѡ�����ʱ�Ͳ�����ʾ��ɫ������
		category.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// ����ÿһ����Ԫ����
		category.setColumnWidth(mColumnWidth_dip);
		// ����������ͼ������
		category.setNumColumns(GridView.AUTO_FIT);
		// ���ö��뷽ʽ
		category.setGravity(Gravity.CENTER);
		// ���ݵ�Ԫ��Ŀ�Ⱥ���Ŀ����������ͼ�Ŀ��
		int width = mColumnWidth_dip * data.size();
		// ��ȡ���ֲ���
		LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		// ���ò���
		category.setLayoutParams(params);
		// ����Adapter
		category.setAdapter(simpleAdapter);
		// ͨ��ID��ȡLinearLayout���ֶ���
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		// ��������ͼ�����ӵ�LinearLayout���ֵ���
		categoryLayout.addView(category);
	}
	
	
	private void setListView(){
		final ArrayList<HashMap<String, String>> financialData = new ArrayList<HashMap<String,String>>();
		String[] financialArray = getResources().getStringArray(R.array.a_financial_index_array);
		for(int i = 0; i < financialArray.length; i++) {
			HashMap<String, String> hashMap1 = new HashMap<String, String>();
			hashMap1.put("financial_index", financialArray[i]);
			hashMap1.put("financial_digit", "000001");
			hashMap1.put("new_price", "2061.79");
			hashMap1.put("amounts_up_down", "+32.55");
			hashMap1.put("amounts_limits", "+1.60%");
			financialData.add(hashMap1);
		}
		SimpleAdapter financialListAdapter = new SimpleAdapter(this, financialData, R.layout.financial_list_item_layout
				, new String[]{"financial_index","financial_digit","new_price","amounts_up_down", "amounts_limits"}
				, new int[]{R.id.financial_index, R.id.financial_digit, R.id.new_price, R.id.amounts_up_down, R.id.amounts_limits});
		
		financialList = (ListView) findViewById(R.id.financial_list);
		financialList.setAdapter(financialListAdapter);		
	}
	
}
