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
package no.nr.lancelot.analysis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import no.nr.lancelot.analysis.SemanticsMap.AttributeFlagFinder;
import no.nr.lancelot.analysis.SemanticsMap.SemanticsMapInitException;

import org.junit.Test;

public class SemanticsMapTest {

    @Test
    public final void testSemanticsMap() {
//        fail("Not yet implemented");
    }

    @Test
    public final void testFindSuggestionsFor() {
//        fail("Not yet implemented");
    }

    @Test
    public final void testParseAttributes() throws Exception {
        try {
            final long[] actualParse = SemanticsMap.parseAttributes(
                new BufferedReader(new FileReader(createMockFile(
                    "##ATTRIBUTES##",
                    "BBB", // Bit 0 
                    "AAA", // Bit 1
                    "CCC"  // Bit 2
                ))),
                new AttributeFlagFinder() {
                    @Override
                    public long translate(final String attributeName) {
                        if (attributeName.equals("AAA")) {
                            return 1 << 4;
                        } else if (attributeName.equals("BBB")) {
                            return 1 << 6;
                        } else if (attributeName.equals("CCC")) {
                            return 1 << 8;
                        } else {
                            throw new RuntimeException();
                        }
                    }
                    
                    @Override
                    public int getAttributeCount() {
                        return 3;
                    }
                }
            );
            
            assertEquals("BBB(bit0) should map to (1 << 6)", 1 << 6, actualParse[0]); 
            assertEquals("AAA(bit1) should map to (1 << 4)", 1 << 4, actualParse[1]);
            assertEquals("CCC(bit2) should map to (1 << 8)", 1 << 8, actualParse[2]);
        } catch (SemanticsMapInitException e) {
            fail(e.getMessage());
        } 
    }

    @Test
    public final void testConvertProfile() {
        final long[] attributeMapping = {
            1  << 5,   // original bit 0
            1  << 7,   // original bit 1
            1  << 12,  // original bit 2
            1  << 19,  // original bit 3
            1  << 22,  // original bit 4
            1L << 42,  // original bit 5
            1L << 60   // original bit 6
        };
        
        assertEquals(0, SemanticsMap.convertProfile(0, attributeMapping));
        
        assertEquals(
            1 << 22,        
            SemanticsMap.convertProfile(1 << 4, attributeMapping)
        );
        
        assertEquals(
          (1 << 5) | (1 << 7) | (1 << 19) | (1 << 22) | (1L << 42) | (1L << 60),        
          SemanticsMap.convertProfile(
              (1 << 0) | (1 << 1) | (1 << 3) | (1 << 4) | (1 << 5) | (1 << 6), 
              attributeMapping
          )
        );
    }
    
    private static final File createMockFile(final String... contents) throws Exception {
        final File tempFile = File.createTempFile("SemanticsMapTestMOCK", "nosuffix");
        final FileWriter fw = new FileWriter(tempFile);
        
        try {
            for (final String s : contents) {
                fw.write(s + '\n');
            }
        } finally {
            fw.close();
        }
        
        return tempFile;
    }
}
