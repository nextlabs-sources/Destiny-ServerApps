package com.nextlabs.destiny.cc.installer.services;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import org.xml.sax.SAXException;

/**
 * Perform Control Center configuration management.
 *
 * @author Sachindra Dasun
 */
public interface ConfigurationManagementService {

    void perform() throws IOException, ParserConfigurationException, SAXException;

}
