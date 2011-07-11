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

import java.net.URL;

public class TestUtils {
	
	private TestUtils() {}

	private URL getLocationOfThisClass() {
		return getClass().getResource(getClass().getSimpleName() + ".class");
	}

	private String getFolder() {
		final String path = getLocationOfThisClass().getFile();
		final String projectPath = path.substring(0, path.indexOf("/bin"));
		final String testDataPath = projectPath + "/Jarmony/test-data";
		return testDataPath;
	}
	
	public static String getFolderPath() {
		return getBasePath() + "/jar-files";
	}
	
	public static String getFilePath(final String fileName) {
		return getFolderPath() + "/" + fileName;
	}
	
	public static String getDummyPath() {
		return getBasePath() + "/dummy-files";
	}
	
	public static String getXsltPath() {
		return getBasePath() + "/../xslt/";
	}
	
	public static String getExpectedOutputPath() {
		return getBasePath() + "/expected-output-files";
	}
	
	public static String getExpectedOutputPath(final String fileName) {
		return getExpectedOutputPath() + "/" + fileName;
	}
	
	public static String getBasePath() { 
		return new TestUtils().getFolder();
	}
	
	public static String getDummyPath(final String fileName) {
		return getDummyPath() + "/" + fileName;
	}
}
