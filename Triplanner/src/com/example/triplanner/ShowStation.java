package com.example.triplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShowStation extends Activity {
	private String[] busData = {"71A","71B","71C","71D", "61B", "P3"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.showstation); 
		
	} 
	
	public void onClick(View v) {
		Intent intent = new Intent(ShowStation.this, ShowTable.class); 
		

		Bundle bundle =new Bundle();
		bundle.putStringArray("tablestring",busData); 
        intent.putExtras(bundle); 
        
		ShowStation.this.startActivity(intent);	

         
      } 
}