package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
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
    

    public String friends_title = "Who do you want to alert?";
    public ArrayList<String> selectedTasks = new ArrayList<String>();
    
    // For demo only, a timer:
    private Timer timer = new Timer(); 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
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
		
		final ListView taskList = (ListView) findViewById(R.id.listView1);
		String tasks[] ={"Water boil","Microwave finish","Microwave explosion","Other kitchen sounds"};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]);
	    }
//	    final StableArrayAdapter adapter = new StableArrayAdapter(this,
//	        android.R.layout.simple_list_item_1, list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, list);
	    
	    taskList.setAdapter(adapter);

	    taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @SuppressLint("NewApi")
		@Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {

	          CheckedTextView item = (CheckedTextView) view;
	          /*
	          //The change color logic is here!
	          if(item.isChecked()) {
	              item.setTextColor(Color.BLACK);
	              item.setChecked(false);
	          }
	          else {
	              item.setTextColor(Color.RED);
	              item.setChecked(true);
	          }*/
	    	  
	        String itemText = (String) parent.getItemAtPosition(position);
	        String yaleBlue = "#3C94D3";
	        String calYellow = "#FFAA3C";
	        String whiteSmoke = "#F5F5F5";
	        String grayBg = "#88676767";
	        String blueBg = "#7d94f0";
	        

	        if (selectedTasks.contains(itemText)) {
//	        	item.setBackgroundColor(Color.parseColor(grayBg));
	        	item.setBackgroundColor(Color.parseColor("#109494"));
	        	item.setTextColor(Color.parseColor(whiteSmoke));
	            item.setChecked(false);
	        	int index = selectedTasks.indexOf(itemText);
	        	selectedTasks.remove(index);     
	        } else {
	        	item.setBackgroundColor(Color.parseColor("#B139ED"));
//	        	item.setBackgroundColor(Color.parseColor(yaleBlue));
	        	item.setTextColor(Color.parseColor(whiteSmoke));
	            item.setChecked(true);
	        	selectedTasks.add(itemText);
		        // For demo only, run 5 second timer and pop up alert
//	            timer.schedule(new MyTimerTask(), 5000);
		        // End demo stuff here
	        }
	        
//	        view.animate().setDuration(2000).alpha(0)
//	            .withEndAction(new Runnable() {
//	              @Override
//	              public void run() {
//	                list.remove(item);
//	                adapter.notifyDataSetChanged();
//	                view.setAlpha(1);
//	              }
//	            });
	      }
	    });
	    
	    // Click all tasks to be on by default
//	    int firstPosition = taskList.getFirstVisiblePosition() - taskList.getHeaderViewsCount(); // This is the same as child #0
//	    for (int i = 0; i < 4; i++) {
//	    	 View rowView = taskList.getChildAt(i);
//		    	System.out.println("getChildCount is: "+taskList.getChildCount()+"\n");
//		    	System.out.println("i is: "+i+"\n");
//		    	System.out.println("curView is: "+rowView);
//             if(rowView != null)
//             {
//                 // do whatever you want here
//            	 rowView.performClick();
//             }
	    	
//	    	View curView = taskList.getChildAt(i);
//	    	System.out.println("i is: "+i+"\n");
//	    	System.out.println("curView is: "+curView);
//	    	curView.performClick();
//	    }
	    
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

	  private class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
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

	  }

	        
//	}
	
	// User clicked on the Analytics button
	protected void doAnalytics(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(this, AnalyticsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// User clicked on the Notifcations button
	protected void doNotifications(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(this, NotificationsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// Alert is ready!
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
	}
	
	// Select-friend list
	private ArrayList mSelectedItems = new ArrayList<String>();
	public void showFriends() {
		String names[] ={"Daniel","Emily","Armando","Steven","Namkyu"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.friends_list, null);
        alertDialog.setView(convertView)
        
        // Specify the list array, the items to be selected by default (null for none),
        // and the listener through which to receive callbacks when items are selected
               .setMultiChoiceItems(names, null,
                          new DialogInterface.OnMultiChoiceClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which,
                           boolean isChecked) {
                       if (isChecked) {
                           // If the user checked the item, add it to the selected items
                           mSelectedItems.add(which);
                       } else if (mSelectedItems.contains(which)) {
                           // Else, if the item is already in the array, remove it 
                           mSelectedItems.remove(Integer.valueOf(which));
                       }
                   }
               });
        alertDialog.setTitle(friends_title);
        
	    alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     });
	    
	    alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     });
	    
//        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
//        lv.setAdapter(adapter);
        alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
