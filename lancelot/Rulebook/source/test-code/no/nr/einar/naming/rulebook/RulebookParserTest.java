package no.nr.einar.naming.rulebook;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static no.nr.einar.naming.rulebook.Severity.*;
import static no.nr.einar.pb.model.Attribute.*;
import static no.nr.lancelot.analysis.LancelotTestUtils.*;

import org.junit.Test;

public class RulebookParserTest {
	private static final URL RULEBOOK_PARSER_TEST_RULEBOOK_URL = createUrl(
		TEST_RESOURCES_URL_PREFIX + "RulebookParserTest.java.rulebook.xml"
	);
	
	private static final Type NO_PARAM = null;
	private static final List<Phrase> NO_REFINEMENTS = Collections.<Phrase>emptyList();
	private static final Set<Rule> NO_RULES = Collections.<Rule>emptySet();
	
	@Test
	public void testParse() throws Exception {
		final Phrase actualRoot = new RulebookParser(RULEBOOK_PARSER_TEST_RULEBOOK_URL).getRoot();
		
		final Phrase expectedParseTree = new Phrase(
			createPhrasePartList(new WildcardPart()),
			Type.ANY,
			ParameterRequirement.derive(null),
			createList(
				new Phrase(
					createPhrasePartList(
						new ConcretePart("accept"), 
					    new WildcardPart()
					),
					Type.ANY,
					ParameterRequirement.derive(null),
					createList(
						new Phrase(
						    createPhrasePartList(
						    	new ConcretePart("accept"),
						    	new TagPart("verb"),
						    	new TagPart("adjective"),
						    	new WildcardPart()
						    ),
						    Type.INT,
						    ParameterRequirement.derive("reference"),
						    NO_REFINEMENTS,
						    createSet(
						    	new Rule(PARAMETER_TYPE_IN_NAME, NOTIFY, true)
						    )
						)
					),	
					createSet(
						new Rule(RETURNS_STRING, INAPPROPRIATE, true),
						new Rule(RETURN_TYPE_IN_NAME, FORBIDDEN, false)
					)
				)
			),
			NO_RULES
		);
		
		assertEquals(expectedParseTree, actualRoot);
	}

	private List<IPhrasePart> createPhrasePartList(final IPhrasePart... parts) {
		return Arrays.asList(parts);
	}
}
