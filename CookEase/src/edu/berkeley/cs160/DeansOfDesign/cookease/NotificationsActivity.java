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
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    public HashMap<String, String> methodsToSelected = new HashMap<String, String>();
    public ListView taskList;
    CustomListAdapter adapter = null;
    String alarm = "Alarm";
    String email = "Email";
    String text = "Text";
    String deftone = "Default Tone";
    String selectedTone = deftone;
    String selectedEmail = "Enter email";
    String selectedText = "Enter phone #";
    Button mButton;
    Ringtone rTone = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notifications);
		
		// Restore preferences
	    SharedPreferences settings = getSharedPreferences("settings", 0);
	    tasksToSelected.put(alarm, settings.getBoolean(alarm, true)); //by default, only alarm selected
	    tasksToSelected.put(email, settings.getBoolean(email, false));
	    tasksToSelected.put(text,settings.getBoolean(text, false));
	    SharedPreferences texts = getSharedPreferences("texts", 0);
	    if (rTone == null) {
	    	rTone = RingtoneManager.getRingtone(getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
	    	methodsToSelected.put(alarm, deftone);
	    } else {
	    	methodsToSelected.put(alarm, texts.getString(alarm, selectedTone));
	    }
	    methodsToSelected.put(email, texts.getString(email, selectedEmail));
	    methodsToSelected.put(text, texts.getString(text, selectedText));
	    
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
		// prototype only, click instructions to sound chosen alarm
		instructionsText = (TextView) findViewById(R.id.textView6);
		instructionsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
//		                	alert();
                	//showFriends();  
                	rTone.play();
                }
            }
        );
		
		taskList = (ListView) findViewById(R.id.listView2);
        taskList.setClickable(true);

        /*final List<CustomList> listOfPhonebook = new ArrayList<CustomList>();
        listOfPhonebook.add(new CustomList(alarm, "Happy Tone"));
        listOfPhonebook.add(new CustomList(email, "joe@berkeley.edu"));
        listOfPhonebook.add(new CustomList(text, "Armando Mota"));*/
        
        String tasks[] ={alarm, email, text};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }

        adapter = new CustomListAdapter(this, list);

        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        	
  	      @SuppressLint("NewApi")
  	      @Override
  	      public void onItemClick(AdapterView<?> parent, final View view,
  	          int position, long id) {
  	    	  
            	RelativeLayout item = (RelativeLayout) view;
            	CheckBox check = (CheckBox) item.getChildAt(2);
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
    	            check.setChecked(false);
    	        	tasksToSelected.put(itemText, false);
    	        } else { //not selected yet
    	        	item.setBackgroundColor(Color.parseColor(purpleBg));
    	            check.setChecked(true);
    	            tasksToSelected.put(itemText, true);
    	        }  
    	        
    	        
            }
        });

        taskList.setAdapter(adapter);
        
        
      
    }

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

	    private List<String> list;
	    String alarm = "Alarm";
	    String email = "Email";
	    String text = "Text";

	    public CustomListAdapter(Context context, List<String> list) {
	        this.context = context;
	        this.list = list;
	    }

	    public int getCount() {
	        return list.size();
	    }

	    public Object getItem(int position) {
	        return list.get(position);
	    }

	    public long getItemId(int position) {
	        return position;
	    }

	    public View getView(int position, View convertView, ViewGroup viewGroup) {
	    	System.out.println("in get view");
	    	View view = convertView;
	    	final int pos = position;
	        if (view == null) {
	            LayoutInflater inflater = (LayoutInflater) context
	                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            view = inflater.inflate(R.layout.custom_notif_row, null);
	        }
	        
	        final TextView textview = (TextView) view.findViewById(R.id.text1);
	        Button b = (Button) view.findViewById(R.id.button1);
	        final CheckBox cb = (CheckBox) view.findViewById(R.id.check1);
	   	 	
	        //set text for notification method and button
	        if (position == 0) {
	   	 		textview.setText(alarm);
	   	 		b.setText(methodsToSelected.get(alarm));
	   	 	} else if (position == 1) {
	   	 		textview.setText(email);
	   	 		b.setText(methodsToSelected.get(email));
	   	 	} else if (position == 2) {
	   	 		textview.setText(text);
	   	 		b.setText(methodsToSelected.get(text));
	   	 	}
	   	
	        //set background color + ischecked of list item
	   	 	if ((position == 0 && tasksToSelected.get(alarm)) ||
	   			(position == 1 && tasksToSelected.get(email)) ||
	   			(position == 2 && tasksToSelected.get(text))) {
	   				view.setBackgroundColor(Color.parseColor(purpleBg));
	   				cb.setChecked(true);
	   	 	} else {
	   	 		view.setBackgroundColor(Color.parseColor(greyBg));
	   	 		cb.setChecked(false);
	   	 	}
	   	 	
	   	 	//if button clicked, we want to set the item to "checked"
	   	 	//and start intent to set alarm/email/text
	        b.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	//System.out.println("button clicked");
	            	if (pos == 0) {
	            		//TODO Emily: select default tone oncreate
	            		//System.out.println("set alarm clicked");
	    	        	//pop up intent to select desired alarm
	            		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
	            		intent.putExtra("position", pos);
	            		System.out.println("onclick");
	            		startActivityForResult(intent, 0);
	            		
	    	        } else if (pos == 1) {
	    	        	//TODO Armando
	    	        	//pop up option to enter email address
	    	        	//if already entered before, old email will be displayed
	    	        	//(should also be able to change email from notif screen if messed up first time): implemented
	    	        } else if (pos == 2) {
	    	        	//TODO Emily
	    	        	//pop up address book to select contact

	    	        }
	            }
	            
	            
	        });
	        
	   	 	
	   	 	notifyDataSetChanged();
	   	 	return view;
	   	

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
	        list.remove(entry);
	        // listPhonebook.remove(view.getId());
	        notifyDataSetChanged();

	    }

	    private void showDialog(CustomList entry) {
	        // Create and show your dialog
	        // Depending on the Dialogs button clicks delete it or do nothing
	    }

	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK && requestCode == 0) {
             Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
             if (uri != null) {
                 selectedTone = uri.toString();
                 rTone = RingtoneManager.getRingtone(getBaseContext(), uri);
                 String name;
                 if (rTone.getTitle(getApplicationContext()).equals(RingtoneManager.getRingtone(getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).getTitle(getApplicationContext()))) {
                	 //System.out.println("RTONE IS DEFAULT");
                	 name = deftone;
                 } else {
                	 name = rTone.getTitle(getBaseContext());
                 }
                 methodsToSelected.put(alarm, name);
                 tasksToSelected.put(alarm, true);
                 SharedPreferences texts = getSharedPreferences("texts", 0);
                 SharedPreferences.Editor editor = texts.edit();
                 editor.putString(alarm, methodsToSelected.get(alarm));
                 editor.commit();
                 SharedPreferences settings = getSharedPreferences("settings", 0);
                 editor = settings.edit();
                 editor.putBoolean(alarm, tasksToSelected.get(alarm));
                 editor.commit();
                 System.out.println("onactresult");
             } //else nothing
         }   //TODO Emily, Armando: add if-else cases for email, text  
			//logic: listview "item" aka relativelayout, and button, both are clickable. 
			//if item clicked: it goes from highlighted -> non or non -> highlighted (and selection intent triggered)
			//if button clicked: selection intent triggered and item selected (always)
			
        
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
       tasksToSelected.put(alarm, settings.getBoolean(alarm, true));
	   tasksToSelected.put(email, settings.getBoolean(email, false));
	   tasksToSelected.put(text,settings.getBoolean(text, false));
	   SharedPreferences texts = getSharedPreferences("texts", 0); 
	   //System.out.println("resume alarm " + texts.getString(alarm, "poop"));
	   methodsToSelected.put(alarm, texts.getString(alarm, selectedTone));
	   methodsToSelected.put(email, texts.getString(email, selectedEmail));
	   methodsToSelected.put(text, texts.getString(text, selectedText));

	   //System.out.println("methodsToSelected is " + methodsToSelected);
	   adapter.notifyDataSetChanged();
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
      editor.commit();
      
      SharedPreferences texts = getSharedPreferences("texts", 0);
      editor = texts.edit();
      if (rTone.getTitle(getApplicationContext()).equals(RingtoneManager.getRingtone(getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).getTitle(getApplicationContext()))) {
       	 methodsToSelected.put(alarm, deftone);
      }
      editor.putString(alarm, methodsToSelected.get(alarm));
      editor.putString(email, methodsToSelected.get(email));
      editor.putString(text, methodsToSelected.get(text));
      editor.commit();
      
    }

}
