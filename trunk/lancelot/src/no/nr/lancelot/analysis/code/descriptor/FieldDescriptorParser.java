package no.nr.lancelot.analysis.code.descriptor;

public final class FieldDescriptorParser {
    
    private FieldDescriptorParser() {}

    public static String parse(final String descriptor) {
        final ITypeParser typeParser = new TypeParser();
        final boolean valid = typeParser.parse(0, descriptor.toCharArray());
        if (valid) {
            return typeParser.getTypeName();
        } else {
            throw new IllegalArgumentException("Unable to parse descriptor: " + descriptor);
        }
    }

}
