package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
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

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

public class AnalyticsActivity extends Fragment {

	private static TextView last5Text;
	private TextView curView;
	
	// For analytics testing
	private static Button waterButton;
	private static Button microwaveButton;
	private static Button graphButton;
	private static Button dbTest;
    private static final int STATS_TEXT = 1;
    private static final int STATS_GRAPH = 0;
	
	private Boolean waterListening = false;
	private Boolean microwaveListening = false;
	private Boolean textShowing = false;
	private Boolean graphShowing = false;
	
	private GraphView graphView;
	
	
	static Activity act;
	DatabaseHandler db;
	
	AnalyticsTracker at;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		act = this.getActivity();
		act.setContentView(R.layout.activity_analytics);
		at = new AnalyticsTracker();
		// Set up test water button
//		waterButton = (Button) act.findViewById(R.id.button1);
//		waterButton.setOnClickListener(
//				new OnClickListener() {
//					public void onClick(View v) {
//						if (waterListening) {
//							waterListening = false;
//							at.finishTime(AnalyticsData.WATER);
//						} else {
//							waterListening = true;
//							at.startTime(AnalyticsData.WATER);
//						}
//					}
//				}
//		);
		
		// Set up test microwave button
//		microwaveButton = (Button) act.findViewById(R.id.button2);
//		microwaveButton.setOnClickListener(
//				new OnClickListener() {
//					public void onClick(View v) {
//						if (microwaveListening) {
//							microwaveListening = false;
//							at.finishTime(AnalyticsData.MICROWAVE);
//						} else {
//							microwaveListening = true;
//							at.startTime(AnalyticsData.MICROWAVE);
//						}
//					}
//				}
//		);
		// Show 5 previous sessions
		dbTest = (Button) act.findViewById(R.id.button4);
        dbTest.setBackgroundResource(R.drawable.text);
		dbTest.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						curView.setText("Last 5 sessions");
						displayStats(STATS_TEXT);
					}
				}
		);
		// Show graph
		graphButton = (Button) act.findViewById(R.id.button3);
        graphButton.setBackgroundResource(R.drawable.graph);
		graphButton.setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						curView.setText("Graph by Month");
						displayStats(STATS_GRAPH);
					}
				}
		);
		curView = (TextView) act.findViewById(R.id.textView4);
		last5Text = (TextView) act.findViewById(R.id.last5Text);
//		displayStats();
        db = AnalyticsTracker.db;

        // For demo, fill database with some examples (previous 5 months)
        Log.d("Insert: ", "Inserting .."); 
        Date dt = new Date();
        // Make the data a little different, so last 5 months, and vary duration randomly
        for (int i = 1; i <= 5; i++) {
            Calendar c = Calendar.getInstance(); 
            c.setTime(dt); 
            c.add(Calendar.MONTH, -i);
            dt = c.getTime();
            // General pattern for generating a random number between MIN and MAX is
            // Min + (int)(Math.random() * ((Max - Min) + 1))
            // Ours will be between 10000 and 10 0000
            long length = 10000 + (int)(Math.random()*((100000-10000)+1));
            String duration = String.valueOf(length);            
            db.addAnalyticsData(new AnalyticsData(dt.toString(), duration, String.valueOf(AnalyticsData.WATER))); 
            
            // Do this for microwave as well
            long length2 = 10000 + (int)(Math.random()*((100000-10000)+1));
            String duration2 = String.valueOf(length2);            
            db.addAnalyticsData(new AnalyticsData(dt.toString(), duration2, String.valueOf(AnalyticsData.MICROWAVE))); 
        }
        displayStats(STATS_TEXT);
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
	
	public void displayStats(int statType) {
//		TextView waterText = (TextView) act.findViewById(R.id.textView8);
//		TextView microwaveText = (TextView) act.findViewById(R.id.textView9);
		
		// Reading last stat
        Log.d("Reading: ", "Reading all stats..");
        String stats = "";
        List<AnalyticsData> dataList = db.getAllAnalyticsData();    
        int listSize = dataList.size();
         
        if (statType == STATS_TEXT) {
        	if (textShowing) {
        		last5Text.setText("");
        		textShowing = false;
        	}
        	if (graphShowing) {
            	LinearLayout layout = (LinearLayout) act.findViewById(R.id.graph);
            	layout.removeView(graphView);
            	graphShowing = false;
        	}
	        // Display last 5 sessions
	        for (int i = listSize - 5; i < listSize; i++) {
	          AnalyticsData d = dataList.get(i);
	        	// Format date for printing
	          String date = d.getDate();
	          date = date.substring(4,10)+" " + date.substring(24);
	          String myDate = date;
	          // Format duration for printing
	          long duration = Long.parseLong(d.getDuration()) / 1000;
	          String myDuration = String.valueOf(duration)+" seconds";
	          // Format datatype for printing
	          String dataType = d.getDataType();
	          System.out.println("dataType is: "+dataType);
	          dataType = (dataType.matches("0") ? "Water boiled in " : "Microwave done in ");
	          // append to "stats"
	          stats += myDate+", "+dataType+myDuration+"\n\n";
	        }
	        last5Text.setText(stats);
	        textShowing = true;
        } else {
        	if (textShowing) {
        		last5Text.setText("");
        		textShowing = false;
        	}
        	if (graphShowing) {
            	LinearLayout layout = (LinearLayout) act.findViewById(R.id.graph);
            	layout.removeView(graphView);
            	graphShowing = false;
        	}
        	// init example series data
        	GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
        	    new GraphView.GraphViewData(1, 2.0d)
        	    , new GraphView.GraphViewData(2, 1.5d)
        	    , new GraphView.GraphViewData(3, 2.5d)
        	    , new GraphView.GraphViewData(4, 1.0d)
        	    , new GraphView.GraphViewData(5, 0.5d)
        	});

        	graphView = new LineGraphView(
        	    act // context
        	    , "Average Cooking Duration" // heading
        	);

        	graphView.getGraphViewStyle().setTextSize(24);
        	graphView.addSeries(exampleSeries); // data
        	graphView.setHorizontalLabels(new String[] {"1/'14", "2/'14", "3/'14", "4/'14", "5/'14", "6/'14"});
        	graphView.setVerticalLabels(new String[] {"3 mins", "1.5 mins", "0 mins"});
        	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
        	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);

        	LinearLayout layout = (LinearLayout) act.findViewById(R.id.graph);
        	layout.addView(graphView);
        	graphShowing = true;
        }
	}
}
