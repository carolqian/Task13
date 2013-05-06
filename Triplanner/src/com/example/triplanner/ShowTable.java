package com.example.triplanner;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
 


public class ShowTable extends ListActivity  {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	        
		  Intent i = getIntent();
		  Bundle extras=i.getExtras();
		  String tableData[]=extras.getStringArray("tablestring");
		  setRows(tableData);
		  
		  
			ListView listView = getListView();
			listView.setTextFilterEnabled(true);
	 
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
				    // When clicked, show a toast with the TextView text
				   // Toast.makeText(getApplicationContext(),
					//((TextView) view).getText(), Toast.LENGTH_SHORT).show();
				    
				    Intent intent = new Intent(ShowTable.this, ShowTableRow.class); 
				    ShowTable.this.startActivity(intent);	
				}
			});
		
	} 
	
	public void setRows(String[] x){
		setListAdapter(new ArrayAdapter<String>(this, R.layout.showtable,x));
	}
	
}
