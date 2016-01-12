package com.kuaxue.download;

import android.os.IBinder;

interface IDownload{
	void download(String filename,String id,IBinder binder);
	void stop(String filename);
	boolean isUpdated(String id);
	void checkUpdated();
}