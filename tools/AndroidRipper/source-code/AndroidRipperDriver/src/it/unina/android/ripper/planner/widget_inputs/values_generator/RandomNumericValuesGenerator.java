package it.unina.android.ripper.planner.widget_inputs.values_generator;

/**
 * Generate Random Integer Values.
 * The interval is limited.
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class RandomNumericValuesGenerator implements ValuesGenerator
{

	/**
	 * Interval Lower Bound 
	 */
	int Min = 0;
	
	/**
	 * Interval Upper Bound
	 */
	int Max = 0;
	
	/**
	 * Constructor
	 * 
	 * @param max Interval Upper Bound
	 */
	public RandomNumericValuesGenerator(int max) {
		this.Max = max;
	}
	
	/**
	 * 
	 * @param min Interval Lower Bound
	 * @param max Interval Upper Bound
	 */
	public RandomNumericValuesGenerator(int min, int max)
	{
		this.Min = min;
		this.Max = max;
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.planner.widget_inputs.values_generator.ValuesGenerator#generate()
	 */
	@Override
	public String generate() {
		int randomInt = Min + (int)(Math.random() * ((Max - Min) + 1));
		return Integer.toString(randomInt);
	}

}
