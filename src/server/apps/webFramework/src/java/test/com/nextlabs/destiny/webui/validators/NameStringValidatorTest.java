package com.nextlabs.destiny.webui.validators;

import javax.faces.validator.ValidatorException;

import junit.framework.TestCase;

/**
 * TODO description
 *
 * @author hchan
 * @date Apr 19, 2007
 */
public class NameStringValidatorTest extends TestCase {
	private NameStringValidator validator = new NameStringValidator();
		
	public void testLetter(){
		validator.validate(null, null, "hi");
	}
	
	public void testNumber(){
		validator.validate(null, null, "123");
	}
	
	public void testLetterNumber(){
		validator.validate(null, null, "hi123");
	}
	
	public void testLetterNumberSpace(){
		validator.validate(null, null, "hi 123");
	}
	
	public void testSpecialChar(){
		validator.validate(null, null, "# % ^ - _ + = ~ : \" < > | { } [ ] .");
	}
	
	
	public void testInvalidChar(){
		try {
			validator.validate(null, null, "@");
			fail();
		} catch (ValidatorException e) {
			assertNotNull(e);
		}
	}
	
	public void testInvalidCharUpperASCII(){
		try {
			validator.validate(null, null, "\u00F6");
			fail();
		} catch (ValidatorException e) {
			assertNotNull(e);
		}
	}
	
	public void testSpecailCharFormat(){
		validator.validate(null, null, "\u0050");
	}
	
	public void testLetterInvalidChar(){
		try {
			validator.validate(null, null, "abc @def3");
			fail();
		} catch (ValidatorException e) {
			assertNotNull(e);
		}
	}
	
	public void testInvalidChar3(){
		try {
			validator.validate(null, null, "(hello)");
			fail();
		} catch (ValidatorException e) {
			assertNotNull(e);
		}
	}
	
	public void testGreyArea(){
		validator.validate(null, null, "        ");
	}
	
	public void testGreyArea2(){
		try {
			validator.validate(null, null, "\n");
			fail();
		} catch (ValidatorException e) {
			assertNotNull(e);
		}
	}
}
