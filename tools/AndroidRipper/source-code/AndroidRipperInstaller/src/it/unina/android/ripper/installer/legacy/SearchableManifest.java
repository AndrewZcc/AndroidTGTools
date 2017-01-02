package it.unina.android.ripper.installer.legacy;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SearchableManifest {
	
	private Document doc;
	
	public SearchableManifest (Document manifest) {
		this.doc = manifest;
	}

	public SearchableManifest (String path) {
		File file = new File(path);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
			try {
				db = dbf.newDocumentBuilder();
				Document manifest = db.parse(file);
				this.doc = manifest;
			} catch (ParserConfigurationException e) {
			} catch (SAXException e) {
			} catch (IOException e) {
			}
	}

	public String parseXpath (String query) {
		try {
			XPath manifestXpath = XPathFactory.newInstance().newXPath();
			XPathExpression manifestXpathExpr = manifestXpath.compile(query);
			Object manifestResult = manifestXpathExpr.evaluate(this.doc);
			return (String)manifestResult;
		} catch (XPathExpressionException e) {
		}
		return "";
	}
	
}