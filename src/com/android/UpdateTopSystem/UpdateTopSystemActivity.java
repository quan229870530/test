package com.android.UpdateTopSystem;

import java.io.File;
import java.io.IOException;

import com.ozing.MediaDataEngine.MediaPractiseJni;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

// for test


public class UpdateTopSystemActivity extends Activity implements OnClickListener  {
	static{
	    System.loadLibrary("iconv");
		System.loadLibrary("UpdateSystem");
	}

	private static final String PRODUCT = "";
	
	private static final int COPY_SYSTEM_SUCCESS = 0x00000001;
	private static final int COPY_SYSTEM_FAILD = 0x00000002;
	private static final int COPY_DATA_SUCCESS = 0x00000004;
	private static final int COPY_DATA_FAILD = 0x00000008;
	private static final String TAG = "JavaMessage";
	private DataEng mDataEng;
	private MediaPractiseJni mMediaPractiseJni;
	//private Button btn_testUSB;
	private ImageButton btn_update, btn_cancel, btn_back;
	private TextView textview_status;
	private ProgressDialog mpDialog;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);

        mDataEng = new DataEng();
        IntentFilter filter = new IntentFilter();

        filter.addAction(MyReceiver.UPDATE_SYS_BROADCAST_ACTION);
        registerReceiver(mBroadcastReceiver, filter);
        
        findViews();              
        
        checkUpdateFiles();
    }
   
    public boolean checkUpdateFiles(){
        
        if((mDataEng.JniM11IsExistSystemFile() == 1) || (mDataEng.JniM11IsExistDataFile() == 1)){
            textview_status.setTextColor(Color.BLUE);
            textview_status.setText(PRODUCT + getString(R.string.tv_tip_update_files_exist));
            btn_update.setEnabled(true);
            return true;
        }
        else{
            textview_status.setTextColor(Color.RED);
            textview_status.setText(PRODUCT + getString(R.string.tv_tip_update_files_not_exist));
            btn_update.setEnabled(false);
            return false;
        }
    }

    
    public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            
            if(action.equals(MyReceiver.UPDATE_SYS_BROADCAST_ACTION))
            {
                checkUpdateFiles();
            }
        }        
    };    
    
    protected void onResume() {
        super.onResume();
        
        checkUpdateFiles();
    };
    
	private void findViews() {
		// TODO Auto-generated method stub
		textview_status = (TextView)findViewById(R.id.textview_status);		
		btn_update = (ImageButton)findViewById(R.id.btn_update);
		btn_update.setEnabled(false);
		btn_update.setOnClickListener(this);
		btn_back = (ImageButton)findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_cancel = (ImageButton)findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		
	}
	
	private Handler myHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
			    case UpdateTopSystemActivity.COPY_DATA_SUCCESS:
			        if(mDataEng.JniM11IsExistSystemFile() == 1){
			            setCopySystemProgressDialog();
			        }
			        else{
    	                new AlertDialog.Builder(UpdateTopSystemActivity.this).setTitle(getString(R.string.tv_tip_update_finish))
    	                .setMessage(getString(R.string.tv_tip_update_sucess))
    	                .setCancelable(false)
    	                .setNegativeButton(getString(R.string.dial_btn_yes), new DialogInterface.OnClickListener() {
    	                    
    	                    @Override
    	                    public void onClick(DialogInterface dialog, int which) {
    	                        // TODO Auto-generated method stub
    	                        
    	                    }
    	                })
    	                .show();
			        }
	                break;
			    case UpdateTopSystemActivity.COPY_DATA_FAILD:
                    new AlertDialog.Builder(UpdateTopSystemActivity.this).setTitle(getString(R.string.tv_tip_update_finish))
                    .setMessage(getString(R.string.tv_tip_copy_failed))
                    .setCancelable(false)
                    .setNegativeButton(getString(R.string.dial_btn_yes), new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					})
							.show();
                    break;
    			case UpdateTopSystemActivity.COPY_SYSTEM_FAILD:
    				new AlertDialog.Builder(UpdateTopSystemActivity.this).setTitle(getString(R.string.tv_tip_update_finish))
    				.setMessage(getString(R.string.tv_tip_data_prepare_failed))
							.setCancelable(false)
							.setNegativeButton(getString(R.string.dial_btn_yes), new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub

								}
					})
							.show();
    				break;
    			case UpdateTopSystemActivity.COPY_SYSTEM_SUCCESS:    			        			    
    			    update();
    				break;
				}
		}
	};
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		    
		case R.id.btn_update:
		    if(mDataEng.JniM11IsExistDataFile() == 1){
		        setCopyDataProgressDialog();
		    }
		    else if(mDataEng.JniM11IsExistSystemFile() == 1){
                setCopySystemProgressDialog();
			}else{
			    // do nothing
			}
			break;

		case R.id.btn_back:
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

private void setCopySystemProgressDialog(){
        
        mpDialog = new ProgressDialog(UpdateTopSystemActivity.this);
        mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        mpDialog.setTitle(getString(R.string.tv_tip_warm));
        mpDialog.setMessage(getString(R.string.tv_tip_update_on_going));
        mpDialog.setIndeterminate(false);
        mpDialog.setCancelable(false);
        new Thread(){  
            public void run(){
                int ret;
				ret = 1;

                if(ret == 1){
                    
                    mpDialog.cancel();
                    
                    Message msg = new Message();
                    msg.what = UpdateTopSystemActivity.COPY_SYSTEM_SUCCESS;
                    UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
                }else{
                    
                    mpDialog.cancel(); 
                    
                    Message msg = new Message();
                    msg.what = UpdateTopSystemActivity.COPY_SYSTEM_FAILD;
                    UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
                }
            }  
        }.start(); 
        mpDialog.show();
    }
	
	
private void setCopyDataProgressDialog(){
		
		mpDialog = new ProgressDialog(UpdateTopSystemActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);  
        mpDialog.setTitle(getString(R.string.tv_tip_warm));
        mpDialog.setMessage(getString(R.string.tv_tip_update_on_going));
        mpDialog.setIndeterminate(false);  //
        mpDialog.setCancelable(false);
        new Thread(){  
            public void run(){
            	if(mDataEng.JniM11CopyDataFile() == 1){
            		mpDialog.cancel(); 
                    Message msg = new Message();
                    msg.what = UpdateTopSystemActivity.COPY_DATA_SUCCESS;
                    UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
            	}else{
            		mpDialog.cancel(); 
                    Message msg = new Message();
                    msg.what = UpdateTopSystemActivity.COPY_DATA_FAILD;
                    UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
            	}
            }  
        }.start(); 
        mpDialog.show();
	}


	private void setToast(String text){
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	private void update() {
	    
	    File file;
	    
	    //
		file = new File("/mnt/external_sd/update.zip");
	    
	    //
	    try {
	        
	        ///RecoverySystem.rebootWipeUserData(UpdateTopSystemActivity.this);
	        
	        RecoverySystem.installPackage(UpdateTopSystemActivity.this, file);
	        //RecoverySystem.installPackageWithWipe(UpdateTopSystemActivity.this, file);
	        
	        
	    } catch (IOException e) {
	       // TODO Auto-generated catch block
	       e.printStackTrace();
	    }
   	    
	}
	
	// get MAC address 
    private void getMacInfo(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        
        textview_status.setTextColor(Color.YELLOW);
        textview_status.setText(info.getMacAddress());        
    }
}

