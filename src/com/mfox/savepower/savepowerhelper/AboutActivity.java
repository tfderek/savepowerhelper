package com.mfox.savepower.savepowerhelper;

import com.mfox.savepower.R;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutActivity extends Activity {
	private ImageView iv_back;
	private TextView tv_version;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		tv_version=(TextView) findViewById(R.id.tv_version_info);
		PackageManager pm=getPackageManager();
		PackageInfo pkgInfo;
		try {
			pkgInfo = pm.getPackageInfo(getPackageName(), 0);
			String versionName=String.format(getResources().getString(R.string.version_info),pkgInfo.versionName);
			tv_version.setText(versionName);			
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AboutActivity.this.finish();
			}
		});
	}
}
