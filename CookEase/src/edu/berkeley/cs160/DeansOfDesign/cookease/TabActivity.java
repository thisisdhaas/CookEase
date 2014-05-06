package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.HashMap;

import edu.berkeley.cs160.DeansOfDesign.cookease.BoilingWaterDetector.OnBoilingEventListener;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;

public class TabActivity extends Activity implements OnBoilingEventListener {
	
	public String alert_message = "Your water is boiling!\nYour microwave is done!";
    public String alert_title = "CookEase Alert";
    public String water = "Water boiling";
    public String microDone = "Microwave Done";
    public String microExplo = "Microwave Explosion";
    public boolean inForeground;
    public Mail sendMail;
	
	// For handling tabs
	ActionBar.Tab tab1, tab2, tab3;
	Fragment fragmentTab1 = new MainActivity();
	Fragment fragmentTab2 = new NotificationsActivity();
	Fragment fragmentTab3 = new AnalyticsActivity();
    
    // For audio processing
    protected BoilingWaterDetector boilingWaterDetector;
    protected boolean waterAlerted = false;
    protected boolean isListening;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_tab);
        inForeground = true;
        
		// Make a mail object to send email with
	    sendMail = new Mail("cookease.app@gmail.com", "deansofdesign");
        
	    //Make tabs
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        tab1 = actionBar.newTab().setText("Home");
        tab2 = actionBar.newTab().setText("Notifications");
        tab3 = actionBar.newTab().setText("Analytics");
        final class MyTabListener implements ActionBar.TabListener {
        	Fragment fragment;
        	
        	public MyTabListener(Fragment fragment) {
        		this.fragment = fragment;
        	}


			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				ft.replace(R.id.fragment_container, fragment);
				setContentView(R.layout.activity_tab);
				
			}

			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				// TODO Auto-generated method stub
				ft.remove(fragment);
				setContentView(R.layout.activity_tab);
			}
        }
        tab1.setTabListener(new MyTabListener(fragmentTab1));
        tab2.setTabListener(new MyTabListener(fragmentTab2));
        tab3.setTabListener(new MyTabListener(fragmentTab3));
        
        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        
        // Set up audio processing.
		boilingWaterDetector = new BoilingWaterDetector(this, 0.1);
		boilingWaterDetector.setOnBoilingEventListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tab, menu);
		return true;
	}

	// Stop listening for things!
    @Override
	public void onDestroy(){
    	super.onDestroy();
    	boilingWaterDetector.stopDetection();
    	inForeground= false;
    }
    
	@Override
	public void processBoilingEvent() {
		if (!waterAlerted) {
			waterAlerted = true;
			runOnUiThread(new Runnable() {
				public void run() {
					// TODO(dhaas): What if main tab isn't selected? How do we handle alerts?
					//emily: new alert logic for bg vs fg
					alert(water); 
				}
			});
		}
	}
	
	// This now pops up the alerts toast in addition to sending an email/text (we can use this to test messaging capabilities for now)
		public void alert(String task) {
			String contentText="";
			MainActivity tab1 = ((MainActivity) fragmentTab1);
			if (task == water) {
				tab1.tasksToSelected.put(water, false);
				tab1.taskList.setItemChecked(0, tab1.tasksToSelected.get(water));
				contentText = "Your water has boiled";
			} else if (task == microDone) {
				tab1.tasksToSelected.put(microDone, false);
				tab1.taskList.setItemChecked(1, tab1.tasksToSelected.get(microDone));
				contentText = "The microwave is done";
			} else if (task == microExplo) { 
				tab1.tasksToSelected.put(microExplo, false);
				tab1.taskList.setItemChecked(2, tab1.tasksToSelected.get(microExplo));
				contentText = "Food is exploding in the microwave";
			}
			AlertDialog.Builder alt = new AlertDialog.Builder(this)
		    .setTitle(alert_title)
		    .setMessage(contentText)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue
		        }
		     });
		     alt.show();
			// sends a test email to the currently selected email address
			sendMessage(1);
			// sends a test text to the currently selected phone number
			sendMessage(2); 
			
			//Standard Android Notif if app not in foreground
			//if (!inForeground) {
				NotificationCompat.Builder mBuilder =
				        new NotificationCompat.Builder(this)
				        .setSmallIcon(R.drawable.ic_launcher) //temp icon
				        .setContentTitle("CookEase Notification")
				        .setContentText(contentText);
				
				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(this, TabActivity.class);
		
				// The stack builder object will contain an artificial back stack for the
				// started Activity.
				// This ensures that navigating backward from the Activity leads out of
				// your application to the Home screen.
				TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
				// Adds the back stack for the Intent (but not the Intent itself)
				stackBuilder.addParentStack(TabActivity.class);
				// Adds the Intent that starts the Activity to the top of the stack
				stackBuilder.addNextIntent(resultIntent);
				PendingIntent resultPendingIntent =
				        stackBuilder.getPendingIntent(
				            0,
				            PendingIntent.FLAG_UPDATE_CURRENT
				        );
				mBuilder.setContentIntent(resultPendingIntent);
				NotificationManager mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(0, mBuilder.build());
		
				//TODO uncomment when water boiling working
				//check if we have to keep listening for other tasks
				//if (!tasksSelected()) {
					// Stop listening for things!
			    //	boilingWaterDetector.stopDetection();
				//}
			//}
			
		}
		
		// Send email or text message, depending on which argument you pass in - 1 is email, 2 is text (phone number)
		public void sendMessage(int mtype) {
			//SharedPreferences texts = getSharedPreferences("texts", 0);
			if (mtype == 1) {
				if (NotificationsActivity.emails != null && NotificationsActivity.emails.size() > 0) {
					String[] emails = NotificationsActivity.emails.values().toArray(new String[NotificationsActivity.emails.size()]);
					String[] toArr = emails; // You can add more emails here if necessary
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
				}
			} else {
				if (NotificationsActivity.numbers != null && NotificationsActivity.numbers.size() > 0) {
					final String[] textnum = NotificationsActivity.numbers.values().toArray(new String[NotificationsActivity.numbers.size()]);
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
		}
		
		private void sendSMS(String[] numbers, String message) {
	       SmsManager sms = SmsManager.getDefault();
	       int i = 0;
	       while (i < numbers.length) {
	    	   sms.sendTextMessage(numbers[i], null, message, null, null);
	    	   i += 1;
	       }
	    }
		
		@Override
		public void onResume(){
			super.onResume();
		   inForeground = true;
	    }

	    @Override
		public void onPause(){
	       super.onPause();
	      inForeground = false;
	    }
	
}
