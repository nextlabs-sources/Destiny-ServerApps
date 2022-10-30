/**
 * 
 */
package com.bluejungle.destiny.webui.browsabledatapicker.converters;

import com.bluejungle.destiny.webui.converters.TokenConverter;

import junit.framework.TestCase;

/**
 * TODO description
 *
 * @author hchan
 * @date Apr 3, 2007
 */
public class TokenConverterTest extends TestCase{
	public void test(){
		TokenConverter tc = new TokenConverter();
		String result = tc.getAsString(null, null, "   ");
		assertEquals("", result);
		
		result = tc.getAsString(null, null, "  ");
		assertEquals("", result);
		
		result = tc.getAsString(null, null, " a    b  ");
		assertEquals("a b", result);
		
		result = tc.getAsString(null, null, " a   b           ");
		assertEquals("a b", result);
	}
}
