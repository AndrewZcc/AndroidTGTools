package it.unina.android.ripper.model;

import java.io.Serializable;

/**
 * Input
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class Input implements Serializable {
	private WidgetDescription widget;
	private String value;
	private String inputType;
	
	public Input()
	{
		super();
	}

	public Input(WidgetDescription widget, String inputType, String value) {
		super();
		this.widget = widget;
		this.value = value;
		this.inputType = inputType;
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

	public String getInputType() {
		return inputType;
	}

	public void setInputType(String inputType) {
		this.inputType = inputType;
	}
	
	@Override
	public String toString()
	{
		return ((widget!=null)?widget.toString():"[widget=null]") + "[inputType="+inputType+"][value="+value+"]";
	}
	
	public String toXMLString()
	{
		String xml = new String("");
		xml += "<input " +					
					"inputType=\""+inputType+"\" " +
					"value=\""+value+"\" " +
				">\n";
		
		if (widget != null)
			xml += widget.toXMLString();
		else
			xml += "<widget id=\"null\" type=\"null\" />\n";
		
		xml += "</input>\n";
		return xml;
	}
}
