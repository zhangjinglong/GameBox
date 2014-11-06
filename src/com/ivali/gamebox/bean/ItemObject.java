package com.ivali.gamebox.bean;

import android.app.Notification;

public class ItemObject {
	
	//picture   text   url filepath appname   noti  notiId
	//isdownloadfinish     
	private String text;
	private int iconId;
	private int notiId;
	private Notification noti;
	private String url;
	private String filepath;
	private String appname;
	public String getAppname() {
		return appname;
	}
	public void setAppname(String appname) {
		this.appname = appname;
	}
	private boolean downloadFlag;
	public boolean isDownloadFlag() {
		return downloadFlag;
	}
	public void setDownloadFlag(boolean downloadFlag) {
		this.downloadFlag = downloadFlag;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getIconId() {
		return iconId;
	}
	public void setIconId(int iconId) {
		this.iconId = iconId;
	}
	public int getNotiId() {
		return notiId;
	}
	public void setNotiId(int notiId) {
		this.notiId = notiId;
	}
	public Notification getNoti() {
		return noti;
	}
	public void setNoti(Notification noti) {
		this.noti = noti;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	

}
