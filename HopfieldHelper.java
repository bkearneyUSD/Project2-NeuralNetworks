import java.util.Scanner;
import java.io.*;
import java.util.Arrays;
import java.util.Random;

public class HopfieldHelper {

    public static void train(File trainingDataFile) 
    {
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(trainingDataFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File cannot be opened by scanner");
            e.printStackTrace();
        }

        // System.out.println(fileReader.next());
        int imageDimension = fileReader.nextInt();
        fileReader.nextLine();
        int numImages = fileReader.nextInt();
        fileReader.nextLine();
        
        Sample[] samples = new Sample[numImages];
       
        // for loop reads in all of the data from our training file
        for (int n = 0; n < numImages; n++) {
            int count = imageDimension;
            String image = "";

            while (count > 0) {
                String line = fileReader.nextLine();
                int lineSize = line.length();
                count = count - lineSize;

                if (count < 0) {
                    System.out.println("Read input incorrectly, count < 0");
                    System.exit(-1);
                }
                image += line + "\n";
            }
            samples[n] = new Sample(image);
        } // end for loop

        fileReader.close();

        // for (int i = 0; i < numImages; i++) {
        //     System.out.println(samples[i].printableImg);
        // }

        // intialize weights
        float[][] weights = new float[imageDimension][imageDimension];
        setInitialWeights(weights, imageDimension);

        hebbTraining(samples, weights);
        
        // output results to a file
        makeWeightFile(weights, inputDimension, outputDimension, trainedWeightsFileName, thresholdTheta, classifications);
    }

    public static void test(String trainedWeightsFileName, String testingSetFileName) {
        // Calls helper functions to test our saved weights against a set of testing patterns
        
        Weights weights = getWeightsFromFile(trainedWeightsFileName);
        if (fileCompatibility(weights.weights, testingSetFileName)) {
            testingLoop(weights.weights, testingSetFileName, weights.thresholdTheta, weights.classifications);
        }
        else {
            System.out.println("The Weights file selected has incompatible dimensions with the testing file");
        }
    }

    private static void hebbTraining(Sample[] samples, float[][] weights) {
        // Create a weight matrix for each individual image and then add all weight matrices together to get the final weights matrix
    }

    private static void setInitialWeights(float[][] weights, int imageDimension) {
        // Helper function that initializes the values of our 2 dimensional weights array according to user input
        float initialWeight = 0;
        for (int i = 0; i < imageDimension; i++) {
            Arrays.fill(weights[i], initialWeight);
        }
    }

    private static void makeWeightFile(float[][] weights, int inputDimension, int outputDimension, String trainedWeightsFileName, float thresholdTheta, char[] classifications) {
        // This method saves our weights to a file that can be used to test against the noise testing files.

        // Tries to open the file
        try {
            File weightsFile = new File(trainedWeightsFileName);
            if (weightsFile.createNewFile()) {
              System.out.println("\nFile created: " + weightsFile.getName());
            } else {
              System.out.println("\nFile already exists, overwriting file.");
            }
        } 
        catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
            System.exit(1);
        }
       
        // Try to write to the file
        try {
            FileWriter myWriter = new FileWriter(trainedWeightsFileName);
            myWriter.write(Integer.toString(inputDimension) + "\n" + Integer.toString(outputDimension) + "\n"  + Float.toString(thresholdTheta) + "\n");

            for (int z = 0; z < classifications.length; z++) {
                myWriter.write(classifications[z] + " ");
            }
            myWriter.write("\n");

            for(int i=0; i<outputDimension; i++) {
                String line = "";
                for(int j=0; j<inputDimension + 1; j++) {
                    line = line + Float.toString(weights[i][j]) + " ";
                }
                myWriter.write(line + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the file.\n");
        } 
        catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static Weights getWeightsFromFile(String trainedWeightsFileName) {
        // This method reads in the data from our saved weights file.

        float[][] weights;
        File trainedWeightsFile = new File(trainedWeightsFileName);

        Scanner fileReader = null;
        try {
            fileReader = new Scanner(trainedWeightsFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(1);
        }


        int inputDimension = fileReader.nextInt();
        int outputDimension = fileReader.nextInt();
        float thresholdTheta = fileReader.nextFloat();
        fileReader.nextLine();
        String classificationString = fileReader.nextLine();
        String[] classifications = classificationString.split(" ");
        
        weights = new float[outputDimension][inputDimension + 1];

        for (int i = 0; i < outputDimension; i++) {
            for (int j = 0; j < inputDimension + 1; j++) {
                weights[i][j] = fileReader.nextFloat();
            }
        }

        Weights weightsObj = new Weights(thresholdTheta, weights, classifications);

        fileReader.close();

        return weightsObj;
    }

    private static boolean fileCompatibility(float[][] weights, String testingSetFileName) {
        // This method checks if the dimensions of the saved weights are equal to the dimensions
        // of the file that is being tested against the saved weights. 

        int weightOutputs = weights.length;
        int weightInputs = weights[0].length - 1;

        File testingSetFile = new File(testingSetFileName);

        Scanner fileReader = null;
        try {
            fileReader = new Scanner(testingSetFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(1);
        }


        int inputDimension = fileReader.nextInt();
        int outputDimension = fileReader.nextInt();

        fileReader.close();

        return (inputDimension == weightInputs && outputDimension == weightOutputs);
    }

    private static void testingLoop(float[][] weights, String testingSetFile, float thresholdTheta, String[] classifications) {
        // Tests the saved weights against the patterns of the testing file to produce a classification.
        
        Sample[] samples = makeSamples(testingSetFile);
        for (Sample s :samples) {
            int[] testingOutput = new int[s.outputs.length];
            for (int out_i = 0; out_i < s.outputs.length; out_i++) {
                float y_in = weights[out_i][0]; // set y_in to the bias wb
                for (int in_i = 1; in_i < s.inputs.length + 1; in_i++) {
                    y_in = y_in + weights[out_i][in_i] * s.inputs[in_i - 1];
                }
                // Activation Function
                if (y_in < -thresholdTheta){
                    testingOutput[out_i] = -1;
                }
                else if (y_in > thresholdTheta) {
                    testingOutput[out_i] = 1;
                }
                else {
                    testingOutput[out_i] = 0;
                }
            }
            // Have output for this testing pair
            System.out.print("The test classification output is:      " + Arrays.toString(testingOutput) + "\n");
            boolean foundOne = false;
            String printClass = "Undecided";
            for (int i = 0; i < testingOutput.length; i++) {
                if (testingOutput[i] == 0) {
                    printClass = "Undecided";
                    foundOne = true;
                }
                else if (!foundOne && testingOutput[i] == 1) {
                    printClass = classifications[i];
                    foundOne = true;
                }
                else if (foundOne && testingOutput[i] == 1) {
                    printClass = "Undecided";
                }
            }
            System.out.print("The test classification character is " + printClass + "\n");
            System.out.print("The expected classification output is:  " + Arrays.toString(s.outputs) + "\n");
            System.out.print("The expected classification character is " + s.classification + "\n");
            if (Arrays.equals(testingOutput, s.outputs)) {
                System.out.print("Classification Successful\n\n");
            }
            else {
                System.out.print("Classification Failed\n\n");
            }

        }

    }

    private static Sample[] makeSamples(String filename) {
        // Method creates Sample class objects from the training data read
        
        File file = new File(filename);
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(file);
        }
        catch(FileNotFoundException e) {
            System.out.println("File not Found");
            e.printStackTrace();
        }

        int inputDimension = fileReader.nextInt();
        int outputDimension = fileReader.nextInt();
        int numTestingPairs = fileReader.nextInt();
        Sample[] samples = new Sample[numTestingPairs];
       
        for (int n = 0; n < numTestingPairs; n++) {
            int[] inputArray = new int[inputDimension]; 
            for (int i = 0; i < inputDimension; i++) {
                inputArray[i] = fileReader.nextInt();
            }
            
            int[] outputArray = new int[outputDimension];
            for (int j = 0; j < outputDimension; j++) {
                outputArray[j] = fileReader.nextInt();
            }

            String classification = fileReader.next();

            samples[n] = new Sample(inputArray, outputArray, classification);
        }
        fileReader.close();
        return samples;
    }

} //end perceptronHelper Class