package com.ozing.MediaDataEngine;//

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//import org.apache.http.util.EncodingUtils;




import android.R.integer;
import android.R.string;
import android.content.Intent;
import android.os.Environment;

//
public class MediaPractiseJni {
		//
	public  MediaPractiseJni()
	{
		
	}                                                          
 
//	
	public native int JniinitResouce();	
	
	static    
	{
		System.out.println("loadLibrary start");
		System.loadLibrary("iconv");
		System.loadLibrary("MediaPractise-jni");
		System.out.println("loadLibrary end");
	}
                                                                
	
}
