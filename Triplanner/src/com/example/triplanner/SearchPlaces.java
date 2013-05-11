package com.example.triplanner;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

@SuppressLint("NewApi")
public class SearchPlaces extends FragmentActivity implements OnClickListener {
	private GoogleMap mMap;
	private String search;
	private EditText et;
	private Button button;
	private List<Address> availableAddresses;
	
	private Button origin;
	private Button destination;
	
	Location ori;
	Location des;
	private Button plan;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchplaces);
        button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(this);
        
	    SharedPreferences preferences = getSharedPreferences("triplanner", 0);
        et = (EditText) findViewById(R.id.editText1);
        et.setText(preferences.getString("search", ""));
        
        origin = (Button) findViewById(R.id.button2);
        destination = (Button) findViewById(R.id.button3);
        
        plan = (Button)findViewById(R.id.plan);
        plan.setEnabled(false);
        ori = null;
        des = null;
    }
    
    
    
    
	@Override
	public void onClick(View v) {		
		if (v.getId() == R.id.button1) {
			Log.d("search", "in click");
			search = et.getEditableText().toString().trim();	
			Log.d("search", "search :" + search);
			setUpMapIfNeeded();
		}
	}

	
	private void showNoAddress() {
		AlertDialog.Builder builder = new AlertDialog.Builder(SearchPlaces.this);
        builder.setTitle("No Address Available")
        	   .setMessage("There is no address available now. Please go back and search a valid address.")
               .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       dialog.dismiss();
                   }
               });
        AlertDialog dialog = builder.create();
        dialog.show();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		SharedPreferences preferences = getSharedPreferences("triplanner", 0);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("search", et.getEditableText().toString());
		editor.commit();
	}
    
	
	/** zhimeng
	 * Result From SearchPlacesDetail.java */
    @Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		int decision = arg2.getIntExtra("decision", -1);
		if(decision != -1){
			switch(decision){
			case SearchPlacesDetail.DECISION_ORI:
				ori = new Location(
						arg2.getStringExtra("title"),
						arg2.getStringExtra("snippet"),
						arg2.getDoubleExtra("lat", 0),
						arg2.getDoubleExtra("lon", 0));
				origin.setText("Origin Set.(X)");
				origin.setVisibility(View.VISIBLE);
				origin.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						ori = null;
						origin.setVisibility(View.INVISIBLE);
						plan.setEnabled(false);
					}
				});
				break;
			case SearchPlacesDetail.DECISION_DES:
				des = new Location(
						arg2.getStringExtra("title"),
						arg2.getStringExtra("snippet"),
						arg2.getDoubleExtra("lat", 0),
						arg2.getDoubleExtra("lon", 0));
				destination.setText("Destination Set.(X)");
				destination.setVisibility(View.VISIBLE);
				destination.setOnClickListener(new Button.OnClickListener(){
					@Override
					public void onClick(View v) {
						des = null;
						destination.setVisibility(View.INVISIBLE);
						plan.setEnabled(false);
					}
				});
				break;
			}
			if(ori != null && des != null){
				plan.setEnabled(true);
				plan.setOnClickListener(new Button.OnClickListener(){

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.d("AHA","I MADE IT");
						Intent intent = new Intent(SearchPlaces.this, TripPlan.class);
						intent.putExtra("startAddr", ori.snippet);
						intent.putExtra("endAddr", des.snippet);
						intent.putExtra("startLoc", ori.lat + ", " + ori.lon);
						intent.putExtra("endLoc", des.lat + ", " + des.lon);
						startActivity(intent);
					}
				});
			}
		}
		
	}
    /** zhimeng: Location info */
    public static class Location{
    	public String title;
    	public String snippet;
    	public double lat;
    	public double lon;
		public Location(String title, String snippet, double lat, double lon) {
			super();
			this.title = title;
			this.snippet = snippet;
			this.lat = lat;
			this.lon = lon;
		}
    }

    
    
	private void setUpMapIfNeeded() {
    	if (search == null || search.length() < 1) {
    		Log.d("search", "not get search term");
    		return;
    	}
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                                .getMap();
        }
            // Check if we were successful in obtaining the map.
        if (mMap != null) {
        	mMap.clear();
        	mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
				
				@Override
				public void onInfoWindowClick(Marker marker) {
					String title = marker.getTitle();
					String snippet = marker.getSnippet();
					double lat = marker.getPosition().latitude;
					double lon = marker.getPosition().longitude;
					Intent intent = new Intent(getApplicationContext(),SearchPlacesDetail.class);
					intent.putExtra("title", title);
					intent.putExtra("snippet",snippet);
					intent.putExtra("lat", lat);
					intent.putExtra("lon", lon);
					startActivityForResult(intent, 0);
				}
			});
			
			Geocoder geocoder = new Geocoder(this);
			try {
				double minLatitude = Integer.MAX_VALUE;
				double maxLatitude = Integer.MIN_VALUE;
				double minLongitude = Integer.MAX_VALUE;
				double maxLongitude = Integer.MIN_VALUE;

				List<Address> addresses = geocoder.getFromLocationName(search, 10);
				availableAddresses = addresses;
				Log.d("map", "inconsole");
				if (addresses == null || addresses.size() == 0) {
					showNoAddress();
				} else {
					if (addresses.size() == 1) {
						Address address = addresses.get(0);
						StringBuilder sb = new StringBuilder();
						for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
							sb.append(" ");
							sb.append(address.getAddressLine(j));
						}
						
						LatLng laln = new LatLng(address.getLatitude(), address.getLongitude());			
		                CameraUpdate center=
		                        CameraUpdateFactory.newLatLng(laln);
		                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
		
		                    mMap.moveCamera(center);
		                    mMap.animateCamera(zoom);
							mMap.addMarker(new MarkerOptions()
					        .position(laln)
					        .title(address.getFeatureName())
					        .snippet(sb.toString()));	
						
					} else {					
						Log.d("map size", "" + addresses.size());
						for (int i = 0; i < addresses.size(); i++) {
							Address address = addresses.get(i);
							Log.d("map", "add marker " + i);
							StringBuilder sb = new StringBuilder();
							for (int j = 0; j < address.getMaxAddressLineIndex(); j++) {
								sb.append(address.getAddressLine(j));
							}
							
							double lat = address.getLatitude();
							double lon = address.getLongitude();
							LatLng laln = new LatLng(lat, lon);
							maxLatitude = Math.max(lat, maxLatitude);
							minLatitude = Math.min(lat, minLatitude);
							maxLongitude = Math.max(lon, maxLongitude);
							minLongitude = Math.min(lon, minLongitude);
						
							
							mMap.addMarker(new MarkerOptions()
					        .position(laln)
					        .title(address.getFeatureName())
					        .snippet(sb.toString()));
						}	
						
						CameraUpdate bound = CameraUpdateFactory.newLatLngBounds(new LatLngBounds( 
								new LatLng(minLatitude, minLongitude), new LatLng(maxLatitude, maxLongitude)), 50);						
						mMap.moveCamera(bound);
//							
//						mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
//						    @Override
//						    public void onCameraChange(CameraPosition arg0) {
//						        // Move camera.
//						        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds( 
//										new LatLng(ia, io), new LatLng(aa, ao)), 20));	
//						        // Remove listener to prevent position reset on camera move.
//						        mMap.setOnCameraChangeListener(null);
//						    }
//						});
					}
				}
			} catch (IOException e) {
				Log.d("error", e.getMessage());
			}
            
        }
    }





}
