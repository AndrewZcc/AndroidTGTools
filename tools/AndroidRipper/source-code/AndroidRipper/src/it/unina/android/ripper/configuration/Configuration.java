package it.unina.android.ripper.configuration;

import it.unina.android.ripper.RipperTestCase;

/**
 * Configuration
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Configuration implements IConfiguration
{
	/**
	 * AUT Package
	 */
	public static String PACKAGE_NAME = "%%_PACKAGE_NAME_%%";
	
	/**
	 * AUT Class Name
	 */
	public static String CLASS_NAME = "%%_CLASS_NAME_%%";
	
	/**
	 * Ripper Package
	 */
	public static String RIPPER_PACKAGE = RipperTestCase.class.getPackage().getName();
	
	/**
	 * Sleep time after an event
	 */
	public static int SLEEP_AFTER_EVENT = 1000;
	
	/**
	 * Sleep time on throbber
	 */
	public static int SLEEP_ON_THROBBER = 3000;
	
	/**
	 * Sleep time on restart
	 */
	public static int SLEEP_AFTER_RESTART = 1000;
	
	/**
	 * Sleep time after task
	 */
	public static int SLEEP_AFTER_TASK = 1000;
	
	/**
	 * Ripper Configuration File
	 */
	public final static String CONFIGURATION_FILE = "configuration.xml";	
	
	/**
	 * Aut Class Object
	 */
	public static Class<?> autActivityClass;
	
	
	/**
	 * Extractor Component Class Name
	 */
	public static String EXTRACTOR_CLASS = "%%_EXTRACTOR_CLASS_%%";
	
	/**
	 * Load configuration
	 */
	static {
		//Prefs.setMainNode(RIPPER_PACKAGE);
		//Prefs.updateMainNode();

		try {
			autActivityClass = Class.forName(CLASS_NAME);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
