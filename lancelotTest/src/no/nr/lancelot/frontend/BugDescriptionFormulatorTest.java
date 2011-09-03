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
package no.nr.lancelot.frontend;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

import no.nr.lancelot.frontend.BugDescriptionFormulator;
import no.nr.lancelot.model.Attribute;

import org.junit.Test;

public class BugDescriptionFormulatorTest {
    @Test
    public void testConcat() {
        assertEquals("", BugDescriptionFormulator.concat(""));
        assertEquals("AA", BugDescriptionFormulator.concat("AA"));
        assertEquals("AABBCC", BugDescriptionFormulator.concat("AA", "BB", "CC"));
    }

    @Test
    @SuppressWarnings("serial")
    public void testConjugate() {
        assertEquals(
            "",
            BugDescriptionFormulator.conjugate(
                new ArrayList<String>(),
                "sep",
                "conj"
           )
        );
        
        assertEquals(
            "A",
            BugDescriptionFormulator.conjugate(
                new ArrayList<String>(){{
                    add("A");
                }},
                "sep",
                "conj"
           )
        );

        assertEquals(
            "AsepconjB",
            BugDescriptionFormulator.conjugate(
                new ArrayList<String>(){{
                    add("A"); add("B");
                }},
                "sep",
                "conj"
           )
        );
        
        assertEquals(
            "AsepBsepCsepconjD",
            BugDescriptionFormulator.conjugate(
                new ArrayList<String>(){{
                    add("A"); add("B"); add("C"); add("D");
                }},
                "sep",
                "conj"
           )
        );
    }
    
    @Test 
    public void knowsAllAttributes() {
        for (final Attribute attribute : Attribute.values())
            assertTrue(
                "BugDescriptionFormulator must know attribute " + attribute, 
                BugDescriptionFormulator.ATTRIBUTE_DESCRIPTIONS.containsKey(attribute)
            );
    }
}
