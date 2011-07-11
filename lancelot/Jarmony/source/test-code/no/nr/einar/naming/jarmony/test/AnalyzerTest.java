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

import java.io.IOException;

import no.nr.einar.naming.jarmony.Analyzer;
import no.nr.einar.naming.jarmony.Configuration;
import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public final class AnalyzerTest {
	
	@BeforeClass
	public static void initializeLancelot() throws Exception{
		LancelotTestUtils.loadDefaultConfiguration();
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void configCannotBeNull() {
		new Analyzer(null);
	}
	
	@Test
	public void t() throws IOException {
		final String path = TestUtils.getDummyPath("dummy.jar");
		final Analyzer testObj = new Analyzer(new Configuration(path));
		testObj.injectFactory(new MockJFAFactory());
		testObj.analyze();
	}

}
