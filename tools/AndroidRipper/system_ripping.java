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