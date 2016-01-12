package com.kuaxue.download;

interface IOnDownloadListener{
	void onStart(); 
	void progress(long postion, long length);
	void onFinish();
	void onException();
}