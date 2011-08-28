package no.nr.einar.pb.analysis.code.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import no.nr.einar.pb.analysis.code.descriptor.TypeParser;

import org.junit.Test;

public final class TypeParserTest {

	@Test(expected=IllegalArgumentException.class)
	public void charsCannotBeNull() {
		parse(0, null);
	}

	@Test(expected=IllegalArgumentException.class)
	public void charsCannotBeZeroLength() {
		parse(0, new char[] {});
	}

	@Test(expected=IllegalArgumentException.class)
	public void firstCharIndexCannotBeNegative() {
		parse(-1, new char[] { 'c' });
	}

	@Test(expected=IllegalArgumentException.class)
	public void firstCharIndexMustBeWithinArrayBounds() {
		parse(1, new char[] { 'c' });
	}

	@Test
	public void parseIntegerCannotBeLowercase() {
		final boolean success = parse(0, new char[] {'i'});
		assertFalse(success);
	}

	@Test
	public void parseIllegalCharacter() {
		final boolean success = parse(0, new char[] {' '});
		assertFalse(success);
	}

	@Test
	public void parsePrimitives() {
		final char[] primitiveChars = new char[] {
				'V', 'B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z'
		};
		final String[] primitiveNames = new String[] {
				"void", "byte", "char", "double", 
				"float", "int", "long", "short", "boolean"
		};
		for (int i = 0; i < primitiveChars.length; i++) {
			final char c = primitiveChars[i];
			final String name = primitiveNames[i];
			legal(new char[] {c}, name);
		}
	}

	@Test
	public void parseSimpleReferenceType() {
		legal("Foo");
	}
	
	@Test
	public void parseUppercaseReferenceType() {
		legal("FOO");
	}
	
	@Test
	public void parseReferenceTypeWithUnderscore() {
		legal("FOO_BAR");
	}
	
	/*
	 * The example is taken from Jython.
	 */
	@Test
	public void parseReferenceTypeWithLeadingUnderscore() {
		legal("_weakref$AbstractReference");
	}
	
	/*
	 * The example is taken from Poseidon.
	 */
	@Test
	public void parseReferenceTypeWithUnderscoreAfterDollar() {
		legal("ConfigurationOld$_B");
	}
	
	/*
	 * The example is taken from Eclipse.
	 */
	@Test
	public void parseReferenceTypeWithUnderscoreAndDollar() {
		legal("ASTProvider$WAIT_FLAG");
	}

	@Test
	public void parseReferenceTypeWithNamespace() {
//		legal("Ljava/lang/String;", "String");
		legal("Ljava/lang/String;", "java.lang.String");
	}

	@Test
	public void parseReferenceTypeWithDollars() {
		legal("A$B$C$D");
	}

	@Test
	public void parseReferenceTypeWithDollarsInNamespaceIsIllegal() {
		illegal("Lfoo$bar/Baz;");
	}

	@Test
	public void parseReferenceTypeWithDollarOnlyIsIllegal() {
		illegal("L$;");
	}

	@Test
	public void parseEmptyReferenceTypeIsIllegal() {
		illegal("L;");
	}

	@Test
	public void parseReferenceTypeOffset() {
		legal("IZLFoo;SJ", "Foo", 2, 6);
	}

	@Test
	public void parseIncompleteReference() {
		illegal("L");
	}

	@Test
	public void parseNamespaceWithUnderscore() {
//		final String expectedType = "ClassdiagramLayouter";
//		final String fullType = "Lorg/argouml/uml/diagram/static_structure/layout/" + expectedType + ";";
		final String expectedType = "org.argouml.uml.diagram.static_structure.layout.ClassdiagramLayouter";
		final String fullType = "L" + expectedType.replace('.', '/') + ";";
		legal(fullType, expectedType, 0, fullType.length() - 1);
	}

	private boolean parse(final int index, final char[] chars) {
		return (new TypeParser()).parse(index, chars);
	}

	private void legal(final char[] chars, final String expected,
			final int startParsingAtIndex, final int shouldStopAtIndex) {
		final TypeParser tp = new TypeParser();
		final boolean success = tp.parse(startParsingAtIndex, chars);
		assertTrue("Type parsing failed.", success);
		final String type = tp.getTypeName();
		assertNotNull(type);
		assertEquals(expected, type);
		assertEquals(shouldStopAtIndex, tp.getIndexOfLastCharRead());
	}

	private void legal(final char[] chars, final String expected) {
		legal(chars, expected, 0, chars.length - 1);
	}

	private void legal(final String input, final String expected,
			final int startIndex, final int endIndex) {
		legal(input.toCharArray(), expected, startIndex, endIndex);
	}

	private void legal(final String input, final String expected) {
		legal(input, expected, 0, input.length() - 1);
	}
	
	private void legal(final String expected) {
		legal("L" + expected + ";", expected);
	}

	private void illegal(final String input, final int offset) {
		final boolean success = (new TypeParser()).parse(offset, input.toCharArray());
		assertFalse(success);
	}

	private void illegal(final String input) {
		illegal(input, 0);
	}

}
