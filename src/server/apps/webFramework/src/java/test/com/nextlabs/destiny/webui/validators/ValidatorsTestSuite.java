package com.nextlabs.destiny.webui.validators;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * tests the validators
 *
 * @author hchan
 * @date Apr 19, 2007
 */
public class ValidatorsTestSuite {
	public static void main(String[] args) {
        junit.textui.TestRunner.run(ValidatorsTestSuite.suite());
    }

    public static Test suite() {
        TestSuite suite = new TestSuite("Test for com.nextlabs.destiny.webui.validators");
        //$JUnit-BEGIN$
        suite.addTestSuite(NameStringValidatorTest.class);
        //$JUnit-END$
        return suite;
    }
}
