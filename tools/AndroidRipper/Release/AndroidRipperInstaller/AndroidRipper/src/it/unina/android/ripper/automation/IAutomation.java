package it.unina.android.ripper.automation;

import it.unina.android.ripper.automation.robot.IRobot;
import android.app.Activity;

/**
 * Automation Component
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface IAutomation {

	/**
	 * Fire Event
	 * 
	 * @param widgetId
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 * @param value
	 */
	public abstract void fireEvent(String widgetId, Integer widgetIndex,
			String widgetName, String widgetType, String eventType, String value);

	/**
	 * Fire Event
	 * 
	 * @param widgetId
	 * @param widgetIndex
	 * @param widgetType
	 * @param eventType
	 */
	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetType, String eventType);

	/**
	 * Fire Event
	 * 
	 * @param widgetId
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 */
	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetName, String widgetType, String eventType);

	/**
	 * Fire Event
	 * 
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 */
	public abstract void fireEvent(int widgetIndex, String widgetName,
			String widgetType, String eventType);

	/**
	 * Fire Event
	 * 
	 * @param widgetId
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 * @param value
	 */
	public abstract void fireEvent(int widgetId, int widgetIndex,
			String widgetName, String widgetType, String eventType, String value);

	/**
	 * Fire Event
	 * 
	 * @param widgetIndex
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 * @param value
	 */
	public abstract void fireEvent(int widgetIndex, String widgetName,
			String widgetType, String eventType, String value);

	/**
	 * Fire Event
	 * 
	 * @param widgetName
	 * @param widgetType
	 * @param eventType
	 * @param value
	 */
	public abstract void fireEvent (String widgetName, String widgetType, String eventType, String value);
	
	/**
	 * Set Input Field
	 * 
	 * @param widgetId
	 * @param interactionType
	 * @param value
	 */
	public abstract void setInput(int widgetId, String interactionType, String value);

	/**
	 * Restart
	 */
	public abstract void restart();

	/**
	 * Wait for the Loading Dialog to disappear
	 */
	public abstract void waitOnThrobber();

	/**
	 * Change Orientation
	 * 
	 * @param orientation
	 */
	public abstract void setActivityOrientation(int orientation);

	/**
	 * Sleep
	 * 
	 * @param time
	 */
	public abstract void sleep(int time);

	/**
	 * Return Current Activity
	 * 
	 * @return
	 */
	public abstract Activity getCurrentActivity();

	/**
	 * Finalize Robot Instance
	 * 
	 * @throws Throwable
	 */
	public abstract void finalizeRobot() throws Throwable;

	/**
	 * Get Robot Instance
	 * 
	 * @return
	 */
	public IRobot getRobot();
}