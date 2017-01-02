package it.unina.android.ripper.planner.widget_inputs.values_generator;

/**
 * Generate a Constant Value
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ConstantValuesGenerator implements ValuesGenerator
{
	/**
	 * Constant Value
	 */
	String value = "42";
	
	/**
	 * Constructor
	 */
	public ConstantValuesGenerator()
	{
		super();
	}
	
	/**
	 * Constructor
	 * 
	 * @param value Constant Value
	 */
	public ConstantValuesGenerator(String value) {
		super();
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator#generate()
	 */
	@Override
	public String generate() {
		return this.value;
	}

}
