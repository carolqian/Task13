package com.example.triplanner;





import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class TripPlan extends Activity{ 
	private Button button1;  
	
	private TextView from;
	private TextView to;
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
	      
	    
		  
		  
	      button1.setOnClickListener(new OnClickListener() {
	            
				@Override
				public void onClick(View arg0) {
					//tv.setText("Route 1:  20mins 9 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					//tv2.setText("Route 2:  30mins 10 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					//tv3.setText("Route 3:  35mins 12 stops\n 1.XXXXXXX \n 2.XXXXXXX");
					Intent intent = new Intent(TripPlan.this, ShowRoute.class); 
					TripPlan.this.startActivity(intent);	
	 
				}
	 
			});
		
	} 
	 
}
