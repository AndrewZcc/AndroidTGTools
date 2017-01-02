package it.unina.android.ripper.planner;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;

/**
 * Planner of Tasks
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public abstract class Planner
{
	/**
	 * Max Tasks generated for Items contained in LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in PREFERENCE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_PREFERENCES_LIST = 9999;
	
	/**
	 * Max Tasks generated for Items contained in SINGLE_CHOICE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_SINGLE_CHOICE_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in MULTI_CHOICE_LIST_VIEW widget
	 */
	public static int MAX_INTERACTIONS_FOR_MULTI_CHOICE_LIST = 3;
	
	/**
	 * Max Tasks generated for Items contained in SPINNER widget
	 */
	public static int MAX_INTERACTIONS_FOR_SPINNER = 9;
	
	/**
	 * Max Tasks generated for Items contained in RADIO_GROUP widget
	 */
	public static int MAX_INTERACTIONS_FOR_RADIO_GROUP = 9;
	
	/**
	 * Enable BACK button press events
	 */	
	public static boolean CAN_GO_BACK = true;
	
	/**
	 * Enable CHANGE ORIENTATION event
	 */
	public static boolean CAN_CHANGE_ORIENTATION = true;
	
	/**
	 * Enable MENU button press event
	 */
	public static boolean CAN_OPEN_MENU = true;
	
	/**
	 * Enable SCROLL DOWN event
	 */
	public static boolean CAN_SCROLL_DOWN = false;
	
	/**
	 * Enable KEY PRESS events
	 */
	public static boolean CAN_GENERATE_KEY_PRESS_EVENTS = false;
	
	/**
	 * Enable LONG KEY PRESS events
	 */
	public static boolean CAN_GENERATE_LONG_KEY_PRESS_EVENTS = false;
	
	/**
	 * Enables SWAP event between TAB VIEWS
	 */
	public static boolean CAN_SWAP_TAB = true;	
	
	/**
	 * Enable HOME event
	 */	
	public static boolean CAN_GO_BACK_ON_HOME_ACTIVITY = true;
	
	/**
	 * Plan Tasks
	 * 
	 * @param currentTask starting task
	 * @param activity target ActivityDescription
	 * @return list of tasks
	 */
	public TaskList plan(Task currentTask, ActivityDescription activity) {
		return plan(currentTask ,activity, null);
	}
	
	/**
	 * Plan Tasks
	 * 
	 * @param currentTask starting task
	 * @param activity target ActivityDescription
	 * @param options configuration
	 * @return list of tasks
	 */
	public abstract TaskList plan(Task currentTask, ActivityDescription activity, String ... options);
}
