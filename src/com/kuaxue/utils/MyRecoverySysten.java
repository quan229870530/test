package com.kuaxue.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.ConditionVariable;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.Log;

public class MyRecoverySysten {
	private static final String TAG = "com.android.UpdateTopSystem";
	private static File RECOVERY_DIR = new File("/cache/recovery");
    private static File COMMAND_FILE = new File(RECOVERY_DIR, "command");
    private static File LOG_FILE = new File(RECOVERY_DIR, "log");
    private static String LAST_PREFIX = "last_";
	private static void bootCommand(Context context, String arg) throws IOException {
        RECOVERY_DIR.mkdirs();  // In case we need it
        COMMAND_FILE.delete();  // In case it's not writable
        LOG_FILE.delete();

        FileWriter command = new FileWriter(COMMAND_FILE);
        try {
            command.write(arg);
            command.write("\n");
        } finally {
            command.close();
        }

        // Having written the command file, go ahead and reboot
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");

        throw new IOException("Reboot failed (no permissions?)");
    }
	public static void installPackage(Context context, File packageFile)
	        throws IOException {
	        String filename = packageFile.getCanonicalPath();
	        Log.w(TAG, "!!! REBOOTING TO INSTALL " + filename + " !!!");
	        if(filename.endsWith(".img")){
		        String arg = "--update_rkimage=" + filename +
		            "\n--locale=" + Locale.getDefault().toString();
		        bootCommand(context, arg);
	        }else if(filename.endsWith(".zip")){
	        	String arg = "--update_package=" + filename +
			            "\n--locale=" + Locale.getDefault().toString();
			        bootCommand(context, arg);
	        }
	    }
	
	 public static void rebootDataCache(Context context, File packageFile) throws IOException {
		 	String filename = packageFile.getCanonicalPath();
		 	Log.w(TAG, filename);
			if(filename.endsWith(".img")){
				bootCommand(context, "--update_rkimage="+ filename +"\n--locale=zh_CN\n--wipe_data\n--locale=zh_CN");
			}
			else if(filename.endsWith(".zip")){
				bootCommand(context, "--update_package="+ filename +"\n--locale=zh_CN\n--wipe_data\n--locale=zh_CN");
			}
	    }
	
}
