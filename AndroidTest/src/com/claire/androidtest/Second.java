package com.claire.androidtest;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Second extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.second);
		
		TextView tx = (TextView) findViewById(R.id.mytextview);
		tx.setText(getIntent().getExtras().getString("thetext"));
	}
}
