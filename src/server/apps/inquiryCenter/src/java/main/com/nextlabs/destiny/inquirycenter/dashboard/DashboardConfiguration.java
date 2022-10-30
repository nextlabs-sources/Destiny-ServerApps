/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2014 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.dashboard;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.richfaces.json.JSONArray;
import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.bluejungle.destiny.container.dcc.DCCResourceLocators;
import com.bluejungle.destiny.container.dcc.INamedResourceLocator;
import com.bluejungle.destiny.container.dcc.ServerRelativeFolders;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
/**
 * @author nnallagatla
 *
 */
public class DashboardConfiguration {
	
	public static final Log LOG = LogFactory.getLog(DashboardConfiguration.class);
	
	public static final String ID = "id";
	public static final String TYPE = "type";
	public static final String TITLE = "title";
	
	public static final String PROPERTIES_TAG = "properties";
	public static final String CELL_TAG = "td";
	public static final String NAME_TAG = "name";
	public static final String VALUE_TAG = "value";
	
	public static final String DASHBOARD_CONFIG_FILE_NAME = "dashboard.xml"; 
	
	/*
	 * maintain these and reload only if the file has been modified
	 */
	private static String dashboardTemplate = null;
	private static String paramsJSON = null;
	
	private static long lastReadTime;
	
	
	static 
	{
		StringBuilder contentStr = new StringBuilder();
		StringBuilder paramsStr = new StringBuilder();
		try {
			
			populateContentAndParamsJSONFromConfig(contentStr, paramsStr);
			dashboardTemplate = contentStr.toString();
			paramsJSON = paramsStr.toString();
			lastReadTime = System.currentTimeMillis();
			
		} catch (ParserConfigurationException e) {
			LOG.error("Error parsing dashboard configuration file", e);
		} catch (SAXException e) {
			LOG.error("Error parsing dashboard configuration file", e);
		} catch (IOException e) {
			LOG.error("Error reading dashboard configuration file", e);
		} catch (TransformerException e) {
			LOG.error(e);
		} catch (JSONException e) {
			LOG.error("Error while generating JSON parameters", e);
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getConfigFileLocation()
	{
		IComponentManager compMgr = ComponentManagerFactory.getComponentManager();
		INamedResourceLocator serverResourceLocator = (INamedResourceLocator) compMgr.getComponent(DCCResourceLocators.SERVER_HOME_RESOURCE_LOCATOR);
		return serverResourceLocator.getFullyQualifiedName(ServerRelativeFolders.CONFIGURATION_FOLDER.getPathOfContainedFile(DASHBOARD_CONFIG_FILE_NAME));
	}
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public static String getConfigFileContents() throws IOException
	{
		String configFileLocation = getConfigFileLocation();
		File file = new File(configFileLocation);
		if (!file.exists())
		{
			throw new FileNotFoundException("dashboard configuration file not found");
		}
		
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String str = null;
		while ((str = reader.readLine()) != null)
		{
			builder.append(str).append(System.getProperty("line.separator"));
		}
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		return builder.toString();
	}
	
	/**
	 * 
	 * @param content
	 * @param paramsJSON
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 * @throws JSONException
	 */
	public static void populateContentAndParamsJSONFromConfig(StringBuilder content, StringBuilder params) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException, JSONException
	{
		
		/*
		 * check if file modified timestamp is after lastRead
		 */
		
		if (!isReloadNeeded())
		{
			content.append(dashboardTemplate);
			params.append(paramsJSON);
			return;
		}
		synchronized(DashboardConfiguration.class)
		{
			if (!isReloadNeeded())
			{
				content.append(dashboardTemplate);
				params.append(paramsJSON);
				return;
			}
			
			List<Map<String,String>> paramsList = new ArrayList<Map<String,String>>();
			populateContentAndParamsFromConfig(content, paramsList);
			JSONArray arr = new JSONArray();
			/*
			 * [{},{},{}]
			 */
			for (Map<String,String> paramsMap : paramsList)
			{
				JSONObject obj = new JSONObject();
				for (Iterator<Map.Entry<String, String>> it = paramsMap.entrySet().iterator(); it.hasNext(); )
				{
					Map.Entry<String, String> entry = it.next();
					obj.put(entry.getKey(), entry.getValue());
				}
				arr.put(obj);
			}
			params.append(arr.toString());
			
			dashboardTemplate = content.toString();
			paramsJSON = params.toString();
			lastReadTime = System.currentTimeMillis();
		}
	}
	
	/**
	 * Do we need to reload configuration file
	 * @return
	 */
	private static boolean isReloadNeeded() {	
		
		String location = getConfigFileLocation();
		File file = new File (location);
		long lastModified = file.lastModified();
		
		if (lastModified <= lastReadTime && dashboardTemplate != null && paramsJSON != null)
		{
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param content
	 * @param params
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws TransformerException
	 */
	public static void populateContentAndParamsFromConfig(StringBuilder content, List<Map<String, String>> params) 
			throws ParserConfigurationException, SAXException, IOException, TransformerException
	{
		if (content == null || params == null)
		{
			return;
		}
		
		LOG.info("--------------Loading Dashboard configuration file-------------");
		
		String location = getConfigFileLocation();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setXIncludeAware(false);
		factory.setExpandEntityReferences(false);

		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(location);
		
		NodeList properties = doc.getElementsByTagName(PROPERTIES_TAG);
		
		populateProperties(properties, params);
		
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.METHOD, "xml");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");
		trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(doc.getDocumentElement());

		trans.transform(source, result);
		String xmlString = sw.toString();
		
		xmlString=xmlString.replaceAll("\\<\\?xml(.+?)\\?\\>", "").trim();
		
		content.append(xmlString);
	}
	
	/**
	 * 
	 * @param propertiesNodes
	 * @param paramsList
	 */
	private static void populateProperties(NodeList propertiesNodes, List<Map<String, String>> paramsList)
	{
		for (int i = 0; i < propertiesNodes.getLength(); i++)
		{
			//LOG.info("properties " + (i+1));
			//properties node in cell
			Node cell = propertiesNodes.item(i);
			//list of property nodes in propertyList
			NodeList propertyList = cell.getChildNodes();
			
			HashMap<String, String> paramsMap = new HashMap<String, String>();
			for (int j = 0; j < propertyList.getLength(); j++)
			{
				Node property = propertyList.item(j);
				String name = null, value = null;
				
				NodeList nameValueElements = property.getChildNodes();
				
				//LOG.info("property " + (j+1));
				for (int k = 0; k < nameValueElements.getLength(); k++)
				{
					Node element = nameValueElements.item(k);
					if (element.getNodeName().equalsIgnoreCase(NAME_TAG))
					{
						name = element.getTextContent();
					}
					else if (element.getNodeName().equalsIgnoreCase(VALUE_TAG))
					{
						value = element.getTextContent();
					}
				}
				
				//LOG.info("name: " + name + " value: " + value);
				/*
				 * if name and value are not 
				 */
				if (name != null && !name.isEmpty() && value != null && !value.isEmpty())
				{
					paramsMap.put(name, value);
				}
			}
			paramsList.add(paramsMap);
		}
	}
}
