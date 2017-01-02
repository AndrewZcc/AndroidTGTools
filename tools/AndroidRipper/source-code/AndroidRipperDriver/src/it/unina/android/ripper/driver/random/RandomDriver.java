package it.unina.android.ripper.driver.random;

import java.util.ArrayList;

import it.unina.android.ripper.autoandroidlib.Actions;
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
import it.unina.android.ripper.scheduler.DebugRandomScheduler;
import it.unina.android.ripper.scheduler.RandomScheduler;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.MaxEventsTerminationCriterion;
import it.unina.android.ripper.termination.TerminationCriterion;

/**
 * RandomDriver
 * 
 * TODO:
 * - extracted_events instead of tasklist
 * - selected_event instead of event
 * - both in activity
 * 
 * @author Testing
 *
 */

public class RandomDriver extends AbstractDriver {
	/**
	 * Number of random events to trigger
	 */
	public int NUM_EVENTS = 1000;

	/**
	 * Number of events for each session
	 * 
	 * 0 = until NUM_EVENTS
	 */
	public int NUM_EVENTS_PER_SESSION = 0;

	/**
	 * Number of events between each coverage.ec retrieval
	 * 
	 * 0 = no coverage
	 */
	public int COVERAGE_FREQUENCY = 100;

	/**
	 * Random Seed
	 */
	public long RANDOM_SEED = System.currentTimeMillis();

	/**
	 * Number of events between each xml log creation
	 * 
	 * 0 = until end of the session
	 */
	public int NEW_LOG_FREQUENCY = 0;

	/**
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param ripperOutput
	 * @param terminationCriterion
	 */
	public RandomDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, RipperOutput ripperOutput,
			TerminationCriterion terminationCriterion) {
		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.ripperOutput = ripperOutput;

		this.addTerminationCriterion(terminationCriterion);

		// TODO: move
		// Planner.CAN_GO_BACK_ON_HOME_ACTIVITY = false;

		// TODO: create coverage dir
	}

	/**
	 * 
	 * @param scheduler
	 * @param planner
	 * @param ripperInput
	 * @param ripperOutput
	 * @param terminationCriteria
	 */
	public RandomDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput, RipperOutput ripperOutput,
			ArrayList<TerminationCriterion> terminationCriteria) {
		super();

		this.scheduler = scheduler;
		this.planner = planner;
		this.ripperInput = ripperInput;
		this.ripperOutput = ripperOutput;

		for (TerminationCriterion tc : terminationCriteria) {
			this.addTerminationCriterion(tc);
		}

		// TODO: create coverage dir
	}

	/**
	 * Main Ripping Loop - Random Implementation
	 */
	@Override
	public void rippingLoop() {
		//reset counters
		nEvents = 0;
		nTasks = 0;
		nFails = 0;
		nRestart = 0;

		boolean bootstrap = false;

		long t1 = System.currentTimeMillis();

		long startup_time = 0;

		do {
			nRestart++;

			//startup process
			long startup_time_t1 = System.currentTimeMillis();
			boolean started = this.startup();
			startup_time += System.currentTimeMillis() - startup_time_t1;

			if (running && started) {
				createLogFileAtCurrentTimeMillis();

				if (bootstrap == false) {
					if (PULL_COVERAGE_ZERO) {
						try {
							pullCoverage(0);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

					bootstrap = true;
				}

				try {

					do {
						notifyRipperLog("Alive...");
						if (rsSocket.isAlive() == false) {
							notifyRipperLog("... NOT Alive!");
							break;
						}

						// describe
						ActivityDescription activity = getCurrentDescriptionAsActivityDescription();
						if (activity != null) {

							this.appendLineToLogFile(this.ripperOutput.outputActivityDescription(activity));

							// plan
							TaskList plannedTasks = plan(null, activity);
							scheduler.addTasks(plannedTasks);

							// schedule
							Task t = scheduler.nextTask();

							if (t == null) {
								notifyRipperLog("No scheduled task!");

								appendLineToLogFile("\n<error type=\"nothing_scheduled\" />\n");
								continue; // nothing to do
							}

							//execute
							Message msg = this.executeTask(t);

							//handle execution result
							if (msg == null || running == false) {
								// do nothing
								notifyRipperLog("msg == null || running == false");
							} else {

								if (msg != null && msg.isTypeOf(MessageType.ACK_MESSAGE)) {

									nTasks++;
									nEvents++;

								} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {

									nFails++;
									this.appendLineToLogFile("\n<failure type=\"fail_message\" />\n");

								} else {

									notifyRipperLog("executeTask(): something went wrong?!?");
									this.appendLineToLogFile("\n<error type='executeTask' />\n");

								}

								// TODO
								if (SCREENSHOT) {

								}

								if (PULL_COVERAGE && COVERAGE_FREQUENCY != 0 && (nEvents - 1) >= COVERAGE_FREQUENCY
										&& ((nEvents - 1) % COVERAGE_FREQUENCY == 0)) {
									notifyRipperLog("pull coverage...");
									pullCoverage(nEvents - 1);
								}

							}

						}

						if (NUM_EVENTS_PER_SESSION > 0 && (nEvents % NUM_EVENTS_PER_SESSION == 0)) {
							notifyRipperLog("session limit reached : " + nEvents + "|" + NUM_EVENTS_PER_SESSION);
							break;
						}

						if (NEW_LOG_FREQUENCY > 0 && (nEvents % NEW_LOG_FREQUENCY == 0)) {
							endLogFile();
							createLogFileAtCurrentTimeMillis();
						}

					} while (running && this.checkTerminationCriteria() == false);

				} catch (Exception ex) {
					ex.printStackTrace();
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

		long executionTime = System.currentTimeMillis() - t1;

		this.notifyRipperLog("Execution Time: " + executionTime);

		String reportXML = "<?xml version=\"1.0\"?><report>\n";
		reportXML += "<seed>" + RANDOM_SEED + "</seed>\n";
		reportXML += "<events>" + nEvents + "</events>\n";
		reportXML += "<execution_time>" + executionTime + "</execution_time>\n";
		reportXML += "<restart>" + nRestart + "</restart>\n";
		reportXML += "<failure>" + nFails + "</failure>\n";
		reportXML += "<tasks>" + nTasks + "</tasks>\n";
		reportXML += "<startup_time>" + startup_time + "</startup_time>\n";
		reportXML += "</report>";

		writeReportFile(reportXML);

		this.notifyRipperEnded();

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

		if (t != null && t.size() > 0) {

			Event evt = t.get(0);
			try {
				this.appendLineToLogFile(this.ripperOutput.outputFiredEvent(evt));
				msg = executeEvent(evt);
			} catch (AckNotReceivedException e1) {
				msg = null;
				notifyRipperLog("executeTask(): AckNotReceivedException"); // failure
				this.appendLineToLogFile("\n<error type='AckNotReceivedException' />\n");
			} catch (NullMessageReceivedException e2) {
				msg = null;
				notifyRipperLog("executeTask(): NullMessageReceivedException"); // failure
				this.appendLineToLogFile("\n<error type='NullMessageReceivedException' />\n");
			}

			Actions.sleepMilliSeconds(SLEEP_AFTER_EVENT);

		}

		return msg;
	}
}
