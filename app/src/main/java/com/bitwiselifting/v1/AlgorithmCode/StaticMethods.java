package com.bitwiselifting.v1.AlgorithmCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class StaticMethods {

	public static ExerciseData analyseData(ArrayList<ArrayList<Float>> values){
		float lowTolerance = 6; //if max above this value...
		double zeroTolerance = .1;
		int numReps = 0;
		/*
		 * Gather relevant columns of data needed to perform analysis and filter data if needed
		 */
		ArrayList<Float> dataColumn = StaticMethods.getColumn(values, 3);
		ArrayList<Float> tiltColumn = StaticMethods.getColumn(values, 2);
		ArrayList<Float> filteredData = StaticMethods.exponentialFilter(dataColumn);
		values = StaticMethods.replaceColumn(values, filteredData, 3);

		/*
		 * Write the output of the filtered data for testing
		 */
		//StaticMethods.writeFile(values, "./data/output.csv");

		/*
		 * Find the zeros in the data with tolerance specified by zeroTolerance
		 */
		ArrayList<Float> temp = StaticMethods.findZeros(values, 3, 0,zeroTolerance);
		ArrayList<Integer> timesBetweenReps = new ArrayList<Integer>();

		/*
		 * Differentiate the reps and determine the time between them.
		 */
		for(int i = 0; i<temp.size()-1; i++ ) {
			//find time between zeroes
			int locTime1 = StaticMethods.getColumn(values, 0).indexOf(temp.get(i)); //for the first zero get time 1
			int locTime2 = StaticMethods.getColumn(values, 0).indexOf(temp.get(i+1)); //for the second zero get time 2
			int timebetween = locTime2 - locTime1; 									//subtract them
			Float max = StaticMethods.findMinAndMax(StaticMethods.getColumn(values, 3), locTime1, locTime2)[1]; //find the max between the two time points
			if(max>lowTolerance) {													//is the max between them greater than out threshold?
				System.out.println(timebetween + " | " + locTime1 + " | " + locTime2); //that's a rep
				numReps++; //increment reps
				timesBetweenReps.add(timebetween); //record the time this rep took
			}
		}

		/*
		 * Save a value of the average time between reps
		 */
		ArrayList<Double> valuesToWrite = new ArrayList<Double>();

		//create values
		Double average = 0.0;
		for(int i: timesBetweenReps){
			average+=(double)i;
		}
		average = 1.0*average/timesBetweenReps.size();

		ExerciseData ex = new ExerciseData();
		ex.setNumReps(numReps);
		ex.setAverageTime(average);


		Float[] tilts = findMinAndMax(tiltColumn,0,tiltColumn.size()-1);
		if(tilts[0] > tilts[1]){
			ex.setMaxTilt(Math.round(tilts[0]));
		}
		else{
			ex.setMaxTilt(Math.round(tilts[1]));
		}
		return ex;
	}

	public static ArrayList<Float> exponentialFilter(ArrayList<Float> input){
		float lastValue = 0;
        double a = 0.9;
        int currLine = 0;
        ArrayList<Float> result = new ArrayList<Float>();
        for(float val: input) {
        	float value = val+(float)9.8;
        	value = (float)((1-a) *value+((a)*lastValue));
            result.add(value);
        	lastValue = value;
            currLine++;
            //System.out.println(value);
        }
        return result;
	}
	
	public static ArrayList<Float> getColumn(ArrayList<ArrayList<Float>> input, int column){
		Float[] output = new Float[input.size()];
		int i = 0;
		for(ArrayList<Float> f : input) {
			output[i] = f.get(column);
			i++;
		}
		return new ArrayList<Float>(Arrays.asList(output));
	}
	
	public static ArrayList<ArrayList<Float>> readFile(String fileName){
        File file= new File(fileName);

        // this gives you a 2-dimensional array of strings
        ArrayList<ArrayList<Float>> lines = new ArrayList<>();
        Scanner inputStream;

        try{
            inputStream = new Scanner(file);

            while(inputStream.hasNext()){
                String line= inputStream.next();
                String[] values = line.split(",");
                Float[] floatrow = new Float[values.length];
          
                int i = 0;
                for(String s : values){
                    float res = Float.parseFloat(s);
                    floatrow[i] = res;
                    i++;
                }
                // this adds the currently parsed line to the 2-dimensional string array
                lines.add(new ArrayList<Float>(Arrays.asList(floatrow)));
            }
            inputStream.close();
        }catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        return lines;
	}
	
	public static void writeFile2DFArr(ArrayList<ArrayList<Float>> output, String fileName) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		} 
		
		for(ArrayList<Float> line : output) {
			String toWrite = "";
			for(Float value : line) {
				toWrite+=value+",";
			}
			if(toWrite.charAt(toWrite.length()-1) == ',') {
				//last char is a , we need to remove it
				toWrite = toWrite.substring(0, toWrite.length());
			}
			try {
				writer.write(toWrite + "\n");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}
        try {
			writer.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void writeFile1DDArr(ArrayList<Double> output, String fileName) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(fileName);
		} catch (IOException e1) {
			
			e1.printStackTrace();
		} 
		
		
			String toWrite = "";
			for(double value : output) {
				toWrite+=value+",";
			}
			if(toWrite.charAt(toWrite.length()-1) == ',') {
				//last char is a , we need to remove it
				toWrite = toWrite.substring(0, toWrite.length());
			}
			try {
				writer.write(toWrite + "\n");
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
        try {
			writer.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ArrayList<Float>> replaceColumn(ArrayList<ArrayList<Float>> values, ArrayList<Float> column, int columnNum){
		int k = 0;
		int i = 0;
		for(ArrayList<Float> row: values) {
			int j = 0;
			for(Float value: row) {
				if(j==columnNum) {
					values.get(i).set(j, column.get(k));
					k++;
				}
				j++;
			}
			i++;
		}
		
		return values;
	}
	/*
	public static Result detectReps(ArrayList<ArrayList<Float>> data, int columndata, int columntime) {
		ArrayList <Integer> zeros = findZeros(data.get(columndata),columndata,columntime);
		if(zeros == null) {
			return new Result(0);
		}
		
		
		return null;
	}
	*/
	
	public static Float[] findMinAndMax(ArrayList<Float> data, int point1, int point2) {
		Float min = (float) 0;
		Float max = (float) 0;
		for(int i=point1; i<=point2; i++) {
			max = Math.max(data.get(i),max);
			min = Math.min(data.get(i), min);
		}
		return new Float[] {min,max};
	}
	/*
	 * Returns the times at which zeros occur
	 */
	public static ArrayList<Float> findZeros(ArrayList<ArrayList<Float>> full, int columndata, int columntime, double tolerance){
		ArrayList<Float> data = StaticMethods.getColumn(full, columndata);
		ArrayList<Float> time = StaticMethods.getColumn(full, columntime);
		
		Float lastVal = data.get(0);
		ArrayList<Float> zeros = new ArrayList<Float>();
		int i = 0;
		for(Float f : data) {
			if(f > tolerance && lastVal < tolerance) {
				zeros.add(time.get(i));
			}
			else if(f < tolerance && lastVal > tolerance) {
				zeros.add(time.get(i));
			}
			lastVal = f;
			i++;
		}
		return zeros;
	}
}
