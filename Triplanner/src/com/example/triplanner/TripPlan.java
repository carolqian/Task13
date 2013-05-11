package com.example.triplanner;

import java.util.Calendar;
import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class TripPlan extends Activity implements OnClickListener, OnCheckedChangeListener { 
	private Button button1;  
	private EditText fromT;
	private EditText toT;
	private TimePicker timePicker;
	private Date date;
	public final static int NO_TIME = 0;
	public final static int DEPARTURE_TIME = 1;
	public final static int ARRIVAL_TIME = 2;
	private String startLoc = null;
	private String endLoc = null;
	private CheckBox startGps;
	private CheckBox endGps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.tripplan); 
	      
	      button1 = (Button) findViewById(R.id.button1); 	      		
	      
	      fromT = (EditText) findViewById(R.id.editText1);
	      toT = (EditText) findViewById(R.id.editText2);
	      timePicker = (TimePicker) findViewById(R.id.timePicker1);  
	      startGps = (CheckBox) findViewById(R.id.checkBox1);
	      endGps = (CheckBox) findViewById(R.id.checkBox2);
	      
	      SharedPreferences preferences = getSharedPreferences("triplanner", 0);
	      fromT.setText(preferences.getString("from", ""));
	      toT.setText(preferences.getString("to", ""));
	      Calendar cal = Calendar.getInstance();
	      cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
	      cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
	      date = cal.getTime();
	      
	      Intent intent = getIntent();
	      String startAddr = intent.getStringExtra("startAddr");
	      if (startAddr != null) {
	    	  String endAddr = intent.getStringExtra("endAddr");
	    	  startLoc = intent.getStringExtra("startLoc");
	    	  endLoc = intent.getStringExtra("endLoc");
	    	  
	    	  fromT.setText(startAddr);
	    	  toT.setText(endAddr);
	      }
	      
	      button1.setOnClickListener(this);	
	      
	      startGps.setOnCheckedChangeListener(this);
	      endGps.setOnCheckedChangeListener(this);
	} 
	
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences preferences = getSharedPreferences("triplanner", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("from", fromT.getEditableText().toString());
		editor.putString("to", toT.getEditableText().toString());
		editor.commit();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			Intent intent = new Intent(TripPlan.this, ShowRoute.class); 					
			
			setGPS(startGps.isChecked(), endGps.isChecked());							
			
			intent.putExtra("startLoc", startLoc);
			intent.putExtra("endLoc", endLoc);
			
			
			intent.putExtra("time", date.getTime());
			
	        RadioButton depart = (RadioButton) findViewById(R.id.radioButton1);
	        RadioButton arrive = (RadioButton) findViewById(R.id.radioButton2);
			int timeType;
	        if (!depart.isChecked() && !arrive.isChecked()) {
	        	timeType = NO_TIME;
	        } else if (depart.isChecked()) {
	        	timeType = DEPARTURE_TIME;
	        	intent.putExtra("time", date.getTime() / 1000);
	        } else {
	        	timeType = ARRIVAL_TIME;
	        	intent.putExtra("time", date.getTime() / 1000);
	        }
			intent.putExtra("timeType", timeType);
			TripPlan.this.startActivity(intent);	
		} 		
	}

	private void setGPS(boolean start, boolean end) {
		if (!start && !end) {
			startLoc = fromT.getEditableText().toString();
			endLoc = toT.getEditableText().toString();
			return;
		}
		
	      LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	      
	      LocationListener listener  = new LocationListener() {			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				Log.d("plan", "lat: " + location.getLatitude());
				Log.d("plan", "lon: " + location.getLongitude());
			}
		};
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
	      Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	      Loc laln = loc == null? new Loc() :new Loc(loc.getLatitude(), loc.getLongitude());
		
	      if (start) {
			startLoc = laln.latitude + "," + laln.longitude;
		} else {
			startLoc = fromT.getEditableText().toString();
		}
		
		if (end) {
			endLoc = laln.latitude + "," + laln.longitude;
		} else {
			endLoc = toT.getEditableText().toString();
		}
	}

	private static class Loc {
		double latitude;
		double longitude;
		
		public Loc () {
			latitude = 0;
			longitude = 0;
		}
		
		public Loc (double lat, double lon) {
			this.latitude = lat;
			this.longitude = lon;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (buttonView.getId() == startGps.getId()) {
			if (isChecked) {
				fromT.setEnabled(false);				
			} else {
				fromT.setEnabled(true);
			}
			
			
		} else if (buttonView.getId() == endGps.getId()) {
			if (isChecked) {
				toT.setEnabled(false);
			} else {
				toT.setEnabled(true);				
			}
		}
		
	}
	 
}
