package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.NullValuesGenerator;

public class ClickableWidgetInputPlanner extends WidgetInputPlanner {

	public ClickableWidgetInputPlanner(WidgetDescription widget) {
		super(widget, new NullValuesGenerator(), InteractionType.CLICK);
	}
	
}
