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

import static org.junit.Assert.*;
import no.nr.einar.naming.jarmony.Configuration;
import no.nr.einar.naming.jarmony.Configuration.OutputFileFormat;
import no.nr.einar.naming.jarmony.Configuration.OutputTarget;
import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

import static no.nr.einar.naming.jarmony.Configuration.OutputTarget.*;
import static no.nr.einar.naming.jarmony.Configuration.OutputFileFormat.*;

public final class ConfigurationTest {
    
    @BeforeClass
    public static void initializeLancelot() throws Exception{
        LancelotTestUtils.loadDefaultConfiguration();
    }
    
    private static final String JAR_FILE = "foo.jar";
    private static final String REPORT = "foo-jarmony-report.html";
    private static final String XSLT = Configuration.DEFAULT_XSLT_FILE;
    
    private Configuration testObj = new Configuration(JAR_FILE);
    
    @Test
    public void defaultConfiguration() {
        check(File, HTML, REPORT, XSLT);
    }
    
    @Test
    public void overrideOutputTarget() {
        testObj.setOutputTarget(Console);
        check(Console, HTML, REPORT, XSLT);
    }
    
    @Test
    public void overrideOutputFile() {
        final String outFile = "report.html";
        testObj.setOutputFile(outFile);
        check(File, HTML, outFile, XSLT);
    }
    
    @Test
    public void overrideOutputFormat() {
        testObj.setOutputFileFormat(XML);
        check(File, XML, "foo-jarmony-report.xml", XSLT);
    }
    
    @Test
    public void overrideXsltFile() {
        final String xsltFile = "fancy.xslt";
        testObj.setXsltFile(xsltFile);
        check(File, HTML, REPORT, xsltFile);
    }
    
    @Test
    public void fileNameIndependentOfFormat() {
        final String outFile = "report.html";
        testObj.setOutputFile(outFile);
        testObj.setOutputFileFormat(XML);
        check(File, XML, outFile, XSLT);
    }
    
    private void check(final OutputTarget expectedTarget,
            final OutputFileFormat expectedFormat,
            final String expectedOutputFile,
            final String expectedXsltFile) {
        assertEquals(JAR_FILE, testObj.getInputFile());
        assertEquals(expectedTarget, testObj.getOutputTarget());
        assertEquals(expectedFormat, testObj.getOutputFileFormat());
        assertEquals(expectedOutputFile, testObj.getOutputFile());
        assertEquals(expectedXsltFile, testObj.getXsltFile());
    }

}
