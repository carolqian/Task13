package com.example.triplanner;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends Activity {
	private WebView webView; 
	private Button button1;
	private Button button2;
	private Button button3;
	private Button button4;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://www.google.com");

		button1 = (Button) findViewById(R.id.button1); 
		button2 = (Button) findViewById(R.id.button2); 
		button3 = (Button) findViewById(R.id.button3); 
		button4 = (Button) findViewById(R.id.button4); 
		
		addListenerOnButton();
	}

public void addListenerOnButton(){
    	
    	
    	
		button1.setOnClickListener(new OnClickListener() {
            
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MainActivity.this, TripPlan.class); 
				MainActivity.this.startActivity(intent);				
		        
 
			}
 
		});
		
		button2.setOnClickListener(new OnClickListener() {
            
			@Override
			public void onClick(View arg0) {
   
				Intent intent = new Intent(MainActivity.this, BusSchedule.class); 
				MainActivity.this.startActivity(intent);		
 
			}
 
		});
		button3.setOnClickListener(new OnClickListener() {
            
			@Override
			public void onClick(View arg0) {
   
				Intent intent = new Intent(MainActivity.this, ShowStation.class); 
				MainActivity.this.startActivity(intent);		
 
			}
 
		});
		button4.setOnClickListener(new OnClickListener() {
    
		@Override
		public void onClick(View arg0) {
			Intent intent = new Intent(MainActivity.this, SearchPlaces.class); 
			MainActivity.this.startActivity(intent);			
	        
	
		}

		});
    }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
