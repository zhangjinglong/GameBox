package com.ivali.gamebox;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.ivali.gamebox.bean.ItemObject;
import com.ivali.gamebox.service.UpdateService;
import com.ivali.gamebox.util.ApkUtil;
import com.ivali.gamebox.util.CommonUtil;
import com.ivali.gamebox.util.FileUtil;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {
	SimpleAdapter saItem = null;
	AdapterView.OnItemClickListener onItemClickListener=null;
	ArrayList<ItemObject> objects=null;
	MyBroadcastReceiver myBroadcastReceiver;
	//已经安装的apk的列表
	ArrayList<Integer> installCompletelist=new ArrayList<Integer>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		objects=initData();
        ArrayList<HashMap<String, Object>> meumList = new ArrayList<HashMap<String, Object>>();
        for(int i = 0;i < 9;i++)
        {
            HashMap<String, Object> map = new HashMap<String, Object>();
            ItemObject object=objects.get(i);
            map.put("ItemText",	object.getText() );
            map.put("ItemImage", object.getIconId());
            meumList.add(map);
        }
		saItem = new SimpleAdapter(this, meumList, // 数据源
				R.layout.gridview_item, // xml实现
				new String[] { "ItemImage", "ItemText" }, // 对应map的Key
				new int[] { R.id.ItemImage, R.id.ItemText }); // 对应R的Id

		onItemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(objects.get(arg2).getAppname()!=null){
					Intent updateIntent = new Intent();
			    	updateIntent.setClass(MainActivity.this, UpdateService.class);
					updateIntent.putExtra(
							"app_name",objects.get(arg2).getAppname()
							);
					updateIntent.putExtra(
							"url",objects.get(arg2).getUrl()
							);
					updateIntent.putExtra(
							"noti",objects.get(arg2).getNotiId()+""
							);
					updateIntent.putExtra(
							"filepath",objects.get(arg2).getFilepath()
							);
					startService(updateIntent);
				}else{
					Toast.makeText(getApplicationContext(), "当前没有应用可以下载。。。" , 0)
					.show();
				}
				
				// Toast用于向用户显示一些帮助/提示
			}
		};   
		 //监听是否下载所有应用的广播
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction("com.ivali.gamebox.download");
        myBroadcastReceiver=new MyBroadcastReceiver();
        this.registerReceiver(myBroadcastReceiver, intentFilter);
		showdownloadDialog();
		
	}
	public static CustomDialog mAlertDialog;
	 public void showdownloadDialog() {
	        if (mAlertDialog != null) {
	            mAlertDialog.dismiss();
	            mAlertDialog = null;
	        }
	        mAlertDialog = new CustomDialog.Builder(MainActivity.this)
	                .setLayoutResources(R.layout.custom_message_dialog)
	                .setTitle(true, "九宫格")
	                .setMessage("abc")
	                .setPositiveButton("确定", true,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog,
	                                    int which) {
	                            	 mAlertDialog.dismiss();
		                             MainActivity.this.finish();
	                            }
	                        })
	                .setNegativeButton("取消", true,
	                        new DialogInterface.OnClickListener() {
	                            public void onClick(DialogInterface dialog,
	                                    int which) {
	                                mAlertDialog.dismiss();
	                                MainActivity.this.finish();
	                            }
	                        }).setGridViewAdapter(saItem)
	                        .setGridOnItemClickListener(onItemClickListener).create();
	        mAlertDialog.show();
	    }
	 
	 public ArrayList<ItemObject> initData(){
		 ArrayList<ItemObject> objectlist=new ArrayList<ItemObject>();
		 //url
		 String [] urls=getResources().getStringArray(R.array.app_url_array);
		//text
		 String [] textstemp=getResources().getStringArray(R.array.app_text_array);
		 String [] texts=new String[9];
		 
			 for (int i = 0; i < texts.length; i++) {
				if(i<textstemp.length){
					texts[i]=textstemp[i];
				}else{
					texts[i]="敬请期待";
				}
		 }
		 //iconId  
		 int [] icons={R.drawable.guide1,R.drawable.guide1,R.drawable.guide1,
				 R.drawable.guide2,R.drawable.guide2,R.drawable.guide2,
				 R.drawable.guide3,R.drawable.guide3,R.drawable.guide3
		 };
		//notiId
		 for (int i = 0; i < icons.length; i++) {
			ItemObject object=new ItemObject();
			if(i<urls.length){
				object.setUrl(urls[i]);
				String appname=CommonUtil.getFilename(urls[i]);
				object.setAppname(appname);
				FileUtil.createFile(appname);
				object.setFilepath(FileUtil.updateDir+"/"+appname);
			}
			object.setText(texts[i]);
			object.setIconId(icons[i]);
			object.setNotiId(i);
			objectlist.add(object);
		}
		 
		 return objectlist;
	 }

	 class MyBroadcastReceiver extends BroadcastReceiver{

			@Override
			public void onReceive(Context arg0, Intent intent) {
				// TODO Auto-generated method stub
				Log.e("TTTTTTTTTTTTT", "0000000000000000000000");
				String action=intent.getAction();
				if("com.ivali.gamebox.download".equals(action)){
					if(intent.getBooleanExtra("flag", false) && intent.getStringExtra("filepath")!=null){
						//安装
						String filepath=intent.getStringExtra("filepath");
						if(ApkUtil.isRoot()){
							ApkUtil.slientInstall(new File(filepath));
							final PackageManager manager = MainActivity.this.getPackageManager();
							final HashMap<String, String> pack=CommonUtil.installPackagesInfo(MainActivity.this);
							if(intent.getStringExtra("notiId")!=null){
								final int notiId=Integer.parseInt(intent.getStringExtra("notiId"));
								installCompletelist.add(notiId);
								onItemClickListener = new OnItemClickListener() {
									@Override
									public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
											long arg3) {
										// TODO Auto-generated method stub
										if(installCompletelist.contains(arg2)){
											String label=objects.get(arg2).getText();
											String baoname=pack.get(label);
											Intent pmIntent = manager.getLaunchIntentForPackage(baoname);
							                MainActivity.this.startActivity(pmIntent);
										}else{
											if(objects.get(arg2).getAppname()!=null){
												Intent updateIntent = new Intent();
										    	updateIntent.setClass(MainActivity.this, UpdateService.class);
												updateIntent.putExtra(
														"app_name",objects.get(arg2).getAppname()
														);
												updateIntent.putExtra(
														"url",objects.get(arg2).getUrl()
														);
												updateIntent.putExtra(
														"noti",objects.get(arg2).getNotiId()+""
														);
												updateIntent.putExtra(
														"filepath",objects.get(arg2).getFilepath()
														);
												startService(updateIntent);
											}else{
												Toast.makeText(getApplicationContext(), "当前没有应用可以下载。。。" , 0)
												.show();
											}
										}
									}
								}; 
								mAlertDialog.grid.setOnItemClickListener(onItemClickListener);
								
							}
						 
						}else{
							//一般安装
							Intent in=ApkUtil.getApkFileIntent(new File(filepath));
							startActivity(in);
							//监听安装的广播   一旦有就认为安装了
						}
						
					}
				}
			}
	    	
	    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	 @Override
		protected void onDestroy() {
			// TODO Auto-generated method stub
		  	this.unregisterReceiver(myBroadcastReceiver);
			super.onDestroy();
		}
}
