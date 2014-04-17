package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.view.ViewGroup;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;

public class NotificationsActivity extends Activity {

    public ArrayList<String> selectedTasks = new ArrayList<String>();
    private static TextView analyticsText = null;
    private static TextView homeText = null;
    private static TextView instructionsText = null;
    public String friends_title = "Who do you want to alert?";
    String greyBg = "#84a689";
    String purpleBg = "#a684a1";
    String white = "#ffffff";
    String darkPurple = "#1A0637";
    public HashMap<String, Boolean> tasksToSelected = new HashMap<String, Boolean>();
    public ListView taskList;
    CustomListAdapter adapter = null;
    String alarm = "Alarm";
    String email = "Email";
    String text = "Text";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		// Restore preferences
	    SharedPreferences settings = getSharedPreferences("settings", 0);
	    tasksToSelected.put(alarm, settings.getBoolean(alarm, true)); //by default, only alarm selected
	    tasksToSelected.put(email, settings.getBoolean(email, false));
	    tasksToSelected.put(text,settings.getBoolean(text, false));
		
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
		
		taskList = (ListView) findViewById(R.id.listView2);
        taskList.setClickable(true);

        final List<CustomList> listOfPhonebook = new ArrayList<CustomList>();
        listOfPhonebook.add(new CustomList(alarm, "Happy Tone"));
        listOfPhonebook.add(new CustomList(email, "joe@berkeley.edu"));
        listOfPhonebook.add(new CustomList(text, "Armando Mota"));

        CustomListAdapter adapter = new CustomListAdapter(this, listOfPhonebook);

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

  	      @SuppressLint("NewApi")
  		@Override
  	      public void onItemClick(AdapterView<?> parent, final View view,
  	          int position, long id) {

            	
            	CheckedTextView item = (CheckedTextView) view;
            	System.out.println(item);
    	        String itemText;
    	        
    	        // User selected "Alarm"
            	if (position == 0) {
            		itemText = alarm;
            	}
            	// User selected "Email"
            	else if (position == 1) {
            		itemText = email;
            	}
            	// User selected "Text"
            	else {
            		itemText = text;
            	}
            	
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
    	        
    	        if (position == 0) {
    	        	//pop up spinner? to select desired alarm
    	        	//(spinner should be selectable from notif screen if messed up first time)
    	        } else if (position == 1) {
    	        	//pop up option to enter email address
    	        	//if already entered before, old email will be displayed
    	        	//(should also be able to change email from notif screen if messed up first time)
    	        } else if (position == 2) {
    	        	//pop up address book to select contact
    	        	//(should also be able to select contact from notif screen if messed up the first time)
    	        }
            	
    	        /*if (selectedTasks.contains(itemText)) {
    	        	item.setTextColor(Color.parseColor(white));
//    	            item.setChecked(false);
    	        	int pos = selectedTasks.indexOf(itemText);
    	        	selectedTasks.remove(pos);	                
    	        } else {
//    	        	item.setBackgroundColor(Color.parseColor(yaleBlue));
    	        	item.setTextColor(Color.parseColor(darkPurple));
//    	            item.setChecked(true);
    	        	selectedTasks.add(itemText);
    	        }*/
//                showToast(listOfPhonebook.get(position).getName());
            }
        });

        taskList.setAdapter(adapter);
        
        taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    taskList.setItemChecked(0, tasksToSelected.get(alarm));
	    taskList.setItemChecked(1, tasksToSelected.get(email));
	    taskList.setItemChecked(2, tasksToSelected.get(text));
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
	
	public class CustomListAdapter extends BaseAdapter implements OnClickListener {
	    private Context context;

	    private List<CustomList> listPhonebook;
	    String alarm = "Alarm";
	    String email = "Email";
	    String text = "Text";

	    public CustomListAdapter(Context context, List<CustomList> listPhonebook) {
	        this.context = context;
	        this.listPhonebook = listPhonebook;
	    }

	    public int getCount() {
	        return listPhonebook.size();
	    }

	    public Object getItem(int position) {
	        return listPhonebook.get(position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup viewGroup) {
	    	View view = convertView;
	        if (view == null) {
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = inflater.inflate(R.layout.custom_notif_row, null);
	        }
	        
	        CheckedTextView temp = (CheckedTextView) view.findViewById(R.id.text1);
	   	 	if (position == 0) {
	   	 		temp.setText(alarm);
	   	 	} else if (position == 1) {
	   	 		temp.setText(email);
	   	 	} else if (position == 2) {
	   	 		temp.setText(text);
	   	 	}
	   	
	   	 	if ((position == 0 && tasksToSelected.get(alarm)) ||
	   			(position == 1 && tasksToSelected.get(email)) ||
	   			(position == 2 && tasksToSelected.get(text))) {
	   				view.setBackgroundColor(Color.parseColor(purpleBg));
	   	 	} else {
	   	 		view.setBackgroundColor(Color.parseColor(greyBg));
	   	 	}
	   	 	notifyDataSetChanged();
	   	 	return view;
	   	
	        /*TextView tvContact = (TextView) convertView.findViewById(R.id.tvContact);
	        tvContact.setText(entry.getName());

	        TextView tvPhone = (TextView) convertView.findViewById(R.id.tvMobile);
	        tvPhone.setText(entry.getPhone());*/

	        // Set the onClick Listener on this button
//	        Button btnRemove = (Button) convertView.findViewById(R.id.btnRemove);
//	        btnRemove.setFocusableInTouchMode(false);
//	        btnRemove.setFocusable(false);
//	        btnRemove.setOnClickListener(this);
	        // Set the entry, so that you can capture which item was clicked and
	        // then remove it
	        // As an alternative, you can use the id/position of the item to capture
	        // the item
	        // that was clicked.
//	        btnRemove.setTag(entry);

	        // btnRemove.setId(position);
	        

	        //return convertView;
	    }

	    @Override
	    public void onClick(View view) {
	    	CustomList entry = (CustomList) view.getTag();
	        listPhonebook.remove(entry);
	        // listPhonebook.remove(view.getId());
	        notifyDataSetChanged();

	    }

	    private void showDialog(CustomList entry) {
	        // Create and show your dialog
	        // Depending on the Dialogs button clicks delete it or do nothing
	    }

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.notifications, menu);
		return true;
	}
	
	@Override
    protected void onResume(){
		super.onResume();
		
       // Restore preferences
       SharedPreferences settings = getSharedPreferences("settings", 0);
       tasksToSelected = new HashMap<String, Boolean>();
       tasksToSelected.put(alarm, settings.getBoolean(alarm, true));
	   tasksToSelected.put(email, settings.getBoolean(email, false));
	   tasksToSelected.put(text,settings.getBoolean(text, false));
    }

    @Override
    protected void onPause(){
       super.onPause();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(alarm, tasksToSelected.get(alarm));
      editor.putBoolean(email, tasksToSelected.get(email));
      editor.putBoolean(text, tasksToSelected.get(text));

      // Commit the edits!
      editor.commit();
    }

}
