package com.android.UpdateTopSystem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CopyFailureDialog extends Dialog {

	private Dialog dialog;
	private Context context;
	private ProgressBar my_progress;
	private TextView tv_message;
	private TextView tv_progress_data;
	private ImageView iv_quit;
	OnSubmit submit;

	public CopyFailureDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceStates) {
		// TODO Auto-generated method stub
		super.onCreate(onSaveInstanceState());
		this.setCancelable(false);// 设置点击屏幕Dialog不消失
		setContentView(R.layout.copy_failure);
		dialog = this;
		findView();
		quit();
	}

	public interface OnSubmit {
	}

	public void setSubmit(OnSubmit submit) {
		this.submit = submit;
	}

	public void setMessage(String message) {
		tv_message.setText(message);
	};

	public void setProgress(int progress) {
		my_progress.setProgress(progress);
		tv_progress_data.setText(progress / 10 + "%");
	};

	public void quit() {	
		iv_quit.setOnClickListener(new android.view.View.OnClickListener
		() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
	}

	private void findView() {
		my_progress = (ProgressBar) findViewById(R.id.my_progress);
		tv_message = (TextView) findViewById(R.id.tv_message);
		tv_progress_data = (TextView) findViewById(R.id.tv_progress_data);
		iv_quit = (ImageView) findViewById(R.id.quit);
	}
}
