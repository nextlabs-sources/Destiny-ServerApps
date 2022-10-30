package com.nextlabs.destiny.cc.installer.services;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Perform Control Center uninstallation.
 *
 * @author Sachindra Dasun
 */
public interface UninstallService {

    void uninstall() throws InterruptedException, IOException, InvocationTargetException, IllegalAccessException;

}
