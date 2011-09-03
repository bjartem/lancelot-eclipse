package no.nr.lancelot.analysis.code.descriptor;

public final class PrimitiveTypeParser implements ITypeParser {

    private String typeName;
    private int lastCharReadIndex;

    public String getTypeName() {
        return typeName;
    }

    public boolean parse(final int firstCharIndex, final char[] chars) {
        typeName = getPrimitiveType(chars[firstCharIndex]);
        lastCharReadIndex = (typeName != null) ? firstCharIndex : -1;
        return (typeName != null);
    }

    private String getPrimitiveType(final char c) {
        switch (c) {
        case 'V': return "void";
        case 'B': return "byte";
        case 'C': return "char";
        case 'D': return "double";
        case 'F': return "float";
        case 'I': return "int";
        case 'J': return "long";
        case 'S': return "short";
        case 'Z': return "boolean";
        default: return null;
        }
    }

    public int getIndexOfLastCharRead() {
        return lastCharReadIndex;
    }

}
