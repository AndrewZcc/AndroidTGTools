package it.unina.android.ripper.extractor;

import it.unina.android.ripper.automation.robot.IRobot;
import it.unina.android.ripper.configuration.Configuration;
import it.unina.android.ripper.constants.InteractionType;
import it.unina.android.ripper.constants.RipperSimpleType;
import it.unina.android.ripper.extractor.helper.ReflectionHelper;
import it.unina.android.ripper.log.Debug;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.WidgetDescription;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AbsSpinner;
import android.widget.AdapterView;
import android.widget.Checkable;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * Uses the JAVA Reflection API to extract information about the current GUI Interface
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ReflectionExtractor implements IExtractor
{
	/**
	 * Root View
	 */
	Class<?> viewRootClass = null;
	
	/**
	 * Robot Instance
	 */
	IRobot robot = null;
	
	/**
	 * Constructor
	 * 
	 * @param robot Robot Instance
	 */
	public ReflectionExtractor(IRobot robot)
	{
		this.robot = robot;
		
		try
		{
			viewRootClass = Class.forName("android.view.ViewRoot");
		}
		catch(Throwable t) {
			t.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see it.unina.android.ripper.extractor.IExtractor#extract()
	 */
	@Override
	public ActivityDescription extract()
	{
		
		
		ActivityDescription ret = new ActivityDescription();
	
		Activity activity = robot.getCurrentActivity();
		
		//activity info
		ret.setTitle(activity.getTitle().toString());
		ret.setActivityClass(activity.getClass());
		ret.setName(activity.getClass().getSimpleName());
		ret.setHasMenu(this.activityHasMenu(activity));
		ret.setHandlesKeyPress(this.handlesKeyPress(activity));
		ret.setHandlesLongKeyPress(this.handlesLongKeyPress(activity));
		
		boolean isTabActivity = this.isTabActivity(activity);
		ret.setIsTabActivity(isTabActivity);
		if (isTabActivity)
			ret.setTabsCount(this.getTabActivityTabsCount(activity));
			
		
		ret.setListeners(this.getActivityListeners(activity));
		
		try
		{
			robot.home();
			
			//widgets
			ArrayList<View> viewList = robot.getViews();
			
			if (viewList != null)
			{
				int index = 0;
				for (View v: viewList) {
					WidgetDescription wd = new WidgetDescription();
					
		            Debug.info(this, "Found widget: id=" + v.getId() + " ("+ v.toString() + ")");                        
		            
		            wd.setId(v.getId());
		            wd.setType(v.getClass());
		            wd.setName(this.detectName(v));
		            
		            this.setViewListeners(ret, wd, v);
		            
		            this.setValue(v, wd);
		            
		            wd.setEnabled(v.isEnabled());
		            
		            wd.setVisible(v.getVisibility() == 0);
		            
		            //wd.setTextualId(this.reflectTextualIDbyNumericalID(v.getId()));
		            if (v.getId() > 0 && v.getResources() != null)
		            	wd.setTextualId(v.getResources().getResourceEntryName(v.getId()));
		            
		            wd.setIndex(index++);
		            
					if (v instanceof TextView) {
						wd.setTextType(((TextView)v).getInputType());
					}
		            
		            if (v instanceof TabHost) {                        
		            	//Log.d(TAG, "Found tabhost: id=" + w.getId());
		            }		            
		            
	            	setCount(v, wd);
		            
		           //ripper like
		           wd.setSimpleType(RipperSimpleType.getSimpleType(v));
		           
		           ViewParent parentView = null;
		           
		           try
		           {
		        	   parentView = v.getParent();
		           }
		           catch(Throwable t) {
		        	   t.printStackTrace();
		           }

		           if (parentView != null && View.class.isInstance(parentView))
		           {
		        	   View parent = (View)parentView;
		        	   
		        	   wd.setParentId(parent.getId());
			           wd.setParentType(parent.getClass().getCanonicalName());
			           wd.setParentName(this.detectName(parent));
			           
			           if (parent.getId() < 0)
			           {
			        	   try
			        	   {
				        	   View ancestor = detectFirstAncestorWithId(parent);
				        	   
				        	   if (ancestor != null)
				        	   {
				        		   wd.setAncestorId(ancestor.getId());
				        		   wd.setAncestorType(ancestor.getClass().getCanonicalName());
				        	   }
				        	   else
				        	   {
				        		   wd.setAncestorType("root");
				        	   }
			        	   }
			        	   catch(Throwable ex)
			        	   {
			        		   ex.printStackTrace();
			        		   wd.setAncestorType("null");
			        	   }
			        		   
			           }
		           }
		           else if (parentView != null && viewRootClass != null && viewRootClass.isInstance(parentView))
		           {
		        	   wd.setParentType("root");
		           }
		           else
		           {
		        	   wd.setParentType("null");
		           }
		           
		           try
		           {
		        	   View parent = detectFirstAncestorWithId(v);
		           }
		           catch(Throwable ex) {
		        	   ex.printStackTrace();
		           }
		           
		           
		           ret.addWidget(wd);
				}
			}
			
		}
		catch (java.lang.Throwable t) {
			t.printStackTrace();
		}
			
		return ret;
	}
	
	/**
	 * Detect the first Ancestor that owns a valid id value
	 * 
	 * @param v Widget
	 * @return
	 * @throws Exception
	 */
	private View detectFirstAncestorWithId(View v) throws Exception
	{
		if ( (v == null) || (v != null && v.getParent() == null) )
			return null;
		
		ViewParent parentView = v.getParent();
		
		if (parentView != null && View.class.isInstance(parentView))
        {
     	   View parent = (View)parentView;
     	   
     	  if (parent != null && parent.getId() > 0)
     	  {
     		  return parent;
     	  }
     	  else
     	  {
     		 return detectFirstAncestorWithId(parent);
     	  }
        }
		else if (parentView != null && viewRootClass != null && viewRootClass.isInstance(parentView))
		{
			return null; //root
		}
		else
		{
			throw new Exception("null found");
		}
	}
		
	/**
	 * Set Listeners for the Widget
	 * 
	 * @param ad ActivityDescription instance
	 * @param wd WidgetDescription instance
	 * @param v Widget
	 */
	private void setViewListeners(ActivityDescription ad, WidgetDescription wd, View v)
	{
		if ( 
				v instanceof android.opengl.GLSurfaceView == false
			&&	v instanceof View
		)
		{
			wd.setListeners(ReflectionHelper.reflectViewListeners(v));
		}
		
		try
		{
			// Class.isInstance <-> instanceof
			if (
					Class.forName("com.android.internal.view.menu.IconMenuItemView").isInstance(v)
				&&	ad.hasMenu()
			)
			{
				wd.addListener("OnClickListener", true);
			}
		}
		catch(Throwable ex)
		{
			ex.printStackTrace();
		}
		
		if (v instanceof android.widget.TabHost)
		{
			wd.addSupportedEvent(InteractionType.SWAP_TAB);
		}
	}
	
	/**
	 * Detect Name of the Widget
	 * 
	 * @param v Widget
	 * @return
	 */
	private String detectName (View v) {
		String name = "";
		if (v instanceof TextView) {
			TextView t = (TextView)v;
			name = t.getText().toString();
			if (v instanceof EditText) {
				CharSequence hint = ((EditText)v).getHint();
				name = (hint==null)?"":hint.toString();
			}
		} else if (v instanceof RadioGroup) {
			RadioGroup g = (RadioGroup)v;
			int max=g.getChildCount();
			String text = "";
			for (int i=0; i<max; i++) {
				View c = g.getChildAt(i);
				text = detectName (c);
				if (!text.equals("")) {
					name = text;
					break;
				}
			}
		}
		return name;
	}
	
	/**
	 * Set Value of the Widget
	 * 
	 * @param v Widget
	 * @param wd WidgetDescription instance
	 */
	private void setValue (View v, WidgetDescription wd)
	{		
		// Checkboxes, radio buttons and toggle buttons -> the value is the checked state (true or false)
		if (v instanceof Checkable) {
			wd.setValue(String.valueOf(((Checkable) v).isChecked()));
		}

		// Textview, Editview et al. -> the value is the displayed text
		if (v instanceof TextView) {
			wd.setValue(((TextView) v).getText().toString());
			return;
		}
		
		// Progress bars, seek bars and rating bars -> the value is the current progress
		if (v instanceof ProgressBar) {
			wd.setValue(String.valueOf(((ProgressBar) v).getProgress()));
		}
				
	}
	
	/**
	 * Set Count of the Widget
	 * 
	 * @param v Widget
	 * @param wd WidgetDescription instance
	 */
	@SuppressWarnings("rawtypes")
	public static void setCount (View v, WidgetDescription w) {
		// For lists, the count is set to the number of rows in the list (inactive rows - e.g. separators - count as well)
		if (v instanceof AdapterView) {
			w.setCount(((AdapterView)v).getCount());
			return;
		}
		
		// For Spinners, the count is set to the number of options
		if (v instanceof AbsSpinner) {
			w.setCount(((AbsSpinner)v).getCount());
			return;
		}
		
		// For the tab layout host, the count is set to the number of tabs
		if (v instanceof TabHost) {
			w.setCount(((TabHost)v).getTabWidget().getTabCount());
			return;
		}
		
		// For grids, the count is set to the number of icons, for RadioGroups it's set to the number of RadioButtons
		if (v instanceof ViewGroup) {
			w.setCount(((ViewGroup)v).getChildCount());
			return;
		}
		
		// For progress bars, seek bars and rating bars, the count is set to the maximum value allowed
		if (v instanceof ProgressBar) {
			w.setCount(((ProgressBar)v).getMax());
			return;
		}
	}
	
	/**
	 * Get Listeners for the Activity
	 * 
	 * @param activity Activity
	 * @return
	 */
	private HashMap<String, Boolean> getActivityListeners(Activity activity)
	{
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		//sensors
		ret.put("SensorEventListener", ReflectionHelper.scanClassForInterface(activity.getClass(), "android.hardware.SensorEventListener"));
		ret.put("SensorListener", ReflectionHelper.scanClassForInterface(activity.getClass(), "android.hardware.SensorListener"));
		ret.put("OrientationListener", ReflectionHelper.scanClassForInterface(activity.getClass(), "android.view.OrientationListener"));
		
		//location
		ret.put("LocationListener", ReflectionHelper.scanClassForInterface(activity.getClass(), "android.location.LocationListener"));
		
		return ret;
	}
	
	/**
	 * Check if the Activity has a menu
	 * 
	 * @param activity Activity
	 * @return
	 */
	private Boolean activityHasMenu(Activity activity)
	{
		return 	(
				ReflectionHelper.hasDeclaredMethod(activity.getClass(), "onCreateOptionsMenu")
				|| ReflectionHelper.hasDeclaredMethod(activity.getClass(), "onPrepareOptionsMenu")
				);
	}
	
	/**
	 * Check if the Activity handles keypress events
	 * 
	 * @param activity Activity
	 * @return
	 */
	private boolean handlesKeyPress(Activity activity)
	{
		return ReflectionHelper.hasDeclaredMethod(activity.getClass(), "onKeyDown");
	}
	
	/**
	 * Check if the Activity handles long keypress events
	 * 
	 * @param activity Activity
	 * @return
	 */
	private boolean handlesLongKeyPress(Activity activity)
	{
		return ReflectionHelper.hasDeclaredMethod(activity.getClass(), "onKeyLongPress");
	}

	/**
	 * Check if Activity is a tab activity
	 * 
	 * @param activity Activity
	 * @return
	 */
	private boolean isTabActivity(Activity activity)
	{
		return ReflectionHelper.isDescendant(activity.getClass(), android.app.TabActivity.class);
	}
	
	/**
	 * Get Tabs count
	 * 
	 * @param activity Activity
	 * @return
	 */
	public int getTabActivityTabsCount(Activity activity)
	{
		return ((android.app.TabActivity)activity).getTabHost().getChildCount();
	}
}
