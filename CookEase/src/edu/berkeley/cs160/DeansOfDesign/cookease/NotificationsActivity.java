package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationsActivity extends Activity {

    public ArrayList<String> selectedTasks = new ArrayList<String>();
    private static TextView analyticsText = null;
    private static TextView homeText = null;
    private static TextView instructionsText = null;
    public String friends_title = "Who do you want to alert?";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		// Set up notifications button
		homeText = (TextView) findViewById(R.id.textView3);
		homeText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                	doHome(v);
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
		// Demo only, click instructions for friends list
		instructionsText = (TextView) findViewById(R.id.textView6);
		instructionsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
//		                	alert();
                	showFriends();  
                }
            }
        );
		
		ListView list = (ListView) findViewById(R.id.listView1);
        list.setClickable(true);

        final List<CustomList> listOfPhonebook = new ArrayList<CustomList>();
        listOfPhonebook.add(new CustomList("Alarm", "Happy Tone"));
        listOfPhonebook.add(new CustomList("Email", "joe@berkeley.edu"));
        listOfPhonebook.add(new CustomList("Text", "Armando Mota"));

        CustomListAdapter adapter = new CustomListAdapter(this, listOfPhonebook);

        list.setOnItemClickListener(new OnItemClickListener() {

	        String calYellow = "#FFAA3C";
	        String whiteSmoke = "#F5F5F5";
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long index) {
            	
  	            TextView item = (TextView) ((ViewGroup) view).getChildAt(position);
    	        String itemText ;
            	// User selected "Alarm"
            	if (position == 0) {
            		itemText = "Alarm";
            	}
            	// User selected "Email"
            	else if (position == 1) {
            		itemText = "Email";
            	}
            	// User selected "Text"
            	else {
            		itemText = "Text";
            	}
    	        if (selectedTasks.contains(itemText)) {
    	        	item.setTextColor(Color.parseColor(whiteSmoke));
//    	            item.setChecked(false);
    	        	int pos = selectedTasks.indexOf(itemText);
    	        	selectedTasks.remove(pos);	                
    	        } else {
//    	        	item.setBackgroundColor(Color.parseColor(yaleBlue));
    	        	item.setTextColor(Color.parseColor(calYellow));
//    	            item.setChecked(true);
    	        	selectedTasks.add(itemText);
    	        }
//                showToast(listOfPhonebook.get(position).getName());
            }
        });

        list.setAdapter(adapter);
    }

//    private void showToast(String message) {
//        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
//    }
		
/*		final ListView taskList = (ListView) findViewById(R.id.listView1);
		String tasks[] ={"Phone Alarm","Email a friend","Text a friend"};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]);
	    }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, list);
	    
	    taskList.setAdapter(adapter);
	    
	    for (int i=0; i < tasks.length-1; i++) {
	    	taskList.setItemChecked(i, true);
	    }

	    taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @SuppressLint("NewApi")
		@Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {

	          TextView item = (TextView) view;
	          
	          //The change color logic is here!
	          if(item.isChecked()) {
	              item.setTextColor(Color.BLACK);
	              item.setChecked(false);
	          }
	          else {
	              item.setTextColor(Color.RED);
	              item.setChecked(true);
	          }
	    	  
	        final String itemText = (String) parent.getItemAtPosition(position);
	        String yaleBlue = "#3C94D3";
	        String calYellow = "#FFAA3C";
	        String whiteSmoke = "#F5F5F5";
	        String grayBg = "#88676767";
	        if (selectedTasks.contains(itemText)) {
//	        	item.setBackgroundColor(Color.parseColor(grayBg));
	        	item.setTextColor(Color.parseColor(whiteSmoke));
//	            item.setChecked(false);
	        	int index = selectedTasks.indexOf(itemText);
	        	selectedTasks.remove(index);     
	        } else {
//	        	item.setBackgroundColor(Color.parseColor(yaleBlue));
	        	item.setTextColor(Color.parseColor(calYellow));
//	            item.setChecked(true);
	        	selectedTasks.add(itemText);
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
	    });		*/

	// User clicked on the Analytics button
	protected void doAnalytics(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(this, AnalyticsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// User clicked on the Analytics button
	protected void doHome(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(this, MainActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// Select-friend list
	private ArrayList mSelectedItems = new ArrayList<String>();
	public void showFriends() {
		String names[] ={"Daniel","Emily","Armando","Steven","Namkyu"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(NotificationsActivity.this);
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
		getMenuInflater().inflate(R.menu.notifications, menu);
		return true;
	}

}
