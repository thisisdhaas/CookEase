package edu.berkeley.cs160.DeansOfDesign.wekamodeltrainer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.mfcc.MFCC;

public class AudioFeaturizer {
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
	private static final HashMap<Feature,String> FeatureNames;
	static
	{
		FeatureNames = new HashMap<Feature, String>();
		FeatureNames.put(Feature.RMS0, "Root Mean Square0");
		FeatureNames.put(Feature.BEAT0, "Strength Of Strongest Beat0");
		FeatureNames.put(Feature.MFCC0, "MFCC0");
		FeatureNames.put(Feature.MFCC1, "MFCC1");
		FeatureNames.put(Feature.MFCC2, "MFCC2");
		FeatureNames.put(Feature.MFCC3, "MFCC3");
		FeatureNames.put(Feature.MFCC4, "MFCC4");
		FeatureNames.put(Feature.MFCC5, "MFCC5");
		FeatureNames.put(Feature.MFCC6, "MFCC6");
		FeatureNames.put(Feature.MFCC7, "MFCC7");
		FeatureNames.put(Feature.MFCC8, "MFCC8");
		FeatureNames.put(Feature.MFCC9, "MFCC9");
		FeatureNames.put(Feature.MFCC10, "MFCC10");
		FeatureNames.put(Feature.MFCC11, "MFCC11");
		FeatureNames.put(Feature.MFCC12, "MFCC12");
	}
	private static final Feature[] mFeatures = Feature.values(); 
	private static final Attribute ClassFeature;
	static
	{
		ArrayList<String> attrValues = new ArrayList<String>();
		attrValues.add("-1");
		attrValues.add("1");
		ClassFeature = new Attribute("Is Boiling Sound", attrValues);
	}
	private Instances dataset;
	
	public AudioFeaturizer() {
		// set up the data headers/attributes
		ArrayList<Attribute> attInfo = new ArrayList<Attribute>(mFeatures.length);
		for (Feature feature : mFeatures) {
			attInfo.add(new Attribute(FeatureNames.get(feature)));
		}
		attInfo.add(ClassFeature);
		dataset = new Instances("TestInstances", attInfo, 0);
		dataset.setClassIndex(ClassFeature.index());
	}
	
	public Instances getDataset(){
		return dataset;
	}
	
	public Instance run(AudioEvent ae, Boolean positiveClass) {
		Instance featureVals = new DenseInstance(mFeatures.length+1);
		Instances data = new Instances(dataset);
		featureVals.setDataset(data);
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
		if (positiveClass) {
			featureVals.setValue(mFeatures.length, "1");
		} else {
			featureVals.setValue(mFeatures.length, "-1");
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
		PrintStream out = System.out;
		System.setOut(new PrintStream(new NullOutputStream()));
		final MFCC mfcc = new MFCC(512, (int)ae.getSampleRate());
		mfcc.process(ae);
		float mfccs[] = mfcc.getMFCC();
		double mfccDoubles[] = new double[mfccs.length];
		for (int i = 0; i < mfccs.length; i++) {
			mfccDoubles[i] = (double) mfccs[i];
		}
		System.setOut(out);
		return mfccDoubles;
	}
	
	/**Writes to nowhere*/
	public class NullOutputStream extends OutputStream {
	  @Override
	  public void write(int b) throws IOException {
	  }
	}
}
