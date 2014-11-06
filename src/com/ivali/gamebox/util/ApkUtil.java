package com.ivali.gamebox.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import android.content.Intent;
import android.net.Uri;

public class ApkUtil {
	
	 public static  boolean isRoot() {
	        boolean flag = false;
	        
	        try {
	            if ((!new File("/system/bin/su").exists())
	                    && (!new File("/system/xbin/su").exists())) {
	                flag = false;
	            } else {
	                flag = true;
	            }
	        } catch (Exception e) {
	            
	        } 
	        return flag;
	    }
	 
	 
	 //android获取一个用于打开apk文件的intent
	    public static Intent getApkFileIntent(File file)
	    {
	        Intent intent = new Intent(); 
	        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
	        intent.setAction(android.content.Intent.ACTION_VIEW); 
	        intent.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive"); 
	        return intent;
	    }
	    
	    /**
	     * 静默安装
	     * 
	     * @param file
	     * @return
	     */
	    public static boolean slientInstall(File file) {
	    	if(file==null){
	    		return true;
	    	}
	        boolean result = false;
	        Process process = null;
	        OutputStream out = null;
	        try {
	            process = Runtime.getRuntime().exec("su");
	            out = process.getOutputStream();
	            DataOutputStream dataOutputStream = new DataOutputStream(out);
	            //dataOutputStream.writeBytes("chmod 777 " + file.getPath() + "\n");
	            dataOutputStream
	                    .writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm install -r "
	                            + file.getPath());
	            // 提交命令
	            dataOutputStream.flush();
	            // 关闭流操作
	            dataOutputStream.close();
	            out.close();
	            int value = process.waitFor();
	            
	            // 代表成功
	            if (value == 0) {
	                result = true;
	            } else if (value == 1) { // 失败
	                result = false;
	            } else { // 未知情况
	                result = false;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        return result;
	    }
	    
	    /**
	     * 静默卸载
	     * 
	     * @param file
	     * @return
	     */
	    public static boolean slientUnInstall(String packagename) {
	        boolean result = false;
	        Process process = null;
	        OutputStream out = null;
	        try {
	            process = Runtime.getRuntime().exec("su");
	            out = process.getOutputStream();
	            DataOutputStream dataOutputStream = new DataOutputStream(out);
	            dataOutputStream
	                    .writeBytes("LD_LIBRARY_PATH=/vendor/lib:/system/lib pm uninstall  "
	                            + packagename);
	            // 提交命令
	            dataOutputStream.flush();
	            // 关闭流操作
	            dataOutputStream.close();
	            out.close();
	            int value = process.waitFor();
	            
	            // 代表成功
	            if (value == 0) {
	                result = true;
	            } else if (value == 1) { // 失败
	                result = false;
	            } else { // 未知情况
	                result = false;
	            }
	        } catch (IOException e) {
	            e.printStackTrace();
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	        
	        return result;
	    }
	    

}
