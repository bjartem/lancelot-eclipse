package no.nr.einar.pb.analysis.name.splitter.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import no.nr.einar.pb.analysis.name.splitter.NameSplitter;

import org.junit.Test;

public final class NameSplitterTest {

	@Test
	public void splitSingleWord() {
		splitTest("foo", new String[] {"foo"});
	}

	@Test
	public void splitTwoWords() {
		splitTest("getName", new String[] {"get", "name"});
	}

	@Test
	public void splitThreeWords() {
		splitTest("getLastName", new String[] {"get", "last", "name"});
	}

	@Test
	public void splitThreeWordsWithAcronym() {
		splitTest("getXMLParser", new String[] {"get", "XML", "parser"});
	}

	@Test
	public void splitWithSingleLetter() {
		splitTest("getAJob", new String[] {"get", "a", "job"});
	}

	@Test
	public void splitWithTrailingAcronym() {
		splitTest("getXML", new String[] {"get", "XML"});
	}

	@Test
	public void splitWithSingleDigitNumber() {
		splitTest("get1Peach", new String[] {"get", "1", "peach"});
	}

	@Test
	public void splitWithLongNumber() {
		splitTest("get256Peaches", new String[] {"get", "256", "peaches"});
	}
	
	@Test
	public void splitWithTrailingTwoLetterAcronym() {
		splitTest("getID", new String[] {"get", "ID"});
	}

	@Test
	public void splitWithTrailingPluralizedAcronym() {
		splitTest("getAllURLs", new String[] {"get", "all", "URLs"});
	}
	
	@Test
	public void splitWithAcronymBeforeNumber() {
		splitTest("getenvJDK14", new String[] {"getenv", "JDK", "14"});
	}
	
	@Test
	public void splitWithTwoLetterAcronymBeforeNumber() {
		splitTest("setWantOS2NoVM", new String[] {"set", "want", "OS", "2", "no", "VM"});
	}
	
	private void splitTest(final String methodName, final String[] expected) {
		final NameSplitter splitter = new NameSplitter();
		final List<String> actual = splitter.split(methodName);
		assertNotNull(actual);
		assertEquals(expected.length, actual.size());
		for (int i = 0; i < expected.length; i++) {
			final String exp = expected[i];
			final String act = actual.get(i);
			assertEquals(exp, act);
		}
	}


}
