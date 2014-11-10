package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaowu.news.custom.ConstomSimpleAdapter;
import com.xiaowu.news.model.Category;
import com.xiaowu.news.service.SyncHttp;
import com.xiaowu.news.update.UpdateManager;
import com.xiaowu.news.util.DensityUtil;
import com.xiaowu.news.util.StringUtil;

/**
 * 
 * @author wwj
 * 
 */
public class MainActivity extends Activity {

	private final int COLUMNWIDTH_PX = 56; 				// GridViewÿ����Ԫ��Ŀ��(����)
	private final int FLINGVELOCITY_PX = 800; 			// ViewFilper�����ľ���(����)
	private final int NEWSCOUNT = 5; 					// ��ʾ���ŵ�����
	private final int SUCCESS = 0;						// �������ųɹ�
	private final int NONEWS = 1;						// û������
	private final int NOMORENEWS = 2;					// û�и�������
	private final int LOADERROR = 3;					// ����ʧ��

	private long exitTime;								//�����ؼ��˳���ʱ��
	private int mColumnWidth_dip;						
	private int mFlingVelocity_dip;
	private int mCid; 									// ���ű��
	private String mCategoryTitle;						// ���ŷ������
	private ListView mNewslist; 						// �����б�
	private SimpleAdapter mNewslistAdapter; 			// Ϊ���������ṩ��Ҫ��ʾ���б�
	private ArrayList<HashMap<String, Object>> mNewsData; // �洢������Ϣ�����ݼ���
	private LayoutInflater mInflater; 					// ������̬����û��loadmore_layout����
	
	private Button category_Button = null;				// ���ŷ�������������Ҳ鿴�İ�ť
	
	private HorizontalScrollView categoryScrollView = null;// ˮƽ����ͼ

	private Button mTitleBarRefresh;					// ��������ˢ�°�ť
	private ProgressBar mTitleBarProgress;				// ������
	private Button mLoadmoreButton;						// ���ظ��ఴť

	private LoadNewsAsyncTack mLoadNewsAsyncTack;		// ����LoadNewsAsyncTack����
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_home_layout);
		
		//ͨ��id����ȡ��ť������
		mTitleBarRefresh = (Button) findViewById(R.id.titlebar_refresh);
		mTitleBarProgress = (ProgressBar) findViewById(R.id.titlebar_progress);
		
		mTitleBarRefresh.setOnClickListener(loadmoreListener);
		// ��pxת��Ϊdip
		mColumnWidth_dip = DensityUtil.px2dip(this, COLUMNWIDTH_PX);
		mFlingVelocity_dip = DensityUtil.px2dip(this, FLINGVELOCITY_PX);
		//��ʼ�����ŷ���ı��
		mCid = 1;
		mCategoryTitle = "����";
		mInflater = getLayoutInflater();
		//�洢������Ϣ�����ݼ���
		mNewsData = new ArrayList<HashMap<String, Object>>();
		// ��ȡ������Դ
		String[] categoryArray = getResources().getStringArray(
				R.array.categories);

		// ����һ��List���飬�������HashMap����
		final List<HashMap<String, Category>> categories = new ArrayList<HashMap<String, Category>>();
		// �ָ������ַ���
		for (int i = 0; i < categoryArray.length; i++) {
			String temp[] = categoryArray[i].split("[|]");
			if (temp.length == 2) {
				int cid = StringUtil.string2Int(temp[0]);
				String title = temp[1];
				Category type = new Category(cid, title);
				// ����һ��HashMap����������ż�ֵ��
				HashMap<String, Category> hashMap = new HashMap<String, Category>();
				hashMap.put("category_title", type);
				categories.add(hashMap);
			}
		}
		ConstomSimpleAdapter categoryAdapter = new ConstomSimpleAdapter(this,
				categories, R.layout.category_item_layout,
				new String[] { "category_title" },
				new int[] { R.id.category_title });
		// ����һ��������ͼ, ����ʵ�����ű���Ĳ���
		GridView category = new GridView(this);
		// ���õ�Ԫ��ı���ɫΪ͸��������ѡ�����ʱ�Ͳ�����ʾ��ɫ������
		category.setSelector(new ColorDrawable(Color.TRANSPARENT));
		// ����ÿһ�����ű���Ŀ��
		category.setColumnWidth(mColumnWidth_dip);
		// ����������ͼ������
		category.setNumColumns(GridView.AUTO_FIT);
		// ���ö��뷽ʽ
		category.setGravity(Gravity.CENTER);
		// ���ݵ�Ԫ��Ŀ�Ⱥ���Ŀ����������ͼ�Ŀ��
		int width = mColumnWidth_dip * categories.size();
		// ��ȡ���ֲ���
		LayoutParams params = new LayoutParams(width, LayoutParams.MATCH_PARENT);
		// ���ò���
		category.setLayoutParams(params);
		// ����Adapter
		category.setAdapter(categoryAdapter);
		// ͨ��ID��ȡLinearLayout���ֶ���
		LinearLayout categoryLayout = (LinearLayout) findViewById(R.id.category_layout);
		// ��������ͼ�����ӵ�LinearLayout���ֵ���
		categoryLayout.addView(category);

		// ��ӵ�Ԫ�����¼�
		category.setOnItemClickListener(new OnItemClickListener() {
			TextView categoryTitle;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				for (int i = 0; i < parent.getCount(); i++) {
					categoryTitle = (TextView) parent.getChildAt(i);
					categoryTitle.setTextColor(0XFFADB2AD);
					categoryTitle.setBackgroundDrawable(null);

				}
				categoryTitle = (TextView) view;
				categoryTitle.setTextColor(0xFFFFFFFF);
				categoryTitle
						.setBackgroundResource(R.drawable.image_categorybar_item_selected_background);
				Toast.makeText(MainActivity.this, categoryTitle.getText(),
						Toast.LENGTH_SHORT).show();
				//��ȡ���ŷ�����
				mCid = categories.get(position).get("category_title").getCid();
				mCategoryTitle = categories.get(position).get("category_title").getTitle();
				mLoadNewsAsyncTack = new LoadNewsAsyncTack();
				mLoadNewsAsyncTack.execute(0, true);
			}

		});
		
		//��һ�λ�ȡ�����б�
		getSpecCatNews(mCid, mNewsData, 0, true);
		// ��ͷ
		categoryScrollView = (HorizontalScrollView) findViewById(R.id.categorybar_scrollView);
		category_Button = (Button) findViewById(R.id.category_arrow_right);
		category_Button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				categoryScrollView.fling(mFlingVelocity_dip);
			}
		});

		mNewslistAdapter = new SimpleAdapter(this, mNewsData,
				R.layout.newslist_item_layout, new String[] {
						"newslist_item_title", "newslist_item_digest",
						"newslist_item_source", "newslist_item_ptime" },
				new int[] { R.id.newslist_item_title,
						R.id.newslist_item_digest, R.id.newslist_item_source,
						R.id.newslist_item_ptime });
		mNewslist = (ListView) findViewById(R.id.news_list);
		
		View footerView = mInflater.inflate(R.layout.loadmore_layout, null);
		//��LiseView������ӡ����ظ��ࡱ
		mNewslist.addFooterView(footerView);
		//��ʾ�б�
		mNewslist.setAdapter(mNewslistAdapter);
		
		mNewslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this,
						NewsDetailActivity.class);
				intent.putExtra("categoryTitle", mCategoryTitle);
				intent.putExtra("newsData", mNewsData);
				intent.putExtra("position", position);
				startActivity(intent);
			}

		});

		mLoadmoreButton = (Button) findViewById(R.id.loadmore_btn);
		mLoadmoreButton.setOnClickListener(loadmoreListener);
		
	}

	/**
	 * ��ȡָ�����͵������б�
	 * 
	 * @param cid
	 * @return
	 */
	private int getSpecCatNews(int cid, List<HashMap<String, Object>> newsList,
			int startnid, boolean firstTime) {

		// ����ǵ�һ�μ��صĻ�
		if (firstTime) {
			newsList.clear();
		}
		//����:http://10.0.2.2:8080/web/getSpecifyCategoryNews
		//wifi��������192.168.220.1
		String url = "http://10.0.2.2:8080/web/getSpecifyCategoryNews";
		String params = "startnid=" + startnid + "&count=" + NEWSCOUNT
				+ "&cid=" + cid;
		SyncHttp syncHttp = new SyncHttp();

		try {
			// ͨ��HttpЭ�鷢��Get���󣬷����ַ���
			String retStr = syncHttp.httpGet(url, params);
			JSONObject jsonObject = new JSONObject(retStr);
			int retCode = jsonObject.getInt("ret");
			if (retCode == 0) {
				JSONObject dataObj = jsonObject.getJSONObject("data");
				// ��ȡ������Ŀ
				int totalNum = dataObj.getInt("totalnum");
				if (totalNum > 0) {
					// ��ȡ�������ż���
					JSONArray newslistArray = dataObj.getJSONArray("newslist");
					// ����JSON��ʽ������������ӵ����ݼ��ϵ���
					for (int i = 0; i < newslistArray.length(); i++) {
						JSONObject newsObject = (JSONObject) newslistArray
								.opt(i);
						HashMap<String, Object> hashMap = new HashMap<String, Object>();
						hashMap.put("nid", newsObject.getInt("nid"));
						hashMap.put("newslist_item_title",
								newsObject.getString("title"));
						hashMap.put("newslist_item_digest",
								newsObject.getString("digest"));
						hashMap.put("newslist_item_source",
								newsObject.getString("source"));
						hashMap.put("newslist_item_ptime",
								newsObject.getString("ptime"));
						hashMap.put("newslist_item_comments",
								newsObject.getInt("commentcount"));
						newsList.add(hashMap);
					}
					return SUCCESS;
				} else {
					//��һ�μ��������б�
					if (firstTime) {
						return NONEWS;			//û������
					} else {
						return NOMORENEWS;		//û�и�������
					}
				}
			} else {
				return LOADERROR;			//��������ʧ��
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return LOADERROR;			//��������ʧ��
		}
	}

	/**
	 * Ϊ�����ظ��ࡱ��ť���������ڲ���
	 */
	private OnClickListener loadmoreListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			mLoadNewsAsyncTack = new LoadNewsAsyncTack();
			switch (v.getId()) {
			//������ظ���
			case R.id.loadmore_btn:
				mLoadNewsAsyncTack.execute(mNewsData.size(), false);	//���ǵ�һ�μ����������б�
				break;
			//���ˢ�°�ť
			case R.id.titlebar_refresh:
				mLoadNewsAsyncTack.execute(0, true);
				break;
			}
		}
	};

	/**
	 * �첽����UI
	 * @author wwj
	 *
	 */
	private class LoadNewsAsyncTack extends AsyncTask<Object, Integer, Integer> {

		//׼������
		@Override
		protected void onPreExecute() {
			mTitleBarRefresh.setVisibility(View.GONE);
			mTitleBarProgress.setVisibility(View.VISIBLE);
			mLoadmoreButton.setText(R.string.loadmore_text);
		}
		//�ں�̨����
		@Override
		protected Integer doInBackground(Object... params) {
			return getSpecCatNews(mCid, mNewsData, (Integer) params[0],
					(Boolean) params[1]);
		}
		//��ɺ�̨����
		@Override
		protected void onPostExecute(Integer result) {
			switch (result) {
			//����Ŀû������
			case NONEWS:
				Toast.makeText(MainActivity.this, R.string.nonews, Toast.LENGTH_SHORT)
						.show();
				break;
			//����Ŀû�и�������
			case NOMORENEWS:
				Toast.makeText(MainActivity.this, R.string.nomorenews,
						Toast.LENGTH_SHORT).show();
				break;
			//����ʧ��
			case LOADERROR:
				Toast.makeText(MainActivity.this, R.string.loadnewserror, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			mTitleBarRefresh.setVisibility(View.VISIBLE);			//ˢ�°�ť����Ϊ�ɼ�
			mTitleBarProgress.setVisibility(View.GONE);				//����������Ϊ���ɼ�
			mLoadmoreButton.setText(R.string.loadmore_btn);			//��ť��Ϣ�滻Ϊ�����ظ��ࡱ
			mNewslistAdapter.notifyDataSetChanged();				//֪ͨListView��������
		}
	}
	
	/**
	 * ��Ӳ˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(1, 1, 1, "����");
		menu.add(1, 2, 2, "�˳�");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case 1:
			UpdateManager updateManager = new UpdateManager(MainActivity.this);
			//������
			updateManager.checkUpdate();
			break;
		case 2:
			finish();
			break;
		}
		return true;
	}
	
	/**
	 * �����������¼�
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK
    			&& event.getAction() == KeyEvent.ACTION_DOWN){
    		if((System.currentTimeMillis() - exitTime > 2000)){
    			Toast.makeText(getApplicationContext(), R.string.backcancel
    					, Toast.LENGTH_LONG).show();
    			exitTime = System.currentTimeMillis();
    		}
    		else{
    			finish();
    			System.exit(0);
    		}
    		return true;
    	}
		return super.onKeyDown(keyCode, event);
	}
}
