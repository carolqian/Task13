package com.example.triplanner;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;

public class ShowStation extends Activity {
	private String[] busData = {"71A","71B","71C","71D", "61B", "P3"};
	private double latitude;
	private double longitude;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.showstation); 
		
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
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		};
		
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
		
	} 
	
	public void onClick(View v) {
		Intent intent = new Intent(ShowStation.this, ShowTable.class); 
		

		Bundle bundle =new Bundle();
		bundle.putStringArray("tablestring",busData); 
        intent.putExtras(bundle); 
        
		ShowStation.this.startActivity(intent);	

         
      } 
}