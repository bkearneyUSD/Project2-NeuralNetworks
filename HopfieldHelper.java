import java.io.*;
import java.util.*;

public class HopfieldHelper {

    public static void train(File trainingDataFile, String weightsFilename) {
        // By Brenton
        // This function uses helper functions to train our weight matrix and ultimately output results to a txt file.
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
        // By Brenton
        // Helper function that initializes the values of our 2 dimensional weights array according to user input
        int initialWeight = 0;
        for (int i = 0; i < imageDimension; i++) {
            Arrays.fill(weights[i], initialWeight);
        }
    }

    private static void hebbTraining(Sample[] samples, int[][] weights) {
        // By Brenton
        // Creates a weight matrix for each individual image and then adds all weight matrices together. The sum
        // The int[][] weights parameter is updated as the final weight matrix.
        for(Sample sample : samples) {
            int[][] transposeMatrix = transposeMultiplication(sample.img);
            matrixAddition(weights, transposeMatrix);
        }
    }

    private static int[][] transposeMultiplication(int[] image) {
        // By Joe
        // Takes in the vector image and multiplies image by its own transpose, returning the product.
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
        // By Joe
        // Takes in two matrices and adds them together, updating currentSum with the result.
        for(int i = 0; i < currentSum.length; i++) {
            for(int j = 0; j < currentSum[i].length; j++) {
                currentSum[i][j] = currentSum[i][j] + matrix[i][j];
            }
        }
    }

    private static void makeWeightFile(int[][] weights, int imageDimension, String trainedWeightsFilename) {
        // By Joe
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
            System.out.println("Successfully wrote to the weights file.\n");
        } 
        catch (IOException e) {
            System.out.println("An error occurred while writing to the weights file.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void test(File testingDataFile, File trainedWeightsFile) {
        // By Brenton
        // Runs our saved weights against a file of images to test our algorithm's effectiveness through helper functions.
        int[][] weights = getWeightsFromFile(trainedWeightsFile);

        File solutionFile = makeSolutionFile();

        if (fileCompatibility(weights, testingDataFile)) {
            testingHelper(weights, testingDataFile, 50, solutionFile);
        }
        else {
            System.out.println("The weights file and testing file are incompatable...");
        }
    }

    private static File makeSolutionFile() {
        // By Patrick
        // This method saves our weights to a file that can be used to test against the noise testing files.

        // Tries to open the file
        File resultsFile = null;
        try {
            resultsFile = new File("results.txt");
            if (resultsFile.createNewFile()) {
              System.out.println("\nResults will be found in the new file, results.txt");
            } else {
              System.out.println("\nFesults file already exists, overwriting results.txt.");
            }
        } 
        catch (IOException e) {
            System.out.println("An error occurred while creating the file.");
            e.printStackTrace();
            System.exit(1);
        }
        return resultsFile;
    }

    private static int[][] getWeightsFromFile(File trainedWeightsFile) {
        // By Patrick
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
        // By Patrick
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

    private static void testingHelper(int[][] weights, File testingDataFile, int maxNumberOfCycles, File solutionFile) {
        // By Brenton
        // Tests the saved weights against the patterns of the testing file to produce a classification.
        Sample[] samples = getSamples(testingDataFile);
        int imageDimension = samples[0].img.length;

        // Creating a list of indexes to use for random index updates without repeats during the testing loop.
        ArrayList<Integer> numberList = new ArrayList<Integer>();
        for (int i = 0; i < imageDimension; i++) {
            numberList.add(i);
        }

        try {
            FileWriter resultsWriter = new FileWriter(solutionFile);

            for (Sample s : samples) {
                int[] y_i = s.img;

                boolean converged = false;
                int numberOfCycles = 0;
                while (!converged) {
                    converged = true;
                    // Randomize the list of possible index values for random ordering
                    Collections.shuffle(numberList);
                    for (int randomIndex : numberList) {
                        int y_in_i = s.img[randomIndex];
                        for (int z = 0; z < imageDimension; z++) {
                            y_in_i += y_i[z] * weights[z][randomIndex];
                        }
                        // y_in_i is now finished, do activation
                        int new_y_i = 0;
                        if (y_in_i > 0) {
                            new_y_i = 1;
                        }
                        else if (y_in_i < 0) {
                            new_y_i = -1;
                        }
                        // check if the value changed
                        if (y_i[randomIndex] != new_y_i) {
                            converged = false;
                            // broadcast y_i
                            y_i[randomIndex] = new_y_i;
                        }
                    } //end for loop
                    numberOfCycles++;
                    if (numberOfCycles == maxNumberOfCycles) {
                        converged = true;
                        System.out.println("Max number of cycles reached.");
                    }
                } // end while loop

                String output = "\nThe image from the testing file:\n" + s.printableImg + "\nThe image returned from the Hopfield Net:\n\n" + getPrintableImage(y_i) + "\n\n";
                resultsWriter.write(output);
                System.out.print(output);
            } //end for loop for the sample s
            resultsWriter.close();
            System.out.println("Successfully wrote to the results file.\n");
        }
        catch (IOException e) {
            System.out.println("An error occurred while writing to the results file.");
            e.printStackTrace();
            System.exit(1);
        }
    }

private static String getPrintableImage(int[] img) {
    // By Patrick
    // Function takes in an image vector of integers with value 1,-1 and returns a printable version of O and ' ' characters.
    double rowLength = Math.sqrt(img.length);
    int currentRow = 0;
    String printableImage = "";
    for (int i = 0; i < img.length; i++) {
        if (currentRow == rowLength) {
            currentRow = 0;
            printableImage += "\n";
        }
        if (img[i] > 0) {
            printableImage += "O";
        }
        else if (img[i] < 0) {
            printableImage += " ";
        }
        else {
            printableImage += "@";
        }
        currentRow++;
    }
    return printableImage;
}

    private static Sample[] getSamples(File dataFile) {
        // By Patrick
        // Reads in our sample data and created a list of custom Sample objects to be used for training.
        Scanner fileReader = null;
        try {
            fileReader = new Scanner(dataFile);
        }
        catch(FileNotFoundException e) {
            System.out.println("File cannot be opened by scanner");
            e.printStackTrace();
        }

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
