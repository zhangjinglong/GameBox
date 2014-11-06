package com.ivali.gamebox.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class CommonUtil {
	//获取文件名，如bzdw.apk
    public  static String getFilename(String urlpath) 
    {
        return urlpath.substring(urlpath.lastIndexOf("/") + 1, urlpath.length());
    }
    //检测是否联网
    public static boolean checkNetWorkState(Context ctx){
    	ConnectivityManager cm=(ConnectivityManager)ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo wifiInfo=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    	NetworkInfo networkInfo=cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
    	if(wifiInfo.isAvailable() || networkInfo.isAvailable()){
    		return true;
    	}
    	return false;
    }
    
   public static HashMap<String, String> installPackagesInfo(Context ctx){  
        // 获取packageManager对象  
        PackageManager packageManager = ctx.getPackageManager();  
        /*getInstalledApplications 返回当前设备上安装的应用包集合 
         * ApplicationInfo对应着androidManifest.xml中的application标签。通过它可以获取该application对应的信息 
         */  
        List<ApplicationInfo> applicationInfos = packageManager.getInstalledApplications(0);  
        HashMap<String, String> resultMap = new HashMap<String, String>();  
        Iterator<ApplicationInfo> iterator = applicationInfos.iterator();  
        while(iterator.hasNext()){  
            ApplicationInfo applicationInfo = iterator.next();  
            String packageName = applicationInfo.packageName;// 包名  
            String packageLabel = packageManager.getApplicationLabel(applicationInfo).toString();//获取label  
            resultMap.put(packageLabel, packageName);  
        }  
          
        return resultMap;  
          
    }  
    
}
