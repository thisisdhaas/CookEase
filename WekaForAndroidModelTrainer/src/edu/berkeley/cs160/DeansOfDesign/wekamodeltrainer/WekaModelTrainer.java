package edu.berkeley.cs160.DeansOfDesign.wekamodeltrainer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;

public class WekaModelTrainer {

	private static final String arffPath = "/Users/dhaas/Desktop/minimal.arff";
	private static final String modelPath = "/Users/dhaas/Desktop/boiling_water_clf.model";
	
	public static void main(String[] args) {
		// load the training data
		System.out.println("Loading training data...");
		Instances data;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(arffPath));
			data = new Instances(reader);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't load .arff file!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't parse .arff file!");
		}
		data.setClassIndex(data.numAttributes() - 1);
		System.out.println("Done!");
		
		// train and evaluate the model, with 10-fold stratified cross-validation
		System.out.println("Training the model...");
		Classifier model = (Classifier) new RandomForest();
		String options[] = { "-D"};
		try {
			((RandomForest) model).setOptions(options);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		try {
		//	data.stratify(10);
			model.buildClassifier(data);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Couldn't train the model!");
		}
		System.out.println("Done!");
		
		// test the model
		System.out.println("Evaluating the model with 10-fold cross-validation...");
		try {
			Evaluation modelEval = new Evaluation(data);
			modelEval.crossValidateModel(model, data, 10, new Random());
			//modelEval.evaluateModel(model, data);
			System.out.println(modelEval.toSummaryString());
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
