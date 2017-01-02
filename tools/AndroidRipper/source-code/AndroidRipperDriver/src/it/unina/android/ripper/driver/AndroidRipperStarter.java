package it.unina.android.ripper.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.comparator.GenericComparator;
import it.unina.android.ripper.comparator.GenericComparatorConfiguration;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.driver.random.RandomDriver;
import it.unina.android.ripper.driver.systematic.SystematicDriver;
import it.unina.android.ripper.driver.systematic.TestCasesExecutionSystematicDriver;
import it.unina.android.ripper.input.RipperInput;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.LimitedDepthBreadthScheduler;
import it.unina.android.ripper.scheduler.LimitedDepthDepthScheduler;
import it.unina.android.ripper.scheduler.RandomScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.TerminationCriterion;

/**
 * Configure the AndroidRipperDriver, handle console output, start up the ripping process
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class AndroidRipperStarter implements RipperEventListener {

	/**
	 * Version
	 */
	public final static String VERSION = "2016.04.26";
	
	/**
	 * Configuration
	 */
	Properties conf;
	
	/**
	 * Driver Instance
	 */
	AbstractDriver driver;
	
	/**
	 * Configuration file name
	 */
	String configFile;
	
	/**
	 * RipperDriver type
	 */
	String driverType = null;
	
	/**
	 * Constructor
	 * 
	 * @param driverType RipperDriver type
	 * @param configFile Configuration file name
	 */
	public AndroidRipperStarter(String driverType, String configFile) {
		super();
		if (new File(configFile).exists() == false) {
			throw new RuntimeException("File "+configFile+" not Found!");
		} else {
			this.configFile = configFile;
			this.driverType = driverType;
		}
	}
	
	/**
	 * Configure and StartUp Ripping Process
	 */
	public void startRipping()
	{
		if (driverType.equals("s")) {
			println("Systematic Ripper " + VERSION);
		} else if (driverType.equals("r")) {
			println("Random Ripper " + VERSION);
		} else if (driverType.equals("tc")) {
			println("Hybrid Ripper " + VERSION);
		} else {
			throw new RuntimeException("Driver Type not supported!");
		}
		
		println("Loading configuration");
		conf = this.loadConfigurationFile(this.configFile);
		
		Scheduler scheduler = null;
		Planner planner = null;
		RipperInput ripperInput = null;
		RipperOutput ripperOutput = null;
		TerminationCriterion terminationCriterion = null;
		IComparator comparator = null;
		
		if (conf != null)
		{
			String pullCoverage = conf.getProperty("coverage", "0");
			String pullCoverageZero = conf.getProperty("coverage_zero", "0");
			String coveragePath = null;
			try { coveragePath = conf.getProperty("coverage_path", ((new java.io.File( "." ).getCanonicalPath())+"/coverage/")); } catch (IOException e) { }
			String reportFile = conf.getProperty("report_file", "report.xml");
			String logFilePrefix = conf.getProperty("log_file_prefix", "log_");
			String avd_name = conf.getProperty("avd_name", null);
			String avd_port = conf.getProperty("avd_port", "5554");
			String aut_package = conf.getProperty("aut_package", null);
			String aut_main_activity = conf.getProperty("aut_main_activity", null);
			String ping_max_retry = conf.getProperty("ping_max_retry", "10");
			String ack_max_retry = conf.getProperty("ack_max_retry", "10");
			String failure_threshold = conf.getProperty("failure_threshold", "10");
			String ping_failure_threshold = conf.getProperty("ping_failure_threshold", "3");
			String sleep_after_task = conf.getProperty("sleep_after_task", "0");
			
			String logcatPath = null;
			try { logcatPath = conf.getProperty("logcat_path", ((new java.io.File( "." ).getCanonicalPath())+"/logcat/")); } catch (IOException e) { }
			
			String xmlOutputPath = null;
			try { xmlOutputPath = conf.getProperty("xml_path", ((new java.io.File( "." ).getCanonicalPath())+"/model/")); } catch (IOException e) { }
			
			String junitOutputPath = null;
			try { junitOutputPath = conf.getProperty("junit_path", ((new java.io.File( "." ).getCanonicalPath())+"/junit/")); } catch (IOException e) { }

			String schedulerClass = conf.getProperty("scheduler", ((driverType.equals("r"))?"it.unina.android.ripper.scheduler.RandomScheduler":"it.unina.android.ripper.scheduler.DepthScheduler"));//BreadthScheduler"));
			String terminationCriterionClass = conf.getProperty("termination_criterion", ((driverType.equals("r"))?"it.unina.android.ripper.termination.MaxEventsTerminationCriterion":"it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion"));
			String plannerClass = conf.getProperty("planner", "it.unina.android.ripper.planner.HandlerBasedPlanner");
			String inputClass = conf.getProperty("ripper_input", "it.unina.android.ripper.input.XMLRipperInput");
			String ripperOutputClass = conf.getProperty("ripper_output", "it.unina.android.ripper.output.XMLRipperOutput");
			
			//validation
			if (avd_name == null)
				throw new RuntimeException("avd_name null!");
			if (aut_package == null)
				throw new RuntimeException("aut_package null!");
			if (aut_main_activity == null)
				throw new RuntimeException("aut_main_activity null!");
			
			String ANDROID_RIPPER_SERVICE_WAIT_SECONDS = conf.getProperty("ANDROID_RIPPER_SERVICE_WAIT_SECONDS", "3");
			String ANDROID_RIPPER_WAIT_SECONDS = conf.getProperty("ANDROID_RIPPER_WAIT_SECONDS", "3");
			//String START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = conf.getProperty("START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS", "60");
			//String START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = conf.getProperty("START_EMULATOR_SNAPSHOOT_WAIT_SECONDS", "20");


			//SYSTEMATIC CONFIGURATION PARAMETERS
			String comparatorClass = conf.getProperty("comparator", "it.unina.android.ripper.comparator.GenericComparator");
			
			//RANDOM CONFIGURATION PARAMETERS
			String numEvents = conf.getProperty("events","30000");
			String seed = conf.getProperty("seed", null);
			String coverageFrequency = conf.getProperty("coverage_frequency", "100");
			String newLogFrequency = conf.getProperty("new_log_frequency", "100");
			String num_events_per_session = conf.getProperty("num_events_per_session", "0");
			
			try {
				planner = (Planner) Class.forName(plannerClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: planner class " + plannerClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			try {
				ripperInput = (RipperInput) Class.forName(inputClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: description_loader class " + inputClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			try {
				ripperOutput = (RipperOutput) Class.forName(ripperOutputClass).newInstance();
			} catch (Exception ex) {
				println("ERROR: ripper_output class " + ripperOutputClass);
				ex.printStackTrace();
				System.exit(1);
			}
			
			if (new java.io.File(coveragePath).exists() == false)
				new java.io.File(coveragePath).mkdir();
			
			if (new java.io.File(logcatPath).exists() == false)
				new java.io.File(logcatPath).mkdir();
			
			if (new java.io.File(xmlOutputPath).exists() == false)
				new java.io.File(xmlOutputPath).mkdir();
			
			if (new java.io.File(junitOutputPath).exists() == false)
				new java.io.File(junitOutputPath).mkdir();
			
			// TODO: remove if useless
			//			if (new java.io.File(logFile).exists())
			//				new java.io.File(logFile).delete();
			
			long seedLong = System.currentTimeMillis();
			if (seed != null && seed.equals("") == false) {
				seedLong = Long.parseLong(seed);
			}
			
			if (schedulerClass != null) {
				
				if ( schedulerClass.equals("it.unina.android.ripper.scheduler.LimitedDepthBreadthScheduler") ) {
					int max = 10; //TODO: read from config
					scheduler = new LimitedDepthBreadthScheduler(max);
				} else if ( schedulerClass.equals("it.unina.android.ripper.scheduler.LimitedDepthDepthScheduler") ) {
					int max = 10; //TODO: read from config
					scheduler = new LimitedDepthDepthScheduler(max);
				} else if ( schedulerClass.equals("it.unina.android.ripper.scheduler.RandomScheduler") ) {
					int max = 10; //TODO: read from config
					scheduler = new RandomScheduler(seedLong);
				} else {
					
					try {
						scheduler = (Scheduler) Class.forName(schedulerClass).newInstance();
					} catch (Exception ex) {
						println("ERROR: scheduler class " + schedulerClass);
						ex.printStackTrace();
						System.exit(1);
					}
					
				}
				
			} else {
				System.out.println("ERROR: scheduler class undefined");
				System.exit(1);
			}
			
			if ( driverType.equals("s") || driverType.equals("tc") ) {
				
				try {
					terminationCriterion = (TerminationCriterion) Class.forName(terminationCriterionClass).newInstance();
				} catch (Exception ex) {
					println("ERROR: termination_criterion class " + terminationCriterionClass);
					ex.printStackTrace();
					System.exit(1);
				}
				
				if (comparatorClass.equals("it.unina.android.ripper.comparator.GenericComparator")) {			
					String comparatorConfiguration = conf.getProperty("comparator_configuration", "DefaultComparator");
					if (comparatorConfiguration != null) {
						
						if ( comparatorConfiguration.equals("DefaultComparator") ) {
							comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getDefaultComparator() );
						} else if ( comparatorConfiguration.equals("NameComparator") ) {
							comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getNameComparator() );
						} else if ( comparatorConfiguration.equals("CustomWidgetSimpleComparator") ) {
							comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getCustomWidgetSimpleComparator() );
						} else if ( comparatorConfiguration.equals("CustomWidgetIntensiveComparator") ) {
							comparator = new GenericComparator( GenericComparatorConfiguration.Factory.getCustomWidgetIntensiveComparator() );
						} else {
							println("ERROR: comparator configuration not found");
						}
						
					} else {
						println("ERROR: comparator configuration undefined");
					}
				} else {
					
					try {
						comparator = (IComparator) Class.forName(comparatorClass).newInstance();
					} catch (Exception ex) {
						println("ERROR: comparator class " + comparatorClass);
						ex.printStackTrace();
						System.exit(1);
					}
					
				}
			}
			else if (driverType.equals("r"))
			{
				terminationCriterion = new it.unina.android.ripper.termination.MaxEventsTerminationCriterion(Integer.parseInt(numEvents));
				
			}
			
			println("Starting Ripper...");
			if (driverType.equals("s")) {
				
				driver = new SystematicDriver(
						scheduler,
						planner,
						ripperInput,
						comparator,
						terminationCriterion,
						ripperOutput
				);
				terminationCriterion.init(driver);
				
			} else if (driverType.equals("r")) {
				
				driver = new RandomDriver(scheduler, planner, ripperInput, ripperOutput, terminationCriterion);
				
				terminationCriterion.init(driver);
				
				((RandomDriver)driver).RANDOM_SEED = seedLong;	
				((RandomDriver)driver).NUM_EVENTS = Integer.parseInt(numEvents);
				((RandomDriver)driver).NUM_EVENTS_PER_SESSION = Integer.parseInt(num_events_per_session);
				((RandomDriver)driver).NEW_LOG_FREQUENCY = Integer.parseInt(newLogFrequency);	
				((RandomDriver)driver).COVERAGE_FREQUENCY = Integer.parseInt(coverageFrequency);
				
			} else if (driverType.equals("tc")) {

				driver = new TestCasesExecutionSystematicDriver(
						scheduler,
						planner,
						ripperInput,
						comparator,
						terminationCriterion,
						ripperOutput
				);
				
				terminationCriterion.init(driver);
				
			} else {
				
				throw new RuntimeException("Driver Type not supported!");
				
			}

			if (driver != null) {
				//apply common configuration parameters
				driver.PULL_COVERAGE = pullCoverage.equals("1");
				driver.PULL_COVERAGE_ZERO = pullCoverageZero.equals("1");
				driver.COVERAGE_PATH = coveragePath;
				driver.REPORT_FILE = reportFile;
				driver.LOG_FILE_PREFIX = logFilePrefix;
				driver.AVD_NAME = avd_name;
				driver.EMULATOR_PORT = Integer.parseInt(avd_port);
				driver.AUT_PACKAGE = aut_package;
				driver.AUT_MAIN_ACTIVITY = aut_main_activity;
				driver.SLEEP_AFTER_TASK = Integer.parseInt(sleep_after_task);			
				driver.PING_MAX_RETRY = Integer.parseInt(ping_max_retry);
				driver.ACK_MAX_RETRY =  Integer.parseInt(ack_max_retry);
				driver.FAILURE_THRESHOLD = Integer.parseInt(failure_threshold);
				driver.PING_FAILURE_THRESHOLD = Integer.parseInt(ping_failure_threshold);
				driver.LOGCAT_PATH = logcatPath;
				driver.XML_OUTPUT_PATH = xmlOutputPath;
				driver.JUNIT_OUTPUT_PATH = junitOutputPath;
	
				Actions.ANDROID_RIPPER_SERVICE_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
				Actions.ANDROID_RIPPER_WAIT_SECONDS = Integer.parseInt(ANDROID_RIPPER_WAIT_SECONDS);
				//Actions.START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
				//Actions.START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = Integer.parseInt(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);
				
				driver.setRipperEventListener(this);
				driver.startRipping();
				while(driver.isRunning())
				{
					//TODO: stop pause commands
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
		}
		else 
		{
			println("ERROR: Missing configuration file!");
			System.exit(1);
		}
		
		
	}
	
	/**
	 * Load the configuration file into a Properties class instance
	 * 
	 * @param fileName configuration file name
	 * @return
	 */
	private Properties loadConfigurationFile(String fileName)
	{
		Properties conf = new Properties();
		
		try {
			conf.load(new FileInputStream(fileName));
			return conf;
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.observer.RipperEventListener#ripperLog(java.lang.String)
	 */
	@Override
	public void ripperLog(String log) {
		println(log);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.observer.RipperEventListener#ripperStatusUpdate(java.lang.String)
	 */
	@Override
	public void ripperStatusUpdate(String status) {
		println(status);
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.observer.RipperEventListener#ripperTaskEneded()
	 */
	@Override
	public void ripperTaskEneded() {

	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.observer.RipperEventListener#ripperEneded()
	 */
	@Override
	public void ripperEneded() {
		println("Ripper Ended!");
		System.exit(0);
	}
	
	/**
	 * Print a formatted debug line
	 * 
	 * @param line line to print
	 */
	protected void println(String line)
	{
		System.out.println("["+System.currentTimeMillis()+"] " + line);
	}
}
