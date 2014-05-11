package edu.berkeley.cs160.DeansOfDesign.wekamodeltrainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.sound.sampled.UnsupportedAudioFileException;

import edu.berkeley.cs160.DeansOfDesign.wekamodeltrainer.AudioFeaturizer;
import edu.berkeley.cs160.DeansOfDesign.wekamodeltrainer.AudioFeatures;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import be.hogent.tarsos.dsp.AudioDispatcher;
import be.hogent.tarsos.dsp.AudioEvent;
import be.hogent.tarsos.dsp.AudioProcessor;

public class WekaModelTrainer {
	private static final Map<String, String> wavToClass;
	static
	{
		wavToClass = new HashMap<String, String>();
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/boiling.wav", 
				AudioFeatures.BOILING);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/boiling2.wav", 
				AudioFeatures.BOILING);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/boiling3.wav", 
				AudioFeatures.BOILING);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/BoilingWater.wav", 
				AudioFeatures.BOILING);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/microdone.wav", 
				AudioFeatures.MICRO_DONE);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/microdone2.wav", 
				AudioFeatures.MICRO_DONE);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/microdone3.wav", 
				AudioFeatures.MICRO_DONE);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/microdone4.wav", 
				AudioFeatures.MICRO_DONE);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/MicrowaveAlarm.wav", 
				AudioFeatures.MICRO_DONE);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/noevent.wav", 
				AudioFeatures.NO_EVENT);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/noevent2.wav", 
				AudioFeatures.NO_EVENT);
		wavToClass.put("/Users/dhaas/Desktop/AudioTrainingData/popcorn.wav",
				AudioFeatures.MICRO_EXPL);
	}
	private static final String arffPath = "/Users/dhaas/Desktop/trainset.arff";
	private static final String modelPath = "/Users/dhaas/Desktop/event_clf.model";
	private static Instances trainingData;
	private static boolean doneFeaturizing = false;
	
	private static synchronized void addInstance(Instance instance) {
		trainingData.add(instance);
	}
	
	public static void main(String[] args) {
		// Featurize the audio
		System.out.println("Featurizing training data...");
		final AudioFeaturizer audioFeaturizer = new AudioFeaturizer();
		trainingData = new Instances(audioFeaturizer.getDataset());
		for (final Map.Entry<String, String> entry : wavToClass.entrySet()) {
			final String wavFile = entry.getKey();
			final String wavClass = entry.getValue();
			System.out.println("Loading and featurizing " + wavFile + "...");
			try {
				doneFeaturizing = false;
				AudioDispatcher dispatcher = AudioDispatcher.fromFile(new File(wavFile),1024, 0);
				dispatcher.addAudioProcessor(new AudioProcessor(){

					@Override
					public boolean process(AudioEvent audioEvent) {
						addInstance(audioFeaturizer.run(audioEvent, wavClass));
						return true;	
					}	

					@Override
					public void processingFinished() {
						System.out.println("Done!");
						doneFeaturizing = true;
					}
				});
				dispatcher.run();
			} catch (UnsupportedAudioFileException e2) {
				// TODO Auto-generated catch block
				//e2.printStackTrace();
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				//e2.printStackTrace();
			}
		
			// featurize files serially
			while (!doneFeaturizing) continue;
		}

		// write the dataset to disk as an arff
		System.out.println("Done Featurizing. Writing arff to disk...");
		try {
			new BufferedWriter(new FileWriter(arffPath)).write(trainingData.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Done!");
		
//		// load the training data
//		System.out.println("Loading training data...");
//		Instances data;
//		try {
//			BufferedReader reader = new BufferedReader(new FileReader(arffPath));
//			data = new Instances(reader);
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Couldn't load .arff file!");
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Couldn't parse .arff file!");
//		}
//		data.setClassIndex(data.numAttributes() - 1);
//		System.out.println("Done!");
		
		// train and evaluate the model, with 10-fold stratified cross-validation
		System.out.println("Training the model...");
		Classifier model = (Classifier) new RandomForest();
		String options[] = {"-I", "100", "-K", "0"};
		try {
			((RandomForest) model).setOptions(options);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
		//	data.stratify(10);
			model.buildClassifier(trainingData);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't train the model!");
		}
		System.out.println("Done!");
		
		// test the model
		System.out.println("Evaluating the model with 10-fold cross-validation...");
		try {
			Evaluation modelEval = new Evaluation(trainingData);
			modelEval.crossValidateModel(model, trainingData, 10, new Random());
			//modelEval.evaluateModel(model, data);
			System.out.println(modelEval.toSummaryString(true));
			System.out.println(modelEval.toClassDetailsString());
			System.out.println(modelEval.toMatrixString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't load data for evaluation!");
		}
		System.out.println("Done!");
		
		// save the model
		System.out.println("Serializing the model to disk...");
		ObjectOutputStream oos;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(modelPath));
			oos.writeObject(model);
			oos.flush();
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Error serializing model!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Error serializing model!");
		}
		System.out.println("Done!");
	}

}
