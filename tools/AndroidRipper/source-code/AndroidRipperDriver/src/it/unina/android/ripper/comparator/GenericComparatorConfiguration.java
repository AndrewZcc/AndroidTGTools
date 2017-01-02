package it.unina.android.ripper.comparator;

import it.unina.android.ripper.constants.SimpleType;

/**
 * GenericComparator configuration class
 * 
 * @author Testing
 *
 */
public class GenericComparatorConfiguration
{
	/**
	 * This class contains simple comparator configurations
	 * 
	 * @author Testing
	 *
	 */
	public static class Factory
	{
		/**
		 * NameComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getNameComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = true;
			ret.compareActivityTitles = true;
			
			return ret;
		}
		
		/**
		 * NullComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getDefaultComparator()
		{
			return new GenericComparatorConfiguration();
		}
		
		/**
		 * CustomWidgetSimpleComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getCustomWidgetSimpleComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = false;
			ret.compareActivityTitles = true;
			
			ret.testIfFilteredWidgetsMatch = true;
			
			String[] filteredWidgetsArray = { 
					SimpleType.EDIT_TEXT,
					SimpleType.BUTTON,
					SimpleType.MENU_VIEW,
					SimpleType.DIALOG_VIEW,
					SimpleType.SINGLE_CHOICE_LIST,
					SimpleType.MULTI_CHOICE_LIST,
					SimpleType.WEB_VIEW,
					SimpleType.TAB_HOST,
					SimpleType.LIST_VIEW,
					SimpleType.IMAGE_VIEW,
					SimpleType.TEXT_VIEW,
					SimpleType.AUTOCOMPLETE_TEXTVIEW,
					SimpleType.SEARCH_BAR
			};
			ret.filteredWidgetsArray = filteredWidgetsArray;
			
			ret.compareWidgetIds = true;
			ret.compareWidgetSimpleType = true;
			
			ret.compareMenuItemCount = true;

			ret.compareListItemCount = true;
			ret.maxListElementsCount = 3;
			
			return ret;
		}
		
		/**
		 * CustomWidgetIntensiveComparator
		 * 
		 * @return
		 */
		public static GenericComparatorConfiguration getCustomWidgetIntensiveComparator()
		{
			GenericComparatorConfiguration ret = new GenericComparatorConfiguration();
			
			ret.compareActivityNames = false;
			ret.compareActivityTitles = true;
			
			ret.testIfFilteredWidgetsMatch = true;
			
			String[] filteredWidgetsArray = {
					SimpleType.EDIT_TEXT,
					SimpleType.BUTTON,
					SimpleType.MENU_VIEW,
					SimpleType.DIALOG_VIEW,
					SimpleType.SINGLE_CHOICE_LIST,
					SimpleType.MULTI_CHOICE_LIST,
					SimpleType.WEB_VIEW,
					SimpleType.TAB_HOST,
					SimpleType.LIST_VIEW,
					SimpleType.IMAGE_VIEW,
					SimpleType.TEXT_VIEW,
					SimpleType.AUTOCOMPLETE_TEXTVIEW,
					SimpleType.SEARCH_BAR
			};
			ret.filteredWidgetsArray = filteredWidgetsArray;
			
			ret.compareWidgetIds = true;
			ret.compareWidgetSimpleType = true;
			
			ret.compareMenuItemCount = true;
			
			ret.compareListItemCount = true;
			ret.maxListElementsCount = 3;
			
			return ret;
		}
	}
	
	/**
	 * Constructor
	 */
	public GenericComparatorConfiguration() { super(); }

	
	
	/* ACTIVITY OPTIONS */
	
	/**
	 * Enable Compare ActivityDescription instances name parameter
	 */
	public boolean compareActivityNames = false;
	
	/**
	 * Enable Compare ActivityDescription instances title parameter 
	 */
	public boolean compareActivityTitles = false;
	
	/**
	 * Enable Compare ActivityDescription instances dialog title parameter 
	 */
	public boolean compareDialogTitle = false;
	
	/**
	 * Enable Compare ActivityDescription instances class parameter 
	 */
	public boolean compareActivityClasses = false;
	
	/**
	 * Enable Compare ActivityDescription instances widget array parameter count 
	 */
	public boolean compareActivityWidgetsCount = false;
	
	/**
	 * Enable Compare ActivityDescription instances widget array content 
	 */
	public boolean testIfWidgetsMatch = false;
	
	/**
	 * Widget Filter
	 */
	public String[] filteredWidgetsArray = {
			/*
			SimpleType.EDIT_TEXT,
			SimpleType.BUTTON,
			SimpleType.MENU_VIEW,
			SimpleType.DIALOG_VIEW,
			SimpleType.SINGLE_CHOICE_LIST,
			SimpleType.MULTI_CHOICE_LIST,
			SimpleType.WEB_VIEW,
			SimpleType.TAB_HOST,
			SimpleType.IMAGE_VIEW 
			*/
	};
	
	/**
	 * Enable Compare ActivityDescription instances filtered widget array parameter count 
	 */
	public boolean compareActivityFilteredWidgetsCount = false;
	
	/**
	 * Enable Compare ActivityDescription instances filtered widget array content 
	 */
	public boolean testIfFilteredWidgetsMatch = false;
	
	
	
	/* WIDGET OPTIONS */
	
	/**
	 * Enable Compare WidgetDescription instances id parameter
	 */
	public boolean compareWidgetIds = false;
	
	/**
	 * Enable Compare WidgetDescription instances SimpleType parameter
	 */
	public boolean compareWidgetSimpleType = false;
	
	/**
	 * Enable Compare WidgetDescription instances visible parameter
	 */
	public boolean testWidgetVisibilityChange = false;
	
	/**
	 * Enable Compare WidgetDescription instances enable parameter
	 */
	public boolean testWidgetEnablingChange = false;
	
	/**
	 * Enable Compare WidgetDescription instances name parameter
	 */
	public boolean compareWidgetNames = false;
	
	/**
	 * Enable Compare WidgetDescription instances class parameter
	 */
	public boolean compareWidgetClasses = false;
	
	
	/* OPTIONS FOR SPECIFIC WIDGETS */

	/* LIST WIDGET OPTIONS */

	/**
	 * Enable Compare count of items for LIST_VIEW WidgetDescription instances
	 */
	public boolean compareListItemCount = false;
	
	/**
	 * Limit Compare count of items for LIST_VIEW WidgetDescription instances to a maximum value.
	 * After that value, the list are considered having the same number of elements.
	 */
	public int maxListElementsCount = 5;

	public boolean testIfBothListHaveAtLeastOneElement = false;
	public boolean testIfBothListHaveMinusThanAFixedNumberOfElements = false;
	public int fixedNumberOfListElements = 5;
	
	
	/* MENU WIDGET OPTIONS */
	
	/**
	 * Enable Compare count of items for MENU_VIEW WidgetDescription instances
	 */
	public boolean compareMenuItemCount = false;
}
