package com.android.UpdateTopSystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	public final static String UPDATE_SYS_BROADCAST_ACTION = "CheckUpdateFilesBroadcast";
	public final static String SDCARD_IS_UNMOUNTED = "SdcardIsUnmounted";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_MEDIA_EJECT)
				|| action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
			sendCheckUpdateFilesBroadcast(context);
		}

		// ¸÷ÖÖÎ´¹ÒÔØ×´Ì¬
		if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")
				|| intent.getAction().equals(
						"android.intent.action.ACTION_MEDIA_UNMOUNTED")
				|| intent.getAction().equals(
						"android.intent.action.ACTION_MEDIA_BAD_REMOVAL")) {
			sendSdcardIsUnmountedBroadcast(context);
		}
	}

	public void sendCheckUpdateFilesBroadcast(Context context) {
		Intent intent = new Intent(UPDATE_SYS_BROADCAST_ACTION);
		context.sendBroadcast(intent);
	}

	public void sendSdcardIsUnmountedBroadcast(Context context) {
		Intent intent = new Intent(SDCARD_IS_UNMOUNTED);
		context.sendBroadcast(intent);
	}
}
