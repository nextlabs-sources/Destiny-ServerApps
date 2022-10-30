/*
 * Created on Apr 17, 2008
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2008 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.defaultimpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.logging.Log;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.EngineConstants;
import org.eclipse.birt.report.engine.api.HTMLRenderOption;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.ReportEngine;

import com.bluejungle.destiny.container.dcc.ServerRelativeFolders;
import com.bluejungle.destiny.inquirycenter.environment.InquiryCenterResourceLocators;
import com.bluejungle.framework.comp.ComponentManagerFactory;
import com.bluejungle.framework.comp.IComponentManager;
import com.bluejungle.framework.comp.IConfigurable;
import com.bluejungle.framework.comp.IConfiguration;
import com.bluejungle.framework.comp.IInitializable;
import com.bluejungle.framework.comp.ILogEnabled;
import com.bluejungle.framework.comp.IStartable;
import com.bluejungle.framework.comp.PropertyKey;
import com.bluejungle.framework.configuration.DestinyRepository;
import com.bluejungle.framework.datastore.hibernate.IHibernateRepository;
import com.bluejungle.framework.environment.webapp.WebAppResourceLocatorImpl;
import com.nextlabs.destiny.container.shared.inquirymgr.report.IReportValueConverter;
import com.nextlabs.destiny.container.shared.inquirymgr.report.impl.InquiryMgrReportValueConverter;
import com.nextlabs.destiny.inquirycenter.report.IReportGenerator;
import com.nextlabs.destiny.inquirycenter.report.birt.HTMLReportImageHandler;

/**
 * @author rlin
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/main/com/nextlabs/destiny/inquirycenter/report/defaultimpl/ReportGenerator.java#1 $
 */

public class ReportGenerator implements IReportGenerator, ILogEnabled,
                                        IInitializable, IConfigurable, IStartable {
    public static final PropertyKey<Long> REPORT_GENERATION_FREQUENCY_PROP_NAME = new PropertyKey<Long>("ReportGenerationFrequency");
    public static final long DEFAULT_REPORT_GENERATION_FREQUENCY_PROP_NAME = 60 * 60 * 1000; // 1
    // hour

    public static final PropertyKey<String> REPORT_OUTPUT_LOCATION = new PropertyKey<String>("ReportOutputLocation");
    public static final String DEFAULT_REPORT_OUTPUT_LOCATION = ServerRelativeFolders.REPORTS_FOLDER.getPath();

    private IReportValueConverter reportValueConverter;
    private Timer timeoutTimer;
    private IConfiguration configuration;
    private Log log;
    private EngineConfig engineConfig;
    private IReportEngine engine;
    private IRunAndRenderTask task;
    private String reportOutputDir;

    @SuppressWarnings("unchecked")
        public void generateReport() {
        long startTime = System.currentTimeMillis();
        IComponentManager compMgr = ComponentManagerFactory
                                    .getComponentManager();
        IHibernateRepository activityDataSrc = (IHibernateRepository) compMgr
                                               .getComponent(DestinyRepository.ACTIVITY_REPOSITORY.getName());
        if (activityDataSrc == null) {
            throw new RuntimeException("Data source "
                                       + DestinyRepository.ACTIVITY_REPOSITORY
                                       + " is not correctly setup for the DABS component.");
        }
  
        Session session = null;
        Connection testConn = null;
        try {

            // generate the report
            Platform.startup(engineConfig);
            IReportEngineFactory factory = (IReportEngineFactory) Platform
                                           .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
            engine = factory.createReportEngine(engineConfig);

            IComponentManager componentManager = ComponentManagerFactory
                                                 .getComponentManager();
            WebAppResourceLocatorImpl resourceLocator = (WebAppResourceLocatorImpl) componentManager
                                                        .getComponent(InquiryCenterResourceLocators.WEB_APP_RESOURCE_LOCATOR_COMP_NAME);
            String saveFileName = reportOutputDir + "/dashboard-temp.html";

            final IReportRunnable design = engine.openReportDesign(resourceLocator.getResourceAsStream("/content/birt/dashboard.rptdesign"));

            session = activityDataSrc.getSession();
            testConn = session.connection();

            prepareData(testConn);

            task = engine.createRunAndRenderTask(design);

            task.getAppContext().put("com.nextlabs.destiny.inquirycenter.report.birt.ReportOdaJdbcDriver", testConn);
            task.setParameterValue("TrendFolder", reportOutputDir);

            HTMLRenderOption options = new HTMLRenderOption();
            options.setOutputFileName(saveFileName);
            options.setOutputFormat("html");
            options.setImageDirectory(reportOutputDir);
            options.setHtmlPagination(false);
            options.setImageHandler(new HTMLReportImageHandler());
            options.setEmbeddable(false);

            task.setRenderOption(options);
            task.run();

            // replace the original report with the newly generated one
            if(!new File(reportOutputDir + "/dashboard.html").delete()) {
                getLog().warn("Unable to delete /dashboard.html.");
            }
            if(!new File(saveFileName).renameTo(new File(reportOutputDir + "/dashboard.html"))) {
                getLog().warn(("Unable to rename " + saveFileName + " to /dashboard.html."));
            }
        } catch (BirtException e) {
            getLog().error(e);
        } catch (HibernateException e) {
            getLog().error(e);
        } catch (RuntimeException e) {
            getLog().error(e);
        } finally {
            if (testConn != null) {
                try {
                    testConn.close();
                    if (!testConn.isClosed()) {
                        getLog().error("connection used for generating dashboard report not closed properly");
                    } else {
                        getLog().info("connection used for generating dashboard report closed properly");
                    }
                } catch (SQLException e) {
                    getLog().error(e);
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (HibernateException e) {
                    getLog().error(e);
                }
            }
            if (task != null) {
                task.close();
            }
            if (engine != null) {
                engine.destroy();
            }
            task = null;
            engine = null;
        }

        getLog().info(
            "report generated at " + (new Date()).toString() + ", used "
        + (System.currentTimeMillis() - startTime) / 1000
        + " seconds");
    }

    @SuppressWarnings("unchecked")
        public void init() {
        // initialize the output directory of the generated reports
        reportOutputDir = configuration.get(REPORT_OUTPUT_LOCATION,
                                            DEFAULT_REPORT_OUTPUT_LOCATION);
        File reportDir = new File(reportOutputDir);
        if (!reportDir.exists()) {
            reportDir.mkdir();
        }

        engineConfig = new EngineConfig();
        engineConfig.setEngineHome("/WEB-INF");
        HashMap hm = engineConfig.getAppContext();

        // set class loader
        // this is necessary so that InquiryCenterConfigPlugin.class is read
        // from WEB-INF/classes
        hm.put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
               ReportEngine.class.getClassLoader());

        engineConfig.setAppContext(hm);

        engine = null;
        task = null;

        long timerInterval = configuration.get(
            REPORT_GENERATION_FREQUENCY_PROP_NAME,
        DEFAULT_REPORT_GENERATION_FREQUENCY_PROP_NAME);

        timeoutTimer = new Timer("ReportGenerator");
        timeoutTimer.scheduleAtFixedRate(new ReportGenerationTask(),
                                         new Date(), timerInterval);
  
        reportValueConverter = new InquiryMgrReportValueConverter();
    }

    public Log getLog() {
        return log;
    }

    public void setLog(Log log) {
        this.log = log;
    }

    public IConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(IConfiguration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        // do nothing
    }

    public void stop() {
        timeoutTimer.cancel();
        engineConfig = null;
        if (task != null) {
            task = null;
        }
        if (engine != null) {
            engine.destroy();
        }
    }

    private class ReportGenerationTask extends TimerTask {
        public void run() {
            generateReport();
        }
    }

    private class Trend {
        private Date day;
        private int quantity;

        public Date getDay() {
            return day;
        }

        public void setDay(Date day) {
            this.day = day;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }

    public void prepareData(Connection connection) {
        List<Trend> result = new ArrayList<Trend>();

        ResultSet resultSet = null;
        Calendar today = Calendar.getInstance();
        Calendar monthAgo = Calendar.getInstance();
        monthAgo.set(Calendar.HOUR_OF_DAY, 0);
        monthAgo.set(Calendar.MINUTE, 0);
        monthAgo.set(Calendar.SECOND, 0);
        monthAgo.set(Calendar.MILLISECOND, 0);
        monthAgo.add(Calendar.DAY_OF_MONTH, -30);
        Calendar weekAgo = Calendar.getInstance();
        weekAgo.set(Calendar.HOUR_OF_DAY, 0);
        weekAgo.set(Calendar.MINUTE, 0);
        weekAgo.set(Calendar.SECOND, 0);
        weekAgo.set(Calendar.MILLISECOND, 0);
        weekAgo.add(Calendar.DAY_OF_MONTH, -7);
        Timestamp todayTimestamp = new Timestamp(today.getTimeInMillis());
        Timestamp monthAgoTimestamp = new Timestamp(monthAgo.getTimeInMillis());
        Timestamp weekAgoTimestamp = new Timestamp(weekAgo.getTimeInMillis());

        for (int i = 0; i < 30; i++) {
            Calendar time = Calendar.getInstance();
            time.setTimeInMillis(today.getTimeInMillis());
            time.set(Calendar.HOUR_OF_DAY, 0);
            time.set(Calendar.MINUTE, 0);
            time.set(Calendar.SECOND, 0);
            time.set(Calendar.MILLISECOND, 0);
            time.add(Calendar.DAY_OF_MONTH, -i);
            Trend trend = new Trend();
            trend.setDay(time.getTime());
            trend.setQuantity(0);
            try {
                Statement stmt = connection.createStatement(
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
                resultSet = stmt
                            .executeQuery("SELECT count(*) FROM report_policy_activity_log WHERE day_nb = "
                                          + Long.toString(time.getTimeInMillis())
                                          + " AND policy_decision = 'D'");
                if (resultSet.next()) {
                    trend.setQuantity(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                getLog().error(e);
            }
            result.add(trend);
        }

        String filename = reportOutputDir + "/trend.csv";
        try {
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Day,Quantity\n");
            DateFormat formatter = new SimpleDateFormat();
            for (int i = 0; i < 30; i++) {
                Trend trend = result.get(i);
                StringBuffer line = new StringBuffer()
                                    .append(formatter.format(trend.getDay()))
                                    .append(",")
                                    .append(trend.getQuantity())
                                    .append("\n");
                out.write(line.toString());
            }
            out.close();
        } catch (IOException e) {
            getLog().error(e);
        }

        String sql = "SELECT policy_name, count(*) FROM report_policy_activity_log WHERE policy_decision = 'D' AND time >= ? AND time <= ? GROUP BY policy_name ORDER BY Count(*) DESC";
        try {
            PreparedStatement stmt = connection.prepareStatement(
                sql, 
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(5);
            stmt.setTimestamp(1, monthAgoTimestamp);
            stmt.setTimestamp(2, todayTimestamp);
            resultSet = stmt.executeQuery();
            filename = reportOutputDir + "/top5denypolicies.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Name,Quantity\n");
            for (int i = 0; i < 5; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(csvSafeString(resultSet.getString(1)))
                                        .append(",")
                                        .append(resultSet.getInt(2))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }

        sql = "SELECT user_name , count(*) FROM report_policy_activity_log WHERE policy_decision = 'D' AND time >= ? AND time <= ? GROUP BY user_name ORDER BY count(*) DESC";
        try {
            PreparedStatement stmt = connection.prepareStatement(
                sql, 
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(10);
            stmt.setTimestamp(1, monthAgoTimestamp);
            stmt.setTimestamp(2, todayTimestamp);
            resultSet = stmt.executeQuery();
            filename = reportOutputDir + "/top10denyusers.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Name,Quantity\n");
            for (int i = 0; i < 10; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(csvSafeString(resultSet.getString(1)))
                                        .append(",")
                                        .append(resultSet.getInt(2))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }

        sql = "SELECT from_resource_name, count(*) FROM report_policy_activity_log WHERE policy_decision = 'D' AND from_resource_name <> '[no attachment]' AND time >= ? AND time <= ? GROUP BY from_resource_name ORDER BY count(*) DESC";
        try {
            PreparedStatement stmt = connection.prepareStatement(
                sql, 
            ResultSet.TYPE_SCROLL_SENSITIVE,
            ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(5);
            stmt.setTimestamp(1, weekAgoTimestamp);
            stmt.setTimestamp(2, todayTimestamp);
            resultSet = stmt.executeQuery();
            filename = reportOutputDir + "/top5denyresources.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Name,Quantity\n");
            for (int i = 0; i < 5; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(csvSafeString(getResourceName(resultSet.getString(1))))
                                        .append(",")
                                        .append(resultSet.getInt(2))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }

        sql = "SELECT from_resource_name, count(*) FROM report_policy_activity_log WHERE policy_decision = 'A' AND from_resource_name <> '[no attachment]' AND time >= ? AND time <= ? GROUP BY from_resource_name ORDER BY count(*) DESC";
        try {
            PreparedStatement stmt = connection
                                     .prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE,
                                                       ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(5);
            stmt.setTimestamp(1, weekAgoTimestamp);
            stmt.setTimestamp(2, todayTimestamp);
            resultSet = stmt.executeQuery();
            filename = reportOutputDir + "/top5allowresources.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Name,Quantity\n");
            for (int i = 0; i < 5; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(csvSafeString(getResourceName(resultSet.getString(1))))
                                        .append(",")
                                        .append(resultSet.getInt(2))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }

        sql = "SELECT time, policy_name, from_resource_name, action, user_name FROM report_policy_activity_log WHERE policy_decision = 'D' ORDER BY time DESC";
        try {
            Statement stmt = connection
                             .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                              ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(10);
            resultSet = stmt.executeQuery(sql);
            filename = reportOutputDir + "/last10deny.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Date,Policy,Resource,Action,User\n");
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            for (int i = 0; i < 10; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(formatter.format(resultSet.getTimestamp(1)))
                                        .append(",")
                                        .append(csvSafeString(resultSet.getString(2)))
                                        .append(",")
                                        .append(csvSafeString(getResourceName(resultSet.getString(3))))
                                        .append(",")
                                        .append(csvSafeString(getFullActionName(resultSet.getString(4))))
                                        .append(",")
                                        .append(csvSafeString(resultSet.getString(5)))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }

        sql = "SELECT time, policy_name, from_resource_name, action, user_name FROM report_policy_activity_log WHERE policy_decision = 'A' ORDER BY time DESC";
        try {
            Statement stmt = connection
                             .createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                                              ResultSet.CONCUR_READ_ONLY);
            stmt.setMaxRows(10);
            resultSet = stmt.executeQuery(sql);
            filename = reportOutputDir + "/last10allow.csv";
            File file = new File(filename);
            if (file.exists()) {
                if(!file.delete()) {
                    getLog().warn("Unable to delete file " + filename + ", this file will be overwritten.");
                }
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            out.write("Date,Policy,Resource,Action,User\n");
            DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
            for (int i = 0; i < 10; i++) {
                if (resultSet.next()) {
                    StringBuffer line = new StringBuffer()
                                        .append(formatter.format(resultSet.getTimestamp(1)))
                                        .append(",")
                                        .append(csvSafeString(resultSet.getString(2)))
                                        .append(",")
                                        .append(csvSafeString(getResourceName(resultSet.getString(3))))
                                        .append(",")
                                        .append(csvSafeString(getFullActionName(resultSet.getString(4))))
                                        .append(",")
                                        .append(csvSafeString(resultSet.getString(5)))
                                        .append("\n");
                    out.write(line.toString());
                }
            }
            out.close();
        } catch (SQLException e) {
            getLog().error(e);
        } catch (IOException e) {
            getLog().error(e);
        }
    }

    /**
     * @param csvValue
     * @return
     */
    private String csvSafeString(String csvValue) {
        // If the value contains double quote, or comma, we want to wrap with double quotes
        boolean containsDoubleQuote = csvValue.indexOf('"') != -1;
        boolean containsComma = csvValue.indexOf(',') != -1;
        if (containsDoubleQuote || containsComma) {
            if (containsDoubleQuote) {
                csvValue = csvValue.replaceAll("\"", "\"\"");
            }
            return "\"" + csvValue + "\"";
        } else
            return csvValue;
    }

    private String getResourceName(String name) {
        if (!name.endsWith("/")) {
            int index = name.lastIndexOf('/');
            if (index != -1) {
                return name.substring(index + 1);
            }
        }
        return name;
    }
 
    private String getFullActionName(String abbrev){
        return reportValueConverter.getActionDisplayName(abbrev);
    }
}
