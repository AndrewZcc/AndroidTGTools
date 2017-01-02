package it.unina.android.ripper.comparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unina.android.ripper.constants.SimpleType;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

/**
 * Generic Comparator.
 * 
 * Contains a set of methods to compare two ActivityDescription Instances. 
 * 
 * @author Nicola Amatucci - REvERSE
 * 
 */
public class GenericComparator implements IComparator, Serializable {

	/* DEBUG FUNCTIONS ;-) */
	public static boolean DEBUG = true;
	public static final String TAG = "GenericComparator";
	public static void debug(String s) { if (DEBUG)	System.out.println("["+TAG+"]"+s); }
	public static void debug(boolean condition, String s) { if (DEBUG && condition)	System.out.println("["+TAG+"]"+s); }
	/* END OF DEBUG FUNCTIONS ;-) */
	
	/**
	 * Comparator Configuration
	 */
	GenericComparatorConfiguration config = null;
	
	/**
	 * Constructor
	 */
	public GenericComparator()
	{
		super();
		this.config = GenericComparatorConfiguration.Factory.getDefaultComparator();
	}

	/**
	 * Constructor
	 * 
	 * @param config Configuration
	 */
	public GenericComparator(GenericComparatorConfiguration config)
	{
		super();
		this.config = config;
	}
	
	/* (non-Javadoc)
	 * @see it.unina.android.ripper.comparator.IComparator#compare(it.unina.android.ripper.model.ActivityDescription, it.unina.android.ripper.model.ActivityDescription)
	 */
	@Override
	public Object compare(ActivityDescription activity1, ActivityDescription activity2)
	{
		//TODO: parameters validation
		
		//Filter widget (if needed)
		ArrayList<WidgetDescription> filteredWidgets1 = new ArrayList<WidgetDescription>();
		ArrayList<WidgetDescription> filteredWidgets2 = new ArrayList<WidgetDescription>();
		if (
					config.testIfFilteredWidgetsMatch
				||	config.compareActivityFilteredWidgetsCount
		)
		{
			List<String> wList = Arrays.asList(config.filteredWidgetsArray);
			filteredWidgets1 = this.filterWidgets(activity1, wList);
			filteredWidgets2 = this.filterWidgets(activity2, wList);
			debug("Widgets size1="+ activity1.getWidgets().size() + " - size2=" + activity2.getWidgets().size());
			debug("FilteredWidgets size1="+ filteredWidgets1.size() + " - size2=" + filteredWidgets2.size());
		}
		
		
		
		//Compare the name parameter of the ActivityDescription instances		
		if (config.compareActivityNames)
		{			 
			 if (		(activity1.getName() != null && activity2.getName() == null) 	
					 ||	(activity1.getName() == null && activity2.getName() != null)
					 ||	(activity1.getName() != null && activity2.getName() != null && activity1.getName().equals(activity2.getName()) == false))
			 {			
				debug("compare activity names -> false");
				return false;
			 } else {			 
				 debug("compare activity names -> true");
			 }
		}
		
			
		
		
		//Compare the title parameter of the ActivityDescription instances	
		if (config.compareActivityTitles)
		{
			 if (		(activity1.getTitle() != null && activity2.getTitle() == null) 	
					 ||	(activity1.getTitle() == null && activity2.getTitle() != null)
					 ||	(activity1.getTitle() != null && activity2.getTitle() != null && activity1.getTitle().equals(activity2.getTitle()) == false))
			 {			
				debug("compare activity titles -> false");
				return false;
			 } else {
			 	 debug("compare activity titles -> true");
			 }
		}
		
		
		
		
		//Compare the class parameter of the ActivityDescription instances	
		if (config.compareActivityClasses)
		{
			String className1 = null;
			String className2 = null;
			
			if (activity1.getActivityClass() != null)
				className1 = activity1.getActivityClass().getCanonicalName();
			else if (activity1.getClassName() != null)
				className1 = activity1.getClassName();
			else
				className1 = null;

			debug("className1 = " + ((className1 == null)?"null":className1) );
			
			if (activity2.getActivityClass() != null)
				className2 = activity2.getActivityClass().getCanonicalName();
			else if (activity2.getClassName() != null)
				className2 = activity2.getClassName();
			else
				className2 = null;
			
			debug("className2 = " + ((className2 == null)?"null":className2) );
			
			if (
						(className1 != null && className2 == null)
					||	(className1 == null && className2 != null)
					||	(className1 != null && className2 != null && className1.equals(className2) == false)
			)
			{
				debug("compare activity classes -> false");
				return false;
			} else {
				debug("compare activity classes -> true");
			}
		}
		
		
		
		//Compare the Widgets array size of the ActivityDescription instances	
		if (config.compareActivityWidgetsCount)
		{
			if (activity1.getWidgets().size() != activity2.getWidgets().size()) {
				debug("compare activity FILTERED widget count -> false "+ activity1.getWidgets().size() + " - " + activity2.getWidgets().size());
				return false;
			} else {
				debug("compare activity FILTERED widget count -> true "+ activity1.getWidgets().size() + " - " + activity2.getWidgets().size());
			}			
		}
		
		
		
		
		//Compare the filtered Widgets array size of the ActivityDescription instances	
		if (config.compareActivityFilteredWidgetsCount)
		{
			if (filteredWidgets1.size() != filteredWidgets2.size()) {
				debug("compare activity FILTERED widget count -> false "+ filteredWidgets1.size() + " - " + filteredWidgets2.size());
				return false;
			} else {
				debug("compare activity FILTERED widget count -> true "+ filteredWidgets1.size() + " - " + filteredWidgets2.size());
			}
		}
				
		
		
		//Test if the Widgets array elements match in the ActivityDescription instances
		if (config.testIfWidgetsMatch)
		{
			ArrayList<WidgetDescription> widgets1 = activity1.getWidgets();
			ArrayList<WidgetDescription> widgets2 = activity2.getWidgets();
			
			if (testIfWidgetsListMatch(widgets1, widgets2) == false)
			{
				debug("test if widgets match -> false");
				return false;
			} else {
				debug("test if widgets match -> true");				
			}
		}

		
		
		
		//Test if the filtered Widgets array elements match in the ActivityDescription instances
		if (config.testIfFilteredWidgetsMatch)
		{
			debug("testIfFilteredWidgetsMatch size1="+ filteredWidgets1.size() + " - size2=" + filteredWidgets2.size());
			
			if (testIfWidgetsListMatch(filteredWidgets1, filteredWidgets2) == false)
			{
				debug("test if FILTERED widgets match -> false");
				return false;
			} else {
				debug("test if FILTERED widgets match -> true");				
			}
		}

		
		return true;
	}

	/**
	 * Filter the Widget array
	 * 
	 * @param activity Activity
	 * @param filteredWidgets List of Widget to Filter
	 * @return
	 */
	private ArrayList<WidgetDescription> filterWidgets(ActivityDescription activity, List<String> filteredWidgets)
	{	
		ArrayList<WidgetDescription> ret = new ArrayList<WidgetDescription>();
		
		for (WidgetDescription wd : activity.getWidgets())
			if (filteredWidgets.contains(wd.getSimpleType()))
				ret.add(wd);
			
		return ret;
	}
	
	/**
	 * Compares two WidgetDescription instances
	 * 
	 * @param w1 Widget
	 * @param w2 Widget
	 * @return
	 */
	protected boolean matchWidget(WidgetDescription w1, WidgetDescription w2)
	{
		//Compare the id parameter of the WidgetDescription instances
		if (config.compareWidgetIds)
		{
			if (w1.getId().equals(w2.getId()) == false) {
				debug("compare widget id ("+w1.getId()+","+w2.getId()+") -> false");
				return false;
			} else {
				debug("compare widget id ("+w1.getId()+","+w2.getId()+") -> true");		
			}
		}
		
		
		//Compare the SimpleType parameter of the WidgetDescription instances
		if (config.compareWidgetSimpleType)
		{
			if (w1.getSimpleType().equals(w2.getSimpleType()) == false) {
				debug("compare widget simple type("+w1.getSimpleType()+","+w2.getSimpleType()+") -> false");
				return false;
			} else {
				debug("compare widget simple type("+w1.getSimpleType()+","+w2.getSimpleType()+") -> true");	
			}				
		}
		
		
		//Compare the visibile parameter of the WidgetDescription instances
		if (config.testWidgetVisibilityChange)
		{
			if (w1.isVisible().equals(w2.isVisible()) == false) {
				debug("test widget visibility change -> false");
				return false;
			} else {
				debug("test widget visibility change -> true");	
			}
		}
		

		//Compare the enabled parameter of the WidgetDescription instances
		if (config.testWidgetEnablingChange)
		{
			if (w1.isVisible().equals(w2.isVisible()) == false) {
				debug("test widget enabling change -> false");
				return false;
			} else {
				debug("test widget enabling change -> true");		
			}
		}
		

		//Compare the item count of two LIST_VIEW WidgetDescription instances		
		if (w1.getSimpleType().equals(SimpleType.LIST_VIEW) && w2.getSimpleType().equals(SimpleType.LIST_VIEW))
		{		
			if (config.compareListItemCount)
			{
				if (w1.getCount().equals(w2.getCount()) == false) {
					if (w1.getCount() >= config.maxListElementsCount && w2.getCount() >= config.maxListElementsCount)
					{
						debug(config.compareListItemCount, "compare list item count (maxListElementsConut) -> true");	
					}
					else
					{
						debug("compare list item count -> false");
						return false;
					}
				} else {
					debug("compare list item count -> true");		
				}
			}	
			
			if (config.testIfBothListHaveAtLeastOneElement)
			{
				if (w1.getCount() >= 1 && w2.getCount() >= 1) {
					debug("testIfBothListHaveAtLeastOneElement -> false");
					return false;
				} else {
					debug("testIfBothListHaveAtLeastOneElement -> true");		
				}
			}
			
			if (config.testIfBothListHaveMinusThanAFixedNumberOfElements)
			{
				if (w1.getCount() <= config.fixedNumberOfListElements && w2.getCount() <= config.fixedNumberOfListElements) {
					debug("testIfBothListHaveMinusThanAFixedNumberOfElements -> false");
					return false;
				} else {
					debug("testIfBothListHaveMinusThanAFixedNumberOfElements -> true");		
				}
			}
			
		}

		//Compare the item count of two MENU_VIEW WidgetDescription instances			
		if (w1.getSimpleType().equals(SimpleType.MENU_VIEW) && w2.getSimpleType().equals(SimpleType.MENU_VIEW))
		{
			if (config.compareMenuItemCount)
			{
				if (w1.getCount().equals(w2.getCount()) == false) {
					debug("compare menu item count -> false");
					return false;
				} else {
					debug(config.compareMenuItemCount, "compare menu item count -> true");	
				}
			}
		}
		
		//Compare the title of two DIALOG_VIEW WidgetDescription instances		
		if (config.compareDialogTitle && w1.getSimpleType().equals(SimpleType.DIALOG_VIEW) && w2.getSimpleType().equals(SimpleType.DIALOG_VIEW))
		{
			if (w1.getName().equals(w2.getName()) == false) {
				debug("compare dialog names -> false");
				return false;
			} else {
				debug("compare dialog names -> true");
			}
		}

		return true;
	}
	
	/**
	 * Test if the array of WidgetDescription instances of two ActivityDescription instances match
	 * 
	 * @param widgets1 Array of WidgetDescription of the first ActivityDescription instance
	 * @param widgets2 Array of WidgetDescription of the second ActivityDescription instance
	 * @return
	 */
	protected boolean testIfWidgetsListMatch(ArrayList<WidgetDescription> widgets1, ArrayList<WidgetDescription> widgets2)
	{
		/**
		 * Implement a contains() method to verify if the widget is already in the list
		 */
		ArrayList<WidgetDescription> checkedAlready = new ArrayList<WidgetDescription>()
		{
			@Override
			public boolean contains(Object o)
			{
				if (o == null && WidgetDescription.class.isInstance(o) == false)
					return false;
				
				return lookFor((WidgetDescription)o, this);
			}
			
		};
		
		//First Pass of comparison
		for (WidgetDescription w1 : widgets1)
		{
			//if (checkedAlready.contains(w1) == false) {
				if(lookFor(w1, widgets2) == false)
				{
					debug("lookFor(w1, widgets2) no " + w1.getId());
					return false;
				} else {
					debug("lookFor(w1, widgets2) yes " + w1.getId());
					checkedAlready.add(w1);
				}
				
			//}
		}
		
		//Second Pass of comparison
		for (WidgetDescription w2 : widgets2)
		{
			if (checkedAlready.contains(w2) == false) {
				if(lookFor(w2, widgets1) == false)
				{
					debug("lookFor(w2, widgets1) no " + w2.getId());
					return false;
				} else {
					debug("lookFor(w2, widgets1) yes " + w2.getId());
				}
					
			}
		}
	
		return true;
	}
	
	/**
	 * Serach for a WidgetDescription in a list of WidgetDescription
	 * @param w1 WidgetDescription to search for
	 * @param widgets Array of WidgetDescription to search in
	 * @return
	 */
	private boolean lookFor(WidgetDescription w1, ArrayList<WidgetDescription> widgets)
	{
		for (WidgetDescription w2 : widgets)
			if (matchWidget(w1, w2))
				return true;
		
		return false;
	}
}
