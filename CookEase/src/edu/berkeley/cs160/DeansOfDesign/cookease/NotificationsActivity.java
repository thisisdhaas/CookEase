package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationsActivity extends Fragment {

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
    static String alarm = "Alarm:";
    static String email = "Email:";
    static String text = "Text:";
    static String deftone = "Default Tone";
    static String selectedTone = deftone;
    static String selectedEmail = "Enter email";
    static String selectedText = "Enter phone #";
    static String selectedName = "Name";
    Button mButton;
    Ringtone rTone = null;
    Activity act;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		act = this.getActivity();
		act.setContentView(R.layout.activity_notifications);
		
		// Restore preferences
	    SharedPreferences settings = act.getSharedPreferences("settings", 0);
	    tasksToSelected.put(alarm, settings.getBoolean(alarm, true)); //by default, only alarm selected
	    tasksToSelected.put(email, settings.getBoolean(email, false));
	    tasksToSelected.put(text,settings.getBoolean(text, false));
	    SharedPreferences texts = act.getSharedPreferences("texts", 0);
	    if (rTone == null) {
	    	rTone = RingtoneManager.getRingtone(act.getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
	    	methodsToSelected.put(alarm, deftone);
	    } else {
	    	methodsToSelected.put(alarm, texts.getString(alarm, selectedTone));
	    }
	    methodsToSelected.put(email, texts.getString(email, selectedEmail));
	    methodsToSelected.put(text, texts.getString(text, selectedText));
	    
		// prototype only, click instructions to sound chosen alarm
		instructionsText = (TextView) act.findViewById(R.id.textView6);
		instructionsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
//		                	alert();
                	//showFriends();  
                	rTone.play();
                }
            }
        );
		
		taskList = (ListView) act.findViewById(R.id.listView2);
        taskList.setClickable(true);
        
        String tasks[] ={alarm, email, text};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }

        adapter = new CustomListAdapter(act, list);

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
    	        	//item.setBackgroundColor(Color.parseColor(greyBg));
    	            check.setChecked(false);
    	        	tasksToSelected.put(itemText, false);
    	        } else { //not selected yet
    	        	//item.setBackgroundColor(Color.parseColor(purpleBg));
    	            check.setChecked(true);
    	            tasksToSelected.put(itemText, true);
    	            // Even though the whole bar has been clicked, we still want to show the contact list
    	            // and set the chosen contact's email to selectedEmail
    	            if (position == 0){
    	            	Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
	            		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
	            		intent.putExtra("position", position);
	            		System.out.println("onclick");
	            		startActivityForResult(intent, 0);
    	            } else {
    	            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	            	startActivityForResult(intent, position);
    	            }
    	        }  
    	        
    	        
            }
        });

        taskList.setAdapter(adapter);
        
        
        return inflater.inflate(R.layout.activity_main, container, false);
    }

	/*// User clicked on the Analytics button
	protected void doAnalytics(View view) {
		// Launch Analytics page
    	Intent intent = new Intent(act, AnalyticsActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}
	
	// User clicked on the Home button
	protected void doHome(View view) {
		// Launch Home page
    	Intent intent = new Intent(act, TabActivity.class);
//    	String img = "sample1";
//    	intent.putExtra(EXTRA_MESSAGE, img);
        startActivity(intent);
	}*/
	
	// Select-friend list
	private ArrayList mSelectedItems = new ArrayList<String>();
	public void showFriends() {
		String names[] ={"Daniel","Emily","Armando","Steven","Namkyu"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);
        LayoutInflater inflater = act.getLayoutInflater();
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
	    String alarm = "Alarm:";
	    String email = "Email:";
	    String text = "Text:";

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
	   				//view.setBackgroundColor(Color.parseColor(purpleBg));
	   				cb.setChecked(true);
	   	 	} else {
	   	 		//view.setBackgroundColor(Color.parseColor(greyBg));
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
	            		
	    	        } else {
	    	        	// Show contact list and set the chosen contact's email or phone number to selectedEmail/selectedText
	    	        	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
	    	        	startActivityForResult(intent, pos);
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
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Log.d("RequestCode", requestCode + "");
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == 0) {
	             Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	             if (uri != null) {
	                 selectedTone = uri.toString();
	                 rTone = RingtoneManager.getRingtone(act.getBaseContext(), uri);
	                 String name;
	                 if (rTone.getTitle(act.getApplicationContext()).equals(RingtoneManager.getRingtone(act.getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).getTitle(act.getApplicationContext()))) {
	                	 //System.out.println("RTONE IS DEFAULT");
	                	 name = deftone;
	                 } else {
	                	 name = rTone.getTitle(act.getBaseContext());
	                 }
	                 methodsToSelected.put(alarm, name);
	                 tasksToSelected.put(alarm, true);
	                 SharedPreferences texts = act.getSharedPreferences("texts", 0);
	                 SharedPreferences.Editor editor = texts.edit();
	                 editor.putString(alarm, methodsToSelected.get(alarm));
	                 editor.commit();
	                 SharedPreferences settings = act.getSharedPreferences("settings", 0);
	                 editor = settings.edit();
	                 editor.putBoolean(alarm, tasksToSelected.get(alarm));
	                 editor.commit();
	                 System.out.println("onactresult");
	             }
			} else {
				Uri contactData = intent.getData();
				ContentResolver resolver = act.getContentResolver();
				Cursor cur =  resolver.query(contactData, null, null, null, null);
				if (cur.moveToFirst()) {
			      selectedName = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            	}
				String id = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
				if (requestCode == 1) {
	                // Get and set email to selectedEmail
	                Cursor emailCur = resolver.query(
	                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
	                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] {id}, null);
	                if (emailCur.moveToFirst()) {//this sets ContactEmail to the first email - this is not ideal if they have many
		                selectedEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
			            methodsToSelected.put(email, selectedEmail);
		                tasksToSelected.put(email, true);
		                SharedPreferences texts = act.getSharedPreferences("texts", 0);
		                SharedPreferences.Editor editor = texts.edit();
		                editor.putString(email, methodsToSelected.get(email));
		                editor.commit();
		                SharedPreferences settings = act.getSharedPreferences("settings", 0);
		                editor = settings.edit();
		                editor.putBoolean(email, tasksToSelected.get(email));
		                editor.commit();
		            } else {
		                Toast.makeText(act, "This contact has a null email.", Toast.LENGTH_SHORT).show();
		            }
	                emailCur.close();
				} else {
					// Get and set phone number to selectedText
			        Cursor phoneCur = resolver.query(
		                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {id}, null);
		            if (phoneCur.moveToFirst()) {
		  	            selectedText = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
		  	            methodsToSelected.put(text, selectedText);
		                tasksToSelected.put(text, true);
		                SharedPreferences texts = act.getSharedPreferences("texts", 0);
		                SharedPreferences.Editor editor = texts.edit();
		                editor.putString(text, methodsToSelected.get(text));
		                editor.commit();
		                SharedPreferences settings = act.getSharedPreferences("settings", 0);
		                editor = settings.edit();
		                editor.putBoolean(text, tasksToSelected.get(text));
		                editor.commit();
		            } else {
		                Toast.makeText(act, "This contact has no phone number(s).", Toast.LENGTH_SHORT).show();
		            }
		            phoneCur.close();
				}
			}
			// For testing purposes - to make sure these are changing appropriately
			//Log.d("Phone number is now: ", selectedText);
			//Log.d("Email is now: ", selectedEmail);
			//Log.d("Alarm tone is now: ", selectedTone);
        }
    }
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		act.getMenuInflater().inflate(R.menu.notifications, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();		
       // Restore preferences	   
       SharedPreferences settings = act.getSharedPreferences("settings", 0);
       tasksToSelected.put(alarm, settings.getBoolean(alarm, true));
	   tasksToSelected.put(email, settings.getBoolean(email, false));
	   tasksToSelected.put(text,settings.getBoolean(text, false));
	   SharedPreferences texts = act.getSharedPreferences("texts", 0); 
	   //System.out.println("resume alarm " + texts.getString(alarm, "poop"));
	   methodsToSelected.put(alarm, texts.getString(alarm, selectedTone));
	   methodsToSelected.put(email, texts.getString(email, selectedEmail));
	   methodsToSelected.put(text, texts.getString(text, selectedText));

	   //System.out.println("methodsToSelected is " + methodsToSelected);
	   adapter.notifyDataSetChanged();
    }

    @Override
	public void onPause(){
       super.onPause();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      SharedPreferences settings = act.getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(alarm, tasksToSelected.get(alarm));
      editor.putBoolean(email, tasksToSelected.get(email));
      editor.putBoolean(text, tasksToSelected.get(text));
      editor.commit();
      
      SharedPreferences texts = act.getSharedPreferences("texts", 0);
      editor = texts.edit();
      if (rTone.getTitle(act.getApplicationContext()).equals(RingtoneManager.getRingtone(act.getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).getTitle(act.getApplicationContext()))) {
       	 methodsToSelected.put(alarm, deftone);
      }
      editor.putString(alarm, methodsToSelected.get(alarm));
      editor.putString(email, methodsToSelected.get(email));
      editor.putString(text, methodsToSelected.get(text));
      editor.commit();
      
    }
}
