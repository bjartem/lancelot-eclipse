/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
package no.nr.einar.naming.jarmony.test;

import no.nr.einar.naming.jarmony.Xslt;
import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public final class XsltTest {
    
    @BeforeClass
    public static void initializeLancelot() throws Exception{
        LancelotTestUtils.loadDefaultConfiguration();
    }
        
    private static final String validXML = "<?xml version='1.0' encoding='UTF-8'?><root/>";
    private static final String invalidXML = "";
    private static final String notFile = "";
    private static final String xsltFile = "/xslt/jarmony.xslt";
    private static final String nonXsltFile = TestUtils.getFilePath("ant.jar");
    
    @Test(expected=NullPointerException.class)
    public void nullYieldsException() {
        new Xslt(null);
    }
    
    @Test(expected=RuntimeException.class)
    public void mustBeFile() {
        check(notFile, validXML);
    }
    
    @Test(expected=RuntimeException.class)
    public void mustBeXsltFile() {
        check(nonXsltFile, validXML);
    }

    @Test(expected=RuntimeException.class)
    public void invalidXML() {
        check(xsltFile, invalidXML);
    }
    
    @Test
    public void validXML() {
        check(xsltFile, validXML);
    }
    
    private void check(final String file, final String s) {
        final Xslt xslt = new Xslt(file);
        xslt.transform(s);
    }


}
