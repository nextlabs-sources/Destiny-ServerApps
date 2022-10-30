/*
 * Copyright 2015 by Nextlabs Inc.
 *
 * All rights reserved worldwide.
 * Created on Apr 6, 2016
 *
 */
package com.nextlabs.destiny.console.services.impl;

import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.buildQuery;
import static com.nextlabs.destiny.console.utils.SearchCriteriaQueryBuilder.withSorts;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import com.nextlabs.destiny.console.config.properties.HelpUrlProperties;
import com.nextlabs.destiny.console.dto.common.HelpContent;
import com.nextlabs.destiny.console.dto.common.SearchCriteria;
import com.nextlabs.destiny.console.dto.common.SearchField;
import com.nextlabs.destiny.console.exceptions.ConsoleException;
import com.nextlabs.destiny.console.init.ConfigurationDataLoader;
import com.nextlabs.destiny.console.search.repositories.HelpContentSearchRepository;
import com.nextlabs.destiny.console.services.HelpContentSearchService;

/**
 *
 * Implementation of the {@link HelpContentSearchService}
 *
 *
 * @author Amila Silva
 * @since 8.0
 *
 */
@Service
public class HelpContentSearchServiceImpl implements HelpContentSearchService {

    private static final Logger log = LoggerFactory
            .getLogger(HelpContentSearchServiceImpl.class);

    private static final String GETTING_STARTED_FOLDER = "getting-started";

    @Resource
    private HelpContentSearchRepository helpContentSearchRepository;

    @Autowired
    private ConfigurationDataLoader configDataLoader;

    @Autowired
    private HelpUrlProperties helpUrlProperties;

    private int id = 0;

    @Override
    public void uploadHelpContent() throws ConsoleException {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("i18n/helptext_en.xlsx");
        if (inputStream != null) {
            helpContentSearchRepository.deleteAll();
            String langCode = "en";
            List<HelpContent> contents = readXlsxHelpFile(inputStream, langCode);
            helpContentSearchRepository.saveAll(contents);
        }
    }

    @Override
    public Page<HelpContent> findHelpByCriteria(SearchCriteria criteria)
            throws ConsoleException {
        try {

            log.debug("Help content search criteria :[{}]", criteria);
            PageRequest pageable = PageRequest.of(criteria.getPageNo(),
                    criteria.getPageSize());

            List<SearchField> searchFields = criteria.getFields();
            BoolQueryBuilder query = buildQuery(searchFields);

            Query searchQuery = new NativeSearchQueryBuilder()
                    .withQuery(query).withPageable(pageable).build();

            searchQuery = withSorts(searchQuery, criteria.getSortFields());

            log.debug("Help search query :{},", query.toString());
            Page<HelpContent> helpContent = helpContentSearchRepository
                    .search(searchQuery);

            log.debug("Help content page :{}, No of elements :{}", helpContent,
                    helpContent.getNumberOfElements());
            return helpContent;
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error encountered in find policies by given criteria", e);
        }
    }

    @Override
    public String getResourceUrl(String name) {
        if (GETTING_STARTED_FOLDER.equals(name)) {
            return String.format("%s/%s/%s.%s", helpUrlProperties.getBase(), name, "index",
                    helpUrlProperties.getContentFormat());
        } else {
            return String.format("%s.%s", helpUrlProperties.getConsole(), helpUrlProperties.getContentFormat());
        }
    }

    private List<HelpContent> readXlsxHelpFile(InputStream inputStream, String langCode)
            throws ConsoleException {
        List<HelpContent> helpContents = new ArrayList<>();

        try (XSSFWorkbook myWorkBook = new XSSFWorkbook(inputStream)) {
            XSSFSheet mySheet = myWorkBook.getSheetAt(0);

            Iterator<Row> rowIterator = mySheet.iterator();

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row == null)
                    continue;

                if (row.getRowNum() == 0)
                    continue;

                HelpContent helpContent = new HelpContent();
                id++;
                helpContent.setId(new Long(id));
                helpContent.setI18nLangCode(langCode);

                readCellValue(row, 0, helpContent);
                readCellValue(row, 1, helpContent);
                readCellValue(row, 2, helpContent);
                readCellValue(row, 3, helpContent);
                readCellValue(row, 4, helpContent);
                readCellValue(row, 5, helpContent);

                helpContents.add(helpContent);

            }
            myWorkBook.close();
        } catch (Exception e) {
            throw new ConsoleException(
                    "Error occured while reading the help file", e);
        }
        return helpContents;

    }

    private static void readCellValue(Row row, int cellIndex,
            HelpContent helpContent) {
        String cellValue = null;

        Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell != null) {
            cellValue = cell.getStringCellValue();

            switch (cellIndex) {
                case 0:
                    helpContent.setAppName(cellValue);
                    break;
                case 1:
                    helpContent.setModule(cellValue);
                    break;
                case 2:
                    helpContent.setSectionTitle(cellValue);
                    break;
                case 3:
                    helpContent.setSubSectionTitle(cellValue);
                    break;
                case 4:
                    helpContent.setField(cellValue);
                    break;
                case 5:
                    helpContent.setHelpText(cellValue);
                    break;
                default:

            }
        }
    }

}
