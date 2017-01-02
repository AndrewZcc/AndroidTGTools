package it.unina.android.ripper.comparator;

import it.unina.android.ripper.model.ActivityDescription;

public interface IComparator {
	
	/**
	 * Compare two ActivivityDescription instances
	 * 
	 * @param activity1
	 * @param activity2
	 * @return
	 */
	public Object compare(ActivityDescription activity1, ActivityDescription activity2);
}
