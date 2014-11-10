package com.xiaowu.news.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.xiaowu.news.R;

/**
 * 
 * @author wwj
 * @date 2012/11/17
 * ʵ��������µĹ�����
 */
public class UpdateManager {
	
	//������...
	private static final int DOWNLOAD = 1;
	//�������
	private static final int DOWNLOAD_FINISH = 2;
	//���������XML��Ϣ
	HashMap<String , String> mHashMap;
	//���ر���·��
	private String mSavePath;
	//��¼����������
	private int progress;
	//�Ƿ�ȡ������
	private boolean cancelUpdate = false;
	//�����Ķ���
	private Context mContext;
	//������
	private ProgressBar mProgressBar;
	//���½������ĶԻ���
	private Dialog mDownloadDialog;
	
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			//�����С�����
			case DOWNLOAD:
				//���½�����
				System.out.println(progress);
				mProgressBar.setProgress(progress);
				break;
			//�������
			case DOWNLOAD_FINISH:
				// ��װ�ļ�
				installApk();
				break;
			}
		};
	};


	public UpdateManager(Context context) {
		super();
		this.mContext = context;
	}
	
	
	/**
	 * ����������
	 */
	public void checkUpdate() {
		if (isUpdate()) {
			//��ʾ��ʾ�Ի���
			showNoticeDialog();
		} else {
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
		}
		
	}
	
	private void showNoticeDialog() {
		// TODO Auto-generated method stub
		//����Ի���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_update_title);
		builder.setMessage(R.string.soft_update_info);
		//����
		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// ��ʾ���ضԻ���
				showDownloadDialog();
			}
		});
		// �Ժ����
		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		Dialog noticeDialog = builder.create();
		noticeDialog.show();
	}
	
	private void showDownloadDialog() {
		// ����������ضԻ���
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.soft_updating);
		// �����ضԻ������ӽ�����
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.softupdate_progress, null);
		mProgressBar = (ProgressBar) view.findViewById(R.id.update_progress);
		builder.setView(view);
		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// ����ȡ��״̬
				cancelUpdate = true;
			}
		});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		//�����ļ�
		downloadApk();
	}
	
	/**
	 * ����APK�ļ�
	 */
	private void downloadApk() {
		// TODO Auto-generated method stub
		// �������߳��������
		new DownloadApkThread().start();
	}


	/**
	 * �������Ƿ��и��°汾
	 * @return
	 */
	public boolean isUpdate() {
		// ��ȡ��ǰ����汾
		int versionCode = getVersionCode(mContext);
		//��version.xml�ŵ������ϣ�Ȼ���ȡ�ļ���Ϣ
		InputStream inStream = ParseXmlService.class.getClassLoader().getResourceAsStream("version.xml");
		// ����XML�ļ��� ����XML�ļ��Ƚ�С�����ʹ��DOM��ʽ���н���
		ParseXmlService service = new ParseXmlService();
		try {
			mHashMap = service.parseXml(inStream);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if(null != mHashMap) {
			int serviceCode = Integer.valueOf(mHashMap.get("version"));
			//�汾�ж�
			if(serviceCode > versionCode) {
				return true;
			}
		}
		return false;
	}

	/**
	 * ��ȡ����汾��
	 * @param context
	 * @return
	 */
	private int getVersionCode(Context context) {
		// TODO Auto-generated method stub
		int versionCode = 0;

		// ��ȡ����汾�ţ���ӦAndroidManifest.xml��android:versionCode
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					"com.xiaowu.news", 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
	
	/**
	 * �����ļ��߳�
	 * @author Administrator
	 *
	 */
	private class DownloadApkThread extends Thread {
		@Override
		public void run() {
			try
			{
				//�ж�SD���Ƿ���ڣ������Ƿ���ж�дȨ��
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
				{
					// ��ȡSDCard��·��
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(mHashMap.get("url"));
					// ��������
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					// ��ȡ�ļ���С
					int length = conn.getContentLength();
					// ����������
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					// ����ļ������ڣ��½�Ŀ¼
					if (!file.exists())
					{
						file.mkdir();
					}
					File apkFile = new File(mSavePath, mHashMap.get("name"));
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					// ����
					byte buf[] = new byte[1024];
					// д�뵽�ļ���
					do
					{
						int numread = is.read(buf);
						count += numread;
						// �����������λ��
						progress = (int) (((float) count / length) * 100);
						// ���½���
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0)
						{
							// �������
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						// д���ļ�
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);//���ȡ����ֹͣ����
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			// ȡ�����ضԻ�����ʾ
			mDownloadDialog.dismiss();
		}
	}
	
	/**
	 * ��װAPK�ļ�
	 */
	private void installApk()
	{
		File apkfile = new File(mSavePath, mHashMap.get("name"));
		if (!apkfile.exists())
		{
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
}
