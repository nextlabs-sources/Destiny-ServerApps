/*
 *  All sources, binaries and HTML pages (C) copyright 2004-2009 by NextLabs, Inc.,
 * San Mateo CA, Ownership remains with NextLabs, Inc., All rights reserved
 * worldwide.
 */
package com.nextlabs.destiny.inquirycenter.report.birt.datagen;

public class ObligationLogData {

    String name;
    String attributeOne;
    String attributeTwo;
    String attributeThree;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttributeOne() {
        return attributeOne;
    }

    public void setAttributeOne(String attributeOne) {
        this.attributeOne = attributeOne;
    }

    public String getAttributeTwo() {
        return attributeTwo;
    }

    public void setAttributeTwo(String attributeTwo) {
        this.attributeTwo = attributeTwo;
    }

    public String getAttributeThree() {
        return attributeThree;
    }

    public void setAttributeThree(String attributeThree) {
        this.attributeThree = attributeThree;
    }

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ObligationLogData [name=");
		builder.append(name);
		builder.append(", attributeOne=");
		builder.append(attributeOne);
		builder.append(", attributeTwo=");
		builder.append(attributeTwo);
		builder.append(", attributeThree=");
		builder.append(attributeThree);
		builder.append("]");
		return builder.toString();
	}
    
    
}
