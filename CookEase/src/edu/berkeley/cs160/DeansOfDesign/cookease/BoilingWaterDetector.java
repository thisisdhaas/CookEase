package edu.berkeley.cs160.DeansOfDesign.cookease;

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
	public static final int SAMPLE_RATE = 16000;
	private AudioProc mAudioProc;
	private boolean mUseMic;
	private AudioFeaturizer mAudioFeaturizer;
	private Classifier mModel;
	private boolean mModelLoaded = false;
	private OnBoilingEventListener mOnBoilingEventListener;
	private double mSensitivity;
	private ArrayList<Integer> mDetectionHistory;
	private static final int HISTORY_SIZE = 100;
		
	public BoilingWaterDetector(Context context, double sensitivity) {
		mUseMic = true;
		mAudioProc = new AudioProc(SAMPLE_RATE, context);
		mAudioProc.setOnAudioEventListener(this);
		mAudioFeaturizer = new AudioFeaturizer();
		loadModel(context);
		mSensitivity = sensitivity;
		mDetectionHistory = new ArrayList<Integer>();
	}

	public void stopDetection() {
		if (mAudioProc.isRecording()) {
			mAudioProc.stop();
		}
	}
	
	public void startDetection() {
		mAudioProc.useMic = mUseMic;
		mAudioProc.listen();
		Log.d(TAG, "listening!");
	}
	
	@Override
	public void processAudioProcEvent(AudioEvent ae) {
		Log.v(TAG, "Got a sample!");
		if (!mModelLoaded) return;
		
		// featurize the sample
		Instance feature_vector = mAudioFeaturizer.run(ae);
		Log.v(TAG, "Got a feature vector!");
		Log.v(TAG, feature_vector.toString());
		
		// run the model on the sample
		String className;
		try {
			int predictedClassIndex = (int) mModel.classifyInstance(feature_vector);
			className = feature_vector.classAttribute().value(predictedClassIndex);
			Log.v(TAG, "predicted the audio class: " + className);
		} catch (Exception e) {
			e.printStackTrace();
			className = "-1";
		}
		
		// Update detection history
		if (className == "1") {
			mDetectionHistory.add(1);
		} else {
			mDetectionHistory.add(0);
		}
		mDetectionHistory.remove(0);
		
		// Check the detection history against the threshold for alert.
		double num_detections = 0;
		for (Integer reading : mDetectionHistory) {
			num_detections += reading;
		}
		double percentBoiling = num_detections / HISTORY_SIZE;
		boolean thresholdExceded = percentBoiling > mSensitivity;
		Log.d(TAG, "percentBoiling: " + percentBoiling);
		
		// call back if we heard boiling water
		if (thresholdExceded && mOnBoilingEventListener != null) {
			Log.d(TAG, "HEARD BOILING!");
			mOnBoilingEventListener.processBoilingEvent();	
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
