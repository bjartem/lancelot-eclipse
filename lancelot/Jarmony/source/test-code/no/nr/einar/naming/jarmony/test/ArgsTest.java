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

import java.util.NoSuchElementException;

import org.junit.BeforeClass;
import org.junit.Test;

import no.nr.einar.naming.jarmony.Args;
import no.nr.einar.naming.jarmony.Configuration;
import no.nr.einar.naming.jarmony.Configuration.OutputTarget;
import no.nr.einar.naming.jarmony.Configuration.OutputFileFormat;
import no.nr.lancelot.analysis.LancelotTestUtils;

public final class ArgsTest {
	
	@BeforeClass
	public static void initializeLancelot() throws Exception{
		LancelotTestUtils.loadDefaultConfiguration();
	}
	
	private static final String JAR_FILE = "asm-tree-2.2.3.jar";
	private static final String JAR_PATH = getValidFile();
	
	private Args testObj;
	
	@Test(expected=IllegalArgumentException.class)
	public void argsCannotBeNull() {
		new Args(null);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void argsCannotBeEmpty() {
		create();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void t() {
		create("foobar");
	}
	
	@Test
	public void firstArgCanBeFile() {
		testObj = create(JAR_PATH);
		final Configuration $ = getConfiguration();
		assertEquals(JAR_PATH, $.getInputFile());
	}
	
	@Test(expected=NoSuchElementException.class)
	public void oneConfigurationOnlyForSingleFile() {
		testObj = create(JAR_PATH);
		getConfiguration();
		getConfiguration();
	}
	
	private Configuration getConfiguration() {
		return testObj.iterator().next();
	}

	@Test
	public void overrideTarget() {
		overrideTarget("-t");
	}
	
	@Test
	public void overrideTarget2() {
		overrideTarget("-target");
	}
	
	private void overrideTarget(final String option) {
		final OutputTarget target = OutputTarget.Console;
		final Configuration $ = check(option, target.name());
		assertEquals(target, $.getOutputTarget());
	}
	
	@Test
	public void overrideFormat() {
		overrideFormat("-f");
	}
	
	@Test
	public void overrideFormat2() {
		overrideFormat("-format");
	}
	
	private void overrideFormat(final String option) {
		final OutputFileFormat format = OutputFileFormat.XML;
		final Configuration $ = check(option, format.name());
		assertEquals(format, $.getOutputFileFormat());
	}
	
	@Test
	public void overrideOutputFile() {
		overrideOutputFile("-o");
	}
	
	@Test
	public void overrideOutputFile2() {
		overrideOutputFile("-outfile");
	}
	
	private void overrideOutputFile(final String option) {
		final String outFile = "my-report.html";
		final Configuration $ = check(option, outFile);
		assertEquals(outFile, $.getOutputFile());
	}
	
	@Test
	public void overrideXsltFile() {
		overrideXsltFile("-x");
	}

	@Test
	public void overrideXsltFile2() {
		overrideXsltFile("-xslt");
	}
	
	private void overrideXsltFile(final String option) {
		final String xsltFile = "my-transform.xslt";
		final Configuration $ = check(option, xsltFile);
		assertEquals(xsltFile, $.getXsltFile());
	}
	
	private Configuration check(final String option, final String value) {
		testObj = create(JAR_PATH, option, value);
		final Configuration $ = getConfiguration();
		assertEquals(JAR_PATH, $.getInputFile());
		return $;
	}
	
	@Test
	public void firstArgCanBeFolder() {
		create(getValidFolder());
	}
	
	@Test
	public void multipleConfigurationOnlyForFolder() {
		testObj = create(getValidFolder());
		getConfiguration();
		getConfiguration();
		getConfiguration();
	}

	private Args create(final String ... args) {
		return new Args(args);
	}
	
	private static String getValidFile() {
		return TestUtils.getFilePath(JAR_FILE);
	}
	
	private String getValidFolder() {
		return TestUtils.getFolderPath();
	}

}
