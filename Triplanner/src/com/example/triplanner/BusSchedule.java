package com.example.triplanner;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class BusSchedule extends Activity {
	String urltest = "http://128.237.200.104/android_connect/getbusstopsbyroute.php";
	private TextView tv;
	private TextView tv1;
	private Button button1;
	private Button button2;
	JSONParser jParser = new JSONParser();
	private ArrayList<String> routearray = new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
	      setContentView(R.layout.schedule); 
	      
	      tv1 = new TextView(this); 
		  tv1 = (TextView)findViewById(R.id.TextView01); 
		  tv1.setText("BUS:");
		  
		   
		  button1 = (Button)findViewById(R.id.button1); 
		  
		  button1.setOnClickListener(new OnClickListener() {
	            
				@Override
				public void onClick(View arg0) {
					new LoadBusStops().execute();
	 
				}
	 
			});
		  
          
	} 
	//************************************
	class LoadBusStops extends AsyncTask<String, String, String> {
		 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
         
        }
 
        /**
         * getting All products from url
         * */
        protected String doInBackground(String... args) {
        	System.out.println("into background");
        	
        	 // get all busstops for given route number
			
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
	                  
	                String busstationid = c.getString("busstationid");
	                
	                routearray.add(busstationid);
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
        	Intent intent = new Intent(BusSchedule.this, ShowBusStopsByRoute.class); 
    		
    		

    		Bundle bundle =new Bundle();
    	//	bundle.putStringArray("tablestring",(String [])routearray.toArray()); 
    	
    		for(String ss : routearray){
    			  
    			  System.out.println("array is" + ss);
    		  }
    		
    		bundle.putStringArrayList("tablestring",routearray); 
            intent.putExtras(bundle); 
            
            BusSchedule.this.startActivity(intent);
 
        }
 
    }


}
