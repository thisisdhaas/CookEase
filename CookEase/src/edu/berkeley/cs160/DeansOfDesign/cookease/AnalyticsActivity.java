package edu.berkeley.cs160.DeansOfDesign.cookease;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class AnalyticsActivity extends Activity {

	private static TextView homeText;
	private static TextView notificatonsText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_analytics);
		
		// Set up home button
		homeText = (TextView) findViewById(R.id.textView3);
		homeText.setOnClickListener(

            new View.OnClickListener() {

                public void onClick(View v) {
                	doHome(v);               	
                }
            }
        );
		
		// Set up analytics button
		notificatonsText = (TextView) findViewById(R.id.textView4);
		notificatonsText.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
//                	alert();
                	doNotifications(v);  
                }
            }
        );
	}
	
	public void doHome(View view) {
    	Intent intent = new Intent(this, MainActivity.class);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.analytics, menu);
		return true;
	}

}
