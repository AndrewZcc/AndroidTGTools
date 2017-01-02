package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;

import java.util.ArrayList;

/**
 * Planner for LIST_VIEW Widget
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ListViewEventPlanner extends WidgetEventPlanner {

	/**
	 * Max list items to be considered
	 */
	int MAX_INTERACTIONS = 3;
	
	/**
	 * Constructor
	 * 
	 * @param widgetDescription Widget
	 * @param MAX_INTERACTIONS Max list items to be considered
	 */
	public ListViewEventPlanner(WidgetDescription widgetDescription, int MAX_INTERACTIONS) {
		super(widgetDescription);
		this.MAX_INTERACTIONS = MAX_INTERACTIONS;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#tap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options) {
		TaskList t = new TaskList();
		int count = mWidget.getCount() != null?mWidget.getCount():0;
		for (int i = 1; i <= Math.min(count, MAX_INTERACTIONS); i++)
			t.addNewTaskForWidget(currentTask, mWidget, InteractionType.LIST_SELECT, inputs, Integer.toString(i));
		return t;
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#longTap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList longTap(Task currentTask, ArrayList<Input> inputs, String... options) {
		TaskList t = new TaskList();
		int count = mWidget.getCount() != null?mWidget.getCount():0;
		for (int i = 1; i <= Math.min(count, MAX_INTERACTIONS); i++)
			t.addNewTaskForWidget(currentTask, mWidget, InteractionType.LIST_LONG_SELECT, inputs, Integer.toString(i));
		return t;
	}

}
