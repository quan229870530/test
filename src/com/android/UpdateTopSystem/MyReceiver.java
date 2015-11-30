package com.android.UpdateTopSystem;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    public final static String UPDATE_SYS_BROADCAST_ACTION = "CheckUpdateFilesBroadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        
        if(action.equals(Intent.ACTION_MEDIA_EJECT) || action.equals(Intent.ACTION_MEDIA_MOUNTED))
        {
            sendCheckUpdateFilesBroadcast(context);
        }
    }        
    
    public void sendCheckUpdateFilesBroadcast(Context context){
        Intent intent = new Intent(UPDATE_SYS_BROADCAST_ACTION);
        context.sendBroadcast(intent);
    }

}
