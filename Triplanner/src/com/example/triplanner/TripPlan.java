package com.example.triplanner;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class TripPlan extends Activity implements OnClickListener { 
	private Button button1;  
	
//	private TextView from;
//	private TextView to;
	private EditText fromT;
	private EditText toT;
	private TimePicker timePicker;
	private Date date;
	public final static int NO_TIME = 0;
	public final static int DEPARTURE_TIME = 1;
	public final static int ARRIVAL_TIME = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.tripplan); 
	      
	      button1 = (Button) findViewById(R.id.button1); 	      
	      
//	      from = new TextView(this); 
//	      from = (TextView)findViewById(R.id.TextView01); 
//	      from.setText("From:");
//	      
//	      to = new TextView(this); 
//	      to = (TextView)findViewById(R.id.TextView02); 
//	      to.setText("To:");		
	      
	      fromT = (EditText) findViewById(R.id.editText1);
	      toT = (EditText) findViewById(R.id.editText2);
	      timePicker = (TimePicker) findViewById(R.id.timePicker1);
	      
	      SharedPreferences preferences = getSharedPreferences("triplanner", 0);
	      fromT.setText(preferences.getString("from", ""));
	      toT.setText(preferences.getString("to", ""));
	      Calendar cal = Calendar.getInstance();
	      cal.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
	      cal.set(Calendar.MINUTE, timePicker.getCurrentMinute());
	      date = cal.getTime();
	      
	      button1.setOnClickListener(this);
		
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
			intent.putExtra("startLoc", fromT.getEditableText().toString());
			intent.putExtra("endLoc", toT.getEditableText().toString());
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
	 
}
