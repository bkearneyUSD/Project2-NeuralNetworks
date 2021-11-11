/* 
* 
* Comp 380 Neural Networks Project 2 - Hopfield Neural Model
* Authors: Brenton Kearney
*          Joe Hart
*          Patrick Sorys
* 
* This project utilizes a discrete Hopfield Network to preform
* basic image association.
* 
*/

import java.util.Scanner;
import java.io.*;

public class Main {

	private static void runTraining(Scanner kb) {
        // By Joe 
        // Function in main that utilizes the Hopfield Helper to train weights
        File trainingData = null;
        String weightsFilename = null;
        
		// The rest of this method validates user inputs to avoid runtime errors.
        boolean validatingFilename = true;
        while (validatingFilename) {
            System.out.println("\nEnter the training data file name:");
            if (kb.hasNextLine()) {
                try {
                    trainingData = new File(kb.nextLine());
                    if (trainingData.canRead()) {
                        validatingFilename = false;
                    }
                    else {
                        System.out.println("File not Found");
                    }
                }
                catch(Exception e) {
                    System.out.println("File not Found");
                    e.printStackTrace();
                }
            }
        }

        boolean validatingWeightsFilename = true;
        while (validatingWeightsFilename) {
            System.out.println("\nEnter a filename for the weights to be save:");
            if (kb.hasNextLine()) {
                weightsFilename = kb.nextLine();
                validatingWeightsFilename = false;
            }
        }
        HopfieldHelper.train(trainingData, weightsFilename);
	}

    private static void runTesting(Scanner kb) {
        // By Joe 
        // Function in main that utilizes the Hopfield Helper to test a set of weights
        String weightsFilename = "";
        String testingFilename = "";
        File weightsFile = null;
        File testingFile = null;

        // The rest of this method validates user inputs to avoid runtime errors.
        boolean validatingFilename = true;
        while (validatingFilename) {
            System.out.println("\nEnter the filename for the weights:");
            if (kb.hasNextLine()) {
                try {
                    weightsFilename = kb.nextLine();
                    weightsFile = new File(weightsFilename);
                    if (weightsFile.canRead()) {
                        validatingFilename = false;
                    }
                    else {
                        System.out.println("File not Found");
                    }
                }
                catch(Exception e) {
                    System.out.println("File not Found");
                    e.printStackTrace();
                }
            }
        }

        validatingFilename = true;
        while (validatingFilename) {
            System.out.println("\nEnter the filename for testing:");
            if (kb.hasNextLine()) {
                try {
                    testingFilename = kb.nextLine();
                    testingFile = new File(testingFilename);
                    if (testingFile.canRead()) {
                        validatingFilename = false;
                    }
                    else {
                        System.out.println("File not Found");
                    }
                }
                catch(Exception e) {
                    System.out.println("File not Found");
                    e.printStackTrace();
                }
            }
        }
        HopfieldHelper.test(testingFile, weightsFile);
    }

    private static void mainLoop() {
        // By Joe 
        // Main loop of the program that handles the users choice of training, testing, or quiting
        boolean running = true;
        while (running) {
            System.out.println("Enter 1 to train using a training data file, enter 2 to use a trained weight settings data file, or enter 3 to quit:");
            Scanner kb = new Scanner(System.in);
            if (kb.hasNextLine()) {
                if (kb.hasNext("1|2|3")) {
                    int option = kb.nextInt();
                            
                    if (option == 1) {
                        kb.nextLine();
                        runTraining(kb);
                    } 
                    else if (option == 2) {
                        kb.nextLine();
                        runTesting(kb);
                    }
                    else if (option == 3) {
                        System.out.println("Have a good day!\n");
                        running = false;
                    }
                }
                else {
                    System.out.println("Invalid input. Please try again...");
                    kb.nextLine();
                }
            }
        }
    }
    
	public static void main(String args[]) {
        // By Joe 
		System.out.println("Welcome to our Hopfield Neural Model for auto-associative memory!\n");
		mainLoop();
	}

}