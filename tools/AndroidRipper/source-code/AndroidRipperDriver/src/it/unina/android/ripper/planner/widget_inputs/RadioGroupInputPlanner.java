package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

public class RadioGroupInputPlanner extends WidgetInputPlanner {

	public RadioGroupInputPlanner(WidgetDescription widget, ValuesGenerator valuesGenerator) {
		super(widget, valuesGenerator, InteractionType.RADIO_SELECT);
	}
	
}
