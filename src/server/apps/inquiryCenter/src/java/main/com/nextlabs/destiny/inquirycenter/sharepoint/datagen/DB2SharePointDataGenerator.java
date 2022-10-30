package com.nextlabs.destiny.inquirycenter.sharepoint.datagen;

import net.sf.hibernate.Session;

public class DB2SharePointDataGenerator
        extends AbstractSharePointDataGenerator {

    /**
     * <p>
     * Constructor
     * </p>
     *
     * @param session
     */
    public DB2SharePointDataGenerator(Session session) {
        super(session);
    }

    @Override
    protected String getSelectColumnFormatting(String columnName) {
        if ("day_nb".equals(columnName)) {
            return " to_char(to_date('1970-01-01 00','yyyy-mm-dd hh24') + ( day_nb ) /1000/60/60/24 , 'DD-MON-YYYY') ";
        } else if ("month_nb".equals(columnName)) {
            return " to_char(to_date('1970-01-01 00','yyyy-mm-dd hh24') + ( month_nb ) /1000/60/60/24 , 'MON-YYYY') ";
        } else
            return columnName;
    }

}
