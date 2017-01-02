package it.unina.android.ripper.output;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * Thread that performs the XML Serialization
 * 
 * @author Nicola Amatucci - REvERSE
 *
 */
public class XMLSerializerThread extends Thread {

	private String output;
	protected Document doc;
	
	public XMLSerializerThread(Document doc) {
		super();
		this.doc = doc;
	}
	
	@Override
	public void run() {
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
			
			this.output = new String(stw.toString());
			
			domSrc = null;
			sResult = null;
			stw = null;
			serializer = null;
			tFactory = null;
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public String getOutput() {
		return output;
	}

}
