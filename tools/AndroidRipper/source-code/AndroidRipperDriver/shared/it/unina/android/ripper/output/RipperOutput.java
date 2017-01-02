package it.unina.android.ripper.output;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;

/**
 * Specifies how the element of the model are serialized 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface RipperOutput {
	/**
	 * Output ActivityDescription as a String
	 * @param a
	 * @return
	 */
	public String outputActivityDescription(ActivityDescription a);
	
	/**
	 * Output ActivityDescription and TaskList as a String
	 * 
	 * @param a
	 * @param t
	 * @return
	 */
	public String outputActivityDescriptionAndPlannedTasks(ActivityDescription a, TaskList t);
	
	/**
	 * Output WidgetDescription as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputWidgetDescription(WidgetDescription a);
	
	/**
	 * Output Event as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputEvent(Event a);
	
	/**
	 * Output Fired Event as a String
	 * 
	 * @param evt
	 * @return
	 */
	public String outputFiredEvent(Event evt);
	
	/**
	 * Output Task as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputTask(Task a);
	
	/**
	 * Output TaskList of events extracted from an ActivityDescription as a String
	 * 
	 * @param a
	 * @return
	 */
	public String outputExtractedEvents(TaskList a);
	
	/**
	 * Output TaskList of events extracted from an ActivityDescription and the ActivityDescription as a String
	 * 
	 * @param t
	 * @param from
	 * @return
	 */
	public String outputExtractedEvents(TaskList t, ActivityDescription from);
	
	/**
	 * Output an Event and the ActivityDescription where it is performed as a String
	 * 
	 * @param e
	 * @param a
	 * @return
	 */
	public String outputStep(Event e, ActivityDescription a);
	
	/**
	 * Output an Event and the ActivityDescription where it is performed as a String
	 * together with the fireable events planned on the resulting ActivityDescription
	 * (if any)
	 * 
	 * @param e
	 * @param a
	 * @param t
	 * @return
	 */
	public String outputStepAndPlannedTasks(Event e, ActivityDescription a, TaskList t);
	
	/**
	 * Output the result of the bootstrap step: ActivityDescription and related TaskList
	 * 
	 * @param ad
	 * @param t
	 * @return
	 */
	public String outputFirstStep(ActivityDescription ad, TaskList t);
}
