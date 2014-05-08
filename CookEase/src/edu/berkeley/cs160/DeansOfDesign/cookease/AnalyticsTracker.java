package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.Date;

import android.util.Log;

public class AnalyticsTracker {

	private long waterTime ;
	private long microwaveTime;
	static DatabaseHandler db;
	
	public AnalyticsTracker() {
		this.waterTime = 0;
		this.microwaveTime = 0;
		db = new DatabaseHandler(AnalyticsActivity.act);
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
		long duration = System.currentTimeMillis()+1000; // add 1000 to round up (0 seconds displaying for 0.3 seconds looks weird for analytics)
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
}
