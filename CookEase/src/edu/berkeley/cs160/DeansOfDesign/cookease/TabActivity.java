package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;

import edu.berkeley.cs160.DeansOfDesign.cookease.KitchenEventDetector.OnKitchenEventListener;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

public class TabActivity extends Activity implements OnKitchenEventListener {
	
	public static String alert_message = "";
    public static final String alert_title = "CookEase Alert";
    public static final String water = "Water boiling";
    public static final String microDone = "Microwave Done";
    public static final String microExplo = "Microwave Explosion";
    public static final String waterMessage = "Your water has boiled!";
    public static final String microDoneMessage = "The microwave is done.";
    public static final String microExploMessage = "Food is exploding in the microwave!";
    public boolean inForeground;
    public Mail sendMail;
    public NotificationCompat.Builder mBuilder;
    AlertDialog.Builder alt;
    final int DIALOG_ALERT = 10;
    AlertDialog ad;
    boolean userGreyedOut;
    protected static final Map<String, Boolean> tasksToSelected;
    static {
    	tasksToSelected = new HashMap<String, Boolean>();
    	tasksToSelected.put(water, false);
	    tasksToSelected.put(microDone, false);
	    tasksToSelected.put(microExplo, false);
    }
	
	// For handling tabs
	ActionBar.Tab tab1, tab2, tab3;
	Fragment fragmentTab1 = new MainActivity();
	Fragment fragmentTab2 = new NotificationsActivity();
	//Fragment fragmentTab3 = new AnalyticsActivity();
	
	// For sounding alarms
	protected Ringtone rTone;
    protected boolean alarmOn;
    
    // For audio processing
    protected KitchenEventDetector kitchenEventDetector;
    protected Map<String, Boolean> alertedMap;
    protected static final Map<String, String> eventClassNamesToAppStrings;
    static {
    	eventClassNamesToAppStrings = new HashMap<String, String>();
    	eventClassNamesToAppStrings.put(AudioFeatures.BOILING, MainActivity.water);
    	eventClassNamesToAppStrings.put(AudioFeatures.MICRO_DONE, MainActivity.microDone);
    	eventClassNamesToAppStrings.put(AudioFeatures.MICRO_EXPL, MainActivity.microExplo);
    }
    protected static final Map<String, String> eventAppStringsToClassNames;
    static {
    	eventAppStringsToClassNames = new HashMap<String, String>();
    	eventAppStringsToClassNames.put(MainActivity.water, AudioFeatures.BOILING);
    	eventAppStringsToClassNames.put(MainActivity.microDone, AudioFeatures.MICRO_DONE);
    	eventAppStringsToClassNames.put(MainActivity.microExplo, AudioFeatures.MICRO_EXPL);
    }

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
        //tab3 = actionBar.newTab().setText("Analytics");
        
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
        //tab3.setTabListener(new MyTabListener(fragmentTab3));
        
        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        // actionBar.addTab(tab3);
        
        // Set up alarm
        alarmOn = true;
        rTone = RingtoneManager.getRingtone(getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        
        // Set up audio processing.
        Map<String, Double> sensitivities = new HashMap<String, Double>();
        sensitivities.put(AudioFeatures.BOILING, 0.25d);
        sensitivities.put(AudioFeatures.MICRO_DONE, 0.04d);
        sensitivities.put(AudioFeatures.MICRO_EXPL, 0.1d);
		kitchenEventDetector = new KitchenEventDetector(this, sensitivities);
		kitchenEventDetector.setOnKitchenEventListener(this);
		alertedMap = new HashMap<String, Boolean>();
		resetAlertedMap();
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
    	kitchenEventDetector.stopDetection();
    	inForeground= false;
    }
    
	@Override
	public void processKitchenEvent(String eventType) {
		final String eventTypeForThread = eventType;
		if (!alertedMap.get(eventType)) {
			alertedMap.put(eventType, true);
			runOnUiThread(new Runnable() {
				public void run() {
					alert(eventClassNamesToAppStrings.get(eventTypeForThread));
				}
			});
		}
	}
	
	// This now pops up the alerts toast in addition to sending an email/text (we can use this to test messaging capabilities for now)
	public void alert(String task) {
		String contentText="";
		int uniqueID = 0;
		MainActivity tab1 = ((MainActivity) fragmentTab1);
		if (task == water) {
			tasksToSelected.put(water, false);
			tab1.taskList.setItemChecked(0, tasksToSelected.get(water));
			contentText = waterMessage;
			uniqueID = 0;
		} else if (task == microDone) {
			tasksToSelected.put(microDone, false);
			tab1.taskList.setItemChecked(1, tasksToSelected.get(microDone));
			contentText = microDoneMessage;
			uniqueID = 1;
		} else if (task == microExplo) { 
			tasksToSelected.put(microExplo, false);
			tab1.taskList.setItemChecked(2, tasksToSelected.get(microExplo));
			contentText = microExploMessage;
			uniqueID = 2;
		}
		alert_message += "Alert: " + contentText + "\n";
		/*alt = new AlertDialog.Builder(this)
		    .setTitle(alert_title)
		    .setMessage(contentText)
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            // continue
		        }
		     });
		     alt.show();*/
		showDialog(DIALOG_ALERT);

		// sends a test email to the currently selected email address
		sendMessage(1);
		// sends a test text to the currently selected phone number
		sendMessage(2); 
		
		// sounds the alarm
		if (alarmOn) {
			rTone.play();
		}

		//Standard Android Notif if app not in foreground
		if (!inForeground) {
			if (mBuilder == null) {
				mBuilder = new NotificationCompat.Builder(this)
				.setSmallIcon(R.drawable.cookeaseiconsmall)
				.setContentTitle("CookEase Notification")
				.setAutoCancel(true);
			} 
			mBuilder.setContentText(contentText);

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
			mNotificationManager.notify(uniqueID, mBuilder.build());
			
		}
		//emily:added
		((MainActivity) fragmentTab1).setMic(areTasksSelected());
	}
	
	public boolean areTasksSelected() { //check if there are still tasks selected
		Log.d("tasks", tasksToSelected.toString());
		boolean toReturn = false;
		Set<String> temp = tasksToSelected.keySet();
		Iterator<String> iter = temp.iterator();
		while (iter.hasNext()) {
			if (tasksToSelected.get(iter.next())) {
				toReturn = true;
				break;
			} else {
				toReturn = false;
			}
		}
		if (!toReturn) {
			TextView instrView = (TextView) findViewById(R.id.textView6);
			if (instrView != null) {
				instrView.setText("Tap to listen for events:");
			}
		}
		return toReturn;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_ALERT:
			if (ad != null) { //Stack all messages in just one alert
				ad.dismiss();
			}
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(alert_message);
			builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) { 
					// reset alert_message
					alert_message = "";
					Set<String> temp = tasksToSelected.keySet();
					Iterator<String> iter = temp.iterator();
					/*while (iter.hasNext()) {
						String next = iter.next();
						if (tasksToSelected.get(next)) {
							String nextMessage = "";
							if (next == water) {
								nextMessage = waterMessage;
							} else if (next == microExplo) {
								nextMessage = microExploMessage;
							} else if (next == microDone) {
								nextMessage = microDoneMessage;
							}
							alert_message += "Alert: " + nextMessage + "\n";
						} 
					}*/
					Log.d("alert", alert_message);
				}
			});
			ad = builder.create();
			ad.show();
		}
		return super.onCreateDialog(id);
	}


	// Send email or text message, depending on which argument you pass in - 1 is email, 2 is text (phone number)
	public void sendMessage(int mtype) {
		//SharedPreferences texts = getSharedPreferences("texts", 0);
		if (mtype == 1) {
			if (NotificationsActivity.emails != null && NotificationsActivity.emails.size() > 0) {
				Set<String> emailNames = NotificationsActivity.emails.keySet();
				Iterator iter = emailNames.iterator();
				ArrayList<String> emails = new ArrayList<String>();
				Boolean emailSet;
				String next;
				int size = 0;
				while (iter.hasNext()) {
					next = (String) iter.next();
					emailSet = NotificationsActivity.emailOn.get(next);
					if (emailSet != null && emailSet == true) {
						emails.add(NotificationsActivity.emails.get(next));
						size++;
					}
				}
			
				int counter = 0;
				String[] toArr = new String[size];// You can add more emails here if necessary
				for (String name: emails) {
					if (name != null) {
						toArr[counter] = name;
						counter++;
					}
				}
		
				Log.d("EMAIL IS NOW:", toArr.toString());
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
				Set<String> textNames = NotificationsActivity.numbers.keySet();
				Iterator iter = textNames.iterator();
				ArrayList<String> numbers = new ArrayList<String>();
				Boolean textSet;
				int size = 0;
				String next;
				while (iter.hasNext()) {
					next = (String) iter.next();
					textSet = NotificationsActivity.textOn.get(next);
					if (textSet != null && textSet == true) {
						numbers.add(NotificationsActivity.numbers.get(next));
						size++;
					}
				}
				
				int counter = 0;
				String[] toArr = new String[size];// You can add more emails here if necessary
				for (String name: numbers) {
					if (name != null) {
						toArr[counter] = name;
						counter++;
					}
				}
			
				final String[] textnum = toArr;
				Log.d("textnumiswhat", ""+textnum.length);
				for (String s : textnum) {
					Log.d("textelement", s);
				}
		//		final String[] textnum = NotificationsActivity.numbers.values().toArray(new String[NotificationsActivity.numbers.size()]);
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

	protected void resetAlertedMap() {
		alertedMap.clear();
		alertedMap.put(AudioFeatures.BOILING, false);
		alertedMap.put(AudioFeatures.MICRO_DONE, false);
		alertedMap.put(AudioFeatures.MICRO_EXPL, false);
	}

}
