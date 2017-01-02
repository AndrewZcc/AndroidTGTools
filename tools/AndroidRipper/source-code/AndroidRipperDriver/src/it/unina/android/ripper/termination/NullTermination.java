package it.unina.android.ripper.termination;

import it.unina.android.ripper.driver.AbstractDriver;

/**
 * Never Reach the Termination
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class NullTermination implements TerminationCriterion {

	/**
	 * Constructor
	 */
	public NullTermination() {
		super();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#init(it.unina.android.ripper.driver.AbstractDriver)
	 */
	@Override
	public void init(AbstractDriver dirver) {

	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.termination.TerminationCriterion#check()
	 */
	@Override
	public boolean check() {
		return false;
	}

}
