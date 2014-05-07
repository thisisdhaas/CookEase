package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class AnalyticsActivity extends Fragment {

	private static TextView textViewWater;
	private static TextView textViewMicrowave;
	
	// For analytics testing
	private static Button waterButton;
	private static Button microwaveButton;
	private static Button dbTest;
    private static final int STATS_TEXT = 1;
    private static final int STATS_GRAPH = 1;
	
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
		// Show 5 previous sessions
		dbTest = (Button) act.findViewById(R.id.showText);
		dbTest.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						displayStats(0);
					}
				}
		);
		// Show graph
		Button graphButton = (Button) act.findViewById(R.id.button3);
		graphButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						displayStats(1);
					}
				}
		);
		textViewWater = (TextView) act.findViewById(R.id.textViewWater);
//		displayStats();
        db = new DatabaseHandler(act);

        // For demo, fill database with some examples (previous 5 months)
        Log.d("Insert: ", "Inserting .."); 
        // Make the data a little different, so last 5 months, and vary duration randomly
        for (int i = 1; i <= 5; i++) {
            Date dt = null;
            Calendar c = Calendar.getInstance(); 
            c.setTime(dt); 
            c.add(Calendar.MONTH, -i);
            dt = c.getTime();
            // General pattern for generating a random number between MIN and MAX is
            // Min + (int)(Math.random() * ((Max - Min) + 1))
            // Ours will be between 1000 and 10 000
            long length = 1000 + (int)(Math.random()*((10000-1000)+1));
            String duration = String.valueOf(length);            
            db.addAnalyticsData(new AnalyticsData(dt.toString(), duration, String.valueOf(AnalyticsData.WATER))); 
            
            // Do this for microwave as well
            long length2 = 1000 + (int)(Math.random()*((10000-1000)+1));
            String duration2 = String.valueOf(length2);            
            db.addAnalyticsData(new AnalyticsData(dt.toString(), duration2, String.valueOf(AnalyticsData.MICROWAVE))); 
        }
        
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
	
	public void displayStats(int statType) {
//		TextView waterText = (TextView) act.findViewById(R.id.textView8);
//		TextView microwaveText = (TextView) act.findViewById(R.id.textView9);
		
		// Reading last stat
        Log.d("Reading: ", "Reading all stats..");
        String stats = "";
        List<AnalyticsData> dataList = db.getAllAnalyticsData();    
        int listSize = dataList.size();
         
        if (statType == STATS_TEXT) {
	        // Display last 5 sessions
	        for (int i = listSize - 6; i < listSize; i++) {
	          AnalyticsData d = dataList.get(i);
	        	// Format date for printing
	          String date = d.getDate();
	          date = date.substring(4,10)+" " + date.substring(24);
	          String myDate = "Date: "+ date;
	          // Format duration for printing
	          long duration = Long.parseLong(d.getDuration()) / 1000;
	          String myDuration = "Duration: "+String.valueOf(duration)+" seconds";
	          // Format datatype for printing
	          int dataType = Integer.getInteger(d.getDataType());
	          System.out.println("dataType is: "+dataType);
	          String myDataType = (dataType == AnalyticsData.WATER ? "Water" : "Microwave");
	          // append to "stats"
	          stats += myDate+myDuration+myDataType+"\n\n";
	        }
	        textViewWater.append("\n"+stats);
        } else {
        	// init example series data
        	GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
        	    new GraphView.GraphViewData(1, 2.0d)
        	    , new GraphView.GraphViewData(2, 1.5d)
        	    , new GraphView.GraphViewData(3, 2.5d)
        	    , new GraphView.GraphViewData(4, 1.0d)
        	    , new GraphView.GraphViewData(5, 0.5d)
        	});

        	GraphView graphView = new LineGraphView(
        	    act // context
        	    , "Average Cooking Duration" // heading
        	);
        	
        	graphView.addSeries(exampleSeries); // data
        	graphView.setHorizontalLabels(new String[] {"December 2013", "January 2014", "February 2014", "March 2014", "April 2014", "May 2014"});
        	graphView.setVerticalLabels(new String[] {"30 mins", "15 mins", "0 mins"});

        	LinearLayout layout = (LinearLayout) act.findViewById(R.id.graph);
        	layout.addView(graphView);
        }
	}
}
