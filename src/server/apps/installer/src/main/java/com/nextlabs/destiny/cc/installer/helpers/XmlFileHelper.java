package com.nextlabs.destiny.cc.installer.helpers;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility for reading XML files.
 *
 * @author Sachindra Dasun
 */
public class XmlFileHelper {

    private static final Logger logger = LoggerFactory.getLogger(XmlFileHelper.class);

    private XmlFileHelper() {
    }

    public static Map<String, String> readValues(Path xmlFilePath, Map<String, String> xPathToKeyMappings)
            throws ParserConfigurationException, IOException, SAXException {
        Map<String, String> values = new HashMap<>();
        File xmlFile = xmlFilePath.toFile();
        if (xmlFile.exists()) {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new InputSource(new FileInputStream(xmlFile)));
            XPath xPath = XPathFactory.newInstance().newXPath();
            for (Map.Entry<String, String> entry : xPathToKeyMappings.entrySet()) {
                String expression = entry.getKey();
                try {
                    String value = xPath.evaluate(expression, document);
                    if (StringUtils.isNotEmpty(value)) {
                        values.put(entry.getValue(), value);
                    }
                } catch (Exception e) {
                    logger.debug("Error in reading XPath expression {}", expression);
                }
            }
        }
        return values;
    }

}
