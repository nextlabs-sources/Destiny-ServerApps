package com.nextlabs.destiny.cc.installer.services.impl;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Collections;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nextlabs.destiny.cc.installer.InstallerApplication;
import com.nextlabs.destiny.cc.installer.enums.Component;
import com.nextlabs.destiny.cc.installer.helpers.ParameterHelper;
import com.nextlabs.destiny.cc.installer.helpers.TestHelper;
import com.nextlabs.destiny.cc.installer.services.InstanceConfigurationService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InstallerApplication.class)
public class InstanceConfigurationServiceImplTest {

    @Autowired
    private InstanceConfigurationService instanceConfigurationService;

    @BeforeClass
    public static void init() throws IllegalAccessException, IOException, InvocationTargetException {
        TestHelper.init();
        ParameterHelper.setInitParameters();
    }

    @Test
    public void createServerXmlFile() throws IOException {
        instanceConfigurationService.createServerXmlFile("server.xml", Collections.singleton(Component.INSTALLER));
        assertTrue(Paths.get(System.getProperty("cc.home"), "server", "configuration", "server.xml")
                .toFile().exists());
    }

}