package com.traclabs.ga.server.framework;

import java.net.URL;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.traclabs.ga.util.OrbUtils;

/**
 * Reads GA configuration from XML file. Singleton class
 * 
 * @author Scott Bell
 */
public class GAInitializer {
	private static final String SCHEMA_LOCATION_VALUE = "com/traclabs/ga/server/framework/schema/GAInitSchema.xsd";

	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

	private static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";

	private static String XML_LOCATION = "com/traclabs/ga/server/framework/configuration/DefaultInit.xml";

	private static final String SCHEMA_LOCATION_LABEL = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";

	private static DocumentBuilder myDocumentBuilder = null;

	private static Logger myLogger;

	private static GAInitializer myInitializer;

	private static boolean hasInitialized = false;

	private static String myNodeClassName;

	private static String myGeneClassName;

	private static Properties myNodeProperties;

	/** Default constructor. */
	private static void initialize() {
		if (hasInitialized)
			return;
		myNodeProperties = new Properties();
		myLogger = Logger.getLogger(GAInitializer.class);
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
				.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		documentBuilderFactory.setValidating(true);
		try {
			documentBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE,
					W3C_XML_SCHEMA);
			URL foundURL = GAInitializer.class.getClassLoader()
					.getResource(SCHEMA_LOCATION_VALUE);
			if (foundURL != null) {
				String urlString = foundURL.toString();
				if (urlString.length() > 0)
					documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE,
							urlString);
			}
			myDocumentBuilder = documentBuilderFactory.newDocumentBuilder();
			myDocumentBuilder.setErrorHandler(new DefaultHandler());
			String resolvedXMLLocation = OrbUtils.resolveXMLLocation(XML_LOCATION);
			parseFile(resolvedXMLLocation);
		} catch (IllegalArgumentException e) {
			// This can happen if the parser does not support JAXP 1.2
			myLogger
					.warn("Had trouble configuring parser for schema validation: "
							+ e);
		} catch (ParserConfigurationException e) {
			myLogger.warn("Had trouble configuring parser: " + e);
			e.printStackTrace();
		}
	}

	/** Traverses the specified node, recursively. */
	private static void crawlGA(Node node) {
		// is there anything to do?
		if (node == null)
			return;
		String nodeName = node.getNodeName();
		if (nodeName.equals("Globals")) {
			crawlGlobals(node);
			return;
		} else if (nodeName.equals("Node")) {
			crawlNode(node);
			return;
		} else if (nodeName.equals("Gene")) {
			myGeneClassName = node.getAttributes().getNamedItem("className")
					.getNodeValue();
			return;
		} else {
			Node child = node.getFirstChild();
			while (child != null) {
				crawlGA(child);
				child = child.getNextSibling();
			}
		}

	}

	/**
	 * @param node
	 */
	private static void crawlNode(Node node) {
		myNodeClassName = node.getAttributes().getNamedItem("className")
				.getNodeValue();
		Node child = node.getFirstChild();
		while (child != null) {
			String childName = child.getNodeName();
			if (childName.equals("nodeProperty")) {
				String nameProperty = child.getAttributes().getNamedItem("key")
						.getNodeValue();
				String valueProperty = child.getAttributes().getNamedItem(
						"value").getNodeValue();
				// set Property
				myNodeProperties.setProperty(nameProperty, valueProperty);
			}
			child = child.getNextSibling();
		}
	}

	/**
	 * @param node
	 */
	private static void crawlGlobals(Node node) {
		Properties logProperties = new Properties();
		Node child = node.getFirstChild();
		while (child != null) {
			String childName = child.getNodeName();
			if (childName.equals("log4jProperty")) {
				String nameProperty = child.getAttributes().getNamedItem("key")
						.getNodeValue();
				String valueProperty = child.getAttributes().getNamedItem(
						"value").getNodeValue();
				logProperties.setProperty(nameProperty, valueProperty);
			}
			child = child.getNextSibling();
		}
		PropertyConfigurator.configure(logProperties);
	}

	private static void parseFile(String fileToParse) {
		try {
			Document document = myDocumentBuilder.parse(fileToParse);
			crawlGA(document);
		} catch (Exception e) {
			myLogger.error("error: Parse error occurred - " + e.getMessage());
			Exception se = e;
			if (e instanceof SAXException)
				se = ((SAXException) e).getException();
			if (se != null)
				se.printStackTrace();
			else
				e.printStackTrace();
		}
	}

	/**
	 * @return Returns the myGeneClassName.
	 */
	public static String getGeneClassName() {
		initialize();
		return myGeneClassName;
	}

	/**
	 * @return Returns the myNodeClassName.
	 */
	public static String getNodeClassName() {
		initialize();
		return myNodeClassName;
	}

	/**
	 * @return Returns the myXMLLocation.
	 */
	public static String getXMLLocation() {
		return XML_LOCATION;
	}

	/**
	 * @param myXMLLocation
	 *            The myXMLLocation to set.
	 */
	public static void setXMLLocation(String myXMLLocation) {
		GAInitializer.XML_LOCATION = myXMLLocation;
	}

	/**
	 * @return
	 */
	public static Properties getNodeProperties() {
		return myNodeProperties;
	}
}