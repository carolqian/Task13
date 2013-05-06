package com.example.triplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BusSchedule extends Activity {
	private TextView tv;
	private TextView tv1;
	private Button button1;
	private Button button2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.schedule); 
	      
	      tv1 = new TextView(this); 
		  tv1 = (TextView)findViewById(R.id.TextView01); 
		  tv1.setText("BUS:");
		  
		   
		  tv = new TextView(this); 
		  tv = (TextView)findViewById(R.id.textView2); 
		  tv.setText("BUS:");
		
		  button1 = (Button)findViewById(R.id.button1); 
		  
		  button1.setOnClickListener(new OnClickListener() {
	            
				@Override
				public void onClick(View arg0) {
					//tv.setText("Route 1:  20mins 9 stops\n 1.XXXXXXX \n 2.XXXXXXX"); 
					final String[] busData = new String[] { "Peebles Rd", "Freguson Rd", "Mt Royal Blvd",
						"Grant Ave", "Washington Blvd", "East Liberty Station", "Penn Station", "Liberty Ave"
						 };
					Intent intent = new Intent(BusSchedule.this, ShowTable.class); 
					
					Bundle bundle =new Bundle();
					bundle.putStringArray("tablestring",busData); 
			        intent.putExtras(bundle); 
			        
					BusSchedule.this.startActivity(intent);	
	 
				}
	 
			});
		  
          button2 = (Button)findViewById(R.id.Button01); 
		  
		  button2.setOnClickListener(new OnClickListener() {
	            
				@Override
				public void onClick(View arg0) {
					//tv.setText("Route 1:  20mins 9 stops\n 1.XXXXXXX \n 2.XXXXXXX"); 
			        
					Intent intent = new Intent(BusSchedule.this, ShowTableRow.class); 
					BusSchedule.this.startActivity(intent);	
	 
				}
	 
			});
	} 


}
