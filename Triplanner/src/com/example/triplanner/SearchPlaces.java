package com.example.triplanner;

import java.io.IOException;
import java.util.List;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

@SuppressLint("NewApi")
public class SearchPlaces extends FragmentActivity {
	private GoogleMap mMap;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchplaces);
        setUpMapIfNeeded();
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                                .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // The Map is verified. It is now safe to manipulate the map.
            	GoogleMapOptions options = new GoogleMapOptions();
            	
            	LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            	LocationListener ll = new LocationListener() {
					
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
						LatLng laln = new LatLng(location.getLatitude(), location.getLongitude());
						CameraUpdate center = CameraUpdateFactory.newLatLng(laln);
						CameraUpdate zoom = CameraUpdateFactory.zoomTo(15);
						mMap.moveCamera(center);
						mMap.animateCamera(zoom);
						mMap.addMarker(new MarkerOptions()
				        .position(laln)
				        .title("Your position"));
					}
				};
            	
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

					List<Address> addresses = geocoder.getFromLocationName("pnc bank", 10);
					Log.d("map", "inconsole");
					if (addresses != null && addresses.size() > 0) {
						if (addresses.size() == 1) {
							Address address = addresses.get(0);
							LatLng laln = new LatLng(address.getLatitude(), address.getLongitude());			
			                CameraUpdate center=
			                        CameraUpdateFactory.newLatLng(laln);
			                    CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);
			
			                    mMap.moveCamera(center);
			                    mMap.animateCamera(zoom);
								mMap.addMarker(new MarkerOptions()
						        .position(laln)
						        .title(address.getFeatureName()));							
						} else {					
							Log.d("map size", "" + addresses.size());
							for (int i = 0; i < addresses.size(); i++) {
								Address address = addresses.get(i);
								Log.d("map", "add marker " + i);
								double lat = address.getLatitude();
								double lon = address.getLongitude();
								LatLng laln = new LatLng(lat, lon);
								maxLatitude = Math.max(lat, maxLatitude);
								minLatitude = Math.min(lat, minLatitude);
								maxLongitude = Math.max(lon, maxLongitude);
								minLongitude = Math.min(lon, minLongitude);
							
								
								mMap.addMarker(new MarkerOptions()
						        .position(laln)
						        .title(address.getFeatureName()));
							}
							Log.d("map maxLatitude", maxLatitude + "");
							Log.d("map minLatitude", minLatitude + "");
							Log.d("map maxLongitude", maxLongitude + "");
							Log.d("map minLongitude", minLongitude + "");			
							
							final double ia = minLatitude;
							final double ao = maxLongitude;
							final double aa = maxLatitude;
							final double io = minLongitude;
//							final double ia = 4042477.0;
//							final double ao = -7992284.0;
//							final double aa = 4044456.0;
//							final double io = -7794254.0;
//							CameraUpdate bound = CameraUpdateFactory.newLatLngBounds(new LatLngBounds( 
//									new LatLng(minLatitude, maxLongitude), new LatLng(maxLatitude, minLongitude)), 5);						
//							mMap.moveCamera(bound);
							
							mMap.setOnCameraChangeListener(new OnCameraChangeListener() {
							    @Override
							    public void onCameraChange(CameraPosition arg0) {
							        // Move camera.
							        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds( 
											new LatLng(ia, io), new LatLng(aa, ao)), 20));	
							        // Remove listener to prevent position reset on camera move.
							        mMap.setOnCameraChangeListener(null);
							    }
							});
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("error", e.getMessage());
				}
            }
        }
    }

}
