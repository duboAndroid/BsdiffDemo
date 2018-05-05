package com.cundong.bsdiffdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cundong.utils.PatchUtils;
import com.itheima.bsdiffdemo.R;

import java.io.File;

public class MainActivity extends AppCompatActivity {
	public static final String	PATH				= Environment.getExternalStorageDirectory() + File.separator;
	private static final String	TAG					= "MainActivity";
	public static final String	APP_DEBUG_PATCH		= "app-debug.patch";
	public static final String	APP_DEBUG_NEW_APK	= "app-debug-new.apk";
	private TextView			mTv;
	private String				content				= "bsdiff更新-->2222";
	static {
		System.loadLibrary("ApkPatchLibrary");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTv = (TextView) findViewById(R.id.tv);
		mTv.setText(content);
	}

	public void bsdiffUpdate(View v) {
		File file = new File(Environment.getExternalStorageDirectory(), APP_DEBUG_PATCH);
		if (file.exists()) {
			final String oldApkPath =
					com.itheima.bsdiffdemo.ApkUtils.getSourceApkPath(MainActivity.this, "com.itheima.bsdiffdemo");

			final String newApkPath = PATH + APP_DEBUG_NEW_APK;

			final String patchPath = PATH + APP_DEBUG_PATCH;

			Log.i(TAG, "oldApkPath:" + oldApkPath);
			Log.i(TAG, "newApkPath:" + newApkPath);
			Log.i(TAG, "patchPath:" + patchPath);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("增量更新提示");
			builder.setMessage("是否更新使用bsdiff产生的补丁");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							// String oldApkPath, String newApkPath, String patchPath
							try {

								final int result = PatchUtils.patch(oldApkPath, newApkPath, patchPath);

								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (result == 0) {
											Toast.makeText(getApplicationContext(), "合并apk成功,请安装", Toast.LENGTH_SHORT)
													.show();
											com.itheima.bsdiffdemo.ApkUtils.installApk(MainActivity.this, newApkPath);
										} else {
											Toast.makeText(getApplicationContext(), "合并apk失败", Toast.LENGTH_SHORT)
													.show();
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
			});
			builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
		}
	}
}
