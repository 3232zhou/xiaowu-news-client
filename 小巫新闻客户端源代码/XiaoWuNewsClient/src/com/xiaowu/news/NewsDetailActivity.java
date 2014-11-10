package com.xiaowu.news;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.xiaowu.news.service.SyncHttp;
import com.xiaowu.news.thread.PostCommentsThread;

public class NewsDetailActivity extends Activity {

	private final int FINISH = 0;				//�����̵߳�״̬�Ľ���
	private LayoutInflater mNewsbodyLayoutInflater;
	private ViewFlipper mNewsBodyFlipper;		//��Ļ�л��ؼ�
	private ArrayList<HashMap<String, Object>> mNewsData;
	private float mStartX;						//��ָ���µĿ�ʼλ��
	private int mPosition = 0;					//�������λ��		
	private int mCursor = 0;					//����������ŵ����λ��
	private int mNid;							//���ű��
	private Button mNewsDetailTitleBarComm;		//��ʾ���������İ�ť
	private ConstomTextView mNewsBodyDetail;	//������ϸ����
	private LinearLayout mNewsReplyEditLayout;	//���Żظ��Ĳ���
	private LinearLayout mNewsReplyImgLayout;	//����ͼƬ�ظ��Ĳ���
	private EditText mNewsReplyEditText;		//���Żظ����ı���
	private ImageButton mShareNewsButton;		//�������ŵİ�ť
	private ImageButton mFavoritesButton;		//�ղ����ŵİ�ť
	private boolean keyboardShow;				//�����Ƿ���ʾ
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.arg1) {
			case FINISH:
				//�ѻ�ȡ����������ʾ��������
				ArrayList<HashMap<String, Object>> bodyList = (ArrayList<HashMap<String, Object>>) msg.obj;
				mNewsBodyDetail.setText(bodyList);
				break;
			}
		}
	};

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newsdetails_layout);

		mNewsReplyEditLayout = (LinearLayout) findViewById(R.id.news_reply_edit_layout);
		mNewsReplyImgLayout = (LinearLayout) findViewById(R.id.news_reply_img_layout);
		
		Button newsDetailPrev = (Button) findViewById(R.id.newsdetail_titlebar_previous);
		Button newsDetailNext = (Button) findViewById(R.id.newsdetail_titlebar_next);
		mNewsDetailTitleBarComm = (Button) findViewById(R.id.newsdetail_titlebar_comments);
		mNewsReplyEditText = (EditText) findViewById(R.id.news_reply_edittext);
		mShareNewsButton = (ImageButton) findViewById(R.id.news_share_btn);
		mFavoritesButton = (ImageButton) findViewById(R.id.news_favorites_btn);
		
		
		NewsDetailOnClickListener newsDetailOnClickListener = new NewsDetailOnClickListener();
		
		newsDetailPrev.setOnClickListener(newsDetailOnClickListener);
		newsDetailNext.setOnClickListener(newsDetailOnClickListener);
		mNewsDetailTitleBarComm.setOnClickListener(newsDetailOnClickListener);
		mShareNewsButton.setOnClickListener(newsDetailOnClickListener);
		mFavoritesButton.setOnClickListener(newsDetailOnClickListener);
		
		Button newsReplyPost = (Button) findViewById(R.id.news_reply_post);
		newsReplyPost.setOnClickListener(newsDetailOnClickListener);
		ImageButton newsReplyImgBtn = (ImageButton) findViewById(R.id.news_reply_img_btn);
		newsReplyImgBtn.setOnClickListener(newsDetailOnClickListener);
		
		
		//��ȡ���͵�����
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String categoryName = bundle.getString("categoryTitle");
		TextView titleBarTitle = (TextView) findViewById(R.id.newsdetail_titlebar_title);
		//���ñ������ı���
		titleBarTitle.setText(categoryName);
		//��ȡ���ż���
		Serializable serializable = bundle.getSerializable("newsData");
		mNewsData = (ArrayList<HashMap<String, Object>>) serializable;

		//��ȡ���λ��
		mCursor = mPosition = bundle.getInt("position");
		
		mNewsBodyFlipper = (ViewFlipper) findViewById(R.id.news_body_flipper);
		// ��ȡLayoutInflater����
		mNewsbodyLayoutInflater = getLayoutInflater();
		
		inflateView(0);
		//�����߳�
		new UpdateNewsThread().start();
	}

	/**
	 * ��ʾ��һ������
	 */
	private void showPrevious() {
		if(mPosition > 0) {
			mPosition--;
			//��¼��ǰ���ű��
			HashMap<String, Object> hashMap = mNewsData.get(mPosition);
			mNid = (Integer) hashMap.get("nid");
			if(mCursor > mPosition){
				mCursor = mPosition;
				inflateView(0);
				mNewsBodyFlipper.showNext();
			}
			mNewsBodyFlipper.setInAnimation(this, R.anim.push_right_in);	//������һҳ����ʱ�Ķ���
			mNewsBodyFlipper.setOutAnimation(this, R.anim.push_right_out);	//���õ�ǰҳ��ȥ�Ķ���
			mNewsBodyFlipper.showPrevious();
		}
		else {
			Toast.makeText(NewsDetailActivity.this, "û����һƪ����", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * ��ʾ��һ������
	 */
	private void showNext() {
		if(mPosition < mNewsData.size() - 1){
			// ������һ������
			mNewsBodyFlipper.setInAnimation(this, R.anim.push_left_in);
			mNewsBodyFlipper.setOutAnimation(this, R.anim.push_left_out);
			mPosition++;
			//��¼��ǰ���ű��
			HashMap<String, Object> hashMap = mNewsData.get(mPosition);
			mNid = (Integer) hashMap.get("nid");
			if(mPosition >= mNewsBodyFlipper.getChildCount()){
				inflateView(mNewsBodyFlipper.getChildCount());
			}
			mNewsBodyFlipper.showNext();
		} else {
			Toast.makeText(NewsDetailActivity.this, "û����ƪ����", Toast.LENGTH_SHORT).show();
		}
	}

	private void inflateView(int index) {
		//��ȡ���������Ϣ
		HashMap<String, Object> hashMap = mNewsData.get(mPosition);
		mNid = (Integer) hashMap.get("nid");

		View mNewsBodyView = mNewsbodyLayoutInflater.inflate(
				R.layout.newsbody_layout, null);
		mNewsDetailTitleBarComm.setText(hashMap.get("newslist_item_comments").toString() + "����");
		//���ű���
		TextView newsTitle = (TextView) mNewsBodyView
				.findViewById(R.id.news_body_title);
		newsTitle.setText(hashMap.get("newslist_item_title").toString());
		//���ŵĳ����ͷ���ʱ��
		TextView newsPtimeAndSource = (TextView) mNewsBodyView
				.findViewById(R.id.news_body_ptime_source);
		newsPtimeAndSource.setText(hashMap.get("newslist_item_source").toString() 
				+ "		" + hashMap.get("newslist_item_ptime").toString());
		mNewsBodyDetail = (ConstomTextView) mNewsBodyView
				.findViewById(R.id.news_body_details);
		mNewsBodyDetail.setText(getNewsBody());
		mNewsBodyFlipper.addView(mNewsBodyView, index);
		mNewsBodyDetail.setOnTouchListener(new NewsBodyOntouchListener());
	}

	// �����ڲ���--���ڴ���������İ�ť�Ĵ����¼�
	private class NewsDetailOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			// TODO Auto-generated method stub
			switch (v.getId()) {
			//��һƪ
			case R.id.newsdetail_titlebar_previous:
				showPrevious();
				break;
			//��һƪ
			case R.id.newsdetail_titlebar_next:
				showNext();
				break;
			//����
			case R.id.newsdetail_titlebar_comments:
				Intent intent = new Intent(NewsDetailActivity.this,
						CommentsActivity.class);
				intent.putExtra("nid", mNid);
				startActivity(intent);
				break;
			//��д������ͼƬ
			case R.id.news_reply_img_btn:
				mNewsReplyEditLayout.setVisibility(View.VISIBLE);
				mNewsReplyImgLayout.setVisibility(View.GONE);
				mNewsReplyEditText.requestFocus();
				//��ʾ���뷨
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
				keyboardShow = true;
				break;
			//����ť
			case R.id.news_share_btn:
				Intent shareIntent = new Intent(Intent.ACTION_SEND);
				//���ı�
				shareIntent.setType("text/plain");
				shareIntent.putExtra(Intent.EXTRA_SUBJECT, "����");
				shareIntent.putExtra(Intent.EXTRA_TEXT, "���뽫����������...."+ getTitle());
				startActivity(Intent.createChooser(shareIntent, getTitle()));
				break;
			//�ղذ�ť
			case R.id.news_favorites_btn:
				Toast.makeText(NewsDetailActivity.this, "�ղسɹ�", Toast.LENGTH_SHORT).show();
				break;
			//����ť
			case R.id.news_reply_post:
				//�������뷨
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				String str = mNewsReplyEditText.getText().toString();
				if(str.equals("")){
					Toast.makeText(NewsDetailActivity.this, "����Ϊ��",
							Toast.LENGTH_SHORT).show();
				}
				else {
					mNewsReplyEditLayout.post(new PostCommentsThread(mNid, "������",
							str + "",
							new NewsDetailActivity()));
					mNewsReplyEditLayout.setVisibility(View.GONE);
					mNewsReplyImgLayout.setVisibility(View.VISIBLE);
				}
				break;
			}
		}
	}

	private class NewsBodyOntouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
				//��ָ����
			case MotionEvent.ACTION_DOWN:
				if(keyboardShow){
					mNewsReplyEditLayout.setVisibility(View.GONE);
					mNewsReplyImgLayout.setVisibility(View.VISIBLE);
					//�������뷨
					InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
					m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
					keyboardShow = false;
				}
				//�õ����µĺ������λ��
				mStartX = event.getX();
				break;
			case MotionEvent.ACTION_UP:
				// ���󻬶�
				if (event.getX() < mStartX) {
					showNext();
				}
				// ���һ���
				else if (event.getX() > mStartX) {
					showPrevious();
				}
				break;
			}
			return true;
		}
	}
	
	/**
	 * ����һ���߳��࣬�������»�ȡ�����ŵ���Ϣ
	 * @author Administrator
	 *
	 */
	private class UpdateNewsThread extends Thread {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ArrayList<HashMap<String, Object>> newsStr = getNewsBody();
			Message msg = mHandler.obtainMessage();	//��ȡmsg
			msg.arg1 = FINISH;			
			msg.obj = newsStr;
			mHandler.sendMessage(msg);	//��Handler������Ϣ
		}
	}
	
	
	/**
	 * ��ȡ������ϸ��Ϣ
	 * @return
	 */
	private ArrayList<HashMap<String, Object>> getNewsBody(){
		//String retStr = "��������ʧ��,���Ժ�����";
		ArrayList<HashMap<String, Object>> bodylist = new ArrayList<HashMap<String,Object>>();
		
		SyncHttp syncHttp = new SyncHttp();
		//ģ����:url = "http://10.0.2.2:8080/web/getNews";
		//����:http://127.0.0.1:8080
		//wifi������:http://192.168.220.1:8080
		String url = "http://10.0.2.2:8080/web/getNews";
		String params = "nid=" + mNid;
		try {
			String retString = syncHttp.httpGet(url, params);
			JSONObject  jsonObject = new JSONObject(retString);
			//��ȡ�����룬0��ʾ�ɹ�
			int retCode = jsonObject.getInt("ret");
			if(retCode == 0) {
				JSONObject dataObject = jsonObject.getJSONObject("data");
				JSONObject newsObject = dataObject.getJSONObject("news");
				//retStr = newsObject.getString("body");
				JSONArray bodyArray = newsObject.getJSONArray("body");
				for(int i = 0; i < bodyArray.length(); i++) {
					JSONObject object = (JSONObject) bodyArray.opt(i);
					HashMap<String, Object> hashMap = new HashMap<String, Object>();
					hashMap.put("index", object.get("index"));
					hashMap.put("type", object.get("type"));
					hashMap.put("value", object.get("value"));
					bodylist.add(hashMap);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return bodylist;
	}
	
	
	@Override
	public boolean onCreatePanelMenu(int featureId, Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, 0, 0, "����");
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()) {
		case 0:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			//���ı�
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "����");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "���������������:" + getTitle());
			shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(Intent.createChooser(shareIntent, getTitle()));
			System.out.println(getTitle());
			break;
			
		}
		return super.onOptionsItemSelected(item);
	}
}
