package com.kit.newsSubscriber.news;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class StartActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Thread.sleep(1500L);
		} catch (InterruptedException ignored) {
		}
		goHome();
	}

	/**
	 * 跳转到主活动
	 */
	private void goHome() {
		Intent intent =new Intent(StartActivity.this, MainActivity.class);
		startActivity(intent);
		this.finish();
	}

}
