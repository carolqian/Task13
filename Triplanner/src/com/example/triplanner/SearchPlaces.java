package com.example.triplanner;

import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
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
        origin.setOnClickListener(this);
        destination.setOnClickListener(this);
    }
    
    
    
    
	@Override
	public void onClick(View v) {		
		if (v.getId() == R.id.button1) {
			Log.d("search", "in click");
			search = et.getEditableText().toString().trim();			
			setUpMapIfNeeded();
		} else if (v.getId() == R.id.button2) {
			if (availableAddresses == null || availableAddresses.size() == 0) {
				showNoAddress();
			} else {
				
			}
		} else if (v.getId() == R.id.button3) {
			if (availableAddresses == null || availableAddresses.size() == 0) {
				showNoAddress();
			} else {
				chooseAddress(true);
			}
		}
	}
	
	private void chooseAddress(boolean origin) {
		
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
            // The Map is verified. It is now safe to manipulate the map.
//				lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
//				Location lastLocation = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//				LatLng laln = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//
//                CameraUpdate center=
//                        CameraUpdateFactory.newLatLng(laln);
//                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
//
//                    mMap.moveCamera(center);
//                    mMap.animateCamera(zoom);
//					mMap.addMarker(new MarkerOptions()
//			        .position(laln)
//			        .title("Your position"));
			
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
				}
				if (addresses != null && addresses.size() > 0) {
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
						Log.d("map maxLatitude", maxLatitude + "");
						Log.d("map minLatitude", minLatitude + "");
						Log.d("map maxLongitude", maxLongitude + "");
						Log.d("map minLongitude", minLongitude + "");			
						
//						final double ia = minLatitude;
//						final double ao = maxLongitude;
//						final double aa = maxLatitude;
//						final double io = minLongitude;
//							final double ia = 4042477.0;
//							final double ao = -7992284.0;
//							final double aa = 4044456.0;
//							final double io = -7794254.0;
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
