package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.xiaowu.news.model.HotNewsCategory;
import com.xiaowu.news.util.DensityUtil;
import com.xiaowu.news.util.StringUtil;

public class HotNewsActivity extends Activity {
	private final int COLUMNWIDTH_PX = 75; // GridViewÿ����Ԫ��֮��Ŀ��(����)
	private int mColumnWidth_dip;
	private GridView hotNewsCategory = null;
	private ListView hotNewsList;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hot_news_layout);

		// ����Ԫ��֮�����ؾ���ת��Ϊdip
		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);

		// ����һ�����񲼾�ʵ���Ȱ����ŵ���Ŀ����
		hotNewsCategory = new GridView(this);
		//���GridView�����ֵ���
		addGridView();
		
		hotNewsCategory.setOnItemClickListener(new OnItemClickListener() {
			TextView hotNewsTitle;
		
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				for(int i = 0; i < parent.getCount(); i++) {
					hotNewsTitle = (TextView) parent.getChildAt(i);
					hotNewsTitle.setTextColor(0XFFADB2AD);
					hotNewsTitle.setBackgroundDrawable(null);
				}
				
				hotNewsTitle = (TextView) view;
				hotNewsTitle.setTextColor(0xFFFFFFFF);
				hotNewsTitle
				.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
				Toast.makeText(HotNewsActivity.this, hotNewsTitle.getText(),
						Toast.LENGTH_SHORT).show();
				
				
			}
		});
		List<Map<String,Object>> data = new ArrayList<Map<String,Object>>();
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("hotNewsTitle", "Ц��");
		hashMap.put("hotNewsImage", R.drawable.image_prompt_smiley);
		data.add(hashMap);
		SimpleAdapter adapter = new SimpleAdapter(this, data
				, R.layout.hot_news_list_item_layout
				, new String[]{"hotNewsTitle", "hotNewsImage"}, new int[]{R.id.hotNews_title, R.id.hotnews_image});
		hotNewsList = (ListView) findViewById(R.id.hot_news_list);
		hotNewsList.setAdapter(adapter);
		
		
		
	}
	
	/**
	 *	ʵ�����Ȱ�����������Ŀ
	 */
	private void addGridView() {
		// ��ȡ������Դ
		String[] hotNewsCagoryArray = getResources().getStringArray(
				R.array.howNewsCategories);

		// ����һ��List���飬�������HashMap����
		List<HashMap<String, HotNewsCategory>> data = new ArrayList<HashMap<String, HotNewsCategory>>();
		// �ָ������ַ���
		for (int i = 0; i < hotNewsCagoryArray.length; i++) {
			String temp[] = hotNewsCagoryArray[i].split("[|]");
			if (temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				HotNewsCategory type = new HotNewsCategory(cid, title);
				// ����һ��HashMap����������ż�ֵ��
				HashMap<String, HotNewsCategory> hashMap = new HashMap<String, HotNewsCategory>();
				hashMap.put("hotNews_title", type);
				data.add(hashMap);
			}
		}

		ConstomSimpleAdapter simpleAdapter = new ConstomSimpleAdapter(this,
				data, R.layout.hot_news_category_item_layout,
				new String[] { "hotNews_title" },
				new int[] { R.id.hotNews_title });


		// ���õ�Ԫ��ı���ɫΪ͸��������ѡ�����ʱ�Ͳ�����ʾ��ɫ������
		hotNewsCategory.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// ����ÿһ����Ԫ����
		hotNewsCategory.setColumnWidth(mColumnWidth_dip);
		// ����������ͼ������
		hotNewsCategory.setNumColumns(GridView.AUTO_FIT);
		// ���ö��뷽ʽ
		hotNewsCategory.setGravity(Gravity.CENTER);
		// ���ݵ�Ԫ��Ŀ�Ⱥ���Ŀ����������ͼ�Ŀ��
		int width = mColumnWidth_dip * data.size();
		// ��ȡ���ֲ���
		LayoutParams params = new LayoutParams(width, LayoutParams.WRAP_CONTENT);
		// ���ò���
		hotNewsCategory.setLayoutParams(params);
		// ����Adapter
		hotNewsCategory.setAdapter(simpleAdapter);
		// ͨ��ID��ȡLinearLayout���ֶ���
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		// ��������ͼ�����ӵ�LinearLayout���ֵ���
		categoryLayout.addView(hotNewsCategory);
	}
}
