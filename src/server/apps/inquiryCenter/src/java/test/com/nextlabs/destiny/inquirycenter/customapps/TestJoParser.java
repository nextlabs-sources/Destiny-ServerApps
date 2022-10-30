/*
 * Created on Mar 24, 2010
 * 
 * All sources, binaries and HTML pages (C) copyright 2004-2010 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.customapps;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;

import junit.framework.TestCase;

import com.nextlabs.destiny.container.shared.customapps.ExternalApplicationFileStructure;
import com.nextlabs.destiny.inquirycenter.customapps.mapping.CustomReportUIJO;

/**
 * @author hchan
 * @version $Id: //depot/Destiny/D_Nimbus/release/8.1.2/main/src/server/apps/inquiryCenter/src/java/test/com/nextlabs/destiny/inquirycenter/customapps/TestJoParser.java#1 $
 */

public class TestJoParser extends TestCase{
    static File testFolder;
    static File customAppsFolder;
    
    @Override
    protected void setUp() throws Exception {
        if(testFolder == null){
            String srcRootDir = System.getProperty("src.root.dir");
            if (srcRootDir == null) {
                throw new IllegalArgumentException("src.root.dir is required");
            }
            testFolder = new File(srcRootDir, "server/container/shared/test_files");
            customAppsFolder = new File(testFolder, "customApps");
        }
    }
    
    protected String readWhole(File file) throws IOException {
        StringWriter writer = new StringWriter();
        FileReader reader = new FileReader(file);
        try {
            int c;
            while ((c = reader.read()) != -1) {
                writer.write(c);
            }
        } finally {
            reader.close();
        }
        writer.close();
        return writer.toString();
    }

    public void testGoodFile() throws IOException{
        File app1Folder = new File(customAppsFolder, "app1");
        File uiConfigFile = new File(app1Folder, ExternalApplicationFileStructure.UI_CONFIG_XML);
        String content = readWhole(uiConfigFile);
        
        ExternalReportAppManager externalReportAppManager = new ExternalReportAppManager();
        CustomReportUIJO jo = externalReportAppManager.readUiConfig(content);
        System.out.println(jo);
        
    }
}
