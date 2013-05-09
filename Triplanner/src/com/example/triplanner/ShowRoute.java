package com.example.triplanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

public class ShowRoute extends Activity {
	  ProgressDialog pDialog;
	  private List<TransitBean> routes;
	  private String jsonOutput;
	  private String startLocation;
	  private String endLocation;
	  
	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.showroute);    
	    new GetRoute().execute();
	  }
	  
	   class GetRoute extends AsyncTask<String, String, String> {
			Geocoder geocoder = new Geocoder(ShowRoute.this);
			
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
	            startLocation = i.getStringExtra("startLoc");
	            endLocation = i.getStringExtra("endLoc");
//	            String startLocation = "5000 Forbes Ave Pittsburgh PA";
//	            String endLocation = "3609 Beechwood Blvd Pittsburgh PA";
	            startLocation = startLocation.replace(" ", "+");
	            endLocation = endLocation.replace(" ", "+");
	            int timeType = i.getIntExtra("timeType", 0);
	            String timeToken = "";
	            switch (timeType) {
	            	case TripPlan.NO_TIME:
	            		break;
	            	case TripPlan.DEPARTURE_TIME:
	            		timeToken += "&departure_time=";
//	            		timeToken += "1343605500";
	            		timeToken += i.getLongExtra("time", 1337675679473L);
	            		break;
	            	case TripPlan.ARRIVAL_TIME:
	            		timeToken += "&arrival_time=";
	            		timeToken += i.getLongExtra("time", 1337675679473L);
	            		break;
	            }
	            Log.d("route time:", timeToken);
	            String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=" + startLocation + "&destination=" + endLocation + "&sensor=false" + timeToken + "&mode=transit&alternatives=true";
//	               String stringUrl = "http://maps.googleapis.com/maps/api/directions/json?origin=5000+Forbes+Ave+Pittsburgh+PA&destination=3609+Beechwood+Blvd+Pittsburgh+PA&sensor=false&departure_time=1343605500&mode=transit";
	                StringBuilder response = new StringBuilder();
	            try {
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
	                jsonOutput = response.toString();

	                JSONObject jsonObject = new JSONObject(jsonOutput);
	                routes = TransitHelper.readTransit(jsonObject);
	            } catch (Exception e) {
	            	Log.d("error", e.getMessage());
	            }
	            return null;
	        }
	        
	    	private void showNoRoute() {
	    		AlertDialog.Builder builder = new AlertDialog.Builder(ShowRoute.this);
	            builder.setTitle("No Route Available")
	            	   .setMessage("There is no address available now. Please go back and search a valid address.")
	                   .setNegativeButton("Go back", new DialogInterface.OnClickListener() {
	                       public void onClick(DialogInterface dialog, int id) {
	                           dialog.dismiss();
	                       }
	                   });
	            AlertDialog dialog = builder.create();
	            dialog.show();
	    	}
	        
	        protected void onPostExecute(String file_url) {
	        	pDialog.dismiss();
	        	
	        	if (routes == null || routes.size() < 1) {
	        		showNoRoute();
	        	}	           
	            
//				Log.d("map maxLatitude", maxLatitude + "");
//				Log.d("map minLatitude", minLatitude + "");
//				Log.d("map maxLongitude", maxLongitude + "");
//				Log.d("map minLongitude", minLongitude + "");			
				
	        	final ArrayList<String> list = new ArrayList<String>();
	        	
	            for (TransitBean route : routes) {
	            	list.add(route.toString());
	            }
	            
	    	    final ListView listview = (ListView) findViewById(R.id.listView1);
	    	    
	    	    final RouteArrayAdapter adapter = new RouteArrayAdapter(ShowRoute.this,
	    	        android.R.layout.simple_list_item_1, list, routes);
	    	    listview.setAdapter(adapter);

//	    	    Log.d("route", "before setting");
	    	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	    		@Override
	    	      public void onItemClick(AdapterView<?> parent, final View view,
	    	          int position, long id) {
	    			Log.d("route", "item click");
	    	    	Intent in = new Intent(ShowRoute.this, ShowRouteDetail.class);
	    	    	in.putExtra("routes", jsonOutput);
	    	    	in.putExtra("routeNum", position);
	    			in.putExtra("startLoc", startLocation);
	    			in.putExtra("endLoc", endLocation);
	    	        ShowRoute.this.startActivity(in);
	    	      }

	    	    });
	        }
	    }
	   
		protected class RouteArrayAdapter extends ArrayAdapter<String> {

			HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
			List<TransitBean> routes;
			
			public RouteArrayAdapter(Context context, int textViewResourceId,
					List<String> objects, List<TransitBean> routes) {
				super(context, textViewResourceId, objects);
				for (int i = 0; i < objects.size(); ++i) {
					mIdMap.put(objects.get(i), i);
				}
				this.routes = routes;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View row = inflater.inflate(R.layout.route, parent, false);
				
				TextView time = (TextView) row.findViewById(R.id.textView2);
				TextView distance = (TextView) row.findViewById(R.id.textView4);
				TextView duration = (TextView) row.findViewById(R.id.textView6);
				LinearLayout l = (LinearLayout) row.findViewById(R.id.list);
				
				ArrayList<String> subroutes = new ArrayList<String>();
				TransitBean route = routes.get(position);
				List<Step> steps = route.getSteps();
				for (int i = 0; i < steps.size() - 1; i++) {
					Step step = steps.get(i);
					subroutes.add(step.toString());
					ImageView image = new ImageView(ShowRoute.this);
					TextView t = new TextView(ShowRoute.this);
					t.setTextSize(20);
					if (step.getTravelMode().equals("WALKING")) {
						image.setImageResource(R.drawable.walking_icon);
					} else if (step.getTravelMode().equals("TRANSIT")) {
						image.setImageResource(R.drawable.bus_icon);
						t.setText(step.getTransitDetail().getLineNum());
					}
					l.addView(image);
					l.addView(t);
					ImageView arrow = new ImageView(ShowRoute.this);
					arrow.setImageResource(R.drawable.arrow);
					l.addView(arrow);
				}
				
				Step step = steps.get(steps.size() - 1);
				ImageView image = new ImageView(ShowRoute.this);
				TextView t = new TextView(ShowRoute.this);			
				if (step.getTravelMode().equals("WALKING")) {
					image.setImageResource(R.drawable.walking_icon);
				} else if (step.getTravelMode().equals("TRANSIT")) {
					image.setImageResource(R.drawable.bus_icon);
					t.setText(step.getTransitDetail().getLineNum());
				}
				l.addView(image);
				l.addView(t);				
				
				time.setText(route.getDepartureTime() + " - " + route.getArrivalTime());
				distance.setText(route.getDistance());
				duration.setText(route.getDuration());
				
//	    	    final RouteSubArrayAdapter adapter = new RouteSubArrayAdapter(ShowRoute.this,
//		    	        android.R.layout.simple_list_item_1, subroutes, route);
//		    	list.setAdapter(adapter);
				
				return row;
			}
			
			@Override
			public long getItemId(int position) {
				String item = getItem(position);
				return mIdMap.get(item);
			}

			@Override
			public boolean hasStableIds() {
				return true;
			}

		}
}
