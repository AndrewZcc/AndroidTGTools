package it.unina.android.ripper.driver;

import java.io.File;

/**
 * Contains the Entry Point of AndroidRipperDriver
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public class AndroidRipper {

	/**
	 * Entry Point
	 * 
	 * args[0] exploration strategy
	 * args[1] configuration properties file
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		boolean noProblem = false;
		
		System.out.println("Android Ripper");
		
		if (args.length < 2) {
			System.out.println("ERROR: You haven't specified needed parameters!");
		} else {
			if (checkConfigurationFile(args[1]) == false) {
				System.out.println("ERROR: Config file does not exist!");
			} else if (args[0].equals("tc") || args[0].equals("s") || args[0].equals("r")) {
				new AndroidRipperStarter(args[0], args[1]).startRipping();
			} else {
				System.out.println("ERROR: Exploration Type not supported!");
			}
			noProblem = true;
		}
		
		if (noProblem == false) {
			printUsageInstructions();
		}
	}
	
	/**
	 * Verify if the configuration file exists
	 * 
	 * @param fileName Configuration file name
	 * @return
	 */
	public static boolean checkConfigurationFile(String fileName) {
		if (new File(fileName).exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Print usage instructions
	 */
	public static void printUsageInstructions() {
		System.out.println();
		System.out.println("Usage: java -jar AndroidRipper.jar [s|r|tc] config.properties");
		System.out.println();
		System.out.println("Parameter 1:");
		System.out.println("\ts \t\tsystematic exploration");
		System.out.println("\ttc \t\thybrid manual-systematic exploration");
		System.out.println("\tr \t\trandom exploration");
		System.out.println();
		System.out.println("Parameter 2:");
		System.out.println("- configuration file name and path");
		System.out.println();
		System.out.println();
	}
}
