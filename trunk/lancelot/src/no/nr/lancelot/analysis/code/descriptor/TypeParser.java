package no.nr.lancelot.analysis.code.descriptor;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public final class TypeParser implements ITypeParser {

    private int lastCharReadIndex;
    private String baseTypeName = null;
    private int dimensions = 0; // For arrays.
    private final ITypeParser refParser = new ReferenceTypeParser();
    private final ITypeParser priParser = new PrimitiveTypeParser();
    private boolean valid = false;

    public boolean parse(final int firstCharIndex, final char[] chars) {
        if (chars == null) {
            throw new IllegalArgumentException("The chars array cannot be null!");
        }
        if (chars.length == 0) {
            throw new IllegalArgumentException("The chars array cannot be zero length!");
        }
        if (firstCharIndex < 0) {
            throw new IllegalArgumentException("The index of the first character to read must be non-negative!");
        }
        if (firstCharIndex >= chars.length) {
            throw new IllegalArgumentException("The index of the first character to read must be less than the length of the chars array!");
        }

        int charIndex = firstCharIndex;
        dimensions = 0;
        while (chars[charIndex] == '[') {
            ++charIndex;
            ++dimensions;
        }
        final char firstChar = chars[charIndex];
        if (firstChar == 'L') {
            return parseReferenceType(charIndex + 1, chars);
        } else {
            return parsePrimitiveType(charIndex, chars);
        }
    }

    private boolean parsePrimitiveType(final int firstCharIndex, final char[] chars) {
        return parseType(priParser, firstCharIndex, chars);
    }

    private boolean parseReferenceType(final int firstCharIndex, final char[] chars) {
        return parseType(refParser, firstCharIndex, chars);
    }

    private boolean parseType(final ITypeParser parser, final int firstCharIndex, final char[] chars) {
        valid = parser.parse(firstCharIndex, chars);
        if (valid) {
            baseTypeName = parser.getTypeName();
            lastCharReadIndex = parser.getIndexOfLastCharRead();
        } else {
            baseTypeName = null;
            lastCharReadIndex = -1;
        }
        return valid;
    }

    @SuppressWarnings("SBSC_USE_STRINGBUFFER_CONCATENATION")
    public String getTypeName() {
        if (!valid) {
            System.out.println("Invalid!");
        }
        String result = baseTypeName;
        for (int i = 0; i < dimensions; i++) {
            result += "[]";
        }
        return result;
    }

    public int getIndexOfLastCharRead() {
        return lastCharReadIndex;
    }

}
