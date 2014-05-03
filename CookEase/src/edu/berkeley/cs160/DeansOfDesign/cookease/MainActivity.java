package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import edu.berkeley.cs160.DeansOfDesign.cookease.BoilingWaterDetector.OnBoilingEventListener;

public class MainActivity extends Fragment implements OnBoilingEventListener {
	
/*	Implement Prototype
	- Implement home page (DONE)
	- Implement alert/notification popup (DONE)
	- Implement friend selection (DONE-ish)
	- Implement Analytics page (Namkyu)
	- Implement demo functionality (DONE)*/
	
	

    public final static String EXTRA_MESSAGE = "edu.berkeley.cs160.DeansOfDesign.MESSAGE";
    private static TextView analyticsText = null;
    private static TextView settingsText = null;
    private static TextView instructionText = null;
    
    public String alert_message = "Your water is boiling!\nYour microwave is done!";
    public String alert_title = "CookEase Alert";
    public String water = "Water boiling";
    public String microDone = "Microwave Done";
    public String microExplo = "Microwave Explosion";
    public String other = "Other Kitchen Tasks";
    String greyBg = "#84a689";
    String purpleBg = "#a684a1";
    String white = "#ffffff";
    private Mail sendMail;
    String sendOkay = "";

    public String friends_title = "Who do you want to alert?";
    public HashMap<String, Boolean> tasksToSelected = new HashMap<String, Boolean>();
    public ListView taskList;
    StableArrayAdapter adapter = null;
    Activity act;
    
  
    // For audio processing
    private BoilingWaterDetector boilingWaterDetector;
    private boolean alerted = false;
    
    // For demo only, a timer:
    //private Timer timer = new Timer(); 
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		act = this.getActivity();
		act.setContentView(R.layout.activity_main);
		// Make a mail object to send email with
		//make a Mail object to email with
	    sendMail = new Mail("cookease.app@gmail.com", "deansofdesign");
		
		// Restore preferences
	    SharedPreferences settings = act.getSharedPreferences("settings", 0);
	    tasksToSelected.put(water, settings.getBoolean(water, true));
	    tasksToSelected.put(microDone, settings.getBoolean(microDone, true));
	    tasksToSelected.put(microExplo,settings.getBoolean(microExplo, true));
	    tasksToSelected.put(other,settings.getBoolean(other, true));

		// Demo, click the instructions for alert!
		instructionText = (TextView) act.findViewById(R.id.textView6);
		instructionText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
		                	alert();
                }
            }
        );
		
		// Setup audio processing
		boilingWaterDetector = new BoilingWaterDetector(act, 0.1);
		boilingWaterDetector.setOnBoilingEventListener(this);
		if (tasksToSelected.get(water)) {
			boilingWaterDetector.startDetection();
		}
		
		taskList = (ListView) act.findViewById(R.id.listView1);
		String tasks[] ={water, microDone, microExplo, other};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }


        adapter = new StableArrayAdapter(act,
        		android.R.layout.simple_list_item_multiple_choice, list);
	    
	    taskList.setAdapter(adapter);
	    taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    	@SuppressLint("NewApi")
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, final View view,
	    			int position, long id) {
	    		CheckedTextView item = (CheckedTextView) view;
	    		String itemText = (String) parent.getItemAtPosition(position);
	    		if (tasksToSelected.get(itemText)) { //selected already
	    			//item.setBackgroundColor(Color.parseColor(greyBg));
	    			//item.setTextColor(Color.parseColor(white));
	    			item.setChecked(false);
	    			tasksToSelected.put(itemText, false);
	    			if (itemText == water) {
	    				boilingWaterDetector.stopDetection();
	    			}
	    		} else { //not selected yet
	    			//item.setBackgroundColor(Color.parseColor(purpleBg));
	    			//item.setTextColor(Color.parseColor(white));
	    			item.setChecked(true);
	    			tasksToSelected.put(itemText, true);
	    			if (itemText == water) {
	    				alerted = false;
	    				boilingWaterDetector.startDetection();
	    			}
	    		}
	    		/*//For demo only, run 5 second timer and pop up alert
	        	timer.schedule(new MyTimerTask(), 5000);
		    	// End demo stuff here
	        	view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
	              	@Override
	              	public void run() {
	                	list.remove(item);
	                	adapter.notifyDataSetChanged();
	                	view.setAlpha(1);
	              	}
	            	});
	        	} */
	    	}
	    });   
	    taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    taskList.setItemChecked(0, tasksToSelected.get(water));
	    taskList.setItemChecked(1, tasksToSelected.get(microDone));
	    taskList.setItemChecked(2, tasksToSelected.get(microExplo));
	    taskList.setItemChecked(3, tasksToSelected.get(other));
	    
/*	    int wantedPosition = 10; // Whatever position you're looking for  
	    int wantedChild = wantedPosition - firstPosition;
	    // Say, first visible position is 8, you want position 10, wantedChild will now be 2
	    // So that means your view is child #2 in the ViewGroup:
	    if (wantedChild < 0 || wantedChild >= listView.getChildCount()) {
	      Log.w(TAG, "Unable to get view for desired position, because it's not being displayed on screen.");
	      return;
	    }
	    // Could also check if wantedPosition is between listView.getFirstVisiblePosition() and listView.getLastVisiblePosition() instead.
	    View wantedView = listView.getChildAt(wantedChild);*/ 
	    
	   return inflater.inflate(R.layout.activity_main, container, false);
	}
	/*
	// For demo only
	private class MyTimerTask extends TimerTask{

        @Override
        public void run() {        
            runOnUiThread(new Runnable() {              
                @Override
                public void run() {
                    alert();
                }
            });
        }       
    }
	// Demo end
 	*/
	private class StableArrayAdapter extends ArrayAdapter<String> {
	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	    Context c;
	    
	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      this.c = context;
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
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
	    
	    @Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	    	View view = convertView;
	    	 if( view == null ){
	    	        //We must create a View:
	    		 	LayoutInflater inflater=act.getLayoutInflater();
	    	        view = inflater.inflate(R.layout.custom_row, parent, false);
	    		 	
	    	 }
	    	 CheckedTextView temp = (CheckedTextView) view.findViewById(R.id.text1);
	    	 if (position == 0) {
	    		 temp.setText(water);
	    	 } else if (position == 1) {
	    		 temp.setText(microDone);
	    	 } else if (position == 2) {
	    		 temp.setText(microExplo);
	    	 } else if (position == 3) {
	    		 temp.setText(other);
	    	 }
	    	
	    	/*if ((position == 0 && tasksToSelected.get(water)) ||
	    			(position == 1 && tasksToSelected.get(microDone)) ||
	    			(position == 2 && tasksToSelected.get(microExplo)) ||
	    			(position == 3 && tasksToSelected.get(other))) {
	    				view.setBackgroundColor(Color.parseColor(purpleBg));
	    	} else {
	    		view.setBackgroundColor(Color.parseColor(greyBg));
	    	}*/
	    	adapter.notifyDataSetChanged();
	    	return view;
	    }

	  }

	
	/*// User clicked on the Analytics button
	protected void doAnalytics(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(act, AnalyticsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// User clicked on the Notifications button
	protected void doNotifications(View view) {
		// Launch Notifications page
    	Intent intent = new Intent(act, NotificationsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}*/
	
	// This now pops up the alerts toast in addition to sending an email/text (we can use this to test messaging capabilities for now)
	public void alert() {
		new AlertDialog.Builder(act)
	    .setTitle(alert_title)
	    .setMessage(alert_message)
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue
	        }
	     })
	     .show();
		// sends a test email to the currently selected email address
		sendMessage(1);
		// sends a test text to the currently selected phone number
		sendMessage(2); 
		
	}
	
	// Send email or text message, depending on which argument you pass in - 1 is email, 2 is text (phone number)
	public void sendMessage(int mtype) {
		SharedPreferences texts = act.getSharedPreferences("texts", 0);
		if (mtype == 1) {
		    String email = texts.getString(NotificationsActivity.email, NotificationsActivity.selectedEmail);
			Log.d("EMAIL SENT TO:", email);
			String[] toArr = {email}; // You can add more emails here if necessary
			Log.d("EMAIL IS NOW:", toArr[0]);
			sendMail.setTo(toArr); // load array to setTo function
			sendMail.setFrom("cookease.app@gmail.com"); // who is sending the email 
			sendMail.setSubject("Your water is boiling!"); 
			sendMail.setBody("Your water is boiling.");
			Runnable r = new Runnable() {
			    @Override
			    public void run() {
			    	try {
			    		sendMail.send();
			    	} catch(Exception e) {
			    		// Can't figure out how to alter things while in this thread - every time I try to do something it crashes
			    		// Eventually handling this exception would be nice
			    	}
			    }
			};
			Thread t = new Thread(r);
			t.start();
		} else {
			final String textnum = texts.getString(NotificationsActivity.text, NotificationsActivity.selectedText);
			//Text message send function.  The phone number is stored in textnum variable.
			Runnable r = new Runnable() {
			    @Override
			    public void run() {
			    	try {
			    		sendSMS(textnum, "Your water is boiling!");
			    		//sendSMS("5554", "Your water is boiling!"); //for emulator testing
			    	} catch(Exception e) {
			    		// Can't figure out how to alter things while in this thread - every time I try to do something it crashes
			    		// Eventually handling this exception would be nice
			    	}
			    }
			};
			Thread t = new Thread(r);
			t.start();
			
		}
	}
	
	private void sendSMS(String phoneNumber, String message)
	   {
	       SmsManager sms = SmsManager.getDefault();
	       sms.sendTextMessage(phoneNumber, null, message, null, null);
	    }
	

	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		act.getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}*/
	

	
	@Override
	public void onResume(){
		super.onResume();
		
       // Restore preferences
       SharedPreferences settings = act.getSharedPreferences("settings", 0);
       tasksToSelected = new HashMap<String, Boolean>();
       tasksToSelected.put(water, settings.getBoolean(water, true));
	   tasksToSelected.put(microDone, settings.getBoolean(microDone, true));
	   tasksToSelected.put(microExplo,settings.getBoolean(microExplo, true));
	   tasksToSelected.put(other,settings.getBoolean(other, true));
    }

    @Override
	public void onPause(){
       super.onPause();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = act.getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(water, tasksToSelected.get(water));
      editor.putBoolean(microDone, tasksToSelected.get(microDone));
      editor.putBoolean(microExplo, tasksToSelected.get(microExplo));
      editor.putBoolean(other, tasksToSelected.get(other));

      // Commit the edits!
      editor.commit();
    }
    

    @Override
	public void onDestroy(){
    	super.onDestroy();

    	// Stop listening for things!
    	boilingWaterDetector.stopDetection();
    }

	@Override
	public void processBoilingEvent() {
		if (!alerted) {
			alerted = true;
			act.runOnUiThread(new Runnable() {
				public void run() {
					alert(); 
				}
			});
		}
	}
    
}
