package com.android.UpdateTopSystem;

import com.kuaxue.download.IOnDownloadListener;

import android.os.RemoteException;

public class OnNewDownloadListener extends IOnDownloadListener.Stub{
	private String filename;
	
	public OnNewDownloadListener(String filename) {
		this.filename = filename;
	}

	@Override
	public void progress(long postion, long length) throws RemoteException {
		System.out.println(filename+"("+postion+"-"+length+")");
	}

	@Override
	public void onFinish() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onException() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStart() throws RemoteException {
		// TODO Auto-generated method stub
		
	}
}


