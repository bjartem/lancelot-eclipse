package no.nr.lancelot.analysis.code.descriptor;

public final class ReferenceTypeParser implements ITypeParser {

    private ReferenceTypeParsingState parseState = ReferenceTypeParsingState.ERROR;
    private String typeName = null;
    private int lastCharReadIndex = -1;

    public String getTypeName() {
        if (parseState != ReferenceTypeParsingState.REFERENCE_DONE) {
            throw new IllegalStateException();
        }
        return typeName;
    }

    public boolean parse(final int firstCharIndex, final char[] chars) {
        parseState = ReferenceTypeParsingState.REFERENCE_START;
        for (int i = firstCharIndex; i < chars.length; i++) {
            final char c = chars[i];
            if (parseState == ReferenceTypeParsingState.INITIAL) {
                throw new IllegalStateException("This state should never occur (in this context)!");
            } else if (parseState == ReferenceTypeParsingState.REFERENCE_START) {
                if (Character.isLetter(c) || c == '_') {
                    parseState = ReferenceTypeParsingState.MAYBE_PACKAGE;
                } else {
                    parseState = ReferenceTypeParsingState.ERROR;
                }
            } else if (parseState == ReferenceTypeParsingState.MAYBE_PACKAGE) {
                if (c == '/') {
                    chars[i] = '.';
                    parseState = ReferenceTypeParsingState.REFERENCE_START;
                } else if (c == ';') {
                    parseState = ReferenceTypeParsingState.REFERENCE_DONE;
                } else if (c == '$') {
                    parseState = ReferenceTypeParsingState.INVALID_NOT_PACKAGE;
                } else if (!Character.isLetterOrDigit(c) && c != '_') {
                    parseState = ReferenceTypeParsingState.ERROR;
                }
            } else if (parseState == ReferenceTypeParsingState.INVALID_NOT_PACKAGE) {
                if (Character.isLetterOrDigit(c) || c == '_') {
                    parseState = ReferenceTypeParsingState.VALID_NOT_PACKAGE;
                } else {
                    parseState = ReferenceTypeParsingState.ERROR;
                }
            } else if (parseState == ReferenceTypeParsingState.VALID_NOT_PACKAGE) {
                if (c == ';') {
                    parseState = ReferenceTypeParsingState.REFERENCE_DONE;
                } else if (c == '$') {
                    parseState = ReferenceTypeParsingState.INVALID_NOT_PACKAGE;
                } else if (!Character.isLetterOrDigit(c) && c != '_') {
                    parseState = ReferenceTypeParsingState.ERROR;
                }
            } else if (parseState == ReferenceTypeParsingState.REFERENCE_DONE) {
                throw new RuntimeException("When this state has occured, we should be done looping!");
            }
            if (parseState == ReferenceTypeParsingState.ERROR) {
                return false;
            } else if (parseState == ReferenceTypeParsingState.REFERENCE_DONE) {
                lastCharReadIndex = i;
                break;
            }
        }

        if (parseState == ReferenceTypeParsingState.REFERENCE_DONE) {
            typeName = new String(chars, firstCharIndex, lastCharReadIndex - firstCharIndex);
        } else {
            typeName = null;
        }
        return (typeName != null);
    }

    public int getIndexOfLastCharRead() {
        return lastCharReadIndex;
    }

}
