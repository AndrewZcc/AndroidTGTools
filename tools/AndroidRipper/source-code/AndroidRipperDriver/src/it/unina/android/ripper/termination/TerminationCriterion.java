package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

/**
 * Termination Criterion
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface TerminationCriterion {
	/**
	 * Initialize Termination Criterion
	 * 
	 * The AbstractDriver can be used to take into account the exploration status.
	 * 
	 * @param driver AbstractDriver instance
	 */
	public void init(AbstractDriver driver);
	
	/**
	 * Check the Termination Criterion
	 * 
	 * @return
	 */
	public boolean check();
}
