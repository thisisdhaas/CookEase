package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AnalyticsActivity extends Fragment {

	private static TextView textViewWater;
	private static TextView textViewMicrowave;
	
	// For analytics testing
	private static Button waterButton;
	private static Button microwaveButton;
	private static Button dbTest;
	
	private Boolean waterListening = false;
	private Boolean microwaveListening = false;
	
	private long waterTime = 0;
	private long microwaveTime = 0;
	
	DatabaseHandler db;
	Activity act;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		act = this.getActivity();
		act.setContentView(R.layout.activity_analytics);
		
		// Set up test water button
		waterButton = (Button) act.findViewById(R.id.button1);
		waterButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (waterListening) {
							waterListening = false;
							finishTime(AnalyticsData.WATER);
						} else {
							waterListening = true;
							startTime(AnalyticsData.WATER);
						}
					}
				}
		);
		
		// Set up test microwave button
		microwaveButton = (Button) act.findViewById(R.id.button2);
		microwaveButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (microwaveListening) {
							microwaveListening = false;
							finishTime(AnalyticsData.MICROWAVE);
						} else {
							microwaveListening = true;
							startTime(AnalyticsData.MICROWAVE);
						}
					}
				}
		);
		// Test DB
		dbTest = (Button) act.findViewById(R.id.dbTest);
		dbTest.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						displayStats();
					}
				}
		);
		textViewWater = (TextView) act.findViewById(R.id.textViewWater);
//		displayStats();
        db = new DatabaseHandler(act);
		return inflater.inflate(R.layout.activity_main, container, false);
	}
	
	/*public void doHome(View view) {
    	Intent intent = new Intent(act, TabActivity.class);
        startActivity(intent);
	}
	
	// User clicked on the Notifcations button
	protected void doNotifications(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(act, NotificationsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}*/

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		act.getMenuInflater().inflate(R.menu.analytics, menu);
		return true;
	}

	public void startTime(int dataType) {
		// Record start time
		if (dataType == AnalyticsData.WATER) {
			waterTime = System.currentTimeMillis();
		} else if (dataType == AnalyticsData.MICROWAVE) {
			microwaveTime = System.currentTimeMillis();
		}
	}
	
	public void finishTime(int dataType) {
		// Record finish time, then subtract from start time to get total duration
		long duration = System.currentTimeMillis();
		if (dataType == AnalyticsData.WATER) {
			duration -= waterTime;
		} else if (dataType == AnalyticsData.MICROWAVE) {
			duration -= microwaveTime;
		}
		// Put this in database
		Date date = new Date();
        Log.d("Insert: ", "Inserting .."); 
        db.addAnalyticsData(new AnalyticsData(date.toString(), String.valueOf(duration), String.valueOf(dataType)));
        Log.d("Done inserting: ", "Inserted: " +date+" duration: " +duration+" dataType:"+dataType); 
	}
	
	public void displayStats() {
//		TextView waterText = (TextView) act.findViewById(R.id.textView8);
//		TextView microwaveText = (TextView) act.findViewById(R.id.textView9);
		
		// Reading last stat
        Log.d("Reading: ", "Reading all stats..");
        String stats = "";
        List<AnalyticsData> dataList = db.getAllAnalyticsData();       
         
        for (AnalyticsData d : dataList) {
          stats += "Date: "+d.getDate()+" ,Duration: " + d.getDuration() + " ,dataType: " + d.getDataType()+"\n";
        }
        System.out.println("stats is: " + stats);
        System.out.println("textViewWater is: "+textViewWater);
        textViewWater.append(stats);
//            String log = "Id: "+d.getDate()+" ,Name: " + d.getDuration() + " ,Phone: " + d.getDataType();
                // Writing Contacts to log
//        Log.d("Name: ", log);
        	
        
        
        
//		String waterFile = "waterAnalyticsRecord";
//		String microwaveFile = "microwaveAnalyticsRecord";
		
//		int i = 0;
//		while (i < 2) {
//			String fileName = null;
//			TextView curText = null;
//			if (i == 0) {
//				fileName = waterFile;
//				curText = waterText;
//			} else {
//				fileName = microwaveFile;
//				curText = microwaveText;
//			}
//	        FileInputStream fis = null;
//	        BufferedReader reader = null;
//			try {
//				// Open file and inputstream and stuff like that 
//		    	File myFile = new File(fileName);
//		    	if (myFile.exists()) {
//		    		fis = new FileInputStream(fileName);
//		    	} else {
//		    		// File not found?
//		        	return;
//		        }
//	            reader = new BufferedReader(new InputStreamReader(fis));
//	            // Print these lines
//	            String line = reader.readLine();
//	            while(line != null){
//	            	curText.append(line);
//	                line = reader.readLine();
//	            } 
//			}  catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} finally {
//				try {
//					if (reader == null || fis == null) {
//						return;
//					}
//					reader.close();
//					fis.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
