package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

public class SpinnerInputPlanner extends WidgetInputPlanner {

	public SpinnerInputPlanner(WidgetDescription widget, ValuesGenerator valuesGenerator) {
		super(widget, valuesGenerator, InteractionType.SPINNER_SELECT);
	}
	
}
