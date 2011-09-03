package no.nr.lancelot.analysis.code.descriptor;

import java.util.ArrayList;
import java.util.List;

import no.nr.lancelot.analysis.code.asm.MethodTypeTuple;

public final class MethodDescriptorParser {

    private MethodDescriptorParser() {}

    private static DescriptorParsingState parseState = DescriptorParsingState.ERROR;

    public static MethodTypeTuple getMethodTypeTuple(final String descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException("The descriptor cannot be null!");
        }
        if (descriptor.length() == 0) {
            throw new IllegalArgumentException("The descriptor cannot be empty!");
        }
        final List<String> typeList = parse(descriptor);
        assert typeList.size() >= 1;
        final String[] paramTypes = new String[typeList.size() - 1];
        for (int i = 0; i < paramTypes.length; i++) {
            paramTypes[i] = typeList.get(i);
        }
        final String returnType = typeList.get(typeList.size() - 1);
        return new MethodTypeTuple(returnType, paramTypes);
    }

    private static List<String> parse(final String descriptor) {
        final List<String> result = new ArrayList<String>();
        parseState = DescriptorParsingState.INITIAL;
        final char[] chars = descriptor.toCharArray();
        final ITypeParser typeParser = new TypeParser();
        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];
            if (parseState == DescriptorParsingState.INITIAL) {
                if (c == '(') {
                    parseState = DescriptorParsingState.PARAMETER_TYPES;
                } else {
                    parseState = DescriptorParsingState.ERROR;
                }
            } else if (parseState == DescriptorParsingState.PARAMETER_TYPES) {
                if (c == ')') {
                    parseState = DescriptorParsingState.RETURN_TYPE;
                } else {
                    if (typeParser.parse(i, chars)) {
                        result.add(typeParser.getTypeName());
                        i = typeParser.getIndexOfLastCharRead();
                    } else {
                        parseState = DescriptorParsingState.ERROR;
                    }
                }
            } else if (parseState == DescriptorParsingState.RETURN_TYPE) {
                if (typeParser.parse(i, chars)) {
                    result.add(typeParser.getTypeName());
                    i = typeParser.getIndexOfLastCharRead();
                    parseState = DescriptorParsingState.DONE;
                } else {
                    parseState = DescriptorParsingState.ERROR;
                }
            }
        }
        if (parseState != DescriptorParsingState.DONE) {
            throw new IllegalArgumentException("Unable to parse descriptor: " + descriptor);
        }
        return result;
    }

}
