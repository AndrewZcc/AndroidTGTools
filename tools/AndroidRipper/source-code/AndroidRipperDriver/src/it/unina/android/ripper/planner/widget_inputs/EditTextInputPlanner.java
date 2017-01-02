package it.unina.android.ripper.planner.widget_inputs;

import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.model.WidgetDescription;
import it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator;

public class EditTextInputPlanner extends WidgetInputPlanner {

	public EditTextInputPlanner(WidgetDescription widget, ValuesGenerator valuesGenerator) {
		super(widget, valuesGenerator, InteractionType.WRITE_TEXT);
		// TODO Auto-generated constructor stub
	}
	
}
