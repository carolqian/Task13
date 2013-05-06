package com.example.triplanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class ShowTableRow extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.showtablerow); 
		
	} 
	
	public void onClick(View v) {
		Intent intent = new Intent(ShowTableRow.this, ShowTableColon.class); 
		ShowTableRow.this.startActivity(intent);	

         
      } 
}
