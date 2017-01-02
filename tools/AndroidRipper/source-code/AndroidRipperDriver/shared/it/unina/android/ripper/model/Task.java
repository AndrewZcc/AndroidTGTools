package it.unina.android.ripper.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Task
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Task extends ArrayList<Event> implements Serializable {

	private static final long serialVersionUID = 987654321L;

	public Task()
	{
		super();
	}
	
	public Task(Task baseTask, WidgetDescription widget, String interaction, ArrayList<Input> inputs)
	{
		super();
		
		if (baseTask != null && baseTask.size() > 0)
			this.addAll(baseTask);
		
		this.add(new Event(interaction, widget, null, inputs));
	}
	
	public Task(Task baseTask, WidgetDescription widget, String interaction, ArrayList<Input> inputs, String value)
	{
		super();
		
		if (baseTask != null && baseTask.size() > 0)
			this.addAll(baseTask);
		
		this.add(new Event(interaction, widget, value, inputs));
	}
	
	public void addNewEventForWidget(WidgetDescription widget, String interaction, ArrayList<Input> inputs)
	{
		this.add(new Event(interaction, widget, null, inputs));
	}
	
	public void addNewEventForWidget(WidgetDescription widget, String interaction, ArrayList<Input> inputs, String value)
	{
		this.add(new Event(interaction, widget, value, inputs));
	}

	public void addNewEventForActivity(String interaction)
	{
		this.add(new Event(interaction, null, null, null));
	}
	
	public void addNewEventForActivity(String interaction, String value)
	{
		this.add(new Event(interaction, null, value, null));
	}
}
