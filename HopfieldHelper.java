import java.util.Scanner;
import java.io.*;
import java.util.Arrays;

public class HopfieldHelper {

    public static void train(File trainingDataFile, String weightsFilename) 
    {
        Sample[] samples = getSamples(trainingDataFile);

        int imageDimension = samples[0].img.length;

        // intialize weights
        int[][] weights = new int[imageDimension][imageDimension];
        setInitialWeights(weights, imageDimension);

        hebbTraining(samples, weights);
        
        // output results to a file
        makeWeightFile(weights, imageDimension, weightsFilename);
    }

    private static void setInitialWeights(int[][] weights, int imageDimension) {
        // Helper function that initializes the values of our 2 dimensional weights array according to user input
        int initialWeight = 0;
        for (int i = 0; i < imageDimension; i++) {
            Arrays.fill(weights[i], initialWeight);
        }
    }

    private static void hebbTraining(Sample[] samples, int[][] weights) {
        // Create a weight matrix for each individual image and then add all weight matrices together to get the final weights matrix
        for(Sample sample : samples) {
            int[][] transposeMatrix = transposeMultiplication(sample.img);
            matrixAddition(weights, transposeMatrix);
        }
    }

    private static int[][] transposeMultiplication(int[] image) {
        // todo comment
        int[][] matrix = new int[image.length][image.length];
        for(int i = 0; i < image.length; i++) {
            for(int j = 0; j < image.length; j++) {
                if (i == j) {
                    matrix[i][j] = 0;
                }
                else {
                    matrix[i][j] = image[i] * image[j];
                }
            }
        }
        return matrix;
    }

    private static void matrixAddition(int[][] currentSum, int[][] matrix) {
        // todo comment
        for(int i = 0; i < currentSum.length; i++) {
            for(int j = 0; j < currentSum[i].length; j++) {
                currentSum[i][j] = currentSum[i][j] + matrix[i][j];
            }
        }
    }

    private static void makeWeightFile(int[][] weights, int imageDimension, String trainedWeightsFilename) {
        // This method saves our weights to a file that can be used to test against the noise testing files.

        // Tries to open the file
        try {
            File weightsFile = new File(trainedWeightsFilename);
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
            FileWriter myWriter = new FileWriter(trainedWeightsFilename);
            myWriter.write(Integer.toString(imageDimension) + "\n");

            for(int i = 0; i < weights.length; i++) {
                String line = "";
                for(int j = 0; j < weights[i].length; j++) {
                    line = line + Integer.toString(weights[i][j]) + " ";
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

    private static void test(File testingDataFile, File trainedWeightsFile) {
        int[][] weights = getWeightsFromFile(trainedWeightsFile);

        if (fileCompatibility(weights, testingDataFile)) {
            testingHelper(weights, testingDataFile, 50);
        }
        else {
            System.out.println("The weights file and testing file are incompatable...");
        }
    }

    private static int[][] getWeightsFromFile(File trainedWeightsFile) {
        // This method reads in the data from our saved weights file.
        Scanner fileReader = null;

        try {
            fileReader = new Scanner(trainedWeightsFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(1);
        }


        int imageDimension = fileReader.nextInt();
        fileReader.nextLine();
    
        int[][] weights = new int[imageDimension][imageDimension];

        for (int i = 0; i < imageDimension; i++) {
            for (int j = 0; j < imageDimension; j++) {
                weights[i][j] = fileReader.nextInt();
            }
            fileReader.nextLine();
        }

        fileReader.close();

        return weights;
    }

    private static boolean fileCompatibility(int[][] weights, File testingDataFile) {
        // This method checks if the dimensions of the saved weights are equal to the dimensions
        // of the file that is being tested against the saved weights. 

        int imageDimension = weights.length;

        Scanner fileReader = null;
        try {
            fileReader = new Scanner(testingDataFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File Not Found");
            e.printStackTrace();
            System.exit(1);
        }

        int fileImageDimension = fileReader.nextInt();

        fileReader.close();

        return (imageDimension == fileImageDimension);
    }

    private static void testingHelper(int[][] weights, File testingDataFile, int maxNumberOfCycles) {
        // Tests the saved weights against the patterns of the testing file to produce a classification.
        
        Sample[] samples = getSamples(testingDataFile);

        int imageDimension = samples[0].img.length;

        for (Sample s : samples) {
            int[] y_i = s.img;


            System.out.print("The image from the testing file:\n" + s.printableImg + "\n");
            System.out.print("The image returned from the Hopfield Net:\n" + something + "\n");
            if (Arrays.equals(something, s.img)) {
                System.out.print("Association Successful\n\n");
            }
            else {
                System.out.print("Association Failed\n\n");
            }

        }
    }

    private static Sample[] getSamples(File dataFile) {
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(dataFile);
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

        return samples;
    }
} //end perceptronHelper Class