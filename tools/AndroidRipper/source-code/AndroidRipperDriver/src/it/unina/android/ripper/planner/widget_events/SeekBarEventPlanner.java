package it.unina.android.ripper.planner.widget_events;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

import java.util.ArrayList;

/**
 * Planner for SEEK_BAR Widget
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class SeekBarEventPlanner extends WidgetEventPlanner {
	
	/**
	 * Values Generator
	 */
	ValuesGenerator mValuesGenerator;
	
	/**
	 * Constructor
	 * 
	 * @param widgetDescription Widget
	 * @param valuesGenerator Values Generator
	 */
	public SeekBarEventPlanner(WidgetDescription widgetDescription, ValuesGenerator valuesGenerator) {
		super(widgetDescription);
		this.mValuesGenerator = valuesGenerator; 
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_events.WidgetEventPlanner#tap(it.unina.android.ripper.model.Task, java.util.ArrayList, java.lang.String[])
	 */
	@Override
	protected TaskList tap(Task currentTask, ArrayList<Input> inputs, String... options)
	{
		TaskList t = new TaskList();
		t.add(new Task(currentTask, mWidget, InteractionType.SET_BAR, inputs, mValuesGenerator.generate()));
		return t;
	}

}
