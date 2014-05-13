package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
	
	private Long curMax = (long) Long.MIN_VALUE;
	private Long curMin = (long) Long.MAX_VALUE;
	
	
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
						curView.setText("Water by Month");
						displayStats(STATS_GRAPH);
					}
				}
		);
		curView = (TextView) act.findViewById(R.id.textView4);
		last5Text = (TextView) act.findViewById(R.id.last5Text);
//		displayStats();
        db = AnalyticsTracker.db;
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
        System.out.println("here heh");
        if (statType == STATS_TEXT) {
        	System.out.println("Stats_text");
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
	          stats = myDate+", "+dataType+myDuration+"\n\n" + stats;
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
        	GraphViewSeries exampleSeries = getGraphViewSeries();

        	graphView = new LineGraphView(
        	    act // context
        	    , "Average Cooking Duration" // heading
        	);
        	graphView.getGraphViewStyle().setTextSize(24);
        	graphView.addSeries(exampleSeries); // data
        	graphView.setHorizontalLabels(new String[] {"1/'14", "2/'14", "3/'14", "4/'14", "5/'14"});
        	String maxString = curMax.toString()+"secs";
        	String minString = curMin.toString()+"secs";
        	String avgString = ((curMin+curMax)/2)+"secs";
        	
        	graphView.setVerticalLabels(new String[] {maxString, avgString, minString});
        	graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);
        	graphView.getGraphViewStyle().setHorizontalLabelsColor(Color.BLACK);

        	LinearLayout layout = (LinearLayout) act.findViewById(R.id.graph);
        	layout.addView(graphView);
        	graphShowing = true;
        }
	}

    private GraphViewSeries getGraphViewSeries() {
        List<AnalyticsData> dataList = db.getAllAnalyticsData(); 
		long[] monthlyAverages = new long[]{(long) 0, (long)0,(long)0,(long)0,(long)0};
		int[] monthCount = new int[]{0,0,0,0,0};
        for (AnalyticsData d : dataList) {
        	// Get monthly averages for last 5 months
        	if (Integer.valueOf(d.getDataType()) == AnalyticsData.WATER) {
            	// Get average values per month
        		System.out.println("substring is: "+d.getDate().substring(4, 7));
        		if (d.getDate().substring(4, 7).equals("Jan")) {
        			// add to sum
        			monthlyAverages[0] += Long.valueOf(d.getDuration());
        			// Increment month count
        			monthCount[0] += 1;
        		}
        		if (d.getDate().substring(4, 7).equals("Feb")) {
        			System.out.println("Inside Feb");
        			// add to sum
        			monthlyAverages[1] += Long.valueOf(d.getDuration());
        			// Increment month count
        			monthCount[1] += 1;
        			System.out.println("avg is: "+monthlyAverages[1]+" count is: "+monthCount[1]);
        		}
        		if (d.getDate().substring(4, 7).equals("Mar")) {
        			// add to sum
        			monthlyAverages[2] += Long.valueOf(d.getDuration());
        			// Increment month count
        			monthCount[2] += 1;
        		}
        		if (d.getDate().substring(4, 7).equals("Apr")) {
        			// add to sum
        			monthlyAverages[3] += Long.valueOf(d.getDuration());
        			// Increment month count
        			monthCount[3] += 1;
        		}
        		if (d.getDate().substring(4, 7).equals("May")) {
        			// add to sum
        			monthlyAverages[4] += Long.valueOf(d.getDuration());
        			// Increment month count
        			monthCount[4] += 1;
        		}
        	}
        }
        for (int i = 0; i < 5; i++) {
        	Long curSum = monthlyAverages[i]/1000;
        	int curCount = monthCount[i];
        	Long curAvg = (long)0;
        	if (curCount != 0) {
            	curAvg = curSum / curCount;        		
        	}
        	monthlyAverages[i] = curAvg;
        	if (curAvg < curMin) {
        		curMin = curAvg;
        	}
        	if (curAvg > curMax) {
        		if (i != 4) {
        			curMax = curAvg;
        		}
        	}
        	System.out.println("monthly Average is: "+i+" "+curAvg+" curCount: "+curCount+" curSum "+curSum);
        }
    	// init example series data
    	GraphViewSeries exampleSeries = new GraphViewSeries(new GraphView.GraphViewData[] {
    	    new GraphView.GraphViewData(1, monthlyAverages[0])
    	    , new GraphView.GraphViewData(2, monthlyAverages[1])
    	    , new GraphView.GraphViewData(3, monthlyAverages[2])
    	    , new GraphView.GraphViewData(4, monthlyAverages[3])
    	    , new GraphView.GraphViewData(5, (monthlyAverages[1]+monthlyAverages[2])/2) 
    	});
        
    	return exampleSeries;
    }
}
