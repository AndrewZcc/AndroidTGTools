package it.unina.android.ripper.extractor;

import it.unina.android.ripper.model.ActivityDescription;

/**
 * Extract the ActivityDescription of a GUI Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface IExtractor
{
	/**
	 * Extract the ActivityDescription of a GUI Interface
	 * 
	 * @return
	 */
	public ActivityDescription extract();
}
