package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;
import it.unina.android.ripper.model.TaskList;

/**
 * The Process is ended when no more events are in the Task List
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class EmptyActivityStateListTerminationCriterion implements TerminationCriterion {

	/**
	 * Task List instance
	 */
	TaskList mTaskList;

	/*
	 * Constructor
	 */
	public EmptyActivityStateListTerminationCriterion() {
		super();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#init(it.unina.android.ripper.driver.AbstractDriver)
	 */
	@Override
	public void init(AbstractDriver driver) {	
		this.mTaskList = driver.getScheduler().getTaskList();
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#check()
	 */
	@Override
	public boolean check() {
		return mTaskList.isEmpty();
	}

}
