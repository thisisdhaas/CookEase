package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Fragment {

    public final static String EXTRA_MESSAGE = "edu.berkeley.cs160.DeansOfDesign.MESSAGE";
    private static TextView instructionText = null;
    
    public static String water = "Water boiling";
    public static String microDone = "Microwave Done";
    public static String microExplo = "Microwave Explosion";
    public static String other = "Other Kitchen Tasks";
    String greyBg = "#84a689";
    String purpleBg = "#a684a1";
    String green ="#7BF49B";
    String gray = "#F1D66A";
    String white = "#ffffff";
    //private Mail sendMail;
    String sendOkay = "";
    final int color = 0xFFFFFFFF;
	final int transparent = Color.argb(0, Color.red(color), Color.green(color), Color.blue(color));

    public String friends_title = "Who do you want to alert?";
  
    public ListView taskList;
    private StableArrayAdapter adapter = null;
    private TabActivity act;
    //public boolean inHomeScreen;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	        Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		act = (TabActivity) this.getActivity();
		act.setContentView(R.layout.activity_main);
		// Make a mail object to send email with
	    //sendMail = new Mail("cookease.app@gmail.com", "deansofdesign");
		
		// Restore preferences
	   /* SharedPreferences settings = act.getSharedPreferences("settings", 0);
	    tasksToSelected.put(water, settings.getBoolean(microDone, false));
	    tasksToSelected.put(microDone, settings.getBoolean(microDone, false));
	    tasksToSelected.put(microExplo,settings.getBoolean(microExplo, false));*/
	    //setMic(!act.kitchenEventDetector.isDisabled());
		if (act.userGreyedOut) {
			setMic(false);
		} else {
			setMic(act.areTasksSelected());
		}
	    
		// FOR TESTING ONLY, REMOVE LATER: click the instructions for alert
		instructionText = (TextView) act.findViewById(R.id.textView6);
		instructionText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                	act.alert(microDone); //hardcoded microDone for testing
                	act.alert(microExplo);
                }
                
            }
        );
		
		/*// Listen for selected tasks
		for (Map.Entry<String, Boolean> entry : TabActivity.tasksToSelected.entrySet()) {
			if (entry.getValue()) {
				act.kitchenEventDetector.startDetection(TabActivity.eventAppStringsToClassNames.get(entry.getKey()));
			}
		}*/
		
		taskList = (ListView) act.findViewById(R.id.listView1);
		String tasks[] ={water, microDone, microExplo};
		final ArrayList<String> list = new ArrayList<String>();
	    for (int i = 0; i < tasks.length; ++i) {
	      list.add(tasks[i]); 
	    }


        adapter = new StableArrayAdapter(act,
        		R.layout.custom_row, list);
	    
	    taskList.setAdapter(adapter);
	    taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	    	@SuppressLint("NewApi")
	    	@Override
	    	public void onItemClick(AdapterView<?> parent, final View view,
	    			int position, long id) {
	    		CheckedTextView item = (CheckedTextView) view;
	    		String itemText = (String) parent.getItemAtPosition(position);
	    		String eventClassName = TabActivity.eventAppStringsToClassNames.get(itemText);
	    		if (TabActivity.tasksToSelected.get(itemText)) { //selected already
	    			item.setBackgroundColor(Color.parseColor(gray));
	    			item.setChecked(false);
	    			TabActivity.tasksToSelected.put(itemText, false);
	    			act.kitchenEventDetector.stopDetection(eventClassName);
	    			act.alertedMap.put(eventClassName, false); // reset alerts so the event can get alerted again.
	    		} else { //not selected yet
	    			item.setBackgroundColor(Color.parseColor(green));
	    			item.setChecked(true);
	    			TabActivity.tasksToSelected.put(itemText, true);
	    			act.kitchenEventDetector.startDetection(eventClassName);
	    			TextView instrView = (TextView) act.findViewById(R.id.textView6);
	    			instrView.setText("Currently Listening for:");
	    		}
	    		//setMic(act.kitchenEventDetector.isDetecting());
	    		setMic(act.areTasksSelected());
	    	}
	    });   
	    taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	    taskList.setItemChecked(0, TabActivity.tasksToSelected.get(water));
	    taskList.setItemChecked(1, TabActivity.tasksToSelected.get(microDone));
	    taskList.setItemChecked(2, TabActivity.tasksToSelected.get(microExplo));
	    
	    //Disable/Enable task list based on whether app is listening
	    final ImageView mic = (ImageView) act.findViewById(R.id.img1);
	    mic.setOnClickListener(new View.OnClickListener() {
	    	   //@Override
	    	   public void onClick(View v) {
	    		   if (act.kitchenEventDetector.isDisabled()) {
	    			   act.userGreyedOut = false;
	    			   act.kitchenEventDetector.enable();
	    		   } else {
	    			   act.userGreyedOut = true;
	    			   act.kitchenEventDetector.disable();
	    		   }
	    		  setMic(!act.kitchenEventDetector.isDisabled());
	    	   }        
	    	});
	    
	   return inflater.inflate(R.layout.activity_main, container, false);
	}

	public void setMic(boolean greyIfFalse) {
		final ImageView mic = (ImageView) act.findViewById(R.id.img1);
		if (!greyIfFalse) {
			Log.d("setmic", "gray!");
			   mic.setColorFilter(transparent);
			   //grey out area
			   //taskLayout.setBackgroundColor(Color.parseColor("#ADADAD"));
			   //taskLayout.setAlpha(0.9f);
			   RelativeLayout greyOut = (RelativeLayout) act.findViewById(R.id.greyout);
			   greyOut.setBackgroundColor(Color.parseColor("#292929"));
			   greyOut.setAlpha(0.5f);
			   TextView notlistening = (TextView) act.findViewById(R.id.notlistening);
			   notlistening.setText("Not Listening");
			   //set instructiontextview unclickable
			   TextView tv = (TextView) act.findViewById(R.id.textView6);
			   tv.setClickable(false);
			   tv.setAlpha(0.2f);
			   //set listview unclickable
			   ListView lv = (ListView) act.findViewById(R.id.listView1);
			   lv.setEnabled(false);
			   lv.setAlpha(0.2f);
			   //set listening text
			   TextView listening = (TextView) act.findViewById(R.id.listening);
			   listening.setText("Tap To Listen");
			   act.kitchenEventDetector.disable();
			   
		   } else {//else change mic color to red, ungray out listview
			   Log.d("setmic","red!");
			   mic.setColorFilter(Color.parseColor("#E02200"));
//			   taskLayout.setBackgroundColor(Color.parseColor("#F1D66A"));
//			   taskLayout.setAlpha(0.7f);
			   RelativeLayout greyOut = (RelativeLayout) act.findViewById(R.id.greyout);
			   greyOut.setBackgroundColor(transparent);
			   greyOut.setAlpha(1);
			   TextView notlistening = (TextView) act.findViewById(R.id.notlistening);
			   notlistening.setText("");
			   //set instructiontextview clickable
			   TextView tv = (TextView) act.findViewById(R.id.textView6);
			   tv.setClickable(true);
			   tv.setAlpha(1);
			   //set listview clickable
			   ListView lv = (ListView) act.findViewById(R.id.listView1);
			   lv.setEnabled(true);
			   lv.setAlpha(1);
			   //set listening text
			   TextView listening = (TextView) act.findViewById(R.id.listening);
			   listening.setText("Listening...");
			   act.kitchenEventDetector.enable();
		   }
	}
	
	private class StableArrayAdapter extends ArrayAdapter<String> {
	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
	   // Context c;
	    
	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      //this.c = context;
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
	    	 Drawable dr = null;
	    	 Bitmap bitmap = null;
	    	 Drawable d = null;
	    	 if (position == 0) {
	    		 temp.setText(water);
	    		 dr = getResources().getDrawable(R.drawable.potboil);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 67 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 67, 67, true));
	    	 } else if (position == 1) {
	    		 temp.setText(microDone);
	    		 dr = getResources().getDrawable(R.drawable.microdone);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 100 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 100, 67, true));
	    	 } else if (position == 2) {
	    		 temp.setText(microExplo);
	    		 dr = getResources().getDrawable(R.drawable.microexplo);
	    		 bitmap = ((BitmapDrawable) dr).getBitmap();
	    		 // Scale it to 100 x 67
	    		 d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap,100, 67, true));
	    	 }
    		 temp.setCompoundDrawablesWithIntrinsicBounds(null, null, d, null);
	    	if ((position == 0 && TabActivity.tasksToSelected.get(water)) ||
	    			(position == 1 && TabActivity.tasksToSelected.get(microDone)) ||
	    			(position == 2 && TabActivity.tasksToSelected.get(microExplo))) {
	    				view.setBackgroundColor(Color.parseColor(green));
	    	} else {
	    		view.setBackgroundColor(Color.parseColor(gray));
	    	}
	    	adapter.notifyDataSetChanged();
	    	return view;
	    }
	    
	    @Override
	    public boolean isEnabled(int position) {
	        return !act.kitchenEventDetector.isDisabled();
	    }

	  }
	
	@Override
	public void onResume(){
		super.onResume();
		
       // Restore preferences
       /*SharedPreferences settings = act.getSharedPreferences("settings", 0);
       tasksToSelected = new HashMap<String, Boolean>();
       tasksToSelected.put(water, settings.getBoolean(water, false));
	   tasksToSelected.put(microDone, settings.getBoolean(microDone, false));
	   tasksToSelected.put(microExplo,settings.getBoolean(microExplo, false));*/
	   //setMic(act.kitchenEventDetector.isDetecting());
	   if (act.userGreyedOut) {
			setMic(false);
		} else {
			setMic(act.areTasksSelected());
		}
	   TextView instrView = (TextView) act.findViewById(R.id.textView6);
	   if (act.areTasksSelected()) {
		   instrView.setText("Currently Listening for:");
	   } else {
		   instrView.setText("Tap to listen for events:");
	   }
	   
    }

    @Override
	public void onPause(){
       super.onPause();
       //user left mic on but not listening to anything -- turn it off
       if (!act.userGreyedOut && !act.areTasksSelected()) {
    	   act.kitchenEventDetector.disable();
		} 
      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
     /* SharedPreferences settings = act.getSharedPreferences("settings", 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean(water, tasksToSelected.get(water));
      editor.putBoolean(microDone, tasksToSelected.get(microDone));
      editor.putBoolean(microExplo, tasksToSelected.get(microExplo));*/

      // Commit the edits!
      //editor.commit();
    }
    

    @Override
	public void onDestroy(){
    	super.onDestroy();
    }
}
