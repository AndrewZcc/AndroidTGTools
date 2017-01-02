package it.unina.android.ripper.driver.systematic;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.comparator.GenericComparator;
import it.unina.android.ripper.comparator.GenericComparatorConfiguration;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.ripper.driver.exception.AckNotReceivedException;
import it.unina.android.ripper.driver.exception.NullMessageReceivedException;
import it.unina.android.ripper.input.RipperInput;
import it.unina.android.ripper.input.XMLRipperInput;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.output.XMLRipperOutput;
import it.unina.android.ripper.planner.HandlerBasedPlanner;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.BreadthScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.states.ActivityStateList;
import it.unina.android.ripper.termination.EmptyActivityStateListTerminationCriterion;
import it.unina.android.ripper.termination.TerminationCriterion;

/**
 * ActiveLearning Driver
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class SystematicDriver extends AbstractDriver {
	
	/**
	 * States List File name
	 */
	public static String STATES_LIST_FILE = "activities.xml";

	/**
	 * ActivityDescription Comparator instance
	 */
	protected IComparator comparator;

	/**
	 * ActivityStateList instance
	 * 
	 * TODO: generalize -> now only activity-based state
	 */
	protected ActivityStateList statesList;

	/**
	 * Constructor. Default Components.
	 */
	public SystematicDriver() {
		this(new BreadthScheduler(), new HandlerBasedPlanner(), new XMLRipperInput(),
				new GenericComparator(GenericComparatorConfiguration.Factory.getCustomWidgetSimpleComparator()),
				new EmptyActivityStateListTerminationCriterion(), new XMLRipperOutput());
	}

	/**
	 * Constructor
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param comparator
	 * @param terminationCriterion
	 * @param ripperOutput
	 */
	public SystematicDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, IComparator comparator,
			TerminationCriterion terminationCriterion, RipperOutput ripperOutput) {

		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.comparator = comparator;
		this.statesList = new ActivityStateList(this.comparator);
		this.ripperOutput = ripperOutput;
		
		this.addTerminationCriterion(terminationCriterion);
	}
	
	/**
	 * Constructor
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param comparator
	 * @param terminationCriteria
	 * @param ripperOutput
	 */
	public SystematicDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, IComparator comparator,
			ArrayList<TerminationCriterion> terminationCriteria, RipperOutput ripperOutput) {

		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.comparator = comparator;
		this.statesList = new ActivityStateList(this.comparator);
		this.ripperOutput = ripperOutput;
		
		for (TerminationCriterion tc : terminationCriteria) {
			this.addTerminationCriterion(tc);
		}
		
	}

	/**
	 * Main Ripping Loop - Active Learning Implementation
	 */
	@Override
	public void rippingLoop() {
		// reset counters
		nEvents = 0;
		nTasks = 0;
		nFails = 0;
		nRestart = 0;

		// init acivities.xml
		initStateDescriptionFile();

		boolean bootstrap = false;

		long t1 = System.currentTimeMillis();

		long startup_time = 0;

		do {
			nRestart++;

			long startup_time_t1 = System.currentTimeMillis();
			boolean started = this.startup();
			startup_time += System.currentTimeMillis() - startup_time_t1;

			if (running && started) {
				createLogFile();

				try {
					if (bootstrap == false) {
						this.bootstrap();
						bootstrap = true;

						// close bootstrap logfile and create new logfile
						endLogFile();
						createLogFile();
					}

					Task t = this.schedule();

					if (t != null) {
						Message msg = this.executeTask(t);

						if (msg == null || running == false) {
							// do nothing
							notifyRipperLog("msg == null || running == false");
						} else {
							if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {
								nTasks++;
								nEvents += t.size();

								TaskList plannedTasks = new TaskList();
								if (compareAndAddState(getCurrentDescriptionAsActivityDescription())) {
									plannedTasks = plan(t, getLastActivityDescription());
									scheduler.addTasks(plannedTasks);

									appendStatesDescriptionFile(getLastActivityDescription());
								}

								// output
								if (plannedTasks == null) {
									plannedTasks = new TaskList();
								}
								// appendLineToLogFile(
								// this.ripperOutput.outputStepAndPlannedTasks(t.get(t.size()
								// - 1), getLastActivityDescription(),
								// plannedTasks) );
								ActivityDescription ad = getLastActivityDescription();
								ad.setId(statesList.getEquivalentActivityStateId(ad));
								appendLineToLogFile(
										this.ripperOutput.outputActivityDescriptionAndPlannedTasks(ad, plannedTasks));
							} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {
								nTasks++;
								nFails++;

								if (msg.containsKey("coverage_file")) {
									try {
										pullCoverageFile(msg.get("coverage_file"), nTasks);
									} catch (Throwable throwable) {
										// ignored
									}
								}

								this.appendLineToLogFile("\n<fail />\n");
							} else {
								notifyRipperLog("executeTask(): something went wrong?!?");
								this.appendLineToLogFile("\n<error type='executeTask' />\n");
							}

							//TODO
							if (SCREENSHOT) {
								
							}
							
							if (PULL_COVERAGE) {
								notifyRipperLog("pull coverage before end...");
								pullCoverage(nTasks);
							}
						}

						Actions.sleepMilliSeconds(SLEEP_AFTER_TASK);
					}
				} catch (Throwable throwable) {
					throwable.printStackTrace();
				}

				endLogFile();

			}

			notifyRipperLog("End message...");
			rsSocket.sendMessage(Message.getEndMessage());
			// TODO: wait ack

			try {
				rsSocket.disconnect();
			} catch (Exception ex) {
				// ignored
			}

			notifyRipperLog("Wait process end...");
			Actions.waitForProcessToEnd(AUT_PACKAGE, EMULATOR_PORT);
			notifyRipperLog("Wait test_case end...");
			while (Actions.isRipperActive()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// pullCoverageAfterEnd
			if (PULL_COVERAGE) {
				notifyRipperLog("pull coverage before end...");
				pullCoverageAfterEnd(nTasks);
			}

			// pullJUnitLog
			pullJUnitLog(nTasks);

			this.shutdown();

			this.ifIsPausedDoPause();

		} while (running && this.checkTerminationCriteria() == false);

		closeStateDescriptionFile();

		this.notifyRipperEnded();

		long executionTime = System.currentTimeMillis() - t1;

		this.notifyRipperLog("Execution Time: " + executionTime);

		String reportXML = "<?xml version=\"1.0\"?><report>\n";
		reportXML += "<events>" + nEvents + "</events>\n";
		reportXML += "<execution_time>" + executionTime + "</execution_time>\n";
		reportXML += "<restart>" + nRestart + "</restart>\n";
		reportXML += "<failure>" + nFails + "</failure>\n";
		reportXML += "<tasks>" + nTasks + "</tasks>\n";
		reportXML += "<startup_time>" + startup_time + "</startup_time>\n";
		reportXML += "</report>";

		writeReportFile(reportXML);
	}

	/**
	 * Open the XML Description File (no overwrite)
	 */
	protected void initStateDescriptionFile() {
		this.initStateDescriptionFile(false);
	}
	
	/**
	 * Open the XML Description File
	 * 
	 * @param overwrite
	 */
	protected void initStateDescriptionFile(boolean overwrite) {
		if (overwrite || new File(XML_OUTPUT_PATH + STATES_LIST_FILE).exists() == false) {
			writeStringToFile("<?xml version=\"1.0\"?><states>\n", XML_OUTPUT_PATH + STATES_LIST_FILE);
		}
	}

	/**
	 * Close the XML Description File
	 */
	protected void closeStateDescriptionFile() {
		appendStringToFile("</states>\n", XML_OUTPUT_PATH + STATES_LIST_FILE);
	}

	/**
	 * Append an ActivtyDescription to the XML Description File
	 * 
	 * @param ad
	 */
	protected void appendStatesDescriptionFile(ActivityDescription ad) {
		appendStringToFile("\n" + this.ripperOutput.outputActivityDescription(ad), XML_OUTPUT_PATH + STATES_LIST_FILE);
	}

	/**
	 * Bootstrap Active Learning Ripping Process. Retrieves the first ActivityDescription.
	 * 
	 * Precondition: rsSocket should be connected
	 */
	protected void bootstrap() {
		try {
			if (PULL_COVERAGE_ZERO)
				pullCoverage(0);

			notifyRipperLog("Alive...");
			if (rsSocket.isAlive() == false)
				throw new RuntimeException("Emulator Killed!"); // emulator
																// killed

			// describe
			ActivityDescription activity = getCurrentDescriptionAsActivityDescription();

			if (activity != null) {
				// add to visited states
				statesList.addActivity(activity);

				// output state
				// this.appendLineToLogFile("<startup>\n");

				ActivityDescription ad = statesList.getLatestAdded();
				// this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));
				appendStatesDescriptionFile(ad);

				// plan
				TaskList plannedTasks = plan(null, activity);
				scheduler.addTasks(plannedTasks);

				this.appendLineToLogFile(this.ripperOutput.outputFirstStep(ad, plannedTasks));

				// this.appendLineToLogFile("\n</startup>\n");
			} else {
				throw new RuntimeException("bootstrap(): description error!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Returns the Message related to the execution of the last event of the
	 * task or null if an ack message is not received
	 * 
	 * @param t
	 *            Task
	 * @return Message
	 */
	protected Message executeTask(Task t) {
		Message msg = null;

		// this.appendLineToLogFile("<task>");

		try {
			this.updateLatestDescriptionAsActivityDescription();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}

		// for(Event evt : t)
		for (int i = 0; i < t.size(); i++) {
			Event evt = t.get(i);

			// this.appendLineToLogFile("<begin
			// timestamp=\""+System.currentTimeMillis()+"\" />");
			try {
				ActivityDescription ad = this.getLastActivityDescription();
				ad.setId(statesList.getEquivalentActivityStateId(ad));

				// output activity description
				// this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));

				if (checkBeforeEventStateId(ad, evt)) {
					try {
						this.appendLineToLogFile(this.ripperOutput.outputFiredEvent(evt));
						msg = executeEvent(evt);
						// output firede event
					} catch (AckNotReceivedException e1) {
						msg = null;
						notifyRipperLog("executeTask(): AckNotReceivedException"); // failure
						this.appendLineToLogFile("\n<error type='AckNotReceivedException' />\n");
					} catch (NullMessageReceivedException e2) {
						msg = null;
						notifyRipperLog("executeTask(): NullMessageReceivedException"); // failure
						this.appendLineToLogFile("\n<error type='NullMessageReceivedException' />\n");
					}

					if (msg == null || running == false) {
						break;
					} else if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {
						this.updateLatestDescriptionAsActivityDescription();

						// output
						ad = this.getLastActivityDescription();
						ad.setId(statesList.getEquivalentActivityStateId(ad));

						// ignore last activity
						if (i < t.size() - 1) {
							this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(ad));
						}

						Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);
					} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {
						return msg;
					}
				} else {
					// something went wrong
					// throw new BeforeEventStateAssertionFailedException()???
					this.appendLineToLogFile("\n<error type='BeforeEventStateAssertionFailed' />\n");
				}
			} catch (IOException ex) {
				notifyRipperLog("executeTask(): Description IOException");
				this.appendLineToLogFile("\n<error type='IOException' />\n");
			}

			// this.appendLineToLogFile("<end
			// timestamp=\""+System.currentTimeMillis()+"\" />");
		}

		// this.appendLineToLogFile("</task>");

		return msg;
	}

	/**
	 * Compare a ActivityDescription with the ones contained in the statesList
	 * 
	 * @param activity ActivityDescription to compare
	 * @return
	 */
	protected boolean compareAndAddState(ActivityDescription activity) {
		notifyRipperLog("\tComparator...");
		if (statesList.containsActivity(activity) == null) {
			// add to visited states
			statesList.addActivity(activity);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Check if the ActivityDescription is equivalent (using the Comparator) to the one
	 * where the event evt was planned to be performed
	 * 
	 * @param ad Activity Description
	 * @param evt Event Description
	 * @return
	 * @throws IOException
	 */
	protected boolean checkBeforeEventStateId(ActivityDescription ad, Event evt) throws IOException {
		if (evt.getBeforeExecutionStateUID().equals("UNDEFINED"))
			return true;

		return ad.getId().equals(evt.getBeforeExecutionStateUID());
	}

	/**
	 * Get the XML Description File name with full path
	 * 
	 * @return
	 */
	public String getStatesListFile() {
		return XML_OUTPUT_PATH + STATES_LIST_FILE;
	}

	/**
	 * Set the XML Description File
	 * 
	 * @param statesListFile
	 */
	public void setStatesListFile(String statesListFile) {
		STATES_LIST_FILE = statesListFile;
	}

	/**
	 * Comparator Instance
	 * @return
	 */
	public IComparator getComparator() {
		return comparator;
	}

	/**
	 * ActivityStateList Instance
	 * 
	 * @return
	 */
	public ActivityStateList getStatesList() {
		return statesList;
	}
}
