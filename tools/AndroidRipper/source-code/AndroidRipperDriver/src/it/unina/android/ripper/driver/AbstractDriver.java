package it.unina.android.ripper.driver;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.SocketException;
import java.util.ArrayList;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.autoandroidlib.logcat.LogcatDumper;
import it.unina.android.ripper.driver.exception.AckNotReceivedException;
import it.unina.android.ripper.driver.exception.NullMessageReceivedException;
import it.unina.android.ripper.input.RipperInput;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.observer.RipperEventListener;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.TerminationCriterion;

/**
 * Managers the Ripper Process
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public abstract class AbstractDriver {

	/**
	 * Port of the AndroidRipperService running on the Emulator Default: 8888
	 */
	public int PORT = 8888;

	/**
	 * Name of the Emulator
	 */
	public String AVD_NAME = "test";

	/**
	 * Package of the AUT
	 */
	public String AUT_PACKAGE = "";

	/**
	 * Main Activity Class of the AUT
	 */
	public String AUT_MAIN_ACTIVITY = "";

	/**
	 * Port of the Emualtor
	 */
	public int EMULATOR_PORT = 5554;

	/**
	 * Sleep time after each event
	 */
	public int SLEEP_AFTER_EVENT = 0;

	/**
	 * Sleep time after each task
	 */
	public int SLEEP_AFTER_TASK = 0;

	/**
	 * Sleep time after each restart
	 */
	public int SLEEP_AFTER_RESTART = 0;

	/**
	 * Enable pull coverage
	 */
	public boolean PULL_COVERAGE = true;

	/**
	 * Enable pull coverage before the first event
	 */
	public boolean PULL_COVERAGE_ZERO = true;

	/**
	 * Storage path of coverage files
	 */
	public String COVERAGE_PATH = "";

	/**
	 * Enable screenshot
	 */
	public boolean SCREENSHOT = false;

	/**
	 * Storage path of screenshot files
	 */
	public String SCREENSHOTS_PATH = "./screenshots/";

	/**
	 * Report file name
	 */
	public String REPORT_FILE = "report.xml";

	/**
	 * Prefix of the log file name
	 */
	public String LOG_FILE_PREFIX = "log_";

	/**
	 * PING message max retry number
	 */
	public int PING_MAX_RETRY = 10;

	/**
	 * ACK message max retry number
	 */
	public int ACK_MAX_RETRY = 10;

	/**
	 * Send message failure threshold
	 */
	public int FAILURE_THRESHOLD = 10;

	/**
	 * PING message failure threshold
	 */
	public int PING_FAILURE_THRESHOLD = 3;

	/**
	 * Socket Exception threshold
	 */
	public int SOCKET_EXCEPTION_THRESHOLD = 2;

	/**
	 * Storage path of logcat files
	 */
	public String LOGCAT_PATH = "";

	/**
	 * Storage path of xml files
	 */
	public String XML_OUTPUT_PATH = "";

	/**
	 * Storage path of junit log files
	 */
	public String JUNIT_OUTPUT_PATH = "";

	/**
	 * Scheduler instance
	 */
	public Scheduler scheduler;

	/**
	 * Planner instance
	 */
	public Planner planner;

	/**
	 * RipperServiceSocket instance
	 */
	public RipperServiceSocket rsSocket;

	/**
	 * RipperInput instance
	 */
	public RipperInput ripperInput;

	/**
	 * Termination Criteria
	 */
	public ArrayList<TerminationCriterion> terminationCriteria;

	/**
	 * Ripping Process Running Status
	 */
	public boolean running = true;

	/**
	 * Current log file name
	 */
	public String currentLogFile;

	/**
	 * RipperOutput instance
	 */
	public RipperOutput ripperOutput;

	/**
	 * Current Event Number
	 */
	public int nEvents = 0;

	/**
	 * Current Task Number
	 */
	public int nTasks = 0;

	/**
	 * Number of Failures
	 */
	public int nFails = 0;

	/**
	 * Number of Restarts
	 */
	public int nRestart = 0;

	/**
	 * Constructor
	 */
	public AbstractDriver() {
		super();
		terminationCriteria = new ArrayList<TerminationCriterion>();
	}

	/**
	 * Start Ripping Process
	 */
	public void startRipping() {
		this.running = true;
		notifyRipperLog("Start Ripping Loop...");
		this.rippingLoop();
	}

	/**
	 * Ripping Process Paused
	 */
	public boolean paused = false;

	/**
	 * Pause Ripping Process
	 */
	public void pauseRipping() {
		this.paused = true;
	}

	/**
	 * Resume Ripping Process
	 */
	public void resumeRipping() {
		this.paused = false;
	}

	/**
	 * Stop Ripping Process
	 */
	public void stopRipping() {
		this.running = false;
	}

	/**
	 * Check if the Ripping Process is running
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return this.running;
	}

	/**
	 * Sleep if Ripping Process is paused
	 */
	public void ifIsPausedDoPause() {
		if (paused) {
			do {
				Actions.sleepMilliSeconds(500);
			} while (paused);
		}
	}

	/**
	 * Main Ripping Loop
	 */
	public abstract void rippingLoop();

	/**
	 * Return the Scheduler TaskList
	 * 
	 * @return Task List
	 */
	public TaskList getTaskList() {
		return scheduler.getTaskList();
	}

	/**
	 * Ripper Process Observer
	 */
	RipperEventListener mRipperDriverListener = null;

	/**
	 * Set Ripper Process Observer
	 */
	public void setRipperEventListener(RipperEventListener l) {
		this.mRipperDriverListener = l;
	}

	/**
	 * Notify Ripper Process status to Observer
	 */
	public void notifyRipperStatus(String status) {
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperStatusUpdate(status);
	}

	/**
	 * Notify Ripper Process log entry to Observer
	 */
	public void notifyRipperLog(String log) {
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperLog(log);
	}

	/**
	 * Notify Ripper Process Task Endend to Observer
	 */
	public void notifyRipperTaskEnded() {
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperTaskEneded();
	}

	/**
	 * Notify Ripper Process Endend to Observer
	 */
	public void notifyRipperEnded() {
		if (mRipperDriverListener != null)
			this.mRipperDriverListener.ripperEneded();
	}

	/**
	 * Write the final Report file
	 * 
	 * @param report
	 *            Report file name (with full path)
	 */
	public void writeReportFile(String report) {
		this.writeStringToFile(report, XML_OUTPUT_PATH + REPORT_FILE);
	}

	/**
	 * Write a string to a file (closes the file)
	 * 
	 * @param string
	 *            String to write
	 * @param file
	 *            File to write to
	 */
	public void writeStringToFile(String string, String file) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, false), "UTF-8"));

			out.write(string);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Write a string to a file (append to the file)
	 * 
	 * @param string
	 *            String to write
	 * @param file
	 *            File to write to
	 */
	public void appendStringToFile(String string, String file) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), "UTF-8"));

			out.write(string);
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Number of the current log file
	 */
	public int LOG_FILE_NUMBER = 0;

	/**
	 * Create an xml log file. The name is generated by using
	 * System.currentTimeMillis()
	 */
	public void createLogFileAtCurrentTimeMillis() {
		currentLogFile = XML_OUTPUT_PATH + LOG_FILE_PREFIX + System.currentTimeMillis() + ".xml";

		try {
			FileWriter fileWritter = new FileWriter(currentLogFile, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write("<?xml version=\"1.0\"?><root>\n\r");
			bufferWritter.flush();
			bufferWritter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Create an xml log file. The name is generated by using LOG_FILE_NUMBER
	 * variable
	 */
	public void createLogFile() {
		// currentLogFile = LOG_FILE_PREFIX + System.currentTimeMillis() +
		// ".xml";
		currentLogFile = XML_OUTPUT_PATH + LOG_FILE_PREFIX + LOG_FILE_NUMBER + ".xml";

		try {
			FileWriter fileWritter = new FileWriter(currentLogFile, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write("<?xml version=\"1.0\"?><root>\n\r");
			bufferWritter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		LOG_FILE_NUMBER++;
	}

	/**
	 * Closes the xml log file
	 */
	public void endLogFile() {
		if (currentLogFile == null && currentLogFile.equals("") == false)
			return;

		try {
			FileWriter fileWritter = new FileWriter(currentLogFile, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write("\n\r</root>");
			bufferWritter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Append a line to xml log file
	 * 
	 * @param s
	 *            line
	 */
	public void appendLineToLogFile(String s) {
		if (currentLogFile == null || currentLogFile.equals(""))
			return;

		try {
			FileWriter fileWritter = new FileWriter(currentLogFile, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write(s);
			bufferWritter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Handle pull operation of the coverage.ec file from the emulator
	 * 
	 * @param count
	 *            Coverage file count
	 * @throws SocketException
	 */
	public void pullCoverage(int count) throws SocketException {
		notifyRipperLog("pull coverage...");

		String cov_file_name = "coverage" + System.currentTimeMillis() + ".ec";

		Message mCov = new Message(MessageType.COVERAGE_MESSAGE);
		mCov.addParameter("filename", cov_file_name);
		rsSocket.sendMessage(mCov);

		Message message = null;
		int retryCount = 0;
		do {
			message = rsSocket.readMessage(1000, false);

			if (message != null)
				break;

		} while (running && retryCount++ < ACK_MAX_RETRY);

		if (retryCount > ACK_MAX_RETRY)
			notifyRipperLog("max retry exceded coverage");

		if (message != null && message.isTypeOf(MessageType.ACK_MESSAGE)) {
			// notifyRipperLog("coverage");
			// Actions.pullCoverage(AUT_PACKAGE, cov_file_name, COVERAGE_PATH,
			// count);
			this.pullCoverageFile(cov_file_name, count);
		}
	}

	/**
	 * Call Actions.pullCoverage()
	 * 
	 * @param src
	 *            file on the emulator
	 * @param count
	 *            current coverage file number
	 */
	public void pullCoverageFile(String src, int count) {
		notifyRipperLog("coverage");
		Actions.pullCoverage(AUT_PACKAGE, src, COVERAGE_PATH, count);
	}

	/**
	 * Call Actions.pullCoverageStandardFile()
	 * 
	 * @param count
	 *            current coverage file number
	 */
	public void pullCoverageAfterEnd(int count) {
		notifyRipperLog("pull coverage after end...");
		Actions.pullCoverageStandardFile(AUT_PACKAGE, COVERAGE_PATH, count);
	}

	/**
	 * Call Actions.pullJUnitLog()
	 * 
	 * @param count
	 *            current junit log file number
	 */
	public void pullJUnitLog(int count) {
		notifyRipperLog("junit log");
		Actions.pullJUnitLog(AUT_PACKAGE, JUNIT_OUTPUT_PATH, count);
	}

	/**
	 * Handle PING Request and Response
	 * 
	 * @return
	 */
	public boolean ping() {
		int pingRetryCount = 0;

		try {
			do {
				notifyRipperLog("Ping...");
				Message m = rsSocket.ping();

				if (m != null && m.getType().equals(MessageType.PONG_MESSAGE)) {
					return true;
				} else if (m != null && m.getType().equals(MessageType.PONG_MESSAGE)) {
					notifyRipperLog("Message != PONG -> " + m.getType());
				}

				if (this.running == false)
					return false;

				if (pingRetryCount++ > PING_MAX_RETRY) {
					appendLineToLogFile("\n<failure type=\"ping\" />\n");
					return false;
				}

			} while (true);
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * Wait for an ACK message
	 * 
	 * @return Message
	 * @throws AckNotReceivedException
	 * @throws NullMessageReceivedException
	 */
	public Message waitAck() throws AckNotReceivedException, NullMessageReceivedException {
		Message msg = null;

		// wait for ack
		notifyRipperLog("Wait ack...");

		int retryCount = 0;

		do {

			try {
				msg = rsSocket.readMessage(1000, false);
			} catch (Exception ex) {
				return null;
			}

			if (msg != null)
				break;

		} while (running && retryCount++ < ACK_MAX_RETRY);

		if (running == false) {
			notifyRipperLog("running == false");
			return null;
		}

		if (retryCount > ACK_MAX_RETRY) {
			notifyRipperLog("waitAck() : max retry exceded event ack");
			throw new AckNotReceivedException();
		}

		if (msg == null) {
			notifyRipperLog("null message");
			throw new NullMessageReceivedException();
		}

		return msg;
	}

	/**
	 * Handle retrieval of the current ActivityDescription from the device
	 * 
	 * @return Serialized ActivityDescription (XML)
	 * @throws IOException
	 */
	public String getCurrentDescription() throws IOException {
		// describe
		notifyRipperLog("Send describe msg...");
		String xml = rsSocket.describe();

		if (xml != null) {
			notifyRipperLog(xml);
			// appendLineToLogFile( xml.substring(45, xml.length() - 8) );
		}

		return xml;
	}

	/**
	 * Update the ActivtyDescription currently stored in the
	 * lastActivityDescription variable.
	 * 
	 * Call getCurrentDescriptionAsActivityDescription()
	 * 
	 * @throws IOException
	 */
	public void updateLatestDescriptionAsActivityDescription() throws IOException {
		this.getCurrentDescriptionAsActivityDescription();
	}

	/**
	 * Latest assigned ActivityDescription Unique ID
	 */
	int activityUID = 0;

	/**
	 * Update and Return the ActivtyDescription of the current Activity
	 * 
	 * Store the description in the lastActivityDescription variable.
	 * 
	 * @return Current ActivityDescription
	 * @throws IOException
	 */
	public ActivityDescription getCurrentDescriptionAsActivityDescription() throws IOException {
		this.lastActivityDescription = null;

		String xml = this.getCurrentDescription();

		if (xml != null) {
			this.lastActivityDescription = ripperInput.inputActivityDescription(xml);
			this.lastActivityDescription.setUid(Integer.toString(++activityUID));
		}
		return this.lastActivityDescription;
	}

	/**
	 * Latest retrieved ActivityDescription.
	 */
	public ActivityDescription lastActivityDescription = null;

	/**
	 * Return latest retrieved ActivityDescription.
	 * 
	 * @return
	 */
	public ActivityDescription getLastActivityDescription() {
		return lastActivityDescription;
	}

	/**
	 * Current Logcat file number
	 */
	public int LOGCAT_FILE_NUMBER = 0;

	/**
	 * StartUp of the Ripping Process
	 * 
	 * @return
	 */
	public boolean startup() {
		int pingFailures = 0;

		long startup_t1 = System.currentTimeMillis();

		notifyRipperLog("Start emulator...");
		Actions.restartADBServer();
		Actions.startEmulatorNoSnapshotSave(AVD_NAME, EMULATOR_PORT);

		// wait for emulator
		this.waitForEmulator(EMULATOR_PORT);

		// starts adb logcat dumper
		new LogcatDumper(EMULATOR_PORT, LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + (LOGCAT_FILE_NUMBER) + ".txt")
				.start();
		LOGCAT_FILE_NUMBER++;

		Actions.setRipperActive(running);

		// start ripper service
		notifyRipperLog("Start ripper service...");
		Actions.startAndroidRipperService();

		// redirect port
		notifyRipperLog("Redir port...");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "redir add tcp:" + PORT + ":" + PORT);

		// socket
		rsSocket = new RipperServiceSocket("localhost", PORT);

		// start android ripper test case
		notifyRipperLog("Start ripper...");
		Actions.startAndroidRipper(AUT_PACKAGE);

		int socket_exception_count = 0;

		try {
			notifyRipperLog("Connect...");

			do {
				try {

					if (rsSocket.isConnected() == false)
						rsSocket.connect();
					else
						break;

				} catch (Exception se) {
					socket_exception_count++;
				}
			} while (socket_exception_count <= SOCKET_EXCEPTION_THRESHOLD);
			if ((socket_exception_count >= SOCKET_EXCEPTION_THRESHOLD))
				return false;

			boolean ping = false;
			do {
				ping = this.ping();
				if (ping == false)
					pingFailures++;

			} while (ping == false && pingFailures <= PING_FAILURE_THRESHOLD);

			long startup_time = System.currentTimeMillis() - startup_t1;
			notifyRipperLog("Startup time: " + startup_time);

			// ready to go
			if (running && (pingFailures <= PING_FAILURE_THRESHOLD) // too many
																	// pings,
																	// need a
																	// restart
			) {
				//TODO: this.notifyRipperStarted()
				return true;
			}
		} catch (Exception se) {
			se.printStackTrace();
		}

		return false;
	}

	/**
	 * Kills the emulator
	 */
	public boolean shutdown() {
		notifyRipperLog("Shutdown...");
		Actions.sendMessageToEmualtor(EMULATOR_PORT, "kill");
		Actions.waitEmulatorClosed(EMULATOR_PORT);
		this.notifyRipperTaskEnded();
		return true;
	}

	/**
	 * Check if Emulator is ready to receive commands
	 * 
	 * @param avdPort
	 */
	public void waitForEmulator(Integer avdPort) {
		notifyRipperLog("Waiting for AVD...");
		Actions.waitForEmulator(avdPort);
		notifyRipperLog("AVD online!");
	}

	/**
	 * Return Scheduler instance
	 * 
	 * @return
	 */
	public Scheduler getScheduler() {
		return scheduler;
	}

	/**
	 * Return Planner instance
	 * 
	 * @return
	 */
	public Planner getPlanner() {
		return planner;
	}

	/**
	 * Return RipperOutput instance
	 * 
	 * @return
	 */
	public RipperOutput getRipperOutput() {
		return ripperOutput;
	}

	/**
	 * Evaluate each termination criterion (AND Operator)
	 * 
	 * @return
	 */
	public boolean checkTerminationCriteria() {
		for (TerminationCriterion tc : this.terminationCriteria) {
			if (tc.check() == false)
				return false;
		}

		return true;
	}

	/**
	 * Add a termination criterion
	 * 
	 * @param tc TerminationCriterion to add
	 */
	public void addTerminationCriterion(TerminationCriterion tc) {
		tc.init(this);
		terminationCriteria.add(tc);
	}

	/**
	 * Use the Planner to plan tasks basing on an ActivityDescription 
	 * 
	 * @param t Latest executed Task
	 * @param activity ActivityDescription
	 * @return
	 */
	public TaskList plan(Task t, ActivityDescription activity) {
		notifyRipperLog("Plan...");
		TaskList plannedTasks = planner.plan(t, activity);

		if (plannedTasks != null && plannedTasks.size() > 0) {
			notifyRipperLog("plannedTasks " + plannedTasks.size());

			/*
			 * appendLineToLogFile("\n<extracted_events>"); for (Task tsk :
			 * plannedTasks)
			 * appendLineToLogFile(this.ripperOutput.outputEvent(tsk.get(tsk.
			 * size() - 1))); appendLineToLogFile("</extracted_events>\n");
			 */
		} else {
			// ???
			notifyRipperLog("error in planning!");
			// appendLineToLogFile("\n<error type=\"no_planned_task\" />\n");
			throw new RuntimeException("No planned tasks!");
		}

		return plannedTasks;
	}

	/**
	 * Schedule next task using the Scheduler
	 * 
	 * @return
	 */
	public Task schedule() {
		return scheduler.nextTask();
	}

	/**
	 * Execute an event and returns the message received after its execution or
	 * throws an exception if error
	 * 
	 * @param evt
	 *            Event
	 * @return Message
	 * @throws AckNotReceivedException
	 * @throws NullMessageReceivedException
	 */
	public Message executeEvent(Event evt) throws AckNotReceivedException, NullMessageReceivedException {
		// appendLineToLogFile(this.ripperOutput.outputEvent(evt));
		notifyRipperLog("event:" + evt.toString());

		rsSocket.sendEvent(evt);
		return this.waitAck();
	}
}
