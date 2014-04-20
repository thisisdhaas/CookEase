package edu.berkeley.cs160.DeansOfDesign.cookease;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.SerializationHelper;
import android.content.Context;
import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import edu.berkeley.cs160.DeansOfDesign.cookease.AudioProc.OnAudioEventListener;

public class BoilingWaterDetector implements OnAudioEventListener {
	private static final String TAG = "AudioProc";
	private static final int mModelResourceID = R.raw.boiling_water_clf;
	public static final int SAMPLE_RATE = 16000;
	private AudioProc mAudioProc;
	private boolean mUseMic;
	private AudioFeaturizer mAudioFeaturizer;
	private Classifier mModel;
		
	public BoilingWaterDetector(Context context) {
		mUseMic = true;
		mAudioProc = new AudioProc(SAMPLE_RATE, context);
		mAudioProc.setOnAudioEventListener(this);
		mAudioFeaturizer = new AudioFeaturizer();
		try {
			mModel = (Classifier) SerializationHelper.read(context.getResources().openRawResource(mModelResourceID));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Couldn't deserialize model!");
		}
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
		
		// featurize the sample
		Instance feature_vector = mAudioFeaturizer.run(ae);
		Log.v(TAG, "Got a feature vector!");
		Log.v(TAG, feature_vector.toString());
		
		// run the model on the sample
		double predictedClass = 0;
		try {
			predictedClass = mModel.classifyInstance(feature_vector);
			Log.d(TAG, "predicted the audio class!");
			Log.d(TAG, ""+predictedClass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			predictedClass = -1;
		}
		
		// call back if we heard boiling water
		if (predictedClass == 1) {
			// Do stuff
			Log.d(TAG, "HEARD BOILING!");
		}
	}

}
