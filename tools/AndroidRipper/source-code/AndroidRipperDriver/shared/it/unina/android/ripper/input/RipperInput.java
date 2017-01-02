package it.unina.android.ripper.input;

import java.util.ArrayList;

import org.w3c.dom.Element;

import it.unina.android.ripper.model.ActivityDescription;

/**
 * Specifies how the element of the model are de-serialized
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public interface RipperInput
{
	/**
	 * Specifies how an Activity Description is de-serialized from an XML Element
	 * 
	 * @param description XML Element
	 * @return
	 */
	public ActivityDescription inputActivityDescription(Element description);
	
	/**
	 * Specifies how an Activity Description is de-serialized from a String
	 * 
	 * @param description String description
	 * @return
	 */
	public ActivityDescription inputActivityDescription(String description);
	
	/**
	 * Load an ActivityDescription list from an URI
	 * 
	 * @param sourceURI ActivityDescription list URI
	 * @return
	 */
	public ArrayList<ActivityDescription> loadActivityDescriptionList(String sourceURI);
}
