package com.example.triplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowRoute extends FragmentActivity {  
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		  super.onCreate(savedInstanceState);
//	        
//	      setContentView(R.layout.showroute); 
//	      Intent intent = new Intent(android.content.Intent.ACTION_VIEW, 
//	    		    Uri.parse("http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"));
//	    		startActivity(intent);
//	}
	
	ProgressDialog pDialog;
    GoogleMap map;
    List<LatLng> polyz;
    JSONArray array;

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showroute);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maproute))
                .getMap();
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DUBLIN, 15));
//        map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
        new GetDirection().execute();
    }

    class GetDirection extends AsyncTask<String, String, String> {
    	Address start;
    	Address end;
		LatLng northeast;
		LatLng southwest;
		Geocoder geocoder = new Geocoder(ShowRoute.this);
//		double minLatitude = Integer.MAX_VALUE;
//		double maxLatitude = Integer.MIN_VALUE;
//		double minLongitude = Integer.MAX_VALUE;
//		double maxLongitude = Integer.MIN_VALUE;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowRoute.this);
            pDialog.setMessage("Loading route. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            Intent i = getIntent();
            String startLocation = i.getStringExtra("startLoc");
            String endLocation = i.getStringExtra("endLoc");
//            String startLocation = "5000 Forbes Ave Pittsburgh PA";
//            String endLocation = "3609 Beechwood Blvd Pittsburgh PA";
            startLocation = startLocation.replace(" ", "+");
            endLocation = endLocation.replace(" ", "+");
            int timeType = i.getIntExtra("timeType", 0);
            String timeToken = "";
            switch (timeType) {
            	case TripPlan.NO_TIME:
            		break;
            	case TripPlan.DEPARTURE_TIME:
            		timeToken += "&departure_time=";
//            		timeToken += "1343605500";
            		timeToken += i.getLongExtra("time", 1337675679473L);
            		break;
            	case TripPlan.ARRIVAL_TIME:
            		timeToken += "&arrival_time=";
            		timeToken += i.getLongExtra("time", 1337675679473L);
            		break;
            }
            Log.d("route time:", timeToken);
            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation + "&destination=" + endLocation + "&sensor=false" + timeToken + "&mode=transit";
//               String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=5000+Forbes+Ave+Pittsburgh+PA&destination=3609+Beechwood+Blvd+Pittsburgh+PA&sensor=false&departure_time=1343605500&mode=transit";
                StringBuilder response = new StringBuilder();
            try {
                start = getAddress(startLocation);
                end = getAddress(endLocation);
                URL url = new URL(stringUrl);
                HttpURLConnection httpconn = (HttpURLConnection) url
                        .openConnection();
                if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(
                            new InputStreamReader(httpconn.getInputStream()),
                            8192);
                    String strLine = null;

                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }

                String jsonOutput = response.toString();

                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject bounds = route.getJSONObject("bounds");
                northeast = getBound(bounds.getJSONObject("northeast"));
                southwest = getBound(bounds.getJSONObject("southwest"));
                JSONObject poly = route.getJSONObject("overview_polyline");
                String polyline = poly.getString("points");
                polyz = decodePoly(polyline);
            } catch (Exception e) {

            }

            return null;

        }

        private LatLng getBound(JSONObject bound) throws JSONException {
        	LatLng point = new LatLng(bound.getDouble("lat"), bound.getDouble("lng"));
        	return point;
        }
        
        protected Address getAddress(String location) throws IOException {
        	List<Address> addresses = geocoder.getFromLocationName(location, 10);
        	if (addresses == null || addresses.size() < 1) return null;
        	return addresses.get(0);
        }
        
        protected void onPostExecute(String file_url) {
        	if (polyz == null) {
        		Log.d("route", "no result");
        		return;
        	}
            for (int i = 0; i < polyz.size() - 1; i++) {
                LatLng src = polyz.get(i);
                LatLng dest = polyz.get(i + 1);
                Polyline line = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude,                dest.longitude))
                        .width(2).color(Color.RED).geodesic(true));
                
                
            }
            pDialog.dismiss();
            
//			Log.d("map maxLatitude", maxLatitude + "");
//			Log.d("map minLatitude", minLatitude + "");
//			Log.d("map maxLongitude", maxLongitude + "");
//			Log.d("map minLongitude", minLongitude + "");			
			
	        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 50));	
//	        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
//	        		new LatLng(minLatitude, minLongitude), new LatLng(maxLatitude, maxLongitude)), 50));
            
            map.addMarker(new MarkerOptions()
            .position(new LatLng(start.getLatitude(), start.getLongitude()))
            .title("Start Position")
            .snippet(start.getAddressLine(0) == null? "": start.getAddressLine(0) ));
            map.addMarker(new MarkerOptions()
            .position(new LatLng(end.getLatitude(), end.getLongitude()))
            .title("End Position")
            .snippet(end.getAddressLine(0) == null? "": end.getAddressLine(0) ));
        }
        
        /* Method to decode polyline points */
        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<LatLng>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
                
//    			maxLatitude = Math.max(((double)lat / 1E5), maxLatitude);
//    			minLatitude = Math.min(((double)lat / 1E5), minLatitude);
//    			maxLongitude = Math.max(((double)lng / 1E5), maxLongitude);
//    			minLongitude = Math.min(((double)lng / 1E5), minLongitude);
            }

            return poly;
        }
    }


}
