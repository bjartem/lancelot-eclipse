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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import no.nr.einar.naming.jarmony.Jarmony;

public final class JarmonyTest {
	
	private static final String JAR = "junit-4.2.jar";
	private static final String REPORT = "junit-4.2-jarmony-report";
	private static final String XML_REPORT = REPORT + ".xml";
	private static final String HTML_REPORT = REPORT + ".html";
	private static final String JAR_PATH = TestUtils.getFilePath(JAR);
	
	@After
	public void tearDown() {
		clearReports();
	}
	
	@Test
	public void htmlOutput() throws IOException {
		run(JAR_PATH);
		verify(HTML_REPORT);
	}
	
	@Test
	public void xmlOutput() throws IOException {
		run(JAR_PATH, "-f", "XML");
		verify(XML_REPORT);
	}
	
	@Test
	public void consoleOutput() throws IOException {
		final String capturedOutputFile = "captured.html";
		final String capturedOutputFilePath = TestUtils.getFilePath(capturedOutputFile);
		final PrintStream fileOut = new PrintStream(new FileOutputStream(capturedOutputFilePath));
		final PrintStream temp = System.out;
		System.setOut(fileOut);
		run(JAR_PATH, "-t", "Console");
		System.setOut(temp);
		fileOut.close();
		verify(HTML_REPORT, capturedOutputFile);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void argsCannotBeEmpty() {
		run();
	}

	@Test(expected=IllegalArgumentException.class)
	public void argsCannotHaveMoreThanOneElement() {
		run("foo", "bar");
	}

	@Test(expected=IllegalArgumentException.class)
	public void argsMustBeAValidPath() {
		run(" ");
	}
	
	private void verify(final String report) throws IOException {
		verify(report, report);
	}
	
	private void verify(final String expectedReport, final String actualReport) throws IOException {
		final String actualReportPath = TestUtils.getFilePath(actualReport);
		final String expectedReportPath = TestUtils.getExpectedOutputPath(expectedReport);
		final BufferedReader actual = open(actualReportPath);
		final BufferedReader expected = open(expectedReportPath);
		String actLine = null;
		String expLine = null;
		do {
			actLine = actual.readLine();
			expLine = expected.readLine();
			assertEquals(expLine, actLine);
		} while (actLine != null || expLine != null);
		actual.close();
		expected.close();
	}

	private BufferedReader open(final String path) throws FileNotFoundException {
		return new BufferedReader(new InputStreamReader(new FileInputStream(path)));
	}
	
	private void run(final String ... args) {
		try {
			Jarmony.main(args);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private void clearReports() {
		final File reportsFolder = new File(TestUtils.getFolderPath());
		final FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".xml") || name.endsWith(".html");
			}
		};
		for (final File f : reportsFolder.listFiles(filter)) {
			f.delete();
		}
	}

}
