package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

public class WidgetInputPlanner {

	public static final int RANDOM_VALUE = 0;
	
	protected WidgetDescription mWidget;
	protected ValuesGenerator mValuesGenerator;
	protected String mInteractionType;
	
	public WidgetInputPlanner(WidgetDescription widget, ValuesGenerator valuesGenerator, String interactionType)
	{
		super();
		this.mWidget = widget;
		this.mValuesGenerator = valuesGenerator;
		this.mInteractionType = interactionType;
	}
	
	public boolean canPlanForWidget()
	{
		return mWidget != null && mWidget.isEnabled(); //&& mWidget.getSimpleType() != null && mWidget.getSimpleType().equals("") == false;
	}
	
	public Input getInputForWidget()
	{	
		if (canPlanForWidget())
			return new Input(mWidget, mInteractionType, mValuesGenerator.generate());
		else
			return null;
	}
}
