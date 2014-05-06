package edu.berkeley.cs160.DeansOfDesign.cookease;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.SerializationHelper;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import edu.berkeley.cs160.DeansOfDesign.cookease.AudioProc.OnAudioEventListener;

public class KitchenEventDetector implements OnAudioEventListener {
	
	public interface OnKitchenEventListener {
		public void processKitchenEvent(String eventType);
	}
	
	private static final String TAG = "AudioProc";
	private static final int mModelResourceID = R.raw.event_clf;
	public static final int SAMPLE_RATE = 8000;
	private AudioProc mAudioProc;
	private boolean mUseMic;
	private AudioFeaturizer mAudioFeaturizer;
	private Classifier mModel;
	private boolean mModelLoaded = false;
	private OnKitchenEventListener mOnKitchenEventListener;
	private Map<String, Double> mSensitivities;
	private Map<String, ArrayList<Double>> mDetectionHistory;
	private static final double HISTORY_SIZE = 100d;
	private Set<String> mActiveEvents;
	private boolean mDisabled;
		
	public KitchenEventDetector(Context context, Map<String, Double> sensitivities, String[] activeEvents) {
		mUseMic = true;
		mAudioProc = new AudioProc(SAMPLE_RATE, context);
		mAudioProc.setOnAudioEventListener(this);
		mAudioFeaturizer = new AudioFeaturizer();
		loadModel(context);
		mSensitivities = sensitivities;
		mDetectionHistory = new HashMap<String, ArrayList<Double>>();
		mDetectionHistory.put(AudioFeatures.BOILING, new ArrayList<Double>());
		mDetectionHistory.put(AudioFeatures.MICRO_DONE, new ArrayList<Double>());
		mDetectionHistory.put(AudioFeatures.MICRO_EXPL, new ArrayList<Double>());
		mActiveEvents = new HashSet<String>();
		for (String eventType : activeEvents) {
			mActiveEvents.add(eventType);
		}
	}

	public KitchenEventDetector(Context context, Map<String, Double> sensitivities) {
		this(context, sensitivities, new String[]{});
	}
	
	public void stopDetection() {
		if (mAudioProc.isRecording()) {
			mAudioProc.stop();
			Log.d(TAG, "Stopped listening!");
		}
	}
	
	public void stopDetection(String eventType) {
		if (mActiveEvents.contains(eventType)) {
			Log.d(TAG, "Stopped listening to: " + eventType + "!");
			mActiveEvents.remove(eventType);
			mDetectionHistory.get(eventType).clear();
			if (mActiveEvents.isEmpty()) {
				stopDetection();
			}
		}
	}
	
	public void startDetection() {
		if (!mAudioProc.isRecording()) {
			mAudioProc.useMic = mUseMic;
			mAudioProc.listen();
			Log.d(TAG, "listening!");
		}
	}
	
	public void startDetection(String eventType) {
		Log.d(TAG, "Started listening to: " + eventType + "!");
		if (mActiveEvents.isEmpty()) {
			startDetection();
		}
		mActiveEvents.add(eventType);
	}
	
	public boolean isDetecting() {
		return mAudioProc.isRecording();
	}
	
	public boolean isDisabled() {
		return mDisabled;
	}
	
	public void disable() {
		stopDetection();
		mDisabled = true;
	}
	
	public void enable() {
		if (!mActiveEvents.isEmpty()) {
			startDetection();
		}
		mDisabled = false;
	}
	
	@Override
	public void processAudioProcEvent(AudioEvent ae) {
		try {
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
				className = AudioFeatures.NO_EVENT;
			}
			boolean detectedActiveEvent = mActiveEvents.contains(className);
			
			// Update detection history
			synchronized(this) {
				for (Map.Entry<String, ArrayList<Double>> entry : mDetectionHistory.entrySet()) {
					ArrayList<Double> histArray = entry.getValue();
					if (className == entry.getKey() && detectedActiveEvent) {
						histArray.add(1d);
						Log.d(TAG, "Classified sample as an event: " + className);
						
					}
					else if (className == entry.getKey()) {
						histArray.add(0d);
						Log.d(TAG, "Classified sample as an event: " + className + " but not active!");
					}
					else {
						histArray.add(0d);
					}
					if (histArray.size() > HISTORY_SIZE) {
						histArray.remove(0);
					}
				}
			}

			// Check the detection history against the threshold for alert.
			if (className != AudioFeatures.NO_EVENT) {
				double num_detections = 0d;
				for (Double reading : mDetectionHistory.get(className)) {
					num_detections += reading;
				}
				double percentDetected = num_detections / HISTORY_SIZE;
				boolean thresholdExceded = percentDetected > mSensitivities.get(className);
				Log.v(TAG, "Percent Detected " + className + ": " + new DecimalFormat("0.00").format(percentDetected));

				// call back if we heard an active event
				if (thresholdExceded && mOnKitchenEventListener != null && detectedActiveEvent) {
					Log.d(TAG, "Threshold exceded--HEARD EVENT " + className + "!");
					stopDetection(className);
					mOnKitchenEventListener.processKitchenEvent(className);
				}
			}
		}
		catch (RuntimeException e) {
			// Don't crash the app if something went wrong processing the sample
			Log.v(TAG, "Error processing audio sample, discarding...");
			e.printStackTrace();
		}
	}

	public void setOnKitchenEventListener(OnKitchenEventListener listener) {
		mOnKitchenEventListener = listener;
	}
	
	private void setModel(Classifier model) {
		mModel = model;
		mModelLoaded = true;
	}
	
	private void loadModel(Context context) {
		// For complex models serialization overflows the stack, so run in on a different thread with a big stack!
		Log.d(TAG, "Beginning model load...");
		final Resources resources = context.getResources();
		new Thread(null, new Runnable() {

			@Override
			public void run() {
				try {
					setModel((Classifier) SerializationHelper.read(resources.openRawResource(mModelResourceID)));
					Log.d(TAG, "Model load complete!");
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error de-serializing model!");
				}		
			}
			
		}, "cookeaseModelLoader", 262144L).start();
	}
}
