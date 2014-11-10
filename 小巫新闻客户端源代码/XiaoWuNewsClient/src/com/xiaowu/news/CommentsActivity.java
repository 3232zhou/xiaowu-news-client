package com.xiaowu.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.xiaowu.news.service.SyncHttp;
import com.xiaowu.news.thread.PostCommentsThread;

public class CommentsActivity extends Activity{
	private int mNid;
	private LinearLayout mNewsReplyEditLayout;
	private LinearLayout mNewsReplyImgLayout;
	private ImageButton mNewsReplyImgBtn;
	private EditText mNewsReplyEditText;
	private Button mNewsReplyPost;
	
	List<HashMap<String, Object>> mCommentsData = new ArrayList<HashMap<String,Object>>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments_layout);
		mNewsReplyEditLayout = (LinearLayout) findViewById(R.id.news_reply_edit_layout);
		mNewsReplyImgLayout = (LinearLayout) findViewById(R.id.news_reply_img_layout);
		mNewsReplyImgBtn = (ImageButton) findViewById(R.id.news_reply_img_btn);
		mNewsReplyEditText = (EditText) findViewById(R.id.news_reply_edittext);
		mNewsReplyPost = (Button) findViewById(R.id.news_reply_post);
		
		NewsCommentsOnClickListener newsCommentsOnClickListener = new NewsCommentsOnClickListener();
		mNewsReplyImgBtn.setOnClickListener(newsCommentsOnClickListener);
		mNewsReplyPost.setOnClickListener(newsCommentsOnClickListener);
		
		//��ȡ���ŵı��
		Intent intent = getIntent();
		mNid = intent.getIntExtra("nid", 0);
		
		mCommentsData = new ArrayList<HashMap<String,Object>>();
		Button commentsToNewBtn = (Button) findViewById(R.id.comments_titlebar_news);
		getComments(mNid);
		SimpleAdapter commentsAdapter = new SimpleAdapter(this, mCommentsData
				, R.layout.comments_list_item_layout
				, new String[] {"commentator_from", "comment_ptime", "comment_content"}
				, new int[] { R.id.commentator_from, R.id.comment_ptime, R.id.comment_content});
		ListView commentsList = (ListView) findViewById(R.id.comment_list);
		commentsList.setAdapter(commentsAdapter);
		
		
		//Ϊ��ԭ�ġ���ť���ü����¼�
		commentsToNewBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	/**
	 * ��ȡ���Żظ�����
	 * @param mNid
	 */
	private void getComments(int mNid) {
		// http://10.0.2.2:8080/web/getComments
		String url = "http://10.0.2.2:8080/web/getComments";
		String params = "nid=" + mNid + "&startnid=0&count=10"; 
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
						JSONArray commentsList = dataObj.getJSONArray("commentslist");
						// ����JSON��ʽ������������ӵ����ݼ��ϵ���
						for (int i = 0; i < commentsList.length(); i++) {
							JSONObject commentsObject = (JSONObject) commentsList
									.opt(i);
							HashMap<String, Object> hashMap = new HashMap<String, Object>();
							hashMap.put("cid", commentsObject.getInt("cid"));
							hashMap.put("commentator_from",
									commentsObject.getString("region"));
							hashMap.put("comment_ptime",
									commentsObject.getString("ptime"));
							hashMap.put("comment_content",
									commentsObject.getString("content"));
							mCommentsData.add(hashMap);
						}
					}
					else {
						//û�лظ���Ϣ
						Toast.makeText(CommentsActivity.this, R.string.noreply, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					//��ȡ�ظ���Ϣʧ��
					Toast.makeText(CommentsActivity.this, R.string.getreplyfaild, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//��ȡ�ظ���Ϣʧ��
				Toast.makeText(CommentsActivity.this, R.string.getreplyfaild, Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//��ȡ�ظ���Ϣʧ��
				Toast.makeText(CommentsActivity.this, R.string.getreplyfaild, Toast.LENGTH_SHORT).show();

			}
	}
	
	private class NewsCommentsOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			InputMethodManager m = (InputMethodManager) mNewsReplyEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			// TODO Auto-generated method stub
			switch(v.getId()){
			//ͼƬ��ť
			case R.id.news_reply_img_btn:
				mNewsReplyEditLayout.setVisibility(View.VISIBLE);
				mNewsReplyImgLayout.setVisibility(View.GONE);
				mNewsReplyEditText.requestFocus();
				//��ʾ���뷨
				m.toggleSoftInput(0, InputMethodManager.SHOW_IMPLICIT);
				break;
			//���Ͱ�ť
			case R.id.news_reply_post:
				//�������뷨
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				String str = mNewsReplyEditText.getText().toString();
				if(str.equals("")){
					Toast.makeText(CommentsActivity.this, "����Ϊ��",
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
}
