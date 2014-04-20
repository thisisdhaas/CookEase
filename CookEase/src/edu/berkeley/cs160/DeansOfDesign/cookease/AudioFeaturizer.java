package edu.berkeley.cs160.DeansOfDesign.cookease;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import android.util.Log;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.mfcc.MFCC;

public class AudioFeaturizer {
	private static final String TAG = "AudioFeaturizer";
	
	// TODO: get correct names for these.
	private static enum Feature {
			RMS0,
			BEAT0,
			MFCC0,
			MFCC1,
			MFCC2,
			MFCC3,
			MFCC4,
			MFCC5,
			MFCC6,
			MFCC7,
			MFCC8,
			MFCC9,
			MFCC10,
			MFCC11,
			MFCC12,
	};
	private static final Feature[] mFeatures = Feature.values(); 
	
	public AudioFeaturizer() {
	}
	
	public Instance run(AudioEvent ae) {
		// TODO: actually use tarsosdsp to build features.
		// Desired features: RMS, Beat salience, MFCC.
		// Right now, just generating random features.
		Instance featureVals = new DenseInstance(mFeatures.length);
		double mfcc[] = computeMFCC(ae);
		for (int i = 0; i < mFeatures.length; i++) {
			double featureVal = Math.random();
			boolean setFeatureVal = true;
			switch (mFeatures[i]) {
			case RMS0:
				featureVal = getRMSFeature(ae);
				break;
			case BEAT0:
				// jAudio wasn't using this... so let's leave it blank for now
				//featureVal = getBeatSalienceFeature(ae);
				setFeatureVal = false;
				break;
			case MFCC0:
				featureVal = mfcc[0];
				break;
			case MFCC1:
				featureVal = mfcc[1];
				break;
			case MFCC2:
				featureVal = mfcc[2];
				break;
			case MFCC3:
				featureVal = mfcc[3];
				break;
			case MFCC4:
				featureVal = mfcc[4];
				break;
			case MFCC5:
				featureVal = mfcc[5];
				break;
			case MFCC6:
				featureVal = mfcc[6];
				break;
			case MFCC7:
				featureVal = mfcc[7];
				break;
			case MFCC8:
				featureVal = mfcc[8];
				break;
			case MFCC9:
				featureVal = mfcc[9];
				break;
			case MFCC10:
				featureVal = mfcc[10];
				break;
			case MFCC11:
				featureVal = mfcc[11];
				break;
			case MFCC12:
				featureVal = mfcc[12];
				break;
			default:
				featureVal = Math.random(); // unimplemented feature--just make it random.
				break;
			}
			if (setFeatureVal) {
				featureVals.setValue(i, featureVal);
			}
		}
		return featureVals;
	}
	
	private double getRMSFeature(AudioEvent ae) {
		return ae.getRMS();
	}

	private double getBeatSalienceFeature(AudioEvent ae) {
		// TODO
		return 0.0;
	}
	
	private double[] computeMFCC(AudioEvent ae) {
		final MFCC mfcc = new MFCC(512, (int)ae.getSampleRate());
		mfcc.process(ae);
		float mfccs[] = mfcc.getMFCC();
		double mfccDoubles[] = new double[mfccs.length];
		for (int i = 0; i < mfccs.length; i++) {
			mfccDoubles[i] = (double) mfccs[i];
		}
		return mfccDoubles;
	}
}
