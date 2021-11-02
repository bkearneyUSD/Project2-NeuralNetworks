/* 
* 
* Comp 380 Neural Networks Project 2 - Hopfield Neural Model
* Authors: Brenton Kearney
*          Joe Hart
*          Patrick Sorys
* 
* TODO
* 
*/

import java.util.Scanner;
import java.io.*;

public class Main {

	private static void runTraining(Scanner kb) {
        File trainingData = null;
        
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

        HopfieldHelper.train(trainingData);
	}

    private static void runTesting(Scanner kb) {
        String weightsFilename = "";
        String testingFilename = "";
        File weightsFile = null;
        File testingFile = null;

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
        // HopfieldHelper.test(weightsFile, testingFile);
    }


    private static void mainLoop() {
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
		System.out.println("Welcome to our Hopfield Neural Model for auto-associative memory!\n");
		mainLoop();
	}

}