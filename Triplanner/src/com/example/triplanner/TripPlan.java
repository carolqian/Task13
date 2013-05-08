package com.example.triplanner;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

public class TripPlan extends Activity{ 
	private Button button1;  
	
	private TextView from;
	private TextView to;
	private EditText fromT;
	private EditText toT;
	private TimePicker timePicker;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.tripplan); 
	      
	      button1 = (Button) findViewById(R.id.button1); 	      
	      
	      from = new TextView(this); 
	      from = (TextView)findViewById(R.id.TextView01); 
	      from.setText("From:");
	      
	      to = new TextView(this); 
	      to = (TextView)findViewById(R.id.TextView02); 
	      to.setText("To:");		
	      
	      fromT = (EditText) findViewById(R.id.editText1);
	      toT = (EditText) findViewById(R.id.editText2);
	      timePicker = (TimePicker) findViewById(R.id.timePicker1);
	      
	      SharedPreferences preferences = getSharedPreferences("triplanner", 0);
	      fromT.setText(preferences.getString("from", ""));
	      toT.setText(preferences.getString("to", ""));
		  
	      button1.setOnClickListener(new OnClickListener() {
	            
				@Override
				public void onClick(View arg0) {
					//tv.setText("Route 1:  20mins 9 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					//tv2.setText("Route 2:  30mins 10 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					//tv3.setText("Route 3:  35mins 12 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					Intent intent = new Intent(TripPlan.this, ShowRoute.class); 					
					intent.putExtra("startLoc", fromT.getEditableText().toString());
					intent.putExtra("endLoc", toT.getEditableText().toString());
					TripPlan.this.startActivity(intent);	
	 
				}
	 
			});
		
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
	 
}
