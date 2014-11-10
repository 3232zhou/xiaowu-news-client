package com.xiaowu.news;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.Html;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ConstomTextView extends LinearLayout{

	//�����Ķ���
	private Context mContext;
	//����TypedArray������
	private TypedArray mTypedArray;
	//���ֲ���
	private LayoutParams params;
	
	public ConstomTextView(Context context) {
		super(context);
	}
	
	public ConstomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		this.setOrientation(LinearLayout.VERTICAL);
		//��attrs.xml�ļ����Ǹ���ȡ�Զ�������
		mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.constomTextView);
	}
	
	public void setText(ArrayList<HashMap<String, Object>> datas) {
		//����ArrayList
		for(HashMap<String, Object> hashMap : datas) {
			//��ȡkeyΪ"type"��ֵ
			String type = (String) hashMap.get("type");
			//���value=imaeg
			if(type.equals("image")){
				//��ȡ�Զ�����������
				int imagewidth = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_width, 100);
				int imageheight = mTypedArray.getDimensionPixelOffset(R.styleable.constomTextView_image_height, 100);
				ImageView imageView = new ImageView(mContext);
				params = new LayoutParams(imagewidth, imageheight);
				params.gravity = Gravity.CENTER_HORIZONTAL;	//����
				imageView.setLayoutParams(params);
				//��ʾͼƬ
				imageView.setImageResource(R.drawable.ic_constom);
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				//��imageView��ӵ�LinearLayout����
				addView(imageView);
				//�����첽�̸߳����첽��ʾͼƬ��Ϣ
				new DownloadPicThread(imageView, hashMap.get("value").toString()).start();
			}
			else {
				float textSize = mTypedArray.getDimension(R.styleable.constomTextView_textSize, 16);
				int textColor = mTypedArray.getColor(R.styleable.constomTextView_textColor, 0xFF0000FF);
				TextView textView = new TextView(mContext);
				textView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
				textView.setText(Html.fromHtml(hashMap.get("value").toString()));
				textView.setTextSize(textSize);		//���������С
				textView.setTextColor(textColor);	//����������ɫ
				addView(textView);
			}
		}
	}
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> hashMap = (HashMap<String, Object>) msg.obj;
			ImageView imageView = (ImageView) hashMap.get("imageView");
			LayoutParams params = new LayoutParams(msg.arg1, msg.arg2);
			params.gravity = Gravity.CENTER_HORIZONTAL;	//����
			imageView.setLayoutParams(params);
			Drawable drawable = (Drawable) hashMap.get("drawable");
			imageView.setImageDrawable(drawable);		//��ʾͼƬ
		};
	};
	
	/**
	 * ����һ���߳��࣬�첽����ͼƬ
	 * @author Administrator
	 *
	 */
	private class DownloadPicThread extends Thread {
		private ImageView imageView;
		private String mUrl;
		
		
		public DownloadPicThread(ImageView imageView, String mUrl) {
			super();
			this.imageView = imageView;
			this.mUrl = mUrl;
		}


		@Override
		public void run() {
			// TODO Auto-generated method stub
			Drawable drawable = null;
			int newImgWidth = 0;
			int newImgHeight = 0;
			try {
				drawable = Drawable.createFromStream(new URL(mUrl).openStream(), "image");
				//��ͼƬ��������
				newImgWidth = drawable.getIntrinsicWidth() / 3;
				newImgHeight = drawable.getIntrinsicHeight() / 3;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			//���߳�����2��
			SystemClock.sleep(2000);
			//ʹ��Handler����UI
			Message msg = handler.obtainMessage();
			HashMap<String, Object> hashMap = new HashMap<String, Object>();
			hashMap.put("imageView", imageView);
			hashMap.put("drawable", drawable);
			msg.obj = hashMap;
			msg.arg1 = newImgWidth;
			msg.arg2 = newImgHeight;
			handler.sendMessage(msg);
		}
	}

}
