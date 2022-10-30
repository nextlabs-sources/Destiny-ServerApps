package com.nextlabs.destiny.console.services.impl;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.composite.CompositeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.dto.common.StringFieldValue;
import com.nextlabs.destiny.console.dto.common.TextSearchValue;
import com.nextlabs.destiny.console.dto.config.LoggerAppenderDTO;
import com.nextlabs.destiny.console.dto.config.LoggerConfigDTO;
import com.nextlabs.destiny.console.dto.config.LoggerConfigValueDTO;
import com.nextlabs.destiny.console.model.LoggerConfig;
import com.nextlabs.destiny.console.repositories.LoggerConfigRepository;
import com.nextlabs.destiny.console.services.LoggerConfigService;
import com.nextlabs.destiny.console.services.SysConfigService;
import com.nextlabs.destiny.logmanager.enums.LogLevel;
import com.nextlabs.destiny.logmanager.enums.LoggerConfigType;

/**
 * Implementation of logger configuration service.
 *
 * @author Sachindra Dasun
 */
@Service
public class LoggerConfigServiceImpl implements LoggerConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggerConfigServiceImpl.class);

    private static final String LOGGER_DISPLAY_NAMES = "LOGGER_DISPLAY_NAMES";
    private static final String LOGGER_ATTRIBUTE_NAME = "name";
    private static final String LOGGER_ATTRIBUTE_LEVEL = "level";

    @Autowired
    private LoggerConfigRepository loggerConfigRepository;

    @Autowired
    private SysConfigService sysConfigService;

    @Override
    public List<LoggerConfigDTO> findAll() {
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        List<LoggerConfig> loggerConfigs = new ArrayList<>();
        loggerConfigs.addAll(loggerConfigRepository.findByTypeOrderByIdDesc(LoggerConfigType.DEFAULT));
        loggerConfigs.addAll(loggerConfigRepository.findByTypeOrderByIdDesc(LoggerConfigType.CUSTOM));

        Map<String, String> loggerDisplayNames = new LinkedHashMap<>();
        loggerConfigs.stream()
                .filter(loggerConfig -> StringUtils.isNotEmpty(loggerConfig.getConfig()))
                .forEach(loggerConfig -> {
                    Map<String, String> displayNames = getLoggerDisplayNames(loggerConfig);
                    if (displayNames != null) {
                        loggerDisplayNames.putAll(displayNames);
                    }
                });
        CompositeConfiguration compositeConfiguration = new CompositeConfiguration(
                loggerConfigs.stream()
                        .filter(loggerConfig -> StringUtils.isNotEmpty(loggerConfig.getConfig()))
                        .map(loggerConfig -> {
                            try {
                                Configuration configuration = ConfigurationFactory.getInstance()
                                        .getConfiguration(context, new ConfigurationSource(
                                                new ByteArrayInputStream(loggerConfig.getConfig().getBytes())));
                                if (configuration instanceof AbstractConfiguration) {
                                    return (AbstractConfiguration) configuration;
                                }
                            } catch (IOException e) {
                                LOGGER.error("Error occurred when creating the logger config for id: " + loggerConfig.getId(), e);
                            }
                            return null;
                        }).filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );
        compositeConfiguration.initialize();
        ArrayList<String> orderedLoggerDisplayNames = new ArrayList<>(loggerDisplayNames.keySet());
        return compositeConfiguration.getLoggers().values().stream().map(loggerConfig -> {
            LoggerConfigDTO loggerConfigDTO = new LoggerConfigDTO();
            loggerConfigDTO.setName(loggerConfig.getName());
            String displayName = loggerDisplayNames.get(loggerConfig.getName());
            if (StringUtils.isNotEmpty(displayName)) {
                loggerConfigDTO.setOrder(orderedLoggerDisplayNames.indexOf(loggerConfig.getName()));
                loggerConfigDTO.setDisplayName(displayName);
            }
            loggerConfigDTO.setLevel(LogLevel.fromLog4jLevel(loggerConfig.getLevel()));
            loggerConfigDTO.setAppenders(loggerConfig.getAppenders().values().stream().map(appender -> {
                LoggerAppenderDTO loggerAppenderDTO = new LoggerAppenderDTO();
                loggerAppenderDTO.setType(appender.getClass().getSimpleName());
                loggerAppenderDTO.setName(appender.getName());
                return loggerAppenderDTO;
            }).collect(Collectors.toList()));
            return loggerConfigDTO;
        }).sorted(Comparator.comparingInt(LoggerConfigDTO::getOrder)).collect(Collectors.toList());
    }

    private Map<String, String> getLoggerDisplayNames(LoggerConfig loggerConfig) {
        try {
            String displayNamesComment = null;
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);

            Document document = documentBuilderFactory
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(loggerConfig.getConfig().getBytes()));
            NodeList nodeList = document.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Element.COMMENT_NODE) {
                    Comment commentNode = (Comment) node;
                    String comment = commentNode.getData();
                    if (comment.startsWith(LOGGER_DISPLAY_NAMES)) {
                        displayNamesComment = comment;
                        break;
                    }
                }
            }
            if (StringUtils.isNotEmpty(displayNamesComment)) {
                Map<String, String> displayNames = new LinkedHashMap<>();
                try (Scanner scanner = new Scanner(displayNamesComment.replace(LOGGER_DISPLAY_NAMES, ""))) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (StringUtils.isNotEmpty(line)) {
                            String[] keyValuePair = line.split("=");
                            if (keyValuePair.length > 1) {
                                displayNames.put(keyValuePair[0].trim(), keyValuePair[1].trim());
                            }
                        }
                    }
                }
                return displayNames;
            }
        } catch (Exception e) {
            LOGGER.error("Error in parsing logger display names from XML comment for type " + loggerConfig.getType(), e);
        }
        return null;
    }

    public String getCustomConfig() {
        String customLoggerConfig = loggerConfigRepository.findTopByTypeOrderByIdDesc(LoggerConfigType.CUSTOM)
                .map(LoggerConfig::getConfig).orElse(null);
        if (StringUtils.isEmpty(customLoggerConfig)) {
            customLoggerConfig = loggerConfigRepository.findTopByTypeOrderByIdDesc(LoggerConfigType.DEFAULT)
                    .map(LoggerConfig::getConfig)
                    .orElse(null);
        }
        return customLoggerConfig;
    }

    public void save(String config) {
        LoggerConfig loggerConfig = loggerConfigRepository.findTopByTypeOrderByIdDesc(LoggerConfigType.CUSTOM).orElseGet(() -> {
            LoggerConfig customLoggerConfig = new LoggerConfig();
            customLoggerConfig.setType(LoggerConfigType.CUSTOM);
            return customLoggerConfig;
        });
        loggerConfig.setConfig(config);
        loggerConfigRepository.save(loggerConfig);
        sysConfigService.sendLoggerRefreshRequest();
    }

    @Override
    public void updateLoggers(List<LoggerConfigValueDTO> loggerConfigValueDTOS) throws ParserConfigurationException,
            IOException, SAXException, TransformerException {
        LoggerConfig loggerConfig = loggerConfigRepository.findTopByTypeOrderByIdDesc(LoggerConfigType.CUSTOM).orElse(null);
        if (loggerConfig == null) {
            loggerConfig = new LoggerConfig();
            loggerConfig.setType(LoggerConfigType.CUSTOM);
            loggerConfig.setConfig(loggerConfigRepository.findTopByTypeOrderByIdDesc(LoggerConfigType.DEFAULT)
                    .map(LoggerConfig::getConfig)
                    .orElse(null));
        }
        if (StringUtils.isNotEmpty(loggerConfig.getConfig())) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);

            Document document = documentBuilderFactory
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(loggerConfig.getConfig().getBytes()));
            loggerConfigValueDTOS.stream()
                    .filter(loggerConfigValueDTO -> loggerConfigValueDTO.getName().isEmpty())
                    .forEach(loggerConfigValueDTO -> {
                        NodeList nodeList = document.getElementsByTagName("Root");
                        for (int i = 0; i < nodeList.getLength(); i++) {
                            Node node = nodeList.item(i);
                            node.getAttributes().getNamedItem(LOGGER_ATTRIBUTE_LEVEL).setNodeValue(loggerConfigValueDTO.getLevel().getLog4jLevel().toString());
                        }
                    });
            NodeList nodeList = document.getElementsByTagName("Logger");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                for (LoggerConfigValueDTO loggerConfigValueDTO : loggerConfigValueDTOS) {
                    if (loggerConfigValueDTO.getName().equals(node.getAttributes().getNamedItem(LOGGER_ATTRIBUTE_NAME).getNodeValue())) {
                        node.getAttributes().getNamedItem(LOGGER_ATTRIBUTE_LEVEL).setNodeValue(loggerConfigValueDTO.getLevel().getLog4jLevel().toString());
                        break;
                    }
                }
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            String configurationXml = stringWriter.getBuffer().toString();
            loggerConfig.setConfig(configurationXml);
            loggerConfigRepository.save(loggerConfig);
            sysConfigService.sendLoggerRefreshRequest();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<LoggerConfigDTO> search(SearchCriteria searchCriteria) {
        List<LoggerConfigDTO> loggerConfigs = findAll();
        if (searchCriteria != null) {
            loggerConfigs = loggerConfigs.stream().filter(loggerConfigDTO -> {
                for (SearchField searchField : searchCriteria.getFields()) {
                    if (searchField != null && searchField.getField() != null && searchField.getValue() != null) {
                        switch (searchField.getField()) {
                            case "level": {
                                if (searchField.getValue() instanceof StringFieldValue) {
                                    StringFieldValue stringFieldValue = (StringFieldValue) searchField.getValue();
                                    if (stringFieldValue.getValue() instanceof List<?>) {
                                        List<String> values = stringFieldValue.getValue() != null
                                                ? (ArrayList<String>) stringFieldValue.getValue()
                                                : new ArrayList<>();
                                        if (!values.contains(loggerConfigDTO.getLevel().toString())) {
                                            return false;
                                        }
                                    }
                                }
                                break;
                            }
                            case "text": {
                                String value = null;
                                if (searchField.getValue() instanceof TextSearchValue) {
                                    value = ((TextSearchValue) searchField.getValue()).getValue();
                                }
                                String loggerName = loggerConfigDTO.getDisplayName() == null ? loggerConfigDTO.getName()
                                        : loggerConfigDTO.getDisplayName();
                                if (loggerName != null && !StringUtils.isEmpty(value)
                                        && !loggerName.toUpperCase().contains(value.toUpperCase())) {
                                    return false;
                                }
                                break;
                            }
                            default:
                                LOGGER.info("Incorrect field: {}", searchField.getField());
                        }
                    }
                }
                return true;
            }).collect(Collectors.toList());
        }
        return loggerConfigs;
    }

    public boolean isValid(String config) {
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);

            documentBuilderFactory.newDocumentBuilder()
                    .parse(new ByteArrayInputStream(config.getBytes()));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
