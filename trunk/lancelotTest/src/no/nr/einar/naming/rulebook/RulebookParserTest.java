package no.nr.einar.naming.rulebook;

import static no.nr.lancelot.frontend.LancelotTestUtils.TEST_RESOURCES_URL_PREFIX;
import static no.nr.lancelot.frontend.LancelotTestUtils.createURI;
import static no.nr.lancelot.model.Attribute.PARAMETER_TYPE_IN_NAME;
import static no.nr.lancelot.model.Attribute.RETURNS_STRING;
import static no.nr.lancelot.model.Attribute.RETURN_TYPE_IN_NAME;
import static no.nr.lancelot.rulebook.Severity.FORBIDDEN;
import static no.nr.lancelot.rulebook.Severity.INAPPROPRIATE;
import static no.nr.lancelot.rulebook.Severity.NOTIFY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.List;

import no.nr.lancelot.rulebook.Phrase;
import no.nr.lancelot.rulebook.Rule;
import no.nr.lancelot.rulebook.RulebookParser;

import org.junit.Test;

public class RulebookParserTest {
    private static final URL RULEBOOK_PARSER_TEST_RULEBOOK_URL = createURI(
        TEST_RESOURCES_URL_PREFIX + "RulebookParserTest.java.rulebook.xml"
    );
    
    @Test
    public void testParse() throws Exception {
        final Object[][] expectedResults = {
            { 
                "accept-*", 
                new Rule[]{
                    new Rule(RETURNS_STRING, INAPPROPRIATE, true),
                    new Rule(RETURN_TYPE_IN_NAME, FORBIDDEN, false)
                }
            },
            {
                "int accept-[verb]-[adjective]-*(reference...)",
                new Rule[]{
                    new Rule(PARAMETER_TYPE_IN_NAME, NOTIFY, true)  
                }
            },
            {
                "void accept-[verb]-[adjective]-*())",
                new Rule[]{
                }
            }
        };
        
        final List<Phrase> actualParsedPhrases = 
                new RulebookParser(RULEBOOK_PARSER_TEST_RULEBOOK_URL).getParsedPhrases();
        
        assertEquals(expectedResults.length, actualParsedPhrases.size());

        for (Object[] data : expectedResults) {
            final String text = (String) data[0];
            final Rule[] rules = (Rule[]) data[1];
            
            Phrase actualPhrase = null;
            for (Phrase p : actualParsedPhrases) {
                if (p.getPhraseText().equals(text)) {
                    actualPhrase = p; 
                    break; 
                }
            }
            
            assertNotNull(actualPhrase);
            assertEquals(rules.length, actualPhrase.getRules().size());
            
            for (final Rule r : rules) {
                assertTrue(actualPhrase.getRules().contains(r));
            }
        }
    }
}
