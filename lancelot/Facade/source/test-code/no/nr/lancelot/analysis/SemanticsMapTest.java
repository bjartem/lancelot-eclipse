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
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import no.nr.einar.naming.rulebook.MethodIdea;
import no.nr.einar.naming.rulebook.MethodPhrase;
import no.nr.einar.naming.rulebook.Phrase;
import no.nr.einar.naming.rulebook.PhraseBuilder;
import no.nr.einar.naming.rulebook.Rule;
import no.nr.einar.naming.rulebook.Type;

import org.junit.BeforeClass;
import org.junit.Test;

public class SemanticsMapTest {
	@BeforeClass
	public static void setupClass() throws Exception {
		LancelotTestUtils.loadProductionConfiguration();
	}

    @Test
    public final void testSemanticsMap() {
//        fail("Not yet implemented");
    }

    @Test
    public final void testFindSuggestionsFor() {
//        fail("Not yet implemented");
    }
    
    @Test
    public void testCreatePhraseA() {
    	assertEquals(
    		createMethodPhrase("thought", "noun", "dance", "verb", "hard", "adjective"),
    		SemanticsMap.createPhrase("thought-dance-hard")
    	);
    }
    
    private static final class PhraseMatchingTestData {
		final String capturingPhrase;
		final String reverseMapText;
		final Type returnType;
		final Type paramTypeOrNull;

		PhraseMatchingTestData(
			final String capturingPhrase, 
			final String reverseMapText,
			final Type returnType,
			final Type paramTypeOrNull
		) {
			this.capturingPhrase = capturingPhrase;
			this.reverseMapText = reverseMapText;
			this.returnType = returnType;
			this.paramTypeOrNull = paramTypeOrNull;
		}

		PhraseMatchingTestData(final String simplePhrase) {
			this(simplePhrase, simplePhrase, Type.VOID, Type.INT);
		}
    }
    
    @Test
    public void testPhraseMatching() {
    	final List<Phrase> MOCK_NO_REFINEMENTS = Collections.emptyList();
    	final Set<Rule> MOCK_NO_RULES = Collections.emptySet();
    	final long MOCK_NULL_SEMANTICS = 0L;

    	final PhraseMatchingTestData[] testDataSet = { 
            new PhraseMatchingTestData("[verb]-[noun]-foo-*-*-*-*"),
            
        	new PhraseMatchingTestData("add-[type]-[pronoun]-*-[adjective]"),
        		
    		new PhraseMatchingTestData( 
    			"void add-[adjective]-[noun]-*(reference...)", 
    			"add-[adjective]-[noun]-*",
    			Type.VOID,
    			Type.REFERENCE
    		),
    		
    		new PhraseMatchingTestData(
    			"Object create-[noun](String...)", 
    			"create-[noun]",
    			Type.OBJECT,
    			Type.STRING
    		)
    	};
    	
    	for (final PhraseMatchingTestData testData : testDataSet) {
			final Phrase capturingPhrase = PhraseBuilder.create(
				testData.capturingPhrase, 
				MOCK_NO_REFINEMENTS, 
				MOCK_NO_RULES
			);
    		final MethodPhrase synthesizedName = SemanticsMap.createPhrase(testData.reverseMapText);
			final MethodIdea synthesizedIdea = new MethodIdea(
    			synthesizedName, 
    			MOCK_NULL_SEMANTICS, 
    			testData.returnType, 
    			testData.paramTypeOrNull
    		);
    		
    		assertTrue(capturingPhrase.captures(synthesizedIdea));
    	}
    }
    
    private static MethodPhrase createMethodPhrase(final String... data) {
    	final List<String> fragments = new LinkedList<String>(),
    			           tags = new LinkedList<String>();
    	for (int i = 0; i < data.length; ++i)
    		(i%2 == 0 ? fragments : tags).add(data[i]);
    	return new MethodPhrase(fragments, tags);
    }
}
