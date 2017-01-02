package it.unina.android.ripper.autoandroidlib.logcat;

import java.io.FileOutputStream;
import java.io.PrintStream;

import com.googlecode.autoandroid.lib.AndroidTools;

/**
 * Dumps the Logcat to a File
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class LogcatDumper extends Thread {
	
	/**
	 * Emulator Port
	 */
	int emulatorPort;
	
	/**
	 * Destination File Name
	 */
	String filename;
	
	/**
	 * Constructor
	 * 
	 * @param port Emulator port
	 */
	public LogcatDumper(int port) {
		this(port, "logcat_" + System.currentTimeMillis() + ".txt");
	}
	
	/**
	 * Constructor
	 * 
	 * @param port Emulator port
	 * @param filename Destination File Name
	 */	
	public LogcatDumper(int port, String filename) {
		super();
		this.emulatorPort = port;
		this.filename = filename;
	}

	@Override
	public void run() {		
				
		try {
			
			FileOutputStream fos = new FileOutputStream(filename, true);
			PrintStream ps = new PrintStream(fos);
			
			AndroidTools tools = AndroidTools.get();
			tools.adb("-s", "emulator-"+emulatorPort, "logcat").connectStderr(ps).connectStdout(ps).waitForSuccess();
			
			try { ps.close(); } catch (Exception ex) {}
			try { fos.close(); } catch (Exception ex) {}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
