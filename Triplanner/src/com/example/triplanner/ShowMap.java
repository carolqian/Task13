package com.example.triplanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class ShowMap extends FragmentActivity {  	
	ProgressDialog pDialog;
    GoogleMap map;
    List<ArrayList<LatLng>> walks;
    List<ArrayList<LatLng>> buses;
    JSONArray array;
    TextView tv;

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showmap);
        map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maproute))
                .getMap();
        tv = (TextView) findViewById(R.id.textView1);
        new GetDirection().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflate = getMenuInflater();
    	inflate.inflate(R.menu.mapmenu, menu);
    	return true;
    }
    
    class GetDirection extends AsyncTask<String, String, String> {
		LatLng northeast;
		LatLng southwest;
		Geocoder geocoder = new Geocoder(ShowMap.this);
		int routeNum;
		TransitBean routeBean;
		
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowMap.this);
            pDialog.setMessage("Loading route. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... args) {
            Intent i = getIntent();

            try {
                String jsonOutput = i.getStringExtra("routes");
    			routeNum = i.getIntExtra("routeNum", 0);
    			
                JSONObject jsonObject = new JSONObject(jsonOutput);

                // routesArray contains ALL routes
                JSONArray routesArray = jsonObject.getJSONArray("routes");
                // Grab the first route
                JSONObject route = routesArray.getJSONObject(0);

                JSONObject bounds = route.getJSONObject("bounds");
                northeast = getBound(bounds.getJSONObject("northeast"));
                southwest = getBound(bounds.getJSONObject("southwest"));
                
                List<TransitBean> routes = TransitHelper.readTransit(jsonObject);
                routeBean = routes.get(routeNum);
            } catch (Exception e) {

            }
            return null;
        }
        
        private void displaySteps() {
        	walks = new ArrayList<ArrayList<LatLng>>();
        	buses = new ArrayList<ArrayList<LatLng>>();
            List<Step> steps = routeBean.getSteps();
            Log.d("map", "step size" + steps.size());
            int count = 0;
            for (int i = 0; i < steps.size(); i++) {
            	Step step = steps.get(i);
            	Log.d("map", "mode: " +  i + " " + step.getTravelMode());
            	Log.d("map", "i " + i);
            	if (step.getTravelMode().equals("WALKING")) {
            		String encode = step.getPolyline();
            		Log.d("map", "walk line: " + encode);
            		ArrayList<LatLng> line = decodePoly(encode);
            		Log.d("map", "walk line " + Arrays.toString(line.toArray()));
            		Log.d("map", "count " + ++count);
            		walks.add(line);
            	} else if (step.getTravelMode().equals("TRANSIT")) {
            		Log.d("map", "transit true");
            		buses.add(decodePoly(step.getPolyline()));
            		TransitDetail detail = step.getTransitDetail();
                    map.addMarker(new MarkerOptions()
                    .position(new LatLng(detail.getArrivalStopLoc()[0], detail.getArrivalStopLoc()[1]))
                    .title("Arrive at " + detail.getDepartureStopName())
                    .snippet("Line Num: "+detail.getLineNum()+" "+"Line Name: "+detail.getLineName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                   
                    map.addMarker(new MarkerOptions()
                    .position(new LatLng(detail.getDepartureStopLoc()[0], detail.getDepartureStopLoc()[1]))
                    .title("Depart from " + detail.getDepartureStopName())
                    .snippet("Line Num: "+detail.getLineNum()+" "+"Line Name: "+detail.getLineName())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//                    Log.d("map", "stop start" + Arrays.toString(routeBean.getStartLocation()));
//                    Log.d("map", "route start" + Arrays.toString(step.getStartLocation()));
            	}
            }
            Log.d("map", "walks size before: " + walks.size());
        }

        private LatLng getBound(JSONObject bound) throws JSONException {
        	LatLng point = new LatLng(bound.getDouble("lat"), bound.getDouble("lng"));
        	return point;
        }
        
        protected void onPostExecute(String file_url) {
//        	displayLine(polyz, Color.RED);
        	displaySteps();
        	Log.d("map", " walks size: " + walks.size());
        	for (int i = 0; i < walks.size(); i++) {
        		displayLine(walks.get(i), Color.BLACK);      		
        	}	
        	for (int i = 0; i < buses.size(); i++) {
        		displayLine(buses.get(i), Color.RED);
        	}
			
	        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(southwest, northeast), 50));	
//	        map.moveCamera(CameraUpdateFactory.newLatLngBounds(new LatLngBounds(
//	        		new LatLng(minLatitude, minLongitude), new LatLng(maxLatitude, maxLongitude)), 50));
//            Log.d("map", "start location: " + Arrays.toString(routeBean.getStartLocation()));
//            Log.d("map", "start location: " + routeBean.getStartAddr());
//            Log.d("map", "end location: " + Arrays.toString(routeBean.getEndLocation()));
     
	        
            Marker m = map.addMarker(new MarkerOptions()
            .position(new LatLng(routeBean.getStartLocation()[0], routeBean.getStartLocation()[1]))
            .title("Start Position")
            .snippet(routeBean.getStartAddr())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            );
            m.showInfoWindow();
            
            map.addMarker(new MarkerOptions()
            .position(new LatLng(routeBean.getEndLocation()[0], routeBean.getEndLocation()[1]))
            .title("End Position")
            .snippet(routeBean.getEndAddr())
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            
            setRouteText();
            
            pDialog.dismiss();		
        }
        
        private void setRouteText() {
			LinearLayout l = (LinearLayout) findViewById(R.id.list2);
			
			List<Step> steps = routeBean.getSteps();
			for (int i = 0; i < steps.size() - 1; i++) {
				Step step = steps.get(i);
				ImageView image = new ImageView(ShowMap.this);
				TextView t = new TextView(ShowMap.this);
				t.setTextSize(20);
				if (step.getTravelMode().equals("WALKING")) {
					image.setImageResource(R.drawable.walking_icon);
				} else if (step.getTravelMode().equals("TRANSIT")) {
					image.setImageResource(R.drawable.bus_icon);
					t.setText(step.getTransitDetail().getLineNum());
				}
				l.addView(image);
				l.addView(t);
				ImageView arrow = new ImageView(ShowMap.this);
				arrow.setImageResource(R.drawable.arrow);
				l.addView(arrow);
			}
			
			Step step = steps.get(steps.size() - 1);
			ImageView image = new ImageView(ShowMap.this);
			TextView t = new TextView(ShowMap.this);			
			if (step.getTravelMode().equals("WALKING")) {
				image.setImageResource(R.drawable.walking_icon);
			} else if (step.getTravelMode().equals("TRANSIT")) {
				image.setImageResource(R.drawable.bus_icon);
				t.setText(step.getTransitDetail().getLineNum());
			}
			l.addView(image);
			l.addView(t);	
			
			TextView tv = (TextView) findViewById(R.id.textView1);
			tv.setText(routeBean.toString());
		
        }
        
        private void displayLine(List<LatLng> line, int color) {
            for (int i = 0; i < line.size() - 1; i++) {
                LatLng src = line.get(i);
                LatLng dest = line.get(i + 1);
                Polyline polyline = map.addPolyline(new PolylineOptions()
                        .add(new LatLng(src.latitude, src.longitude),
                                new LatLng(dest.latitude, dest.longitude))
                        .width(2).color(color).geodesic(true)); 
            }
        }
        
        /* Method to decode polyline points */
        private ArrayList<LatLng> decodePoly(String encoded) {

            ArrayList<LatLng> poly = new ArrayList<LatLng>();
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
            }

            return poly;
        }
    }


}
