package it.unina.android.ripper.driver.systematic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.unina.android.ripper.autoandroidlib.Actions;
import it.unina.android.ripper.autoandroidlib.logcat.LogcatDumper;
import it.unina.android.ripper.comparator.IComparator;
import it.unina.android.ripper.input.RipperInput;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.net.Message;
import it.unina.android.ripper.net.MessageType;
import it.unina.android.ripper.net.RipperServiceSocket;
import it.unina.android.ripper.output.RipperOutput;
import it.unina.android.ripper.planner.Planner;
import it.unina.android.ripper.scheduler.Scheduler;
import it.unina.android.ripper.termination.TerminationCriterion;

/**
 * Hybrid (Manual + ActiveLearning) Driver
 * 
 * TODO: translate comments
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class TestCasesExecutionSystematicDriver extends SystematicDriver {

	/**
	 * Total number of test cases set in the bootstrap() method
	 */
	int totalNumberOfTestCases = 0; // num_tot_testcase

	/**
	 * Current test cases
	 */
	int currentTestCaseIndex = 0; // num_tc_corrente

	/**
	 * Set to false if currentTestCaseIndex < totalNumberOfTestCases to allow
	 * the execution of the first step of the process
	 */
	boolean testCasesAlreadyExecuted = true; // tc_finiti

	/**
	 * Is the first execution?
	 */
	boolean beforeTestCasesProcess = true; // esecuzione1

	/**
	 * Set to true if a new state has been found during the execution of the
	 * test case
	 */
	boolean newStateFoundFromTestExecution = false; // cond_nuovo

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
	public TestCasesExecutionSystematicDriver(Scheduler scheduler, Planner planner, RipperInput ripperInput,
			IComparator comparator, TerminationCriterion terminationCriterion, RipperOutput ripperOutput) {
		super(scheduler, planner, ripperInput, comparator, terminationCriterion, ripperOutput);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sends a message to execute a test case by index
	 * 
	 * @param MAX_RETRY
	 * @param i
	 *            index of the test case (ordinal)
	 * @return
	 * @throws SocketException
	 */
	public String runUserTestCase(int MAX_RETRY, int i) throws SocketException {
		Message runMSGdecr = null;

		rsSocket.sendMessage(Message.getUserTestMessage(Integer.toString(i)));

		int describeCnt = 0;
		do {
			runMSGdecr = rsSocket.readMessageNoTimeout(true);

			if (runMSGdecr != null && runMSGdecr.getType().equals(MessageType.DESCRIBE_MESSAGE) == false) {
				System.out.println("Message != runMSGdecr -> " + runMSGdecr.getType());
				continue;
			}

			if (runMSGdecr != null && runMSGdecr.getParameterValue("wait") != null) {
				try {
					Thread.sleep(1000);
				} catch (Throwable tr) {
				}

				rsSocket.sendMessage(Message.getUserTestMessage(Integer.toString(i)));
				continue;

			} else if (runMSGdecr != null && runMSGdecr.getParameterValue("xml") != null) {

				String xml = runMSGdecr.getParameterValue("xml");
				if (xml != null && xml.length() > 45) {
					return xml;
				}

				rsSocket.sendMessage(Message.getUserTestMessage(Integer.toString(i)));

				if (xml != null)
					System.out.println("xml != null" + xml);

			} else {

				try {
					Thread.sleep(1000);
				} catch (Throwable tr) {
				}

				if (describeCnt++ > MAX_RETRY)
					throw new RuntimeException("JK describeCnt overflow");

				// System.out.println("Describe retry " + describeCnt);
				// this.sendMessage(Message.getDescribeMessage());

				continue;
			}
		} while (true);
	}

	/**
	 * Requests the number of executable test cases available
	 * 
	 * @return
	 * @throws SocketException
	 */
	public String requestNumOfTestCases() throws SocketException {
		Message NumTestCaseMSG = null;
		rsSocket.sendMessage(Message.getNumTestCaseMessage());

		String numero_test_case_rx = null;
		NumTestCaseMSG = rsSocket.readMessage(4000, true);
		System.out.println("Lettura Message richiediNumTestCase -> " + NumTestCaseMSG);

		if (NumTestCaseMSG != null && NumTestCaseMSG.getParameterValue("num") != null) {

			numero_test_case_rx = NumTestCaseMSG.getParameterValue("num");

		}
		System.out.println("Valore numero_test_case ricevuto -> " + numero_test_case_rx);
		return numero_test_case_rx;

	}

	/**
	 * Executes a test case at each iteration of the ripping process
	 * 
	 * @param i
	 *            index of the test case
	 * @return
	 * @throws SocketException
	 */
	public String executeTestCaseBeforeIteration(int i) throws SocketException {
		Message eseguiTestCaseMSG = null;
		rsSocket.sendMessage(Message.getExecuteTestCaseMessage(Integer.toString(i)));
		do {

			String result = null;
			eseguiTestCaseMSG = rsSocket.readMessage(3000, false);

			if (eseguiTestCaseMSG != null && eseguiTestCaseMSG.getParameterValue("runner") != null) {
				result = eseguiTestCaseMSG.getParameterValue("runner");
				return result;
			} else {
				try {
					Thread.sleep(500);
				} catch (Throwable tr) {
				}
				continue;
			}

		} while (true);
	}

	/**
	 * Main Ripping Loop - (Manual + Active Learning) Implementation
	 */
	@Override
	public void rippingLoop() {
		// reset counters
		nEvents = 0;
		nTasks = 0;
		nFails = 0;
		nRestart = 0;

		boolean bootstrap = false;

		long t1 = System.currentTimeMillis();

		long startup_time = 0;

		// loop control on finished test_case
		do {
			// loop ripper
			do {
				nRestart++;

				long startup_time_t1 = System.currentTimeMillis();
				boolean started = this.startup(beforeTestCasesProcess);
				startup_time += System.currentTimeMillis() - startup_time_t1;

				if (running && started) {
					if (beforeTestCasesProcess && !new File(XML_OUTPUT_PATH + STATES_LIST_FILE).exists()) {
						createLogFile(currentTestCaseIndex); // creates logFile
																// before
																// execution
																// ripper

					} else if (bootstrap == true) {
						createLogFile(currentTestCaseIndex);
						// crea logFile successivi al primo

					}

					try {
						if (bootstrap == false) {
							this.bootstrap();
							bootstrap = true;

							// close bootstrap logfile and create new logfile
							endLogFile();
							createLogFile(currentTestCaseIndex);// primo log
																// file
						}

						if (newStateFoundFromTestExecution == true) {
							// Sending message running tests i-th houses and wait response

							System.out.println("Invio msg per esecuzione testcase : " + currentTestCaseIndex);

							String msg_ack = executeTestCaseBeforeIteration(currentTestCaseIndex);

							notifyRipperLog("Ho ricevuto ACK TestCase " + msg_ack);

						}

						Task t = this.schedule();
						notifyRipperLog(" Task schedulato : " + t);
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
									appendLineToLogFile(this.ripperOutput.outputActivityDescriptionAndPlannedTasks(
											getCurrentDescriptionAsActivityDescription(), plannedTasks));
								} else if ((msg != null && msg.isTypeOf(MessageType.FAIL_MESSAGE))) {
									nTasks++;
									nFails++;

									if (msg.containsKey("coverage_file")) {
										try {
											// crea file coverage
											if (beforeTestCasesProcess) {
												pullCoverageFile(msg.get("coverage_file"), nTasks);
											} else {
												pullCoverageFile(msg.get("coverage_file"), nTasks,
														currentTestCaseIndex);
											}

										} catch (Throwable throwable) {
											// ignored
										}
									}

									this.appendLineToLogFile("\n<fail />\n");
								} else {
									notifyRipperLog("executeTask(): something went wrong?!?");
								}

								if (PULL_COVERAGE) {
									notifyRipperLog("pull coverage before end...");
									// crea file coverage
									if (beforeTestCasesProcess) {
										pullCoverage(nTasks);
									} else {
										pullCoverage(nTasks, currentTestCaseIndex);
									}

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
					if (beforeTestCasesProcess) {
						pullCoverageAfterEnd(nTasks);
					} else {
						pullCoverageAfterEnd(nTasks, currentTestCaseIndex);
					}

				}

				// crea file JUnitLog
				if (beforeTestCasesProcess == true) {
					// pullJUnitLog
					pullJUnitLog(nTasks);
				} else {
					pullJUnitLog(nTasks, currentTestCaseIndex);

				}

				this.shutdown();

				this.ifIsPausedDoPause();

			} while (running && this.checkTerminationCriteria() == false);
			// condizioni di uscita ripper

			closeStateDescriptionFile();

			// controllo su raggiungimento num totale di test case
			if (currentTestCaseIndex < totalNumberOfTestCases && beforeTestCasesProcess == false) {
				bootstrap = false; // devo rientrare nel bootstrap perch�
									// num_tc_corrente<num_tot_testcase

				System.out.println("Test utente non terminati");

			} else {
				testCasesAlreadyExecuted = false; // test utente terminati

				System.out.println("Test utente terminati");
				break; // esci dal loop; testcase finiti
			}

		} while (testCasesAlreadyExecuted == true); // loop fin quando i test
													// case non sono terminati

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
	 * Bootstrap Active Learning Ripping Process. Retrieves the first ActivityDescription.
	 * 
	 * Precondition: rsSocket should be connected
	 */
	protected void bootstrap() {

		try {

			ActivityDescription activity = null;
			newStateFoundFromTestExecution = false;

			if (PULL_COVERAGE_ZERO)
				if (beforeTestCasesProcess) { // per file di coverage
					pullCoverage(0);
				} else {
					pullCoverage(0, currentTestCaseIndex + 1);
				}

			notifyRipperLog("Alive...");
			if (rsSocket.isAlive() == false)
				throw new RuntimeException("Emulator Killed!"); // emulator
																// killed

			// controlla esistenza di activities.xml
			if (new File(XML_OUTPUT_PATH + STATES_LIST_FILE).exists()) {
				// se il file esiste vuol dire che abbiamo lanciato almeno una
				// volta il ripper.
				// quindi segue codice per eseguire i test_case utente e cercare
				// un nuovo stato

				beforeTestCasesProcess = false;

				/*
				 * Invia msg a RipperTestCase per sapere il numero totale di
				 * testcase e lo memorizzo in una variabile num_tot_testcase �
				 * la variabile che mi indica quanti sono i testcase utente
				 */

				totalNumberOfTestCases = Integer.parseInt(requestNumOfTestCases());
				notifyRipperLog("Num totale testcase ottenuto da RipperTestCase: " + totalNumberOfTestCases);

				ArrayList<ActivityDescription> adList = ripperInput
						.loadActivityDescriptionList(XML_OUTPUT_PATH + STATES_LIST_FILE);

				initStateDescriptionFile(true);

				for (ActivityDescription ad : adList) {
					statesList.addActivity(ad);
					appendStatesDescriptionFile(ad);
				}

				while (currentTestCaseIndex < totalNumberOfTestCases) {

					currentTestCaseIndex += 1;

					System.out.println("Test case corrente: " + currentTestCaseIndex);
					// ottengo activity description relativo all-esecuzione del
					// test case
					ActivityDescription activityUtente = getCurrentDescriptionAsActivityDescription_Utente(
							currentTestCaseIndex);

					if (PULL_COVERAGE) {
						notifyRipperLog("pull coverage after test case...");
						pullCoverage(0, currentTestCaseIndex);
					}

					// notifyRipperLog("ActivityUtente ricevuta prima di
					// comparazione: "+activityUtente.toString());

					String id_activity = null;
					// compara activityUtente con contenuto stateList
					if ((id_activity = statesList.containsActivity(activityUtente)) != null) {
						// activity gi� presente

						notifyRipperLog("Activity con ID : " + id_activity + " gi� presente in lista ");
						notifyRipperLog("TestCase_" + currentTestCaseIndex + ": nessuno stato nuovo ");

						this.shutdown(); // spegni avd

						boolean started_utente = this.startup(beforeTestCasesProcess); // avvia
																						// avd

						if (started_utente == true) { // se ok continua
							continue;
						} else {
							// notifica mancata accensione avd
							System.out.println("SyDr Mancata accensione avd; started utente " + started_utente);
						}

					} else {
						// activity non presente--> trovato nuovo stato

						statesList.addActivity(activityUtente);
						notifyRipperLog("Trovato NUOVO STATO; Activity con title : <"
								+ activityUtente.getTitle().toString() + "> non presente in lista Activity. Classe :"
								+ activityUtente.getClassName().toString() + " Num.Widget :"
								+ activityUtente.getWidgets().size());
						notifyRipperLog("TestCase_ " + currentTestCaseIndex + " ha trovato uno stato nuovo ");

						activity = activityUtente;
						newStateFoundFromTestExecution = true;

						// spegni avd
						this.shutdown();

						boolean started_utente = this.startup(beforeTestCasesProcess);

						if (started_utente == true) {
							System.out.println("Riavvio AVD; started_utente: " + started_utente);
							break;
						} else {
							// notifica mancata accensione avd
							System.out.println("Mancata accensione avd ");
						}

					}

				}

				notifyRipperLog(" TestCaseNum. :" + currentTestCaseIndex + "; Num.TotaleTC :" + totalNumberOfTestCases
						+ "; condizione stato nuovo :" + newStateFoundFromTestExecution);

				// controllo su test case > se finiti esco
				if (currentTestCaseIndex == totalNumberOfTestCases && newStateFoundFromTestExecution == false) {
					// esci
					closeStateDescriptionFile(); // chiude file activities.xml

					// endLogFile();

					this.shutdown();
					notifyRipperLog(" TestCase terminati ... ");
					notifyRipperEnded();
				}

			}

			// RIPPER funzionamento NORMALE
			// init acivities.xml
			initStateDescriptionFile();

			notifyRipperLog("START RIPPER");

			if (newStateFoundFromTestExecution == false) {
				// describe
				activity = getCurrentDescriptionAsActivityDescription();
			}

			if (activity != null) {

				if (newStateFoundFromTestExecution == false) {
					// add to visited states
					statesList.addActivity(activity);
					// notifyRipperLog("statelist originale: " +statesList);
				}
				// output state
				// this.appendLineToLogFile("<startup>\n");
				if (beforeTestCasesProcess == false) {
					createLogFile(currentTestCaseIndex); // crea log file test
															// case corrente
				}

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
	 * StartUp of the Ripping Process
	 * 
	 * @param firstExecution is the first execution?
	 * @return
	 */
	protected boolean startup(boolean firstExecution) {
		// parametro 'esecuzione1' per differenziare i logcat delle 2 differenti
		// esecuzioni
		this.beforeTestCasesProcess = firstExecution;
		int pingFailures = 0;

		long startup_t1 = System.currentTimeMillis();

		notifyRipperLog("Start emulator...");
		Actions.startEmulatorNoSnapshotSave(AVD_NAME, EMULATOR_PORT);

		// wait for emulator
		this.waitForEmulator(EMULATOR_PORT);

		// starts adb logcat dumper
		// new LogcatDumper(EMULATOR_PORT, LOGCAT_PATH + "logcat_" +
		// EMULATOR_PORT + "_" + (LOGCAT_FILE_NUMBER) + ".txt").start();
		// LOGCAT_FILE_NUMBER++;

		String file_logcat = LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + (LOGCAT_FILE_NUMBER) + ".txt";

		if (firstExecution == true && !(new File(file_logcat).exists())) { // se
																		// esecuzione
																		// 1 e
																		// non
																		// esiste
																		// il
																		// file
																		// logcat
			new LogcatDumper(EMULATOR_PORT,
					LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_" + (LOGCAT_FILE_NUMBER) + ".txt").start();
		} else { // se esecuzione1 == false OR file_logcat esiste il nome del
					// file comprende la stringa "Esecuzione2"
			new LogcatDumper(EMULATOR_PORT,
					LOGCAT_PATH + "logcat_" + EMULATOR_PORT + "_Exec2_" + (LOGCAT_FILE_NUMBER) + ".txt").start();
		}

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
				// TODO: this.notifyRipperStarted()
				return true;
			}
		} catch (Exception se) {
			se.printStackTrace();
		}

		return false;
	}

	/**
	 * Number of the current test case
	 * 
	 * NOTE: Il nome del log file e' caratterizzato dal numero di test case corrente e da un indice.
	 */
	int num_tc_old = 0;

	public void createLogFile(int num_tc_corrente) {
		// se num_tc_corrente > num_tc_old vuol dire che devo scrivere il log
		// del test case successivo resettando l-indice
		if (num_tc_corrente > num_tc_old) {
			// indice di log; log file number reset
			LOG_FILE_NUMBER = 0;
		}
		// currentLogFile = LOG_FILE_PREFIX + System.currentTimeMillis() +
		// ".xml";
		currentLogFile = XML_OUTPUT_PATH + LOG_FILE_PREFIX + "test_" + num_tc_corrente + "_" + LOG_FILE_NUMBER + ".xml";

		try {
			FileWriter fileWritter = new FileWriter(currentLogFile, false);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.write("<?xml version=\"1.0\"?><root>\n\r");
			bufferWritter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		LOG_FILE_NUMBER++; // incremento indice
		num_tc_old = num_tc_corrente;
	}

	/**
	 * Close current XML log file
	 * 
	 * @param num_tc_corrente
	 */
	public void endLogFile(int num_tc_corrente) {
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
	 * Handle pull operation of the coverage.ec file from the emulator
	 * 
	 * @param count Coverage file number 
	 * @param testCaseIndex Test Case Index
	 * @throws SocketException
	 */
	public void pullCoverage(int count, int testCaseIndex) throws SocketException {
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
			Actions.pullCoverageForUserTestCase(AUT_PACKAGE, cov_file_name, COVERAGE_PATH, count, testCaseIndex);
		}
	}

	/**
	 * Handle pull operation of the coverage.ec file from the emulator
	 * 
	 * @param src source coverage file name
	 * @param count Coverage file number 
	 * @param testCaseIndex Test Case Index
	 */
	protected void pullCoverageFile(String src, int count, int testCaseIndex) {
		notifyRipperLog("coverage");
		Actions.pullCoverageForUserTestCase(AUT_PACKAGE, src, COVERAGE_PATH, count, testCaseIndex);
	}

	/**
	 * Pull from the emulator the jUnit log for a Test Case
	 * 
	 * @param count Coverage file number 
	 * @param testCaseIndex Test Case Index
	 */
	public void pullJUnitLog(int count, int testCaseIndex) {
		notifyRipperLog("junit log");
		Actions.pullJUnitLogForUserTestCase(AUT_PACKAGE, COVERAGE_PATH, count, testCaseIndex);
	}

	/**
	 * Handle pull operation of the coverage.ec file from the emulator
	 * 
	 * @param count Coverage file number 
	 * @param testCaseIndex Test Case Index
	 */
	public void pullCoverageAfterEnd(int count, int testCaseIndex) {
		notifyRipperLog("pull coverage after end...");
		Actions.pullCoverageStandardFileForUserTestCase(AUT_PACKAGE, COVERAGE_PATH, count, testCaseIndex);
	}

	/**
	 * runUserTestCase() and return current ActivityDescription (XML)
	 * 
	 * NOTE: richiede activity description relativo all'esecuzione del test case utente
	 * 
	 * @param i index of the test case
	 * @return current ActivityDescription (XML)
	 * @throws IOException
	 */
	public String getCurrentDescription_Utente(int i) throws IOException {
		// describe
		notifyRipperLog("Metodo Utente richiede describe...");
		String xml = runUserTestCase(10, i);

		if (xml != null) {
			notifyRipperLog(xml);
			// appendLineToLogFile( xml.substring(45, xml.length() - 8) );
		}

		return xml;
	}

	/**
	 * Unique ID of an Activity obtained from the execution of a test case
	 *
	 * NOTE: richiama metodo getCurrentDescription_Utente(i) e setta uid
	 */
	int activityUIDutente = 0;

	/**
	 * runUserTestCase() and return current ActivityDescription
	 * 
	 * @param i index of the test case
	 * @return current ActivityDescription
	 * @throws IOException
	 */
	public ActivityDescription getCurrentDescriptionAsActivityDescription_Utente(int i) throws IOException {
		this.lastActivityDescription = null;

		String xml = this.getCurrentDescription_Utente(i);

		if (xml != null) {
			this.lastActivityDescription = ripperInput.inputActivityDescription(xml);
			this.lastActivityDescription.setUid(Integer.toString(++activityUIDutente));
		}
		return this.lastActivityDescription;
	}

}
