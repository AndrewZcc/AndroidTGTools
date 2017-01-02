package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;

import java.util.ArrayList;

/**
 * Planner for TEXT_VIEW Widget
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public class TextViewEventPlanner extends WidgetEventPlanner {
	
	/**
	 * Constructor
	 *  
	 * @param widgetDescription widget
	 */
	public TextViewEventPlanner(WidgetDescription widgetDescription) {
		super(widgetDescription);
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#tap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.CLICK_ON_TEXT, inputs));
		return t;
	}

}
