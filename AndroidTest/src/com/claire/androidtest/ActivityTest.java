package com.claire.androidtest;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ActivityTest extends Activity implements OnClickListener {

	private EditText et2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ConnectivityManager conman = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		TextView tv = (TextView) findViewById(R.id.mytextview);
		boolean wifi = conman.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
		if (wifi) {
			tv.setText("The wifi is on.");
		} else {
			tv.setText("The wifi is off.");
		}
		
		Log.d("TEST", "DEBUG");
		
		final EditText et = (EditText) findViewById(R.id.editText1);
		Button b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityTest.this, Second.class);
				intent.putExtra("thetext", et.getText().toString());
				startActivity(intent);
			}
		});
		
		ImageButton b2 = (ImageButton) findViewById(R.id.imageButton1);
		b2.setOnClickListener(this);
		
		Button b3 = (Button) findViewById(R.id.button3);
		registerForContextMenu(b3);
		
		Button b4 = (Button) findViewById(R.id.button4);
		b4.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				AlertDialog.Builder ab = new AlertDialog.Builder(ActivityTest.this);
				ab.setMessage("Are you sure you want to exit?");
				ab.setCancelable(false);
				ab.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ActivityTest.this.finish();
					}
				});
				
				ab.setNegativeButton("No", new DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();					
					}
				});
				
				AlertDialog alert = ab.create();
				alert.show();
			}
		});
		
		Button b5 = (Button) findViewById(R.id.button5);
		b5.setOnClickListener(this);
		
		Button b6 = (Button) findViewById(R.id.button6);
		b6.setOnClickListener(this);
		
		et2 = (EditText) findViewById(R.id.editText2);
		SharedPreferences sp = getSharedPreferences("preference", 0);
		et2.setText(sp.getString("preference", ""));
		
//		SQLiteDatabase db = openOrCreateDatabase("mydb", MODE_PRIVATE, null);
//		db.execSQL("create talbe user");
		
		Button b7 = (Button) findViewById(R.id.button7);
		b7.setOnClickListener(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences sp = getSharedPreferences("preference", 0);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString("preference", et2.getText().toString());
		editor.commit();
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.contextmenu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.imageButton1) {
			Log.d("image", "Image button is clicked.");
			Intent in = new Intent(this, ListTest.class);
			startActivity(in);
		} else if (v.getId() == R.id.button5) {
			ProgressDialog pd = new ProgressDialog(this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("Waiting...");
			pd.setIndeterminate(false);
			pd.setCancelable(true);
			pd.show();
		} else if (v.getId() == R.id.button6) {
			Intent it = new Intent(this, Settings.class);
			startActivity(it);
		} else if (v.getId() == R.id.button7) {
			Intent it = new Intent(this, Map.class);
			startActivity(it);
		}
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		TextView tv = (TextView) findViewById(R.id.textView1);
		if (item.getItemId() == R.id.item1) {
			tv.setText("Option 1 is clicked!");
		} else if (item.getItemId() == R.id.item2) {
			tv.setText("Option 2 is clicked!");
		}
		
		
		return super.onOptionsItemSelected(item);
	}
}
