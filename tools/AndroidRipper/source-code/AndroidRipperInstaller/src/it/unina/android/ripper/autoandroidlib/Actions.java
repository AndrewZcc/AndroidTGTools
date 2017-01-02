package it.unina.android.ripper.autoandroidlib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;

import com.googlecode.autoandroid.lib.AndroidTools;

/**
 * Interaction with the emulator (AVD) and the Host PC
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Actions {

	/**
	 * Time (in seconds) to wait AndroidRipperService to be started
	 */
	public static int ANDROID_RIPPER_SERVICE_WAIT_SECONDS = 3;
	
	/**
	 * Time (in seconds) to wait AndroidRipperTestCase to be started
	 */
	public static int ANDROID_RIPPER_WAIT_SECONDS = 3;
	
	/**
	 * Time (in seconds) to wait Android Emulator to be started
	 */
	public static int START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS = 0;
	
	/**
	 * Time (in seconds) to wait Android Emulator to be started
	 */
	public static int START_EMULATOR_SNAPSHOOT_WAIT_SECONDS = 0;
	
	/**
	 * AutoAndroidLib instance
	 */
	public static AndroidTools tools = AndroidTools.get();

	/**
	 * Send a message to a specific emulator on localhost
	 * 
	 * @param port Emulator Port
	 * @param message Message
	 */
	public static void sendMessageToEmualtor(int port, String message)
	{
		try {
			Socket socket = new Socket("localhost",port);
			
			sleepSeconds(1);
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			
			try {
				String emulator_console_auth_tokenFile = System.getProperty("user.home")+"/.emulator_console_auth_token";
				
				if (new File(emulator_console_auth_tokenFile).exists()) {
					BufferedReader br = new BufferedReader(new FileReader(emulator_console_auth_tokenFile));
					String emulator_console_auth_token = br.readLine();
					out.println("auth " + emulator_console_auth_token);
					br.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			out.println(message);
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send a message to a specific emulator on a remote host
	 * 
	 * @param port Emulator Port
	 * @param host Remote Host
	 * @param message Message
	 */
	public static void sendMessageToRemoteEmualtor(int port, String host, String message)
	{
		try {
			Socket socket = new Socket(host,port);
			
			sleepSeconds(1);
			
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			out.println(message);
			out.flush();
			out.close();
			socket.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Send Back Key to main emulator
	 */
	public static void sendBackKey()
	{
		try {
			tools.adb("shell", "input", "keyevent", "4").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

//	public static void killAll()
//	{
//		/*
//		try {
//			tools.adb("shell", "am kill-all");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		*/		
//	}
	
	/**
	 * Send Home Key to main emulator
	 */
	public static void sendHomeKey()
	{
		try {
			tools.adb("shell", "input", "keyevent", "3").waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Start AndroidRipperService on main emulator
	 */
	public static void startAndroidRipperService()
	{
		try {
			tools.adb("shell", "am startservice -a it.unina.android.ripper_service.ANDROID_RIPPER_SERVICE").connectStdout(System.out).connectStderr(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sleepSeconds(ANDROID_RIPPER_SERVICE_WAIT_SECONDS);
	}
	
	/**
	 * Sleep
	 * 
	 * @param seconds seconds to sleep
	 */
	public static void sleepSeconds(int seconds)
	{
		sleepMilliSeconds(seconds * 1000);
	}
	
	/**
	 * Sleep
	 * 
	 * @param milli milliseconds to sleep
	 */
	public static void sleepMilliSeconds(int milli)
	{
		try {
			Thread.sleep(milli);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Ripper Active status
	 */
	public static boolean ripperActive = false;
	
	/**
	 * Ripper Active status
	 * 
	 * @return status
	 */
	public static boolean isRipperActive()
	{
		return ripperActive;
	}
	
	/**
	 * Set Ripper Active Status
	 * @param b status
	 */
	public static void setRipperActive(boolean b)
	{
		ripperActive = b;
	}
	
	/**
	 * Start Android Ripper for an AUT on the main emulator
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 */
	public static void startAndroidRipper(String AUT_PACKAGE)
	{
		createAUTFilesDir(AUT_PACKAGE);
		
		new Thread() {
			public void run()
			{
				try {
					ripperActive = true;
					tools.adb("shell", "am instrument -w -e coverage true -e class it.unina.android.ripper.RipperTestCase it.unina.android.ripper/pl.polidea.instrumentation.PolideaInstrumentationTestRunner").connectStdout(System.out).connectStderr(System.out).waitFor() ;
					ripperActive = false;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}//.connectStdout(System.out).connectStderr(System.err);		
			}
		}.start();			

		sleepSeconds(ANDROID_RIPPER_WAIT_SECONDS);
	}
	
	/**
	 * Create dirs used by AndroidRipperTestCase in the AUT data storage
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 */
	public static void createAUTFilesDir(String AUT_PACKAGE)
	{
		try {
			tools.adb("shell", "mkdir","/data/data/"+AUT_PACKAGE+"/files");
			tools.adb("shell", "chmod", "-R", "777", "/data/data/"+AUT_PACKAGE+"/files");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//sleepSeconds(3);
	}
	
	/**
	 * Start an emulator without loading the snapshot
	 * 
	 * @param AVD_NAME Name of the emulator
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void startEmulatorNoSnapshotLoad(final String AVD_NAME, final int EMULATOR_PORT)
	{
			(new Thread() {
				public void run()
				{
					try {
						//tools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-load", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
						tools.emulator("@"+AVD_NAME,"-no-snapshot-load", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
				
			sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}
	
	/**
	 * Start an emulator without loading the snapshot and wipes the user partition
	 * 
	 * @param AVD_NAME Name of the emulator
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void startEmulatorNoSnapshotLoadWipeData(final String AVD_NAME, final int EMULATOR_PORT)
	{
			(new Thread() {
				public void run()
				{
					try {
						//tools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-load", "-wipe-data" , "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
						tools.emulator("@"+AVD_NAME,"-no-snapshot-load", "-wipe-data" , "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.err).waitForSuccess();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}).start();
			
				
			sleepSeconds(START_EMULATOR_NO_SNAPSHOOT_WAIT_SECONDS);
	}
	
	/**
	 * Start an emulator without saving the snapshot
	 * 
	 * @param AVD_NAME Name of the emulator
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void startEmulatorNoSnapshotSave(final String AVD_NAME, final int EMULATOR_PORT)
	{
		try {
			//tools.emulator("@"+AVD_NAME,"-partition-size","129","-no-snapshot-save", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			tools.emulator("@"+AVD_NAME,"-no-snapshot-save", "-port",Integer.toString(EMULATOR_PORT)).connectStdout(System.out).connectStderr(System.out);
			
			sleepSeconds(START_EMULATOR_SNAPSHOOT_WAIT_SECONDS);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Pull coverage
	 * 		adb pull %/data/data/%APPPACKAGE%/files% %FILESPATH%
	 *  
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverage(final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		pullCoverage(AUT_PACKAGE, "coverage.ec", COV_PATH, COV_COUNTER);
	}
	
	/**
	 * Pull coverage
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverage(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		pullCoverage(EMULATOR_PORT, AUT_PACKAGE, "coverage.ec", COV_PATH, COV_COUNTER);
	}
	
	/**
	 * Pull coverage
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_FILE File name of the coverage file
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverage(final String AUT_PACKAGE, final String COV_FILE, final String COV_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/"+COV_FILE;
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +".ec";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}				
	}
	
	/**
	 * Pull coverage
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_FILE File name of the coverage file
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverage(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_FILE, final String COV_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/"+COV_FILE;
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +".ec";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Pull coverage to a file with the filename in a standard format
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverageStandardFile(final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
			//Actions.sleepSeconds(3);
			DecimalFormat num = new DecimalFormat("00000");
			
			String src = "/data/data/"+AUT_PACKAGE+"/files/coverage.ec";
			String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +"_ec.ec";

			try {
				tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//Actions.sleepSeconds(3);
	}
	
	/**
	 * Pull coverage to a file with the filename in a standard format
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullCoverageStandardFile(final int EMULATOR_PORT, final String AUT_PACKAGE, final String COV_PATH, final int COV_COUNTER)
	{
		//Actions.sleepSeconds(3);
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/coverage.ec";
		String dest = COV_PATH+"coverage"+ num.format(COV_COUNTER) +"_ec.ec";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Actions.sleepSeconds(3);
	}
	
	/**
	 * Pull jUnit Log generated by Polidea Instrumentation
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param DEST_PATH Path on Host PC for log files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullJUnitLog(final String AUT_PACKAGE, final String DEST_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/it.unina.android.ripper-TEST.xml";
		String dest = DEST_PATH+"junit-log-"+ num.format(COV_COUNTER) +".xml";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Pull jUnit Log generated by Polidea Instrumentation
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 * @param AUT_PACKAGE Package of the AUT
	 * @param DEST_PATH Path on Host PC for log files
	 * @param COV_COUNTER Current Coverage file number
	 */
	public static void pullJUnitLog(final int EMULATOR_PORT, final String AUT_PACKAGE, final String DEST_PATH, final int COV_COUNTER)
	{
		DecimalFormat num = new DecimalFormat("00000");
		
		String src = "/data/data/"+AUT_PACKAGE+"/files/it.unina.android.ripper-TEST.xml";
		String dest = DEST_PATH+"junit-log-"+ num.format(COV_COUNTER) +".xml";

		try {
			tools.adb("-s", "emulator-"+EMULATOR_PORT, "pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	/**
	 * Wait for an emulator to be online
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void waitForEmulatorOnline(Integer EMULATOR_PORT) {

		boolean waitingEmulatorBoot = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(EMULATOR_PORT.toString())) {
							if (line.contains("device")) {
								waitingEmulatorBoot = false;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorBoot);
		
	}
	
	/**
	 * Wait for an emulator to boot
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void waitForEmulatorBoot(Integer EMULATOR_PORT) {

		boolean waitingEmulatorOnline = true;

		do {

			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+EMULATOR_PORT+" shell getprop init.svc.bootanim");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
							if (line.contains("stopped")) {
								waitingEmulatorOnline = false;
							}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorOnline);
		
	}

	/**
	 * Wait for a process identified by its package to be ended on an emulator
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void waitForProcessToEnd(String AUT_PACKAGE, Integer EMULATOR_PORT) {

		boolean found = false;

		do {

			found = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+EMULATOR_PORT+" shell ps " + AUT_PACKAGE);

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}
					
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (found);
		
	}
	
	/**
	 * Kill a process running on an emulator identified by the package of the AUT
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param EMULATOR_PORT Port of the emulator
	 * @return
	 */
	public static boolean killProcessByPackage(String AUT_PACKAGE, String EMULATOR_PORT) {
		String pid = getProcessPID(AUT_PACKAGE, EMULATOR_PORT);
		return killProcess(pid);
	}
	
	/**
	 * Kill a process on the default emulator by using its PID
	 * 
	 * @param PID Process ID
	 * @return
	 */
	public static boolean killProcess(String PID) {
		if (PID != null) 
		{
			try {
				tools.adb("shell", "ps", "-9", PID ).waitFor();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	/**
	 * Get the PID of an AUT identified by its package
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param EMULATOR_PORT Port of the emulator
	 * @return Process ID
	 */
	public static String getProcessPID(String AUT_PACKAGE, String EMULATOR_PORT) {

		String pid = null;
		
		try {
			final Process p = Runtime.getRuntime().exec("adb -s emulator-"+EMULATOR_PORT+" shell ps " + AUT_PACKAGE);

			try {
				String line = "";
				BufferedReader input = new BufferedReader(
						new InputStreamReader(p.getInputStream()));
				
				while ((line = input.readLine()) != null) {
					if (line.contains(AUT_PACKAGE)) {
						String[] split = line.split(" +", -3);
						pid = split[1];
					}
				}
				
				input.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			p.waitFor();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return pid;
	}
	
	/**
	 * Wait for an emulator
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void waitForEmulator(Integer EMULATOR_PORT) {
		waitForEmulatorOnline(EMULATOR_PORT);
		System.out.println("Emulator Online!");
		waitForEmulatorBoot(EMULATOR_PORT);
		System.out.println("Emulator Booted!");
	}

	/**
	 * Wait for an emulator to be closed
	 * 
	 * @param EMULATOR_PORT Port of the emulator
	 */
	public static void waitEmulatorClosed(Integer EMULATOR_PORT) {
		boolean waitingEmulatorClose = false;

		do {

			waitingEmulatorClose = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb devices");

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					while ((line = input.readLine()) != null) {
						if (line != null && line.contains(EMULATOR_PORT.toString())) {
							if (line.contains("device")) {
								waitingEmulatorClose = true;
							}
						}
					}
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} while (waitingEmulatorClose);
		
		System.out.println("Emulator offline!");
		
	}

	/**
	 * Wait for a process to end
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param EMULATOR_PORT Port of the emulator
	 * @param maxIter Max Retry
	 * @return
	 */
	public static boolean waitForProcessToEndMaxIterations(String AUT_PACKAGE, int EMULATOR_PORT, int maxIter) {
		boolean found = false;

		int iter = 0;
		
		do {

			found = false;
			
			try {
				final Process p = Runtime.getRuntime().exec("adb -s emulator-"+EMULATOR_PORT+" shell ps " + AUT_PACKAGE);

				try {
					String line = "";
					BufferedReader input = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					
					while ((line = input.readLine()) != null) {
						if (line.contains(AUT_PACKAGE)) {
							found = true;
						}
					}
					
					input.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				p.waitFor();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		} while (found && ++iter <= maxIter );
		
		return (iter > maxIter);
	}
	
	/**
	 * Pull from the emulator a coverage file for a Test Case
	 * 
	 * NOTE: TestCasesExecution*Driver
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_FILE Coverage file name
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param TEST_COV_COUNTER Coverage file number 
	 * @param TEST_CASE_INDEX Current Test Case Index
	 */
	public static void pullCoverageForUserTestCase(final String AUT_PACKAGE, final String COV_FILE, final String COV_PATH, int TEST_COV_COUNTER, int TEST_CASE_INDEX)
	{
		String src = "/data/data/"+AUT_PACKAGE+"/"+COV_FILE;
		String dest = COV_PATH+"coverage_test_"+TEST_CASE_INDEX+ "_"+ TEST_COV_COUNTER +".ec";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Pull from the emulator a coverage file for a Test Case
	 * 
	 * NOTE: TestCasesExecution*Driver
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param COV_PATH Path on Host PC for coverage files
	 * @param TEST_COV_COUNTER Coverage file number 
	 * @param TEST_CASE_INDEX Current Test Case Index
	 */
	public static void pullCoverageStandardFileForUserTestCase(final String AUT_PACKAGE, final String COV_PATH, int TEST_COV_COUNTER, int TEST_CASE_INDEX)
	{
		String src = "/data/data/"+AUT_PACKAGE+"/files/coverage.ec";
		String dest = COV_PATH+"coverage_test_"+TEST_CASE_INDEX+"_"+ TEST_COV_COUNTER +".ec";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Pull from the emulator the jUnit log for a Test Case
	 * 
	 * NOTE: TestCasesExecution*Driver
	 * 
	 * @param AUT_PACKAGE Package of the AUT
	 * @param DEST_PATH Path on Host PC for log files
	 * @param TEST_JUNIT_COUNTER jUnit Log file number 
	 * @param TEST_CASE_INDEX Current Test Case Index
	 */
	public static void pullJUnitLogForUserTestCase(final String AUT_PACKAGE, final String DEST_PATH, final int TEST_JUNIT_COUNTER, int TEST_CASE_INDEX)
	{
		String src = "/data/data/"+AUT_PACKAGE+"/files/it.unina.android.ripper-TEST.xml";
		String dest = DEST_PATH+"junit-log-"+ "test_"+TEST_CASE_INDEX +"_"+TEST_JUNIT_COUNTER +".xml";

		try {
			tools.adb("pull", src, dest).connectStderr(System.out).connectStdout(System.out).waitFor();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	
