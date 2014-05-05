package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NotificationsActivity extends Fragment {

    private static TextView analyticsText = null;
    private static TextView homeText = null;
    private static TextView instructionsText = null;
    public static ArrayList<String> contactsSelected;
    public static HashMap<String, String> emails;
    public static HashMap<String, String> numbers;
    public static HashMap<String, Boolean> emailOn;
    public static HashMap<String, Boolean> textOn;
    public ListView addedList;
    CustomListAdapter adapter = null;
    static String deftone = "Default Tone";
    static String selectedTone = deftone;
    
    
    Button mButton;
    Ringtone rTone = null;
    Activity act;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		act = this.getActivity();
		act.setContentView(R.layout.activity_notifications);
		restorePrefs();
		addedList = (ListView) act.findViewById(R.id.listView2);
        adapter = new CustomListAdapter(act, contactsSelected);
        addedList.setItemsCanFocus(true);
        addedList.setAdapter(adapter);
        
        Button addbutton = (Button) act.findViewById(R.id.add_contact_button);
        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            	startActivityForResult(intent, 1);
            }
        });
        return inflater.inflate(R.layout.activity_main, container, false);
    }

	
	public class CustomListAdapter extends BaseAdapter implements OnClickListener {
		
	    private Context context;
	    private List<String> list;
	    private String name = null;
	    private Boolean tOn;
	    private Boolean eOn;

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
	    	final int pos = position;
	    	View view = convertView;
	    	if (view == null) {
	        	view = View.inflate(context, R.layout.added_contact_row, null);
	        }
	    	view.setClickable(true);
	        view.setFocusable(true);
	    	TextView textview = (TextView) view.findViewById(R.id.contact_text);
	    	final Button ebutton = (Button) view.findViewById(R.id.emailbutton);
	        final Button tbutton = (Button) view.findViewById(R.id.textbutton);
	        if (contactsSelected != null) {
		        name = contactsSelected.get(pos);
		        textview.setText(name);
		        if (emails.containsKey(name) && emailOn.get(name)) {
		        	ebutton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.email, 0, 0);
	        	} else {
	        		ebutton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.emaildark, 0, 0);
		        }
		        if (numbers.containsKey(name) && textOn.get(name)) {
		        	tbutton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.text, 0, 0);
	        	} else {
	        		tbutton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.textdark, 0, 0);
		        }
	        } else {
	        	// There are no contacts, do nothing
	        }          
	        ebutton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	if (emails.containsKey(name)) {
	            		if (emailOn.get(name)) {
	            			emailOn.put(name, false);
	            		} else {
	            			emailOn.put(name, true);
	            		}
	            		notifyDataSetChanged();
	        		} else {
	        			Toast.makeText(act, "This contact has no email.", Toast.LENGTH_SHORT).show();
	        		}
	            }
	        });
	        tbutton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View view) {
	            	if (numbers.containsKey(name)) {
	            		if (textOn.get(name)) {
	            			textOn.put(name, false);
	            		} else {
	            			textOn.put(name, true);
	            		}
	            		notifyDataSetChanged();
	        		} else {
	        			Toast.makeText(act, "This contact has no phone number.", Toast.LENGTH_SHORT).show();
	        		}
	            }
	        });
	        notifyDataSetChanged();
	        return view;
	    }
	    
	    @Override
	    public void onClick(View view) {
	    	// To implement the CustomListAdapter, we need at least a stub of a method here
	    }
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (resultCode == Activity.RESULT_OK) {
			// Change alarm
			if (requestCode == 0) {
	             Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
	             if (uri != null) {
	                 selectedTone = uri.toString();
	                 rTone = RingtoneManager.getRingtone(act.getBaseContext(), uri);
	                 String name;
	                 if (rTone.getTitle(act.getApplicationContext()).equals(RingtoneManager.getRingtone(act.getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)).getTitle(act.getApplicationContext()))) {
	                	 name = deftone;
	                 } else {
	                	 name = rTone.getTitle(act.getBaseContext());
	                 }
	             }
	        // Add email/text number
			} else {
				Uri contactData = intent.getData();
				ContentResolver resolver = act.getContentResolver();
				Cursor emailcur =  resolver.query(contactData, null, null, null, null);
				String selectedName = null;
				if (emailcur.moveToFirst()) {
			      selectedName = emailcur.getString(emailcur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			      if (contactsSelected.contains(selectedName)) {
			    	  Toast.makeText(act, "This contact has already been selected.", Toast.LENGTH_SHORT).show();
			    	  return;
			      } else {
			    	  contactsSelected.add(selectedName);
			      }
				}
				String id = emailcur.getString(emailcur.getColumnIndexOrThrow(ContactsContract.Contacts._ID));
				
                // Set email to selectedEmail
                Cursor emailCur = resolver.query(
                    ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[] {id}, null);
                if (emailCur.moveToFirst()) {//this sets ContactEmail to the first email - this is not ideal if they have many
	                String selectedEmail = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
	                emails.put(selectedName, selectedEmail);
	                emailOn.put(selectedName, true);
	            } else {
	                // Contact has no email, so do nothing
	            }
                emailCur.close();

				// Set phone number to selectedText
		        Cursor phoneCur = resolver.query(
	                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
	                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] {id}, null);
	            if (phoneCur.moveToFirst()) {
	  	            String selectedText = phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));  
	  	            numbers.put(selectedName, selectedText);
	  	            textOn.put(selectedName, true);
	            } else {
	                // Contact has no phone number, so do nothing
	            }
	            phoneCur.close();
			}
        }
    }

	
	
	
	public void openAlarms(View v) {
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    	startActivityForResult(intent, 0);
	}

	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		act.getMenuInflater().inflate(R.menu.notifications, menu);
		return true;
	}
	
	
	public void restorePrefs() {
		if (contactsSelected == null || contactsSelected.isEmpty()) {
			contactsSelected = new ArrayList<String>();
		}
		if (emails == null || emails.isEmpty()) {
			emails = new HashMap<String, String>();
		}
		if (numbers == null || numbers.isEmpty()) {
			numbers = new HashMap<String, String>();
		}
		if (emailOn == null || emailOn.isEmpty()) {
			emailOn = new HashMap<String, Boolean>();
		}
		if (textOn == null || textOn.isEmpty()) {
			textOn = new HashMap<String, Boolean>();
		}
		if (rTone == null) {
			rTone = RingtoneManager.getRingtone(act.getBaseContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		}
	}
	
	
	@Override
    public void onResume(){
		super.onResume();
		adapter.notifyDataSetChanged();
    }
	
    
    // We can get ride of this overridden method if we want to use the back android button and still have it 
    // remember the info when we come back.
    @Override
    public void onDestroy(){
    	contactsSelected = null;
    	emails = null;
    	numbers = null;
    	emailOn = null;
    	textOn = null;
    	super.onDestroy();
    }
}
