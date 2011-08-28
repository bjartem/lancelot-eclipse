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
package no.nr.einar.naming.rulebook;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import no.nr.lancelot.analysis.LancelotTestUtils;

import org.junit.BeforeClass;
import org.junit.Test;

public final class RulebookTest {
	private static final URL RULEBOOK_TEST_RULEBOOK_A_URL = 
		LancelotTestUtils.createUrl(
			LancelotTestUtils.TEST_RESOURCES_URL_PREFIX + "RulebookTest.java.rulebook_a.xml"
		);
	
	private static final long NULL_SEMANTICS = 0L;
	
	private static Rulebook rulebook = null;
	
	@BeforeClass
	public static void setup() throws Exception  {
		rulebook = new Rulebook(RULEBOOK_TEST_RULEBOOK_A_URL);
	}
    
	@Test
    public void testAllMethodsWithInsaneNamesMapToRoot() throws Exception {
		final String rootPhraseId = "ANY$*$ParameterRequirement.EVERYTHING";
		
		for (final Type returnType : Type.values()) {
			for (final Type paramType : Type.values()) {
		    	assertMapping(
		    		new MethodIdea(
			    		createMethodPhrase("shammalahammela", "noun"), 
			    		NULL_SEMANTICS, 
			    		returnType, 
			    		paramType
			    	),
			    	rootPhraseId
			    );
			}
		}
    }
	
	@Test
    public void testMappingB() throws Exception {
    	assertMapping(
    		new MethodIdea(
	    		createMethodPhrase("get", "verb", "name", "noun"), 
	    		NULL_SEMANTICS, 
	    		Type.BOOLEAN, 
	    		Type.INT
	    	),
	    	"ANY$get$name$ParameterRequirement.EVERYTHING"
	    );
    }
	
	@Test
    public void testMappingTopIntReference() throws Exception {
    	assertMapping(
    		new MethodIdea(
	    		createMethodPhrase("top", "adjective", "int", "type", "reference", "noun"), 
	    		NULL_SEMANTICS, 
	    		Type.INT, 
	    		Type.REFERENCE
	    	),
	    	"INT$top$int$reference$ParameterRequirement.TypeParameterRequirement[REFERENCE]"
	    );
    }
    
    private void assertMapping(final MethodIdea idea, final String expectedMatchingSignature) {
    	final Phrase actualMatchingPhrase = rulebook.find(idea);
    	final String actualMatchingSignature = actualMatchingPhrase.constructSignature();
    	assertEquals(expectedMatchingSignature, actualMatchingSignature);
    }
    
    private static MethodPhrase createMethodPhrase(final String... fragmentsAndTags) {
    	final List<String> fragments = new ArrayList<String>();
    	final List<String> tags = new ArrayList<String>();
    	
    	for (int i = 0; i < fragmentsAndTags.length; ++i)
    		(i%2 == 0 ? fragments : tags).add(fragmentsAndTags[i]);
    	
    	return new MethodPhrase(fragments, tags);
    }
}
