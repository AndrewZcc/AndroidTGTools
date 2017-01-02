package it.unina.android.ripper.planner.widget_inputs.values_generator;

/**
 * Generate Null Values
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class NullValuesGenerator implements ValuesGenerator
{
	/**
	 * Constructor
	 */
	public NullValuesGenerator()
	{
		super();
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator#generate()
	 */
	@Override
	public String generate() {
		return null;
	}

}
