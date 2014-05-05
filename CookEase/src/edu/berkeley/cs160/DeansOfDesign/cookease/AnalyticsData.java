package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.util.Date;

public class AnalyticsData {
		public static final int WATER = 0;
		public static final int MICROWAVE = 1;
	    //private variables
		
		     
	    //private variables
	    int _id;
	    String _date;
	    String _duration;
	    String _dataType;
	     
	    // Empty constructor
	    public AnalyticsData(){
	         
	    }
	    // constructor
	    public AnalyticsData(int id, String date, String duration, String dataType){
	        this._id = id;
	        this._date = date;
	        this._duration = duration;
	        this._dataType = dataType;
	    }
	    // constructor
	    public AnalyticsData(String date, String duration, String dataType){
	        this._date = date;
	        this._duration = duration;
	        this._dataType = dataType;
	    }
	     
	    // getting ID
	    public int getID(){
	        return this._id;
	    }
	     
	    // setting id
	    public void setID(int id){
	        this._id = id;
	    }
	     
	    // getting date
	    public String getDate(){
	        return this._date;
	    }
	     
	    // setting name
	    public void setDate(String date){
	        this._date = date;
	    }
	     
	    // getting phone number
	    public String getDuration(){
	        return this._duration;
	    }
	     
	    // setting phone number
	    public void setDuration(String duration){
	        this._duration = duration;
	    }
	    
	    // getting phone number
	    public String getDataType(){
	        return this._dataType;
	    }
	     
	    // setting phone number
	    public void setDataType(String dataType){
	        this._dataType = dataType;
	    }
}
