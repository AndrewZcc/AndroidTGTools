package it.unina.android.ripper.model;


import java.io.Serializable;
import java.util.ArrayList;

/**
 * Event
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Event implements Serializable {

	private String interaction;
	private WidgetDescription widget;
	private String value;
	private ArrayList<Input> inputs;
	
	private String beforeExecutionStateUID = "UNDEFINED";
	private String afterExecutionStateUID = "UNDEFINED";
	
	public Event() {
		super();
	}
	
	public Event(String interaction, WidgetDescription widget, String value, ArrayList<Input> inputs) {
		super();
		this.interaction = interaction;
		this.widget = widget;
		this.value = value;
		this.inputs = inputs;
	}

	public String getInteraction() {
		return interaction;
	}

	public void setInteraction(String interaction) {
		this.interaction = interaction;
	}

	public WidgetDescription getWidget() {
		return widget;
	}

	public void setWidget(WidgetDescription widget) {
		this.widget = widget;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public ArrayList<Input> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<Input> inputs) {
		this.inputs = inputs;
	}

	public void addInput(WidgetDescription widget, String interactionType, String value)
	{
		if (inputs == null) {
			inputs = new ArrayList<Input>();
		}
		this.inputs.add(new Input(widget, interactionType, value));
	}
	
	public void clearInputs()
	{
		this.inputs.clear();
	}
	
	public String getBeforeExecutionStateUID() {
		return beforeExecutionStateUID;
	}

	public void setBeforeExecutionStateUID(String beforeExecutionStateUID) {
		this.beforeExecutionStateUID = beforeExecutionStateUID;
	}

	public String getAfterExecutionStateUID() {
		return afterExecutionStateUID;
	}

	public void setAfterExecutionStateUID(String afterExecutionStateUID) {
		this.afterExecutionStateUID = afterExecutionStateUID;
	}
	
	@Override
	public String toString()
	{
		return ((widget!=null)?widget.toString()+",":"") + interaction;
	}
	
	public String toXMLString()
	{
		String xml = new String("");
		xml += "<event " +					
					"interaction=\""+interaction+"\" " +
					"value=\""+value+"\" " +
				">\n";
		
		if (widget != null)
			xml += widget.toXMLString();
		else
			xml += "<widget id=\"null\" type=\"null\" />\n";
	
		if (this.inputs != null && this.inputs.size() > 0)
			for (Input in : this.inputs)
				xml += in.toXMLString();
		
		xml += "</event>\n";
		
		return xml;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o != null && o instanceof Event) {
			
			Event e = (Event)o;
			
			return (
					(
								(e.widget == null && this.widget == null)
							||	(e.widget != null && this.widget != null && e.widget.getId() == null && this.widget.getId() == null)
							||	(e.widget != null && this.widget != null && e.widget.getId() != null && this.widget.getId() != null && e.widget.getId().equals(this.widget.getId()))
					) 
					&&	e.interaction.equals(this.interaction)
					&&	((e.value == null && this.value == null) || (e.value != null && this.value != null && e.value.equals(this.value)))
					);
					
		}
		
		return false;
	}
}
