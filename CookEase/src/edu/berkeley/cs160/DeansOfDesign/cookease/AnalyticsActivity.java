package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AnalyticsActivity extends Fragment {

	private static TextView homeText;
	private static TextView notificatonsText;
	
	// For analytics testing
	private static Button waterButton;
	private static Button microwaveButton;
	
	private Boolean waterListening = false;
	private Boolean microwaveListening = false;
	
	private long waterTime = 0;
	private long microwaveTime = 0;
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
							finishTime("water");
						} else {
							waterListening = true;
							startTime("water");
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
							finishTime("microwave");
						} else {
							microwaveListening = true;
							startTime("microwave");
						}
					}
				}
		);
		displayStats();
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

	public void startTime(String type) {
		// Record start time
		if (type == "water") {
			waterTime = System.currentTimeMillis();
		} else if (type == "microwave") {
			microwaveTime = System.currentTimeMillis();
		}
	}
	
	public void finishTime(String type) {
		// Record finish time, then subtract from start time to get total duration
		long time = System.currentTimeMillis();
		String fileName = null;
		if (type == "water") {
			time -= waterTime;
			fileName = "waterAnalyticsRecord";
		} else if (type == "microwave") {
			time -= microwaveTime;
			fileName = "microwaveAnalyticsRecord";
		}
		// Record this in either microwave or water file 
		// E.g. 2014-01: 100.42 213.76
		Calendar cal = Calendar.getInstance();  
	    int year = cal.get(cal.YEAR);  
	    int month = cal.get(cal.MONTH)+1; //zero-based
	    String curDate;
	    if (month < 10) {
		    curDate = year+"-"+"0"+month;
	    } else {
		    curDate = year+"-"+month;
	    }
	    
	  //reading file line by line in Java using BufferedReader       
        FileInputStream fis = null;
        BufferedReader reader = null;
        Boolean dateFound = false;
      
        try {
        	// Open file and inputstream and stuff like that 
        	File myFile = new File(fileName);
        	if (myFile.exists()) {
        		fis = new FileInputStream(fileName);
        	} else {
            // File not found?
            	FileOutputStream fos = act.openFileOutput(fileName, Context.MODE_PRIVATE);
            	fos.write("".getBytes());
            	fos.close();
                fis = new FileInputStream(fileName);
            }
            reader = new BufferedReader(new InputStreamReader(fis));
          
            System.out.println("Reading File line by line using BufferedReader");
            
            // Look for our "year-month" in first 7 bytes, maybe just skip to last line and check?
            String line = reader.readLine();
            String date = line.substring(0, 7);
            while(line != null){
            	if (date == curDate) {
            		dateFound = true;
            	}
                line = reader.readLine();
            }                     
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AnalyticsActivity.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AnalyticsActivity.class.getName()).log(Level.SEVERE, null, ex);
          
        } finally {
            try {
                FileWriter fw = new FileWriter(fileName, true); //the true will append the new data
            	if (dateFound) {
            		// Append time to end of string
            		fw.write(" "+time); //appends the string to the file   
            	} else {
            		// Create new line in the file with time
            		fw.write("\n"+curDate+" "+time); //appends the string to the file   
            	}
            	fw.close();
                reader.close();
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(AnalyticsActivity.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }		
		
/*		FileOutputStream fos = openFileOutput(file, Context.MODE_PRIVATE);
		fos.write(string.getBytes());
		fos.close();*/
	}
	
	public void displayStats() {
		TextView waterText = (TextView) act.findViewById(R.id.textView8);
		TextView microwaveText = (TextView) act.findViewById(R.id.textView9);
		String waterFile = "waterAnalyticsRecord";
		String microwaveFile = "microwaveAnalyticsRecord";
		
		int i = 0;
		while (i < 2) {
			String fileName = null;
			TextView curText = null;
			if (i == 0) {
				fileName = waterFile;
				curText = waterText;
			} else {
				fileName = microwaveFile;
				curText = microwaveText;
			}
	        FileInputStream fis = null;
	        BufferedReader reader = null;
			try {
				// Open file and inputstream and stuff like that 
		    	File myFile = new File(fileName);
		    	if (myFile.exists()) {
		    		fis = new FileInputStream(fileName);
		    	} else {
		    		// File not found?
		        	return;
		        }
	            reader = new BufferedReader(new InputStreamReader(fis));
	            // Print these lines
	            String line = reader.readLine();
	            while(line != null){
	            	curText.append(line);
	                line = reader.readLine();
	            } 
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				try {
					if (reader == null || fis == null) {
						return;
					}
					reader.close();
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
