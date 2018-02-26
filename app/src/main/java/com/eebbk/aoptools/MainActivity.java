package com.eebbk.aoptools;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.eebbk.aoptools.annotations.DebugLog;

import java.util.Random;

/**
 * MainActivity
 */

public class MainActivity extends Activity implements View.OnClickListener{
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		findViewById(R.id.click1).setOnClickListener(this);
		findViewById(R.id.click2).setOnClickListener(this);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.click1:
				Log.e("hahaoop","click1");
				test();
				break;
			case R.id.click2:
				Log.e("hahaoop","click2");
				break;
		}
	}

	private void test(){
		Thread t = new Thread(new Runnable() {
			@DebugLog
			@Override
			public void run() {
				int i = new Random().nextInt(5);
				try {
					long start = System.currentTimeMillis();
					Thread.sleep(1000*i);
					Log.e("gaga","test run time :"+(System.currentTimeMillis()-start));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();
	}
}
