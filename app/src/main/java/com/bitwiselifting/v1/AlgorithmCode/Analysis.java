package com.bitwiselifting.v1.AlgorithmCode;


import java.util.ArrayList;

import com.bitwiselifting.v1.AlgorithmCode.StaticMethods;
public class Analysis {
	static float lowTolerance = 6; //if max above this value...
	static double zeroTolerance = .1;

	public static void main(String[] args){
		int numReps = 0;
		/*
		 * Read in our file
		 */
		ArrayList<ArrayList<Float>> values = StaticMethods.readFile("./data/15mspc.csv");
		
		/*
		 * Gather relevant columns of data needed to perform analysis and filter data if needed
		 */
		ArrayList<Float> dataColumn = StaticMethods.getColumn(values, 5);
		ArrayList<Float> filteredData = StaticMethods.exponentialFilter(dataColumn);
		values = StaticMethods.replaceColumn(values, filteredData, 5);
		
		/*
		 * Write the output of the filtered data for testing
		 */
		//StaticMethods.writeFile(values, "./data/output.csv");
		
		/*
		 * Find the zeros in the data with tolerance specified by zeroTolerance
		 */
		ArrayList<Float> temp = StaticMethods.findZeros(values, 5, 0,zeroTolerance);
		ArrayList<Integer> timesBetweenReps = new ArrayList<Integer>();
		
		/*
		 * Differentiate the reps and determine the time between them.
		 */
		for(int i = 0; i<temp.size()-1; i++ ) {
			//find time between zeroes
			int locTime1 = StaticMethods.getColumn(values, 0).indexOf(temp.get(i)); //for the first zero get time 1
			int locTime2 = StaticMethods.getColumn(values, 0).indexOf(temp.get(i+1)); //for the second zero get time 2
			int timebetween = locTime2 - locTime1; 									//subtract them
			Float max = StaticMethods.findMinAndMax(StaticMethods.getColumn(values, 5), locTime1, locTime2)[1]; //find the max between the two time points
			if(max>lowTolerance) {													//is the max between them greater than out threshold?
				System.out.println(timebetween + " | " + locTime1 + " | " + locTime2); //that's a rep
				numReps++; //increment reps
				timesBetweenReps.add(timebetween); //record the time this rep took
			}
		}
		
		/*
		 * Save a value of the average time between reps to a file.
		 * More values can be added as necessary.
		 */
		ArrayList<Double> valuesToWrite = new ArrayList<Double>();
		
		//create values
		Double average = 0.0;
		for(int i: timesBetweenReps){
			average+=(double)i;
		}
		average = 1.0*average/timesBetweenReps.size();
		//add them
		valuesToWrite.add(average);
		
		StaticMethods.writeFile1DDArr(valuesToWrite, "./data/CalibrationValues.csv");
		
		
		
    }
}



