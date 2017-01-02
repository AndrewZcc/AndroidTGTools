package it.unina.android.ripper.xml2junit;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import it.unina.android.ripper.input.XMLRipperInput;
import it.unina.android.ripper.model.ActivityDescription;
import it.unina.android.ripper.model.Event;
import it.unina.android.ripper.model.Input;
import it.unina.android.ripper.model.Task;
import it.unina.android.ripper.model.WidgetDescription;

public class JUnitOutput {

	public static final String CHARSET = "UTF-8";
	
	public static int TIME = 1000;
	public static boolean ASSERT = true;
	
	public static void main(String[] args) {

		if (args.length == 1 && args[0].equals("clean")) {
			new File("./RipperTestCase/assets/activities.xml").delete();
			new File("./RipperTestCase/AndroidManifest.xml").delete();
			new File("./RipperTestCase/src/it/unina/android/ripper/test/RipperTest.java").delete();
		} else if (args.length != 4) {
			printUsage();
		} else{
			File f = new File(args[2]);
			if (f.exists() && f.isDirectory()) {
					try {			
						//JUnitOutput jUnitOutput = new JUnitOutput("kdk.android.simplydo", "kdk.android.simplydo.SimplyDoActivity", "./model/", "CustomWidgetSimpleComparator");
						JUnitOutput jUnitOutput = new JUnitOutput(args[0], args[1], args[2], args[3]);
						jUnitOutput.outputManifest();
						jUnitOutput.outputJUnit();
						jUnitOutput.copyActivitiesXML();
					} catch(Exception ex) {
						ex.printStackTrace();
					}
					
			} else {
				System.out.println("Model dir not found!");
			}
		}
		
	}
	
	public static void printUsage() {
		System.out.println("java -jar XML2jUnitForAndroidRipper.jar [activity under test package] [activity under test with full package] [model directory] [comparator name]");
		System.out.println();
		System.out.println("Comparator name parameter is not validated by the tool for future updates.");
		System.out.println("Valid comparator names: NameComparator, CustomWidgetSimpleComparator, CustomWidgetIntensiveComparator");
		System.out.println();
		System.out.println("To clean the RipperTestCase project: java -jar XML2jUnitForAndroidRipper.jar clean");
	}

	public String templateFile = "./RipperTest.java.template";
	public String outFile = "./RipperTestCase/src/it/unina/android/ripper/test/RipperTest.java";
	public String inDir = ".";
	public PrintStream out = null;
	ByteArrayOutputStream baos = null;
	String templateContent = null;
	String activityUnderTestWithFullPacakage = null;
	String comparatorConfigurationName = null;
	String activityUnderTestPackage = null;
	
	public JUnitOutput(String activityUnderTestPackage, String activityUnderTestWithFullPacakage, String inDir, String comparatorConfigurationName) throws IOException {
		super();
		this.inDir = inDir;
		templateContent = readFile(templateFile, Charset.forName(CHARSET)); //read template file
		this.baos = new ByteArrayOutputStream();
		this.out = new PrintStream(baos);
		this.activityUnderTestWithFullPacakage = activityUnderTestWithFullPacakage;
		this.comparatorConfigurationName = comparatorConfigurationName;
		this.activityUnderTestPackage = activityUnderTestPackage;
	}

	public void copyActivitiesXML() {
		try {
			new File("./RipperTestCase/assets/activities.xml").delete();
			
			String activitiesContent = readFile(inDir+"activities.xml", Charset.forName(CHARSET));
			writeFile("./RipperTestCase/assets/activities.xml", activitiesContent, Charset.forName(CHARSET));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void outputManifest() {
		
		try {
			new File("./RipperTestCase/AndroidManifest.xml").delete();
			
			String manifestContent = readFile("./AndroidManifest.xml.template", Charset.forName(CHARSET));
			manifestContent = manifestContent.replace("%APP_PACKAGE%", activityUnderTestPackage);
			
			writeFile("./RipperTestCase/AndroidManifest.xml", manifestContent, Charset.forName(CHARSET));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void outputJUnit() throws IOException {
		
		new File(outFile).delete();
		
		String tabs = "\t";
		
		XMLRipperInput xmlInput = new XMLRipperInput();
		
		File dir = new File(inDir);
		if (dir.exists() && dir.isDirectory()) {

			//iterate on xml files starting with "log_"; ignores "log_0.xml"
			GenericExtFilter filter = new GenericExtFilter("xml");
			for (File f : dir.listFiles(filter)) {
				
				if (f.getName().startsWith("log_")) {
					
					Task task = new Task();
				
					String name = f.getName().substring("log_".length(), f.getName().length() - ".xml".length());
					
					if (name.equals("0") == false) {
					
						name = "testCase" + String.format("%05d", new Integer(name));
						out.println(tabs+"public void "+name+"() {");
						
						Document doc = loadXML(f.getAbsolutePath());
						if ( doc != null ) {
							NodeList eventNodes = doc.getDocumentElement().getChildNodes();
							for (int i = 0; i < eventNodes.getLength(); i++) {
								Node eventNode = eventNodes.item(i);
								if (eventNode.getNodeType() == Node.ELEMENT_NODE) {
									Element eventElement = (Element)eventNode;
									if (eventElement != null) {
										if (eventElement.getTagName().equals("fired_event")) {
											Event event = xmlInput.inputEvent(eventElement);
											if (event != null) {
												outputEvent(event);
											}
										} else if (ASSERT && eventElement.getTagName().equals("activity")) {
											ActivityDescription ad = xmlInput.inputActivityDescription(eventElement);
											if (ad != null) {
												outputActivityDescription(ad);
											}
										} else if (ASSERT && eventElement.getTagName().equals("activity_description") ) {

											NodeList adNodes = eventElement.getChildNodes();
											for (int j = 0; j < adNodes.getLength(); j++) {
												Node adNode = adNodes.item(j);
												if (adNode.getNodeType() == Node.ELEMENT_NODE) {
													Element adElement = (Element)adNode;
													if (adElement.getTagName().equals("activity") ) {
														ActivityDescription ad = xmlInput.inputActivityDescription(adElement);
														if (ad != null) {
															outputActivityDescription(ad);
														}
														break;
													}
												}
											}
											
										}
									}
								}
							}
						}
						
						out.println(tabs+"}");
						out.println(tabs);
					}
					
				}				

			}

		} else {
			out.println(dir.toString() + " does not exist!");
		}
		
		//FILL IN TEMPLATE FILE
		
		templateContent = templateContent.replaceAll("%SLEEP_AFTER_TASK%", Integer.toString(TIME));
		templateContent = templateContent.replaceAll("%AUT_ACTIVITY_CLASS%", activityUnderTestWithFullPacakage);
		templateContent = templateContent.replaceAll("%TEST_CASES%", baos.toString(CHARSET));
		templateContent = templateContent.replaceAll("%COMPARATOR_NAME%", comparatorConfigurationName);
		writeFile(outFile,templateContent,Charset.forName(CHARSET));
	}

	public class GenericExtFilter implements FilenameFilter {
		private String ext;

		public GenericExtFilter(String ext) {
			this.ext = ext;
		}

		public boolean accept(File dir, String name) {
			return name.endsWith(this.ext);
		}
	}

	public Document loadXML(String name) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			String content = readFile(name, Charset.forName(CHARSET));
			Document doc = builder.parse(new InputSource(new ByteArrayInputStream(content.getBytes(CHARSET))));
			return doc;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
	
	public void writeFile(String path, String outputText, Charset encoding) throws IOException {
		Files.write(Paths.get(path), outputText.getBytes(encoding));
	}
	
	public void outputEvent(Event event) {
		String tabs = "\t\t";		
		WidgetDescription wd = event.getWidget();
				
		for (Input input : event.getInputs()) {
			String widgetId = Integer.toString(input.getWidget().getId());
			String inputType = input.getInputType();
			String value = input.getValue();
			out.println(tabs + "automation.setInput("+widgetId+", \""+inputType+"\", \""+value+"\");");
			//out.println(tabs+"automation.sleep("+TIME+");");
		}
		
		if (wd != null) {
			String widgetId = Integer.toString(wd.getId());
			String widgetIndex = Integer.toString(wd.getIndex());
			String widgetName = wd.getName();
			String widgetType = wd.getSimpleType();
			String eventType = event.getInteraction();
			String value = event.getValue();
		
			out.println(tabs + "automation.fireEvent(\""+widgetId+"\", "+widgetIndex+", \""+widgetName+"\", \""+widgetType+"\", \""+eventType+"\", \""+value+"\");");
		} else {
			String eventType = event.getInteraction();
			String value = event.getValue();
			
			if (value != null) {
				out.println(tabs + "automation.fireEvent(null, null, null, null, \""+eventType+"\", \""+value+"\");");
			} else {
				out.println(tabs + "automation.fireEvent(null, null, null, null, \""+eventType+"\", null);");
			}
		}
		
		//out.println(tabs+"automation.sleep("+TIME+");");
	}

	public void outputActivityDescription(ActivityDescription ad) {
		String tabs = "\t\t";
		out.println(tabs + "assertEquals(\""+ad.getId()+"\",activityStateList.getEquivalentActivityStateId(extractor.extract()));");
	}

}
