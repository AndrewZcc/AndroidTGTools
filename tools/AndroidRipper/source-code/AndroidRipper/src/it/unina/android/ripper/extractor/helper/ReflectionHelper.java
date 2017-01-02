package it.unina.android.ripper.extractor.helper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;

/**
 * Method that exploit Java Reflection API 
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class ReflectionHelper
{
	/**
	 * Log TAG
	 */
	public static final String TAG = "ReflectionHelper";
	
	/**
	 * Check if a class implement an interface
	 * 
	 * @param className CanonicalName of the class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean implementsInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.implementsInterface(myClass, interfaceName);
	}
	
	/**
	 * Check if a class implement an interface
	 * 
	 * @param myClass Class class
	 * @param interfaceName CanonicalName of the interface
	 * @return esito della ricerca
	 */
	public static boolean implementsInterface(Class<?> myClass, String interfaceName)
	{
		for (Class<?> myInterface : myClass.getInterfaces())
		{
			if (myInterface.getCanonicalName().equals(interfaceName))
				return true;
		}
		return false;
	}
	
	/**
	 * Scan a class and its variables searching for something implementing an interface.
	 * 
	 * @param className CanonicalName of the class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static boolean scanClassForInterface(String className, String interfaceName) throws ClassNotFoundException
	{
		Class<?> myClass = (Class<?>) Class.forName(className);
		return ReflectionHelper.scanClassForInterface(myClass, interfaceName);
	}
	
	/**
	 * Scan a class and its variables searching for something implementing an interface.
	 * 
	 * @param myClass Class class
	 * @param interfaceName CanonicalName of the interface
	 * @return
	 * @return
	 */
	public static boolean scanClassForInterface(Class<?> myClass, String interfaceName)
	{
		if ( ReflectionHelper.implementsInterface(myClass, interfaceName))
		{
			Log.v(TAG, "Found interface : " + interfaceName + " in " + myClass.getCanonicalName());
			return true;
		}

		for(Field field : myClass.getDeclaredFields() )
		{
			Class<?> fieldClass = field.getType();
								
			if ( ReflectionHelper.implementsInterface(fieldClass, interfaceName))
			{
				Log.v(TAG, "Found field implements : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
			
			if (fieldClass.getCanonicalName().equals(interfaceName))
			{
				Log.v(TAG, "Found field inline definition : " + interfaceName  + " in " + fieldClass.getCanonicalName());
				return true;
			}
		}
		
		Log.v(TAG, "Not found : " + interfaceName  + " in " + myClass.getCanonicalName());
		return false;
	}
		
	/**
	 * Using Java Reflection API obtains the set of listeners of a View
	 * 
	 * @param view View to reflect
	 * @return 	HashMap<String, Boolean>: key=name of the method, value=esists?
	 */
	public static HashMap<String, Boolean> reflectViewListeners(android.view.View view)
    {
		HashMap<String, Boolean> ret = new HashMap<String, Boolean>();
		
		ret.put( "OnFocusChangeListener", checkIfFieldIsSet(view, "android.view.View", "mOnFocusChangeListener") );
		
		ret.put( "OnClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnTouchListener")
		);
		
		ret.put( "OnLongClickListener",
					checkIfFieldIsSet(view, "android.view.View", "mOnLongClickListener")
				||	checkIfFieldIsSet(view, "android.view.View", "mOnCreateContextMenuListener")
		);
		
		ret.put( "OnKeyListener", checkIfFieldIsSet(view, "android.view.View", "mOnKeyListener") );
		
		if (view instanceof android.widget.TextView) //EditText
		{
			ret.put( "TextChangedListener", checkIfArrayListFieldIsSet(view, "android.widget.TextView", "mListeners") );
		}
		
		if (view instanceof android.widget.AbsListView) //ListView
		{
			ret.put( "OnScrollListener", checkIfFieldIsSet(view, "android.widget.AbsListView", "mOnScrollListener") );			
			ret.put( "OnItemSelectedListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemSelectedListener") );
			ret.put( "OnItemClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemClickListener") );
			ret.put( "OnItemLongClickListener", checkIfFieldIsSet(view, "android.widget.AdapterView", "mOnItemLongClickListener") );			
		}
		
		if (view instanceof android.view.ViewGroup)
		{
			ret.put( "OnHierarchyChangeListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mOnHierarchyChangeListener") );
			ret.put( "AnimationListener", checkIfFieldIsSet(view, "android.view.ViewGroup", "mAnimationListener") );
		}
		
    	return ret;
    }
	
	/**
	 * Check if a field of a class is set
	 * 
	 * @param o Class Instance
	 * @param baseClass (Parent) Class
	 * @param fieldName Field Name
	 * @return
	 */
	public static boolean checkIfFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			boolean ret = (field.get(o) != null);
			Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | " + ((ret)?"ACTIVE":"NOT ACTIVE") );
			return ret;			
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	/**
	 * Check if an ArrayList field of a class is set
	 * 
	 * @param o Class Instance
	 * @param baseClass (Parent) Class
	 * @param fieldName Field Name
	 * @return
	 */
	public static boolean checkIfArrayListFieldIsSet(Object o, String baseClass, String fieldName)
	{
		java.lang.reflect.Field field;
		
		try
    	{
			//TODO: cache
			Class<?> viewObj = Class.forName(baseClass);
			field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			
			ArrayList arrayListField = (ArrayList) field.get(o);
			
			if (arrayListField != null)
			{
				if (arrayListField.size() > 0)
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | ACTIVE" );
					return true;
				}
				else
				{
					Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NOT ACTIVE" );
				}
			}
			else
			{
				Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " FOUND | NULL" );
				return false;
			}
    	}
		catch (Exception e)
		{
			Log.e(TAG, e.toString());
		}
		
		Log.v(TAG, o.getClass().getCanonicalName() + " > " + fieldName + " NOT FOUND");
		
		return false;
	}
	
	/**
	 * Get the value of a private field
	 * 
	 * @param canonicalClassName CanonicalName of the class
	 * @param fieldName Field Name
	 * @param o Class Instance
	 * @return
	 */
	public static Object getPrivateField(String canonicalClassName, String fieldName, Object o)
	{
		try
		{
			Class<?> viewObj = Class.forName(canonicalClassName);
			Field field = viewObj.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field.get(o);
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return null;
	}
	
	/**
	 * Check if the class has a declared method
	 * 
	 * TODO: check the signature of the method instead of the name only
	 * 
	 * @param c Class class
	 * @param methodName Method Name
	 * @return
	 */
	public static boolean hasDeclaredMethod(Class<?> c, String methodName)
	{
		try
		{
			for ( Method m : c.getDeclaredMethods() )
				if (m.getName().equals(methodName))
					return true;
		}
		catch(Exception ex)
		{
			Log.e(TAG, ex.toString());
		}
		
		return false;
	}
	
	/**
	 * Check if the class is a descendant of another class
	 * 
	 * @param descendant Descendant Class
	 * @param ancestor Ancestor Class
	 * @return
	 */
	public static boolean isDescendant(Class<?> descendant, Class<?> ancestor)
	{
		return ancestor.isAssignableFrom(descendant);
	}
}
