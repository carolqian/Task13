package com.example.triplanner;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException; 

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.triplanner.ShowRouteByStation.ShowBusSchedule;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ShowStation extends Activity implements OnClickListener{
	final String TAG = "ShowStation";

	double latitude;
	double longitude;
	
	TextView title;
	ListView list;
	Button showMap;
	
	String locName;
	String jsonOutput;
	List<BusStop> busStops;
	String urltest = "http://"+GlobalVariables.IPaddress+"/getbusroute.php";
	private ArrayList<String> routearray = new ArrayList<String>();
	private String[] busData = {"71A","71B","71C","71D", "61B", "P3"};
	private ProgressDialog pDialog;
	JSONParser jParser = new JSONParser();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.showstation); 
		
		LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		LocationListener listener  = new LocationListener() {
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {}
			@Override
			public void onProviderEnabled(String provider) {}
			@Override
			public void onProviderDisabled(String provider) {}
			@Override
			public void onLocationChanged(Location location) {
				latitude = location.getLatitude();
				longitude = location.getLongitude();
			}
		};
		manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
	    Location loc = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    latitude = loc.getLatitude();
	    longitude = loc.getLongitude();
//		latitude=40.43295;
//		longitude=-79.92137;

		title = (TextView)findViewById(R.id.showstation_title);
		list = (ListView)findViewById(R.id.showstation_list);
		showMap = (Button) findViewById(R.id.button1);
		
		showMap.setOnClickListener(this);
		
		new GetNameTask().execute(latitude,longitude);
	}
	void onGetNameTaskFinish(String name){
		title.setText("Bus Stops near your place");
		new GetBusStationTask().execute(latitude+"",longitude+"");
	}
	void onGetBusStationTaskFinish(JSONObject obj){
		busStops = ReadJSON2(obj);
		//TODO: Show List
		list.setAdapter(new ShowStationAdapter(getApplicationContext(),R.id.showstation_list,busStops));
		list.setOnItemClickListener(new AdapterView.OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				BusStop busStop = busStops.get(arg2);			
				
				//TODO: Launch activity to show bus stop detail.
				
				new LoadBusroute().execute();
				
			}
		});
	}
	
	class ShowStationAdapter extends ArrayAdapter<BusStop>{
		List<BusStop> busStops;
		public ShowStationAdapter(Context context, int textViewResourceId,
				List<BusStop> objects) {
			super(context, textViewResourceId, objects);
			busStops = objects;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			
			if(convertView==null){
				LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.item_showstation, null);
			}
			BusStop bs = busStops.get(position);
			TextView text = (TextView)convertView.findViewById(R.id.iss_text);
			text.setText(bs.toString());//TODO: enrich the snippet of the bus stop
			return convertView;
		}
	}
	
	
	class GetNameTask extends AsyncTask<Double,Void,String>{
		@Override
		protected String doInBackground(Double... params) {
			double lat = params[0];
			double lon = params[1];
			
			try{
				URL url = new URL("http://maps.googleapis.com/maps/api/geocode/"
						+"json?latlng="+lat+","+lon+"&sensor=true");
				HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
				StringBuilder response = new StringBuilder();
				if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }
                String jsonStr = response.toString();
                
                //TODO: needs parsing of the reverse-geocoding result.
                return lat+","+lon;
                
			}catch(Exception e){Log.e(TAG, e.getLocalizedMessage(), e);return null;}
			
			
		}
		@Override
		protected void onPostExecute(String result) {
			ShowStation.this.onGetNameTaskFinish(result);
		}		
	}
	class GetBusStationTask extends AsyncTask<String,Void,JSONObject>{
		@Override
		protected JSONObject doInBackground(String... params) {
			String lat = params[0];
			String lon = params[1];
			
			Log.d(TAG+"-GBST", "lat="+lat+" lon="+lon);
			
			try{
				URL url = new URL("https://maps.googleapis.com/maps/api/place/nearbysearch/"
					+"json?location="+lat+","+lon
					+"&sensor=true&key=AIzaSyDsMoDLJbPM_mYuBB9ySUFPr1U-T8Wmt-4&rankby=distance&types=bus_station");
				HttpURLConnection httpconn = (HttpURLConnection) url.openConnection();
				StringBuilder response = new StringBuilder();
				if (httpconn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader input = new BufferedReader(new InputStreamReader(httpconn.getInputStream()),8192);
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        response.append(strLine);
                    }
                    input.close();
                }
				jsonOutput = response.toString();
                return new JSONObject(jsonOutput); 
			}catch(Exception e){Log.e(TAG, e.getLocalizedMessage(), e);return null;}
			
		}
		@Override
		protected void onPostExecute(JSONObject result) {
			ShowStation.this.onGetBusStationTaskFinish(result);
		}
	}
	
	static List<BusStop> ReadJSON2(JSONObject obj) {
		List<BusStop> bsList = new ArrayList<BusStop>();
		try {
			JSONArray ress = (JSONArray)obj.get("results");
			for(int i = 0;i < ress.length();++i){
				BusStop bs = new BusStop();
				
				JSONObject res = (JSONObject)ress.get(i);
				JSONObject geometry = (JSONObject)res.get("geometry");
				JSONObject location = (JSONObject)geometry.get("location");
				double[] loc = new double[2];
				loc[0]=location.getDouble("lat");
				loc[1]=location.getDouble("lng");
				bs.setIcon((String)res.get("icon"));
				bs.setId((String)res.get("id"));
				bs.setLocation(loc);
				bs.setName((String)res.get("name"));
				bs.setReference((String)res.get("reference"));
				bsList.add(bs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return bsList;
	}
	
	// added by Bruce 5/12/
	
	//************ show route for a bus station **********************************
	
	   
		class LoadBusroute extends AsyncTask<String, String, String> {
			 
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            /*
	            pDialog = new ProgressDialog(ShowStation.this);
	            pDialog.setMessage("Loading products. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();*/
	        }
	 
	        /**
	         * getting All products from url
	         * */
	        protected String doInBackground(String... args) {
	        	System.out.println("into background");
				
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("busstationid", "1"));
	            // getting JSON string from URL
	            JSONObject json = jParser.makeHttpRequest(urltest, "GET", params);
	 
	            // Check your log cat for JSON reponse
	            //Log.d("All Products: ", json.toString());
	    			
	            JSONArray routes = null;
	            try {
					routes = json.getJSONArray("routes");
					routearray.clear();
					for(int i = 0; i < routes.length(); i++){
		                JSONObject c = routes.getJSONObject(i);
		                  
		                String routeid = c.getString("routeid");
		                
		                routearray.add(routeid);
		                //System.out.println("row is " + routeid + " " + busstation + " " + time + " " + run);
		                Log.d(i+" add " , c.toString());               
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	 
	            return null;
	        }
	 
	        /**
	         * After completing background task  
	         * **/
	        protected void onPostExecute(String file_url) { 
	        	Intent intent = new Intent(ShowStation.this, ShowRouteByStation.class); 
	    		
	    		

	    		Bundle bundle =new Bundle();
	    	//	bundle.putStringArray("tablestring",(String [])routearray.toArray()); 
	    	
	    		for(String ss : routearray){
	    			  
	    			  System.out.println("array is" + ss);
	    		  }
	    		
	    		bundle.putStringArrayList("tablestring",routearray); 
	            intent.putExtras(bundle); 
	            
	    		ShowStation.this.startActivity(intent);
	 
	        }
	 
	    }

		@Override
		public void onClick(View v) {
			if (v.getId() == showMap.getId()) {
				Intent intent = new Intent(this, ShowBusMap.class);
				intent.putExtra("jsonOutput", jsonOutput);
				intent.putExtra("lat", latitude);
				intent.putExtra("lon", longitude);
				startActivity(intent);
			}
		}
}