package com.android.UpdateTopSystem;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kuaxue.account.Account;
import com.kuaxue.download.IDownload;
import com.kuaxue.utils.MD5;
import com.kuaxue.utils.MyRecoverySysten;
import com.kuaxue.utils.NetworkAvailable;
import com.ozing.MediaDataEngine.MediaPractiseJni;

// for test

public class UpdateTopSystemActivity extends Activity implements
		OnClickListener {
	static {
		System.loadLibrary("iconv");
		System.loadLibrary("UpdateSystem");
	}
	private Account account = null;
	private IDownload download = null;
	private IDownload myDownload = null;
	private static final String pathVersion = "121.42.206.190:8080/systemupdate/app/getSystemVersion";
	private static final String TFCard = "/mnt/external_sd";
	private static final String TDCard = "/mnt/internal_sd";
	private static final int COPY_SYSTEM_SUCCESS = 0x00000001;
	private static final int COPY_SYSTEM_FAILD = 0x00000002;
	private static final int COPY_DATA_SUCCESS = 0x00000004;
	private static final int COPY_DATA_FAILD = 0x00000008;
	private static final int CURRENTLY_IS_LATEST_VERSION = 0x00000010;
	private static final int FULL_PACKAGE_DOWNLOAD = 0x00000020;
	private static final int INCREMENTAL_PACKAGE_DOWNLOAD = 0x00000040;
	private static final int CHECK_LAYOUT = 0x00000100;
	private static final int PROGRESS_UPDATE = 0x00001000;
	private static final int START_DOWNLOAD = 0x00002000;
	private static final int DOWNLOAD_FINISH = 0x00004000;
	private static final int DOWNLOAD_EXCEPTION = 0x00008000;
	private static final String TAG = "com.android.UpdateTopSystem";
	private DataEng mDataEng;
	private MediaPractiseJni mMediaPractiseJni;

	private CopyFailureDialog copyFailureDialog = null;

	private CheckBox cb_formatting_data_partition;
	private LinearLayout bg_img;
	private RelativeLayout rl_content, rl_update, rl_explanation;
	private ImageButton btn_cancel, btn_update, btn_back;
	private TextView textview_status, tv_data_is_exist, tv_step;
	private ProgressDialog mpDialog;

	private boolean flag = false;
	private String token = "mytoken";
	private String downloadPath;
	private JSONObject result = null;
	private DownloadDataBean1 downloadDataBean1 = new DownloadDataBean1();
	private boolean flag_is_checked = true;//标记是否需要更新data分区
	private boolean flag_is_exist_service_data = true;//标记服务器上是否有数据
	private int flag_is_exist_native_data = 0;// 本地升级数据是否存在的标记
	private static final int SDCARD_IS_UNMOUNT = 0x00000010;
	private static final int TD_EXIST_SYSTEM_DATA = 0x00000001;
	private static final int TD_EXIST_D_DATA = 0x00000002;
	private static final int TF_EXIST_SYSTEM_DATA = 0x00000004;
	private static final int TF_EXIST_D_DATA = 0x00000008;
	private static final int TF_EXIST_IMG_DATA = 0x0000000F;
	private static final int NOT_EXIST_NATIVE_DATA = 0x0000000;
	private String machine_no;// 机器号
	private String serial_number;// 序列号
	private static String version;// 版本A**

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.newlayout);
		
		machine_no = Build.MODEL;// 得到机器号
		version = SystemProperties.get("ro.build.kuaxue.version", "rockchip");// 版本A**
		serial_number = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);// 得到机器的序列号
		
		mDataEng = new DataEng();
		myBindAccount();
		myBindService();
		try {
			if(account != null)
			token = account.getToken();
			if(token != null && "mytoken".equals(token)){
				requestLatestVersion();
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*添加了一行注释*/
		findViews();
		checkLayout();
		initEvent();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyReceiver.UPDATE_SYS_BROADCAST_ACTION);
		registerReceiver(mBroadcastReceiver, filter);
	}

	public boolean checkUpdateFiles() {
		btn_update.setEnabled(true);
		flag_is_exist_native_data = 0;
		if (IsExistSystemFile(TDCard + "/update.zip")) {
			flag_is_exist_native_data |= TD_EXIST_SYSTEM_DATA;
		}
		if (IsExistSystemFile(TFCard + "/update.zip")) {
			flag_is_exist_native_data |= TF_EXIST_SYSTEM_DATA;
		}
		if (mDataEng.JniM11IsExistDataFile() == 1) {
			flag_is_exist_native_data |= TF_EXIST_D_DATA;
		}
		if(IsExistSystemFile(TFCard + "/update.img") || IsExistSystemFile(TDCard + "/update.img")){
			flag_is_exist_native_data |= TF_EXIST_IMG_DATA;
		}
		if(IsExistSystemFile(TDCard + "/D.img")){ 
			flag_is_exist_native_data |= TD_EXIST_D_DATA;
		}
		Log.e(TAG, "FLAG_IS_EXIST_NATIVE_DATA:"+ flag_is_exist_native_data);
		if (flag_is_exist_native_data > 0) {
			return true;
		}
		return false;
	}

	private boolean IsExistSystemFile(String path) {
		File dir = new File(path);
		if(dir.exists() && dir.isFile()){
			//String md5 = MD5.getMD5(dir);
			//if(downloadDataBean1 != null)
			//	if(md5.equals(downloadDataBean1.full_md5) || md5.equals(downloadDataBean1.incremental_md5)){
					return true;
			//	}
		}
		return false;
	}

	// 检查是否有网络更新文件,有更新文件自动更新
	private void checkInterUpdateFiles() {
		Log.e(TAG, "checkInterUpdateFiles");
		new Thread() {
			public void run() {
				Log.e(TAG, "result:"+result);
					if (result != null) {
						jsonAnalysis(result.toString(), downloadDataBean1);
						if(!flag_is_exist_service_data){
							if(flag){
								if(account != null){
									try {
										token = account.getToken();
										if(token != null){
											requestLatestVersion();
											flag = false;
										}
									} catch (RemoteException e) {
										e.printStackTrace();
									}
								}
							}else{
								Looper.prepare();
								setToast(getResources().getString(
										R.string.currently_is_latest_version));
								Looper.loop();
							}
						}
						else if (versionComparison(version,
								downloadDataBean1.version) > 1) {
							Message msg = new Message();
							msg.what = FULL_PACKAGE_DOWNLOAD;
							myHandler.sendMessage(msg);
						} else if (versionComparison(version,
								downloadDataBean1.version) == 1
								&& !"".equals(downloadDataBean1.incremental_path)) {
							Message msg = new Message();
							msg.what = INCREMENTAL_PACKAGE_DOWNLOAD;
							myHandler.sendMessage(msg);
						} else if (versionComparison(version,
								downloadDataBean1.version) == 1
								&& "".equals(downloadDataBean1.incremental_path)
								&& !"".equals(downloadDataBean1.full_path)) {
							Message msg = new Message();
							msg.what = FULL_PACKAGE_DOWNLOAD;
							myHandler.sendMessage(msg);
						} else {
							Message msg = new Message();
							msg.what = CURRENTLY_IS_LATEST_VERSION;
							myHandler.sendMessage(msg);
						}
					}
					else{
						Looper.prepare();
						setToast(getResources().getString(
								R.string.internet_busy));
						Looper.loop();
					}
				
			}
		}.start();
	}

	public BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (action.equals(MyReceiver.UPDATE_SYS_BROADCAST_ACTION)) {
				checkLayout();
			}
			if (action.equals(MyReceiver.SDCARD_IS_UNMOUNTED)) {
				if (myDownload != null) {
					try {
						Log.e(TAG, "stopstopstopstopstop");
						Log.e(TAG, myDownload+"");
						Log.e(TAG, downloadPath);
						myDownload.stop(TDCard + "/update_temp.zip");
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(copyFailureDialog != null){
						copyFailureDialog.dismiss();
					}
				}
			}
		}
	};

	protected void onResume() {
		flag = true;
		super.onResume();
		try {
			if(account != null){
				token = account.getToken();
				if(token != null){
					requestLatestVersion();	
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		checkLayout();
	};

	private void findViews() {
		cb_formatting_data_partition = (CheckBox) findViewById(R.id.cb_formatting_data_partition);
		rl_update = (RelativeLayout) findViewById(R.id.update);
		tv_step = (TextView) findViewById(R.id.tv_step);
		rl_content = (RelativeLayout) findViewById(R.id.content);
		bg_img = (LinearLayout) findViewById(R.id.bg_img);
		tv_data_is_exist = (TextView) findViewById(R.id.data_is_exist);
		btn_update = (ImageButton) findViewById(R.id.btn_update);
		btn_update.setEnabled(false);
		btn_update.setOnClickListener(this);
		btn_back = (ImageButton) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		btn_cancel = (ImageButton) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(this);
		rl_explanation = (RelativeLayout) findViewById(R.id.rl_explanation);
	}

	private Handler myHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UpdateTopSystemActivity.COPY_DATA_SUCCESS:
				if (mDataEng.JniM11IsExistSystemFile() == 1) {
					setCopySystemProgressDialog();
				} else {
					new AlertDialog.Builder(UpdateTopSystemActivity.this)
							.setTitle(getString(R.string.tv_tip_update_finish))
							.setMessage(
									getString(R.string.tv_tip_update_sucess))
							.setCancelable(false)
							.setNegativeButton(
									getString(R.string.dial_btn_yes),
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub

										}
									}).show();
				}
				break;
			case UpdateTopSystemActivity.COPY_DATA_FAILD:
				new AlertDialog.Builder(UpdateTopSystemActivity.this)
						.setTitle(getString(R.string.tv_tip_update_finish))
						.setMessage(getString(R.string.tv_tip_copy_failed))
						.setCancelable(false)
						.setNegativeButton(getString(R.string.dial_btn_yes),
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
				break;
			case UpdateTopSystemActivity.COPY_SYSTEM_FAILD:
				new AlertDialog.Builder(UpdateTopSystemActivity.this)
						.setTitle(getString(R.string.tv_tip_update_finish))
						.setMessage(
								getString(R.string.tv_tip_data_prepare_failed))
						.setCancelable(false)
						.setNegativeButton(getString(R.string.dial_btn_yes),
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).show();
				break;
			case UpdateTopSystemActivity.COPY_SYSTEM_SUCCESS:
				update();
				break;
			case CHECK_LAYOUT:
				checkLayout();
				break;
			case FULL_PACKAGE_DOWNLOAD:
				//myBindService(downloadDataBean1.full_path);
				downloadPath = downloadDataBean1.full_path;
				setNewDownloadProgressDialog(download);
				break;
			case INCREMENTAL_PACKAGE_DOWNLOAD:
				//myBindService(downloadDataBean1.incremental_path);
				downloadPath = downloadDataBean1.incremental_path;
				setNewDownloadProgressDialog(download);
				break;
			case CURRENTLY_IS_LATEST_VERSION:
				Toast.makeText(
						getApplicationContext(),
						getResources().getString(
								R.string.currently_is_latest_version),
						Toast.LENGTH_LONG).show();
				break;
			case START_DOWNLOAD:
				// 弹出进度对话框并设置进度
				copyFailureDialog = new CopyFailureDialog(
						UpdateTopSystemActivity.this, R.style.dialog);
				copyFailureDialog.show();
				copyFailureDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						// TODO Auto-generated method stub
						// 停止下载
						try {
							Log.e(TAG, "stopstopstopstopstop");
							Log.e(TAG, myDownload+"");
							Log.e(TAG, downloadPath);
							myDownload.stop(TDCard + "/update_temp.zip");
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				break;
			case PROGRESS_UPDATE:
				Bundle b = msg.getData();
				int progress = b.getInt("progress");
				copyFailureDialog.setProgress(progress);
				copyFailureDialog.setMessage(getResources().getString(
						R.string.tv_tip_update_on_going1));
				break;
			case DOWNLOAD_FINISH:
				File file = new File(TDCard + "/update.zip");
				if (file.exists()) {
					String fileMD5 = MD5.getMD5(file);
					if(fileMD5.equals(downloadDataBean1.full_md5)
							|| fileMD5.equals(downloadDataBean1.incremental_md5)){
						checkLayout();
						flag_is_exist_native_data |= TD_EXIST_SYSTEM_DATA;
					}
					else{
						file.delete();
						Toast.makeText(
								UpdateTopSystemActivity.this,
								getResources().getString(
								R.string.file_download_fauily),
								Toast.LENGTH_LONG).show();
					}
				}
				copyFailureDialog.dismiss();
				break;
			case DOWNLOAD_EXCEPTION:
				Log.e(TAG, "DOWNLOAD EXCEPTION ");
				try {
					Log.e(TAG, "stopstopstopstopstop");
					Log.e(TAG, myDownload+"");
					Log.e(TAG, downloadPath);
					myDownload.stop(TDCard + "/update_temp.zip");
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				copyFailureDialog.dismiss();
				break;
			}
		}
	};

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_update:
			// 存在本地文件
			Log.e(TAG, "ONCLICK");
			if (flag_is_exist_native_data > 0) {
				if (mDataEng.JniM11IsExistDataFile() == 1 || IsExistSystemFile(TDCard + "/D.img")) {
					setCopyDataProgressDialog();
				}
				else if(IsExistSystemFile(TDCard + "/update.img")){
					if(flag_is_checked){
						try {
							MyRecoverySysten.rebootDataCache(this, new File(TDCard+"/update.img"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						try {
							MyRecoverySysten.installPackage(this, new File(TDCard+"/update.img"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else if(IsExistSystemFile(TFCard + "/update.img")){
					if(flag_is_checked){
						try {
							MyRecoverySysten.rebootDataCache(this, new File(TFCard+"/update.img"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						try {
							MyRecoverySysten.installPackage(this, new File(TFCard+"/update.img"));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}else if (IsExistSystemFile(TDCard + "/update.zip")
						|| IsExistSystemFile(TFCard + "/update.zip")) {
					setCopySystemProgressDialog();
				}
			}
			// 否则不存在本地文件
			else {
				NetworkAvailable networkAvailable = new NetworkAvailable();
				if (networkAvailable.isNetworkAvailable(this)) {
					if(result == null && account != null){
						try {
							token = account.getToken();
							if(token != null){
								requestLatestVersion();
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					checkInterUpdateFiles();
				} else {
					setToast(getString(R.string.No_networks_available));
				}
			}
			break;
		case R.id.btn_back:
		case R.id.btn_cancel:
			finish();
			break;
		}
	}

	private void setCopySystemProgressDialog() {

		mpDialog = new ProgressDialog(UpdateTopSystemActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mpDialog.setTitle(getString(R.string.tv_tip_warm));
		mpDialog.setMessage(getString(R.string.tv_tip_update_on_going));
		mpDialog.setIndeterminate(false);
		mpDialog.setCancelable(false);
		new Thread() {
			public void run() {
				int ret;
				ret = 1;
				if (ret == 1) {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_SYSTEM_SUCCESS;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				} else {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_SYSTEM_FAILD;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				}
			}
		}.start();
		mpDialog.show();
	}

	private void setDownloadProgressDialog() {
		mpDialog = new ProgressDialog(UpdateTopSystemActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mpDialog.setTitle(getString(R.string.tv_tip_warm));
		mpDialog.setMessage(getString(R.string.tv_tip_update_on_going));
		mpDialog.setIndeterminate(false);
		mpDialog.setCancelable(false);
		new Thread() {
			public void run() {
				int ret;
				ret = 1;
				if (ret == 1) {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_SYSTEM_SUCCESS;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				} else {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_SYSTEM_FAILD;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				}
			}
		}.start();
		mpDialog.show();
	}

	private void setCopyDataProgressDialog() {

		mpDialog = new ProgressDialog(UpdateTopSystemActivity.this);
		mpDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mpDialog.setTitle(getString(R.string.tv_tip_warm));
		mpDialog.setMessage(getString(R.string.tv_tip_update_on_going));
		mpDialog.setIndeterminate(false); //
		mpDialog.setCancelable(false);
		new Thread() {
			public void run() {
				if (mDataEng.JniM11CopyDataFile() == 1 && (flag_is_exist_native_data & TF_EXIST_D_DATA)!=0) {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_DATA_SUCCESS;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				} 
				else if(mDataEng.JniM11CopyInternalDataFile() == 1 && (flag_is_exist_native_data & TD_EXIST_D_DATA)!=0){
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_DATA_SUCCESS;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				}
				else {
					mpDialog.cancel();
					Message msg = new Message();
					msg.what = UpdateTopSystemActivity.COPY_DATA_FAILD;
					UpdateTopSystemActivity.this.myHandler.sendMessage(msg);
				}
			}
		}.start();
		mpDialog.show();
		mpDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if(IsExistSystemFile(TFCard + "/update.img")){
					if(flag_is_checked){
						try {
							MyRecoverySysten.rebootDataCache(UpdateTopSystemActivity.this, new File(TFCard+"/update.img"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						try {
							MyRecoverySysten.installPackage(UpdateTopSystemActivity.this, new File(TFCard+"/update.img"));
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} 
				else if (IsExistSystemFile(TDCard + "/update.zip")
						|| IsExistSystemFile(TFCard + "/update.zip")) {
					setCopySystemProgressDialog();
				}
			}
		});
	}

	private void setNewDownloadProgressDialog(final IDownload download) {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				UpdateTopSystemActivity.this);
		builder.setTitle(R.string.tv_tip_warm)
				.setMessage(R.string.discovering_new_version)
				.setNegativeButton(R.string.btn_quit,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
							}
						})
				.setPositiveButton(R.string.btn_yes,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									download.download(TDCard + "/update_temp.zip", downloadPath, new OnNewDownloadListener(downloadPath){
										public void onFinish() throws android.os.RemoteException {
											Message msg = new Message();
											File tempfile = new File(TDCard + "/update_temp.zip");
											String fileMD5 = MD5.getMD5(tempfile);
											if(fileMD5.equals(downloadDataBean1.full_md5) || fileMD5.equals(downloadDataBean1.incremental_md5)){
												File oldf = new File(TDCard
														+ "/update_temp.zip");
												  File newf = new File(TDCard + "/update.zip"); 
												  if(newf.exists()){
													  newf.delete();
												  }
												  oldf.renameTo(newf); 
												  msg.what= DOWNLOAD_FINISH;
												  myHandler.sendMessage(msg);
											}else{
												tempfile.delete();
												copyFailureDialog.dismiss();
												Toast.makeText(
														UpdateTopSystemActivity.this,
														getResources().getString(
														R.string.file_download_fauily),
														Toast.LENGTH_LONG).show();
											}
										};
										public void progress(long postion, long length) throws android.os.RemoteException {
											Message msg = new Message();
											Bundle b = new Bundle();
											b.putInt("progress",
													(int) (postion * 1000 / length));
											msg.setData(b);
											msg.what = PROGRESS_UPDATE;
											myHandler.sendMessage(msg);
										};
										public void onException() throws android.os.RemoteException {
											Message msg = new Message();
											msg.what = DOWNLOAD_EXCEPTION;
											myHandler.sendMessage(msg);
										};
									});
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								Message msg = new Message();
								msg.what = START_DOWNLOAD;
								myHandler.sendMessage(msg);
							}
						});
		Dialog dialog = builder.create();
		dialog.show();
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				btn_update.setClickable(true);
			}
		});
	}

	private void setFinishProgressDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				UpdateTopSystemActivity.this);
		builder.setCancelable(false);
		builder.setTitle(R.string.tv_tip_warm)
				.setMessage(R.string.download_finish)
				.setNegativeButton(R.string.btn_quit,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								checkLayout();
							}
						})
				.setPositiveButton(R.string.btn_yes,
						new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								update();
							}
						}).show();
	}

	@SuppressLint("NewApi")
	private void update() {
		File file;
		if (IsExistSystemFile(TDCard + "/update.zip")) {
			file = new File(TDCard + "/update.zip");
		} else if (IsExistSystemFile(TFCard + "/update.zip")) {
			file = new File(TFCard + "/update.zip");
		} else {
			setToast(getString(R.string.tv_native_data_no_exist));
			return;
		}
		try {
			if(flag_is_checked){
				MyRecoverySysten.rebootDataCache(UpdateTopSystemActivity.this, file);
				//RecoverySystem.installPackage(UpdateTopSystemActivity.this, file);
				//RecoverySystem.rebootWipeCache(UpdateTopSystemActivity.this);
			}else{
				//RecoverySystem.installPackage(UpdateTopSystemActivity.this, file);
				MyRecoverySysten.installPackage(UpdateTopSystemActivity.this, file);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void requestLatestVersion(){
		new Thread(){
			public void run(){
				String url = "http://" + pathVersion;
				Map<String, String> params = new HashMap<String, String>();
				String version = getResources().getString(R.string.version);
				String nonc = System.currentTimeMillis() + "";
				String signature = getSignature(token, nonc, version);
				String machine = machine_no+"";
				JSONObject param = new JSONObject();
				
				try {
					param.put("machine", machine);
					param.put("serial_number", serial_number);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				params.put("param", param.toString());
				params.put("nonc", nonc);
				params.put("signature", signature);
				params.put("token", token);
				try {
					flag_is_exist_service_data = true;
					result = HttpRequest.httpRequest(url, params);
					Log.e(TAG, "result:"+result);
				} catch (Exception e) {
					flag_is_exist_service_data = false;
					e.printStackTrace();
				}
			}
		}.start();
	}

	
	
	private void setToast(String text) {
		Toast toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	// get MAC address
	private void getMacInfo() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		textview_status.setTextColor(Color.YELLOW);
		textview_status.setText(info.getMacAddress());
	}
	
	// 根据本地文件选择布局
	private void checkLayout() {
		if (Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			if (checkUpdateFiles()) {
				rl_explanation.setVisibility(View.VISIBLE);
				bg_img.setBackgroundResource(R.drawable.happy);
				tv_step.setText(R.string.btn_copySystem);
				tv_data_is_exist.setText(R.string.tv_native_data_exist);
				rl_content.setVisibility(View.GONE);
				rl_update.setVisibility(View.VISIBLE);
			} else {
				rl_explanation.setVisibility(View.GONE);
				bg_img.setBackgroundResource(R.drawable.cry);
				tv_step.setText(R.string.tv_online_uadate);
				tv_data_is_exist.setText(R.string.tv_native_data_no_exist);
				rl_content.setVisibility(View.VISIBLE);
				rl_update.setVisibility(View.VISIBLE);
			}
		} else {
			rl_explanation.setVisibility(View.GONE);
			flag_is_exist_native_data = SDCARD_IS_UNMOUNT;
			bg_img.setBackgroundResource(R.drawable.cry);
			rl_update.setVisibility(View.GONE);
			tv_data_is_exist.setText(getString(R.string.sd_unmount));
			rl_content.setVisibility(View.GONE);
		}
	}

	// 比较本地版本和服务器版本
	private int versionComparison(String location, String internet) {
		int version1 = Integer.parseInt(location.substring(1, 3));
		int version2 = Integer.parseInt(internet.substring(1, 3));
		return version2 - version1;
	}
	
	private void deleteUpdateTemp() {
		File fileTemp = new File(TDCard + "/update_temp.zip");
		fileTemp.delete();
		File fileRecording = new File(TDCard + "/update_temp.zip.dwn");
		if (fileRecording.exists()) {
			fileRecording.delete();
		}
	}

	private void jsonAnalysis(String str, DownloadDataBean1 downloadDataBean1) {
		/*Log.e(TAG, str);
		Gson gson = new Gson();
		SystemVersion systemVersion = gson.fromJson(str, SystemVersion.class);
		downloadDataBean1 = systemVersion.systemVersion;*/
		
		try {
			JSONObject dataJson = new JSONObject(str);
			JSONObject systemVersion = dataJson.getJSONObject("systemVersion");
			if(systemVersion!=null){
				downloadDataBean1.createTime = systemVersion
					.getString("createTime");
				downloadDataBean1.data_md5 = systemVersion.getString("data_md5");
				downloadDataBean1.data_path = systemVersion.getString("data_path");
				downloadDataBean1.full_md5 = systemVersion.getString("full_md5");
				downloadDataBean1.full_path = systemVersion.getString("full_path");
				downloadDataBean1.id = systemVersion.getString("id");
				downloadDataBean1.incremental_md5 = systemVersion
						.getString("incremental_md5");
				downloadDataBean1.incremental_path = systemVersion
						.getString("incremental_path");
				downloadDataBean1.machine = systemVersion.getString("machine");
				downloadDataBean1.version = systemVersion.getString("version");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initEvent(){
		cb_formatting_data_partition.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					flag_is_checked = true;
				}else{
					flag_is_checked = false;
				}
			}
		});
	}
	
	// 加密算法
	public static String getSignature(String token, String nonc, String version) {
		String[] params = { token, nonc, version };
		Arrays.sort(params);
		StringBuilder builder = new StringBuilder();
		for (String param : params) {
			builder.append(param);
		}
		return MD5.getMD5(builder.toString().getBytes());
	}
	/**
	 * 获取文件的名字
	 * 
	 * @param path
	 * @return
	 **/
	public String getFileName(String path) {
		int index = path.lastIndexOf("/") + 1;
		String sdcardPath = Environment.getExternalStorageDirectory()
				.getAbsolutePath();
		return sdcardPath + "/" + path.substring(index);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			if(myDownload != null){
				myDownload.stop(TDCard + "/update_temp.zip");
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void myBindService() {
		Intent intent = new Intent("com.kuaxue.download.DownloadService");
		//Log.e(TAG, "myBindService:"+internetPath);
		bindService(intent, new ServiceConnection() {
			
			@Override
			public void onServiceDisconnected(ComponentName name) {}
			
			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				download = IDownload.Stub.asInterface(service);
				
				//downloadPath = internetPath;
				myDownload = download;
				Log.e(TAG, "bind:"+myDownload);
				//setNewDownloadProgressDialog(download);
			}
		},Context.BIND_AUTO_CREATE);
	}
	
	private void myBindAccount() {
		ServiceConnection connection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				account = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				account = Account.Stub.asInterface(service);
				try {
					token = account.getToken();
					if(token != null && !"mytoken".equals(token)){
						requestLatestVersion();
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Intent intent = new Intent("com.kuaxue.account.AccountService");
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}
}
