package com.myproject.config;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import scala.Tuple2;

@Configuration
public class AppConfig {

    
    @Bean
    public SparkConf sparkConf() {
        return new SparkConf().setAppName("SparkSpringBootApp").setMaster("local");
    }

    @Bean
    public JavaSparkContext javaSparkContext(SparkConf sparkConf) {
        return new JavaSparkContext(sparkConf);
    }
	
	/**
	 * Is algo correct (T/F)
	 * What algo predicted (positive/negative)?
	 * 
	 * @param predictionAndLabels: JavaPairRDD<Prediction, Actual_Label>
	 */
	public static void printFScoreBinaryClassfication(JavaPairRDD<Double, Double> predictionAndLabels) {
		long truePositives = predictionAndLabels.filter(pAndL -> pAndL._1.equals(pAndL._2) && pAndL._1.equals(1.0)).count();
		long falsePositives = predictionAndLabels.filter(pAndL -> !pAndL._1.equals(pAndL._2) && pAndL._1.equals(1.0)).count();
		long falseNegatives = predictionAndLabels.filter(pAndL -> !pAndL._1.equals(pAndL._2) && pAndL._1.equals(0.0)).count();
		printAndGetFScore(truePositives, falsePositives, falseNegatives);
	}
	
	private static double printAndGetFScore(long truePositives, long falsePositives, long falseNegatives){
		System.out.println("True positives: " + truePositives);
		System.out.println("False positives: " + falsePositives);
		System.out.println("False negatives: " + falseNegatives);

		Double precision = (double) truePositives/(truePositives + falsePositives);
		if(precision.equals(Double.NaN))
			precision = 0.0;
		
		Double recall = (double) truePositives/(truePositives + falseNegatives);
		if(recall.equals(Double.NaN))
			recall = 0.0;
		
		Double fScore = 2 * precision * recall /(precision + recall);
		if(fScore.equals(Double.NaN))
			fScore = 0.0;
		
		System.out.println("Precision: " + precision);
		System.out.println("Recall: " + recall);
		System.out.println("FScore: " + fScore);
		return fScore;
	}
	
	
	public static void printFScoreMultiClassClassfication(JavaPairRDD<Double, Double> predictionAndLabels, 
			LinkedHashMap<String, Double> classLablsMap) {
		
		int classCount = classLablsMap.size();
		System.out.println(classCount);
		int[][] confusionMatrix = new int[classCount ][classCount ];
		List<Tuple2<Double, Double>> list = predictionAndLabels.collect();
		for(int index = 0; index < list.size(); index ++){
			Tuple2<Double, Double> pAndL = list.get(index);
			int row = pAndL._2.intValue();
			int prediction = pAndL._1.intValue();
			
			if(pAndL._1.equals(pAndL._2))
				confusionMatrix[row][row] = confusionMatrix[row][row] + 1;
			else
				confusionMatrix[row][prediction] = confusionMatrix[row][prediction] + 1;
		}
		
		printCombinedFScore(confusionMatrix, classLablsMap);
	}
	
	/**
	 * 
	 * @param confusionMatrix
	 * @param classLabelsMap: why LinkedHashMap? see comments above
	 */
	public static void printCombinedFScore(int[][] confusionMatrix, LinkedHashMap<String, Double> classLabelsMap){
		int classCount = confusionMatrix[0].length;
		int[] truePositives = new int[classCount];
		int[] falsePositives = new int[classCount];
		int[] falseNegatives= new int[classCount];
		double[] weights = getWeights(confusionMatrix);
		double[] fscores = getWeights(confusionMatrix);
		
		for(int index = 0; index < classCount; index++){
			truePositives[index] = getTruePositives(index, confusionMatrix);
			falsePositives[index] = getFalsePositives(index, confusionMatrix);
			falseNegatives[index] = getFalseNegatives(index, confusionMatrix);
		}
		
		for(int index = 0; index < classCount; index++){
			System.out.println("=================");
			System.out.println("for class: " + index + "--" + String.valueOf(classLabelsMap.keySet().toArray()[index]));
			fscores[index] = printAndGetFScore(truePositives[index], falsePositives[index], falseNegatives[index]);
			System.out.println("=================");
		}
		
		double combinedWeightedFScore = 0;
		for(int index = 0; index < classCount; index++)
			combinedWeightedFScore += fscores[index] * weights[index];
		
		System.out.println("Combined weighted average: " + combinedWeightedFScore);
	}

	/**
	 * @param confusionMatrix
	 * @return Returns weight based on percentage of values that belongs to a particular 
	 * label as compared to whole data set.
	 */
	private static double[] getWeights(int[][] confusionMatrix) {
		int total = 0;
		double[] weights = new double[confusionMatrix[0].length];
		
		for(int row = 0; row< confusionMatrix[0].length; row++){
			double rowTotal = 0.0;
			
			for(int col = 0; col < confusionMatrix[0].length; col++){
				total += confusionMatrix[row][col];
				rowTotal += confusionMatrix[row][col];
			}
			
			weights[row] = rowTotal;
		}
		
		for(int row = 0; row< confusionMatrix[0].length; row++)
			weights[row] = weights[row]/total;
		
		return weights;
	}

	private static int getFalseNegatives(int classIndex, int[][] confusionMatrix) {
		int sum = 0;
		for(int index = 0; index < confusionMatrix[0].length; index ++)
			if(index != classIndex)
				sum += confusionMatrix[classIndex][index];
		
		return sum;
	}

	private static int getFalsePositives(int classIndex, int[][] confusionMatrix) {
		int sum = 0;
		for(int index = 0; index < confusionMatrix[0].length; index ++)
			if(index != classIndex)
				sum += confusionMatrix[index][classIndex];
		
		return sum;
	}

	private static int getTruePositives(int classIndex, int[][] confusionMatrix) {
		return confusionMatrix[classIndex][classIndex];
	}
	
	public static String getResourcesDirectoryPath() {
		String tempFilePath = new File(".").getAbsolutePath();
		return tempFilePath.substring(0, tempFilePath.length() -1) + "src/main/resources/";
	}


}


