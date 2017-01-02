package it.unina.android.ripper.output;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.TaskList;
import it.unina.android.ripper.model.WidgetDescription;

public class XMLRipperOutput implements RipperOutput
{
	public static boolean RUN_IN_THREAD = true; 
	
	@Override
	public String outputActivityDescription(ActivityDescription ad) {
		try {
			Document doc = this.buildActivityDescriptionDocument(ad);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}

	protected Document buildActivityDescriptionDocument(ActivityDescription ad) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		Element activity = doc.createElement(ACTIVITY);
		doc.appendChild(activity);

		activity.setAttribute(ACTIVITY_TITLE, ad.getTitle());
		activity.setAttribute(ACTIVITY_NAME, ad.getName());
		activity.setAttribute(ACTIVITY_CLASS, ad.getClassName());
		activity.setAttribute(ACTIVITY_MENU, (ad.hasMenu() ? "TRUE" : "FALSE"));
		activity.setAttribute(ACTIVITY_HANDLES_KEYPRESS,
				(ad.handlesKeyPress() ? "TRUE" : "FALSE"));
		activity.setAttribute(ACTIVITY_HANDLES_LONG_KEYPRESS,
				(ad.handlesLongKeyPress() ? "TRUE" : "FALSE"));
		activity.setAttribute(ACTIVITY_IS_TABACTIVITY,
				(ad.isTabActivity() ? "TRUE" : "FALSE"));
		activity.setAttribute(
				ACTIVITY_TABS_COUNT,
				(ad.isTabActivity() ? Integer.toString(ad.getTabsCount()) : "0"));
		activity.setAttribute(ACTIVITY_ID, (ad.getId() != null) ? ad.getId()
				: "");
		activity.setAttribute(ACTIVITY_UID, (ad.getUid() != null) ? ad.getUid()
				: "");
		activity.setAttribute(ACTIVITY_IS_ROOT_ACTIVITY, (ad.isRootActivity()  ? "TRUE" : "FALSE") );
		
		HashMap<String, Boolean> listeners = ad.getListeners();
		for (String key : listeners.keySet()) {
			Boolean value = listeners.get(key);

			Element listener = doc.createElement(LISTENER);
			listener.setAttribute(LISTENER_CLASS, key);
			listener.setAttribute(LISTENER_PRESENT, value ? "TRUE" : "FALSE");
			activity.appendChild(listener);
		}

		ArrayList<String> supportedEvents = ad.getSupportedEvents();
		for (String value : supportedEvents) {
			Element supportedEvent = doc.createElement(SUPPORTED_EVENT);
			supportedEvent.setAttribute(SUPPORTED_EVENT_TYPE, value);
			activity.appendChild(supportedEvent);
		}

		for (WidgetDescription wd : ad.getWidgets()) {
			Element widget = this.buildWidgetDescriptionDocument(wd)
					.getDocumentElement();
			//activity.appendChild( doc.importNode((Node)widget, true) );
			activity.appendChild(importElement(doc, widget)) ;
			
		}
		
		return doc;
	}
	
	@Override
	public String outputFirstStep(ActivityDescription ad, TaskList t) {
		try {
			Document doc = this.buildFirstStepDocument(ad, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildFirstStepDocument(ActivityDescription ad, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element firstStep = doc.createElement(FIRST_STEP);
		doc.appendChild(firstStep);
		
		Element activity = null;
		if (ad != null) {
			activity = this.buildActivityDescriptionDocument(ad).getDocumentElement();
			//firstStep.appendChild( doc.importNode((Node)activity, true) );
			firstStep.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			firstStep.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, ad).getDocumentElement();
			//firstStep.appendChild( doc.importNode((Node)extractedEvents, true) );
			firstStep.appendChild(importElement(doc, extractedEvents)) ;
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			firstStep.appendChild(extractedEvents);
		}
		
		return doc;
	}

	@Override
	public String outputWidgetDescription(WidgetDescription wd) {
		
		try {
			Document doc = this.buildWidgetDescriptionDocument(wd);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
		
	}
	
	protected Document buildWidgetDescriptionDocument(WidgetDescription wd) throws ParserConfigurationException {
		
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element widget = doc.createElement(WIDGET);
		doc.appendChild(widget);
		
		widget.setAttribute(WIDGET_ID, Integer.toString(wd.getId()));
		widget.setAttribute(WIDGET_CLASS, wd.getClassName());
		
		widget.setAttribute(WIDGET_SIMPLE_TYPE, wd.getSimpleType());
		
		if (wd.getTextualId() != null)
			widget.setAttribute(WIDGET_R_ID, wd.getTextualId());
		
		if (wd.getTextType() != null)
			widget.setAttribute(WIDGET_TEXT_TYPE, wd.getTextType().toString());
		
		if (wd.getName() != null)
			widget.setAttribute(WIDGET_NAME, wd.getName());
		
		if (wd.getValue() != null)
			widget.setAttribute(WIDGET_VALUE, wd.getValue());
		
		if (wd.getCount() != null)
			widget.setAttribute(WIDGET_COUNT, wd.getCount().toString());
		
		if (wd.getIndex() != null)
			widget.setAttribute(WIDGET_INDEX, wd.getIndex().toString());
		
		widget.setAttribute(WIDGET_ENABLED, wd.isEnabled()?"TRUE":"FALSE");
		widget.setAttribute(WIDGET_VISIBLE, wd.isVisible()?"TRUE":"FALSE");
		
		if (wd.getParentId() != null)
			widget.setAttribute(WIDGET_PARENT_ID, wd.getParentId().toString());
		else
			widget.setAttribute(WIDGET_PARENT_ID, "");

		if (wd.getParentName() != null)
			widget.setAttribute(WIDGET_PARENT_NAME, wd.getParentName());
		else
			widget.setAttribute(WIDGET_PARENT_NAME, "");
		
		if (wd.getParentType() != null)
			widget.setAttribute(WIDGET_PARENT_TYPE, wd.getParentType());
		else
			widget.setAttribute(WIDGET_PARENT_TYPE, "");

		if (wd.getAncestorId() != null)
			widget.setAttribute(WIDGET_ANCESTOR_ID, wd.getAncestorId().toString());
		else
			widget.setAttribute(WIDGET_ANCESTOR_ID, "");
		
		if (wd.getAncestorType() != null)
			widget.setAttribute(WIDGET_ANCESTOR_TYPE, wd.getAncestorType());
		else
			widget.setAttribute(WIDGET_ANCESTOR_TYPE, "");
		
		HashMap<String, Boolean> listeners = wd.getListeners();
		for (String key : listeners.keySet())
		{
			Boolean value = listeners.get(key);
			
			Element listener = doc.createElement(LISTENER);
			listener.setAttribute(LISTENER_CLASS, key);
			listener.setAttribute(LISTENER_PRESENT, (value != null && value)?"TRUE":"FALSE");
			widget.appendChild(listener);
		}
		
		return doc;
	}
	
	@Override
	public String outputEvent(Event evt) {
		try {
			Document doc = this.buildEventDescriptionDocument(evt, EVENT);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public String outputFiredEvent(Event evt) {
		try {
			Document doc = this.buildEventDescriptionDocument(evt, FIRED_EVENT);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}

	protected Document buildEventDescriptionDocument(Event e, String TAG) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element event = doc.createElement(TAG);
		doc.appendChild(event);
		
		event.setAttribute(EVENT_INTERACTION, e.getInteraction());
		event.setAttribute(EVENT_VALUE, e.getValue());
		
		if (e.getWidget() != null) {
			Element widget = null;
			if (e.getWidget() != null) {
				widget = this.buildWidgetDescriptionDocument( e.getWidget() ).getDocumentElement();
				//event.appendChild( doc.importNode((Node)widget, true) );
				event.appendChild(importElement(doc, widget)) ;
			} else {
				widget = doc.createElement(WIDGET); 
				event.appendChild( widget );
			}
		}
		
		if (e.getInputs() != null) {
			for (Input i : e.getInputs())
			{
				Element input = this.buildInputDescriptionDocument(i).getDocumentElement();
				//event.appendChild( doc.importNode((Node)input, true) );
				event.appendChild(importElement(doc, input)) ;
			}
		}
		
		return doc;
	}
	
	protected Document buildInputDescriptionDocument(Input i) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element input = doc.createElement(INPUT);
		doc.appendChild(input);
		
		input.setAttribute(INPUT_TYPE, i.getInputType());
		input.setAttribute(INPUT_VALUE, i.getValue());
		
		if (i.getWidget() != null) { 
			Element widget = null;
			if (i.getWidget() != null) {
				widget = this.buildWidgetDescriptionDocument( i.getWidget() ).getDocumentElement();
				//input.appendChild( doc.importNode((Node)widget, true) );
				input.appendChild(importElement(doc, widget)) ;
			} else {
				widget = doc.createElement(WIDGET); 
				input.appendChild( widget );
			}
		}
		
		return doc;
	}
	
	@Override
	public String outputTask(Task t) {
		try {
			Document doc = this.buildTaskDescriptionDocument(t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildTaskDescriptionDocument(Task t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element task = doc.createElement(TASK);
		doc.appendChild(task);
		
		for (Event e : t) {
			
			Element event = null;
			if (e != null) {
				event = this.buildEventDescriptionDocument(e, EVENT).getDocumentElement();
				//task.appendChild( doc.importNode((Node)event, true) );
				task.appendChild(importElement(doc, event)) ;
			} else {
				event = doc.createElement(EVENT);
				task.appendChild(event);
			}
		}
		
		return doc;
	}

	@Override
	public String outputStep(Event e, ActivityDescription a) {
		try {
			Document doc = this.buildStepDocument(e, a);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildStepDocument(Event e, ActivityDescription a) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element step = doc.createElement(STEP);
		doc.appendChild(step);
		
		Element event = null;
		if (e != null) {
			event = this.buildEventDescriptionDocument(e, FIRED_EVENT).getDocumentElement();		
			//step.appendChild( doc.importNode((Node)event, true) );
			step.appendChild(importElement(doc, event)) ;
		} else {
			event = doc.createElement(FIRED_EVENT);
			step.appendChild(event);
		}

		Element activity = null;
		if (a != null) {
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			//step.appendChild( doc.importNode((Node)activity, true) );
			step.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			step.appendChild(activity);
		}
		
		return doc;
	}
	
	@Override
	public String outputActivityDescriptionAndPlannedTasks(ActivityDescription a, TaskList t) {
		try {
			Document doc = this.buildActivityDescriptionAndPlannedDocument(a, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildActivityDescriptionAndPlannedDocument(ActivityDescription a, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element description = doc.createElement(DESCRIPTION);
		doc.appendChild(description);
		
		Element activity = null;
		if (a != null) {
			String id = a.getId();
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			activity.setAttribute("id", id);
			//description.appendChild( doc.importNode((Node)activity, true) );
			description.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			description.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, null).getDocumentElement();
			description.appendChild( doc.importNode((Node)extractedEvents, true) );
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			//description.appendChild(extractedEvents);
			description.appendChild(importElement(doc, extractedEvents)) ;
		}
		
		return doc;
	}

	@Override
	public String outputStepAndPlannedTasks(Event e, ActivityDescription a, TaskList t) {
		try {
			Document doc = this.buildStepAndPlannedTasksDocument(e, a, t);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	protected Document buildStepAndPlannedTasksDocument(Event e, ActivityDescription a, TaskList t) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element step = doc.createElement(STEP);
		doc.appendChild(step);
		
		Element event = null;
		if (e != null) {
			event = this.buildEventDescriptionDocument(e, FIRED_EVENT).getDocumentElement();
			//step.appendChild( doc.importNode((Node)event, true) );
			step.appendChild(importElement(doc, event)) ;
		} else {
			event = doc.createElement(EVENT);
			step.appendChild(event);
		}		

		Element activity = null;
		if (a != null) {
			activity = this.buildActivityDescriptionDocument(a).getDocumentElement();
			//step.appendChild( doc.importNode((Node)activity, true) );
			step.appendChild(importElement(doc, activity)) ;
		} else {
			activity = doc.createElement(ACTIVITY);
			step.appendChild(activity);
		}
		
		Element extractedEvents = null;
		if (t != null) {
			extractedEvents = this.buildExtractedEventsDocument(t, null).getDocumentElement();
			//step.appendChild( doc.importNode((Node)extractedEvents, true) );
			step.appendChild(importElement(doc, extractedEvents)) ;
		} else {
			extractedEvents = doc.createElement(EXTRACTED_EVENTS);
			step.appendChild(extractedEvents);
		}
		
		return doc;
	}
	
	@Override
	public String outputExtractedEvents(TaskList t) {
		return this.outputExtractedEvents(t, null);
	}
	
	@Override
	public String outputExtractedEvents(TaskList t, ActivityDescription from) {
		try {
			Document doc = this.buildExtractedEventsDocument(t, from);
			return this.XML2String(doc);
		} catch (ParserConfigurationException pex) {
			pex.printStackTrace();
		}
		
		return null;
	}
	
	private Document buildExtractedEventsDocument(TaskList t, ActivityDescription from) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element extractedEvents = doc.createElement(EXTRACTED_EVENTS);
		
		if (from != null) {
			extractedEvents.setAttribute(ACTIVITY_ID, from.getId());
		}
		
		doc.appendChild(extractedEvents);
		
		for (Task task : t) {
			
			Event e = task.get( task.size() - 1 );
		
			Element event = null;
			if (e != null) {
				event = this.buildEventDescriptionDocument(e, EVENT).getDocumentElement();
				//extractedEvents.appendChild( doc.importNode((Node)event, true) );
				extractedEvents.appendChild(importElement(doc, event)) ;
			}
			
		}
		
		return doc;
	}
	
	/**
	 * Return the String representation of an XML Document
	 * 
	 * @param doc XML Document
	 * @return XML String
	 */
	protected String XML2String(Document doc) {
		
		if (RUN_IN_THREAD == false) {
			try {
				StringWriter stw = new StringWriter();
				
				TransformerFactory tFactory = TransformerFactory.newInstance();
				Transformer serializer = tFactory.newTransformer();
				serializer.setOutputProperty("omit-xml-declaration", "yes");
				serializer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
				serializer.setOutputProperty(OutputKeys.INDENT, "yes");
				
				DOMSource domSrc = new DOMSource(doc);
				StreamResult sResult = new StreamResult(stw);
				serializer.transform(domSrc, sResult);
				
				String ret = stw.toString();
				
				domSrc = null;
				sResult = null;
				stw = null;
				serializer = null;
				
				return ret;

//				TransformerFactory transfac = TransformerFactory.newInstance();
//	            //transfac.setAttribute("indent-number", Integer.valueOf(2));
//	            Transformer trans = transfac.newTransformer();
//	            trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
//	            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//	            trans.setOutputProperty(OutputKeys.INDENT, "yes");
//
//	            //create string from xml tree
//	            StringWriter sw = new StringWriter();
//	            StreamResult result = new StreamResult(sw);
//	            DOMSource source = new DOMSource(doc);
//	            trans.transform(source, result);
//	            String xmlString = sw.toString();
//	            
//	           return xmlString;
				
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			
			return null;
			
		} else {
			try {
				XMLSerializerThread xmlSerializerThread = new XMLSerializerThread(doc);
				xmlSerializerThread.start();
				xmlSerializerThread.join();
				return xmlSerializerThread.getOutput();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	/**
	 * Import Node into Document
	 * 
	 * @param document XML Document
	 * @param element Node to import
	 * @return Document
	 */
	private Node importElement(Document document, Element element) {
		try {
			return document.importNode((Node)element, true);
		} catch (DOMException ex) {
			Node newNode = (Node)element.cloneNode(true);
			return document.adoptNode(newNode);
		}
	}

	public static final String ROOT = "description";
	
	public static final String ACTIVITY = "activity";	
	public static final String ACTIVITY_TITLE = "title";
	public static final String ACTIVITY_CLASS = "class";
	public static final String ACTIVITY_NAME = "name";
	public static final String ACTIVITY_MENU = "menu";
	public static final String ACTIVITY_HANDLES_KEYPRESS = "keypress";
	public static final String ACTIVITY_HANDLES_LONG_KEYPRESS = "longkeypress";
	public static final String ACTIVITY_IS_TABACTIVITY = "tab_activity";
	public static final String ACTIVITY_TABS_COUNT = "tab_count";
	public static final String ACTIVITY_ID = "id";
	public static final String ACTIVITY_UID = "uid";
	public static final String ACTIVITY_IS_ROOT_ACTIVITY = "root_activity";	
	
	public static final String LISTENER = "listener";
	public static final String LISTENER_CLASS = "class";
	public static final String LISTENER_PRESENT = "present";
	
	public static final String SUPPORTED_EVENT = "supported_event";
	public static final String SUPPORTED_EVENT_TYPE = "type";
	
	public static final String WIDGET = "widget";
	public static final String WIDGET_ID = "id";
	public static final String WIDGET_INDEX = "index";
	public static final String WIDGET_CLASS = "class";
	public static final String WIDGET_SIMPLE_TYPE = "simple_type";
	
	public static final String WIDGET_TEXT_TYPE = "text_type";
	public static final String WIDGET_NAME = "name";
	public static final String WIDGET_ENABLED = "enabled";
	public static final String WIDGET_VISIBLE = "visible";
	
	public static final String WIDGET_VALUE = "value";
	public static final String WIDGET_COUNT = "count";
	
	public static final String WIDGET_R_ID = "r_id";
	
	public static final String WIDGET_PARENT_ID = "p_id";
	public static final String WIDGET_PARENT_NAME = "p_name";
	public static final String WIDGET_PARENT_TYPE = "p_type";
	
	public static final String WIDGET_ANCESTOR_ID = "ancestor_id";
	public static final String WIDGET_ANCESTOR_TYPE = "ancestor_type";
	
	public static final String EVENT = "event";
	public static final String EVENT_INTERACTION = "interaction";
	public static final String EVENT_VALUE = "value";
	
	public static final String INPUT = "input";
	public static final String INPUT_TYPE = "type";
	public static final String INPUT_VALUE = "value";
	
	public static final String TASK = "task";
	
	public static final String STEP = "step";
	public static final String FINAL_ACTIVITY = "final_activity";
	
	public static final String EXTRACTED_EVENTS = "extracted_events";
	public static final String FIRED_EVENT = "fired_event";
	
	public static final String FIRST_STEP = "bootstrap";
	
	public static final String DESCRIPTION = "activity_description";
}
