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

		// ciclo su controllo test_case finiti
		do {
			// loop ripper
			do {
				nRestart++;

				long startup_time_t1 = System.currentTimeMillis();
				boolean started = this.startup(beforeTestCasesProcess);
				startup_time += System.currentTimeMillis() - startup_time_t1;

				if (running && started) {
					if (beforeTestCasesProcess && !new File(XML_OUTPUT_PATH + STATES_LIST_FILE).exists()) {
						createLogFile(currentTestCaseIndex); // crea logFile
																// prima
																// esecuzione
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
							// invio messaggio di esecuzione test case i-esimo e
							// attendi risposta

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