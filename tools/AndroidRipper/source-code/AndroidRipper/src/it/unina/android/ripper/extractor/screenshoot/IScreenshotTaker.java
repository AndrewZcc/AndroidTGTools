package it.unina.android.ripper.extractor.screenshoot;

import android.app.Activity;

/**
 * Screenshot Taker Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface IScreenshotTaker
{
	/**
	 * Save a Screenshot
	 * 
	 * @param activity Current Activity
	 */
	public void takeScreenshot(Activity activity);
}
