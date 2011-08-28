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
package no.nr.einar.naming.tagging.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import no.nr.einar.naming.tagging.JavaTagger;
import no.nr.einar.naming.tagging.PosTagger;
import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public final class JavaTaggerTest {
    
    private PosTagger testObj;
    
    @BeforeClass
    public static void initializeLancelot() throws Exception{
        LancelotTestUtils.loadProductionConfiguration();
    }
    
    @Before
    public void setUp() {
        testObj = new JavaTagger();
    }
    
    @Test
    public void t() {
        check(new String[] { "get", "last", "name" },  new String[] { "verb", "adjective", "noun" });
    }
    
    @Test
    public void t2() {
        check(new String[] { "is", "default", "scheme" }, new String[] { "verb", "adjective", "noun" });
    }
    
    @Test
    public void t3() {
        check(new String[] { "key", "pressed" }, new String[] { "noun", "adjective" });
    }
    
    @Test
    public void t4() {
        check(new String[] { "action", "performed" }, new String[] { "noun", "verb" });
    }
    
    @Test
    public void inventedadjective0() {
        check(new String[] { "is", "queryable" }, new String[] { "verb", "adjective" });
    }
    
    @Test
    public void notAnInventedadjective() {
        check(new String[] { "is", "table" }, new String[] { "verb", "noun" });
    }

    @Test
    public void inventedadjectiveWithdoubleT() {
        check(new String[] { "is", "committable" }, new String[] { "verb", "adjective" });
    }
    
    @Test
    public void adjectiveButNotInvented() {
        check(new String[] { "is", "able" }, new String[] { "verb", "adjective" });
    }

    @Test
    public void geekLingo() {
        check(new String[] { "accum", "attribs" }, new String[] { "verb", "noun" });
    }

    @Test
    public void moreGeekLingo() {
        check(new String[] { "bootstrap", "multithread", "app" }, new String[] { "verb", "adjective", "noun" });
    }

    
    private void check(final String[] fragments, final String[] expectedTags) {
        assertEqualTags(expectedTags, testObj.tag(getFragments(fragments)));
    }
    
    private void assertEqualTags(final String[] expectedTags, final List<String> tags) {
        assertEquals(expectedTags.length, tags.size());
        for (int i = 0; i < expectedTags.length; i++) {
            final String expected = expectedTags[i];
            final String actual = tags.get(i);
            assertEquals("Failed at fragment " + i + "!", expected, actual);
        }
    }

    private static List<String> getFragments(final String ... parts) {
        return Arrays.asList(parts);
    }

}
