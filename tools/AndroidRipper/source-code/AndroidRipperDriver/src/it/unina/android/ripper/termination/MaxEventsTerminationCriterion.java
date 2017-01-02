package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

/**
 * The Process is ended after a predefined number of fired events
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class MaxEventsTerminationCriterion implements TerminationCriterion {

	/**
	 * AbstractDriver instance
	 */
	AbstractDriver driver;
	
	/**
	 * Maximum Number of Events to be fired
	 */
	int MAX_EVENTS = 0;
	
	/**
	 * Constructor
	 * 
	 * @param MAX_EVENTS Maximum Number of Events to be fired
	 */
	public MaxEventsTerminationCriterion(int MAX_EVENTS) {
		super();
		this.MAX_EVENTS = MAX_EVENTS;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#init(it.unina.android.ripper.driver.AbstractDriver)
	 */
	@Override
	public void init(AbstractDriver driver) {

		this.driver = driver;

	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#check()
	 */
	@Override
	public boolean check() {
		return (this.driver.nEvents >= MAX_EVENTS);
	}

}
