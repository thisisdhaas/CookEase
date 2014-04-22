package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.text.DecimalFormat;
import java.util.ArrayList;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import edu.berkeley.cs160.DeansOfDesign.cookease.AudioProc.OnAudioEventListener;

public class BoilingWaterDetector implements OnAudioEventListener {
	
	public interface OnBoilingEventListener {
		public void processBoilingEvent();
	}
	
	private static final String TAG = "AudioProc";
	private static final int mModelResourceID = R.raw.boiling_water_clf;
	public static final int SAMPLE_RATE = 8000;
	private AudioProc mAudioProc;
	private boolean mUseMic;
	private AudioFeaturizer mAudioFeaturizer;
	private Classifier mModel;
	private boolean mModelLoaded = false;
	private OnBoilingEventListener mOnBoilingEventListener;
	private double mSensitivity;
	private ArrayList<Double> mDetectionHistory;
	private static final double HISTORY_SIZE = 100d;
		
	public BoilingWaterDetector(Context context, double sensitivity) {
		mUseMic = true;
		mAudioProc = new AudioProc(SAMPLE_RATE, context);
		mAudioProc.setOnAudioEventListener(this);
		mAudioFeaturizer = new AudioFeaturizer();
		loadModel(context);
		mSensitivity = sensitivity;
		mDetectionHistory = new ArrayList<Double>();
	}

	public void stopDetection() {
		if (mAudioProc.isRecording()) {
			mDetectionHistory.clear();
			mAudioProc.stop();
		}
	}
	
	public void startDetection() {
		if (!mAudioProc.isRecording()) {
			mAudioProc.useMic = mUseMic;
			mAudioProc.listen();
			Log.d(TAG, "listening!");
		}
	}
	
	@Override
	public void processAudioProcEvent(AudioEvent ae) {
		//Log.v(TAG, "Got a sample!");
		if (!mModelLoaded) return;
		
		// featurize the sample
		Instance feature_vector = mAudioFeaturizer.run(ae);
		//Log.v(TAG, "Got a feature vector!");
		//Log.v(TAG, feature_vector.toString());
		
		// run the model on the sample
		String className;
		try {
			int predictedClassIndex = (int) mModel.classifyInstance(feature_vector);
			className = feature_vector.classAttribute().value(predictedClassIndex);
			//Log.v(TAG, "predicted the audio class: " + className);
		} catch (Exception e) {
			e.printStackTrace();
			className = "-1";
		}
		
		// Update detection history
		synchronized(this) {
			if (className == "1") {
				mDetectionHistory.add(1d);
				Log.d(TAG, "Classified sample as boiling!");
			} else {
				mDetectionHistory.add(0d);
			}
			if (mDetectionHistory.size() > HISTORY_SIZE) {
				mDetectionHistory.remove(0);
			}
		}
		
		// Check the detection history against the threshold for alert.
		double num_detections = 0d;
		for (Double reading : mDetectionHistory) {
			num_detections += reading;
		}
		double percentBoiling = num_detections / HISTORY_SIZE;
		boolean thresholdExceded = percentBoiling > mSensitivity;
		Log.v(TAG, "percentBoiling: " + new DecimalFormat("0.00").format(percentBoiling));
		
		// call back if we heard boiling water
		if (thresholdExceded && mOnBoilingEventListener != null) {
			Log.d(TAG, "Threshold exceded--HEARD BOILING!");
			mOnBoilingEventListener.processBoilingEvent();
			stopDetection();
		}
	}

	public void setOnBoilingEventListener(OnBoilingEventListener listener) {
		mOnBoilingEventListener = listener;
	}
	
	private void setModel(Classifier model) {
		mModel = model;
		mModelLoaded = true;
	}
	
	private void loadModel(Context context) {
		// For complex models serialization overflows the stack, so run in on a different thread with a big stack!
		final Resources resources = context.getResources();
		new Thread(null, new Runnable() {

			@Override
			public void run() {
				try {
					setModel((Classifier) SerializationHelper.read(resources.openRawResource(mModelResourceID)));
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error de-serializing model!");
				}		
			}
			
		}, "cookeaseModelLoader", 262144L).start();
	}
}
