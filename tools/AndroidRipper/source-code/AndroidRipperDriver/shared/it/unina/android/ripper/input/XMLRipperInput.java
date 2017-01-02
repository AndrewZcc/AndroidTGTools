package it.unina.android.ripper.input;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.WidgetDescription;

public class XMLRipperInput implements RipperInput {

	@Override
	public ActivityDescription inputActivityDescription(Element activityElement) {
		ActivityDescription ret = null;
		
		if (activityElement != null) {
			ret = new ActivityDescription();
			
			try {
				ret.setId( activityElement.getAttribute(ACTIVITY_ID) );
			} catch (Exception ex) {
				
			}
			
			try {
				ret.setUid( activityElement.getAttribute(ACTIVITY_UID) );
			} catch (Exception ex) {
				
			}
			
			ret.setTitle(activityElement.getAttribute(ACTIVITY_TITLE));
			ret.setName(activityElement.getAttribute(ACTIVITY_NAME));
			ret.setClassName(activityElement.getAttribute(ACTIVITY_CLASS));
			ret.setHasMenu(activityElement.getAttribute(ACTIVITY_MENU)
					.equalsIgnoreCase("TRUE"));
			ret.setHandlesKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setHandlesLongKeyPress(activityElement.getAttribute(
					ACTIVITY_HANDLES_KEYPRESS).equalsIgnoreCase("TRUE"));
			ret.setIsTabActivity(activityElement.getAttribute(
					ACTIVITY_IS_TABACTIVITY).equalsIgnoreCase("TRUE"));
			ret.setIsRootActivity(activityElement.getAttribute(
					ACTIVITY_IS_ROOT_ACTIVITY).equalsIgnoreCase("TRUE"));
			
			try
			{
				ret.setTabsCount( Integer.parseInt(activityElement.getAttribute(ACTIVITY_TABS_COUNT)));
			} catch(Throwable t){
				ret.setTabsCount(0);
			}
			
			NodeList childNodes = activityElement.getChildNodes();

			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				
				//System.out.println("1)"+node.getNodeName());
				
				if (node.getNodeType() == Node.ELEMENT_NODE)
				{
					
					Element e = (Element)node;
					//System.out.println("2)"+e.getNodeName());
					if (e.getNodeName().equals(LISTENER)) {

						ret.addListener(
								e.getAttribute(LISTENER_CLASS),
								e.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
										"TRUE"));

					} else if (e.getNodeName().equals(WIDGET)) {

						WidgetDescription wd = this.inputWidgetDescription(e);
						ret.addWidget(wd);
						
					}
				}
			}
		} else {
			// malformed xml
		}
		
		return ret;
	}
	
	@Override
	public ActivityDescription inputActivityDescription(String description) {
		
		//System.out.println(description);
		
		ActivityDescription ret = null;

		try {
			//ret = new ActivityDescription();

			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new ByteArrayInputStream(description.getBytes()));
			Element root = doc.getDocumentElement();
			if (root.getTagName().equals(ACTIVITY)) {
			
				ret = this.inputActivityDescription(root);

			} else {
				
				NodeList activityElements = root.getElementsByTagName(ACTIVITY);

				if (activityElements != null && activityElements.getLength() > 0) {
					Element activityElement = (Element) activityElements.item(0);

					ret = this.inputActivityDescription(activityElement);

				} else {
					// malformed xml
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	public WidgetDescription inputWidgetDescription(Element e) {
		WidgetDescription wd = null;
		
		if (e != null) {
			
			wd = new WidgetDescription();
			
			wd.setId(e.getAttribute(WIDGET_ID));
			wd.setClassName(e.getAttribute(WIDGET_CLASS));
			wd.setName(e.getAttribute(WIDGET_NAME));
			wd.setSimpleType(e.getAttribute(WIDGET_SIMPLE_TYPE));
			wd.setValue(e.getAttribute(WIDGET_VALUE));
			wd.setEnabled(e.getAttribute(WIDGET_ENABLED)
					.equalsIgnoreCase("TRUE"));
			wd.setVisible(e.getAttribute(WIDGET_VISIBLE)
					.equalsIgnoreCase("TRUE"));
			
			wd.setIndex(Integer.parseInt(e.getAttribute(WIDGET_INDEX)));
			
			if (e.getAttribute(WIDGET_COUNT) != null && e.getAttribute(WIDGET_COUNT).equals("") == false)
				wd.setCount(Integer.parseInt(e.getAttribute(WIDGET_COUNT)));
			
			NodeList widgetChildNodes = e.getElementsByTagName(LISTENER);
			//System.out.println(widgetChildNodes.getLength());
			for (int index2 = 0; index2 < widgetChildNodes.getLength(); index2++) {
				Node node2 = (Node) widgetChildNodes.item(index2);
				if (node2.getNodeType() == Node.ELEMENT_NODE)
				{
						Element e2 = (Element)node2;
						wd.addListener(
								e2.getAttribute(LISTENER_CLASS),
								e2.getAttribute(LISTENER_PRESENT).equalsIgnoreCase(
										"TRUE"));
				}
			}
			
			try
			{
				//wd.setParentId( Integer.parseInt(activityElement.getAttribute(WIDGET_PARENT_ID)));
				wd.setParentId( Integer.parseInt(e.getAttribute(WIDGET_PARENT_ID)));
			} catch(Throwable t){
				wd.setParentId(-1);
			}
			
			wd.setParentName(e.getAttribute(WIDGET_PARENT_NAME));
			wd.setParentType(e.getAttribute(WIDGET_PARENT_TYPE));
			
			try
			{
				//wd.setAncestorId( Integer.parseInt(activityElement.getAttribute(WIDGET_ANCESTOR_ID)));
				wd.setAncestorId( Integer.parseInt(e.getAttribute(WIDGET_ANCESTOR_ID)));
			} catch(Throwable t){
				wd.setAncestorId(-1);
			}
			
			wd.setAncestorType(e.getAttribute(WIDGET_ANCESTOR_TYPE));			
			
		}
	
		return wd;
	}
	
	public Event inputEvent(Element e) {
		Event event = null;
		
		if (e != null) {
		
			event = new Event();
			ArrayList<Input> inputs = new ArrayList<Input>();
			
			event.setInteraction(e.getAttribute(EVENT_INTERACTION));
			event.setValue(e.getAttribute(EVENT_VALUE));
			
			NodeList childNodes = e.getChildNodes();
			
			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element ce = (Element)node;
					
					if (ce.getNodeName().equals(WIDGET)) {
						WidgetDescription wd = this.inputWidgetDescription(ce);
						event.setWidget(wd);
					}
					
					if (ce.getNodeName().equals(INPUT)) {
						
						Input input = this.inputInput(ce);
						inputs.add(input);
					}
					
				}
			}
			
			event.setInputs(inputs);
		}
		
		return event;
	}
	
	public Input inputInput(Element e) {
		Input input = null;
		
		if (e != null) {
			input = new Input();
			
			input.setInputType(e.getAttribute(INPUT_TYPE));
			input.setValue(e.getAttribute(INPUT_VALUE));
			
			NodeList childNodes = e.getChildNodes();
			
			for (int index = 0; index < childNodes.getLength(); index++) {

				Node node = (Node) childNodes.item(index);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					
					Element ce = (Element)node;
					
					if (ce.getNodeName().equals(WIDGET)) {
						WidgetDescription wd = this.inputWidgetDescription(ce);
						input.setWidget(wd);
						break;
					}
				}
			}
			
		}
		
		return input;
	}
	
	public ArrayList<ActivityDescription> loadActivityDescriptionList(String sourceURI) {
		ArrayList<ActivityDescription> ret = new ArrayList<ActivityDescription>();
		
		NodeList nList = null;
		try {

			File fXmlFile = new File(sourceURI);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName(ACTIVITY);
			
			for (int i = 0; i < nList.getLength(); i++) {
	
				Node nNode = nList.item(i);
	
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ActivityDescription ad = this.inputActivityDescription(eElement);
					ret.add(ad);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public ArrayList<ActivityDescription> loadActivityDescriptionList(InputStream is) {
		ArrayList<ActivityDescription> ret = new ArrayList<ActivityDescription>();
		
		NodeList nList = null;
		try {

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(is);

			doc.getDocumentElement().normalize();

			nList = doc.getElementsByTagName(ACTIVITY);
			
			for (int i = 0; i < nList.getLength(); i++) {
	
				Node nNode = nList.item(i);
	
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ActivityDescription ad = this.inputActivityDescription(eElement);
					ret.add(ad);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public static final String ROOT = "root";
	
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
