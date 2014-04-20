package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
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

    public String friends_title = "Who do you want to alert?";
    public HashMap<String, Boolean> tasksToSelected = new HashMap<String, Boolean>();
    public ListView taskList;
    StableArrayAdapter adapter = null;
  
    
    // For demo only, a timer:
    //private Timer timer = new Timer(); 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Restore preferences
	    SharedPreferences settings = getSharedPreferences("settings", 0);
	    tasksToSelected.put(water, settings.getBoolean(water, true));
	    tasksToSelected.put(microDone, settings.getBoolean(microDone, true));
	    tasksToSelected.put(microExplo,settings.getBoolean(microExplo, true));
	    tasksToSelected.put(other,settings.getBoolean(other, true));
	    
		
		// Set up notifications button
		settingsText = (TextView) findViewById(R.id.textView4);
		settingsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                	doNotifications(v);
                }
            }
        );
		// Set up analytics button
		analyticsText = (TextView) findViewById(R.id.textView5);
		analyticsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
//                	alert();
                	doAnalytics(v);  
                }
            }
        );
		// Demo, click the instructions for alert!
		instructionText = (TextView) findViewById(R.id.textView6);
		instructionText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
		                	alert();
                }
            }
        );
		
		taskList = (ListView) findViewById(R.id.listView1);
		String tasks[] ={water, microDone, microExplo, other};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }

	   //adapter = new StableArrayAdapter(this,
       // android.R.layout.simple_list_item_1, list);

        adapter = new StableArrayAdapter(this,
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
	    			item.setBackgroundColor(Color.parseColor(greyBg));
	    			item.setTextColor(Color.parseColor(white));
	    			item.setChecked(false);
	    			tasksToSelected.put(itemText, false);
	    		} else { //not selected yet
	    			item.setBackgroundColor(Color.parseColor(purpleBg));
	    			item.setTextColor(Color.parseColor(white));
	    			item.setChecked(true);
	    			tasksToSelected.put(itemText, true);
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
	    		 	LayoutInflater inflater=getLayoutInflater();
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
	    	
	    	if ((position == 0 && tasksToSelected.get(water)) ||
	    			(position == 1 && tasksToSelected.get(microDone)) ||
	    			(position == 2 && tasksToSelected.get(microExplo)) ||
	    			(position == 3 && tasksToSelected.get(other))) {
	    				view.setBackgroundColor(Color.parseColor(purpleBg));
	    	} else {
	    		view.setBackgroundColor(Color.parseColor(greyBg));
	    	}
	    	adapter.notifyDataSetChanged();
	    	return view;
	    }

	  }

	
	// User clicked on the Analytics button
	protected void doAnalytics(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(this, AnalyticsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// User clicked on the Notifications button
	protected void doNotifications(View view) {
		// Launch Notifications page
    	Intent intent = new Intent(this, NotificationsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// This now pops up the alerts toast in addition to sending an email/text (we can use this to test messaging capabilities for now)
	public void alert() {
		new AlertDialog.Builder(this)
	    .setTitle(alert_title)
	    .setMessage(alert_message)
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue
	        }
	     })
	     .show();
		
		// emails message to currently selected email
		sendMessage(1);
		
		// texts message to currently selected phone number
		//sendMessage(2); 
		
	}
	
	// Send email or text message, depending on argument you pass in - 1 is email, 2 is text (phone number)
	// The email portion hasn't worked on my emulator yet and I'm not sure if it's the emulator or the code - AM
	public void sendMessage(int mtype) {
		if (mtype == 1) {
			// send email
			String email = NotificationsActivity.getEmail();
			try {   
                GMailSender sender = new GMailSender("cookease.app@gmail.com", "deansofdesign");
                sender.sendMail("This is CookEase",   
                        "Your shit is boiling",   
                        "cookease.app@gmail.com",
                        email);
                Log.d("Email sent to: ", email);
            } catch (Exception e) {   
                Log.e("SendMail", e.getMessage(), e);   
            }
		} else {
			String textnum = NotificationsActivity.getNumber();
			//TODO - Emily: implement text message send function.  The phone number is stored in the selectedText variable.
		}

	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	

	
	@Override
    protected void onResume(){
		super.onResume();
		
       // Restore preferences
       SharedPreferences settings = getSharedPreferences("settings", 0);
       tasksToSelected = new HashMap<String, Boolean>();
       tasksToSelected.put(water, settings.getBoolean(water, true));
	   tasksToSelected.put(microDone, settings.getBoolean(microDone, true));
	   tasksToSelected.put(microExplo,settings.getBoolean(microExplo, true));
	   tasksToSelected.put(other,settings.getBoolean(other, true));
    }

    @Override
    protected void onPause(){
       super.onPause();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(water, tasksToSelected.get(water));
      editor.putBoolean(microDone, tasksToSelected.get(microDone));
      editor.putBoolean(microExplo, tasksToSelected.get(microExplo));
      editor.putBoolean(other, tasksToSelected.get(other));

      // Commit the edits!
      editor.commit();
    }
    

}
