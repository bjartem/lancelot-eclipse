package no.nr.lancelot.analysis.code.asm;

import java.util.Arrays;

public final class MethodTypeTuple {

    private final String returnType;
    private final String[] parameterTypes;

    public MethodTypeTuple(final String returnType, final String[] parameterTypes) {
        if (returnType == null) {
            throw new IllegalArgumentException("Return type cannot be null!");
        }
        if (returnType.length() == 0) {
            throw new IllegalArgumentException("Return type cannot be empty!");
        }
        if (parameterTypes == null) {
            throw new IllegalArgumentException("The parameter type list cannot be null!");
        }
        for (final String paramType : parameterTypes) {
            if (paramType == null) {
                throw new IllegalArgumentException("A parameter type cannot be null!");
            }
            if (paramType.length() == 0) {
                throw new IllegalArgumentException("A parameter type cannot be empty!");
            }
        }

        this.returnType = returnType;
        this.parameterTypes = clone(parameterTypes);
    }

    public String getReturnType() {
        return returnType;
    }

    public String[] getParameterTypes() {
        return clone(parameterTypes);
    }

    private static String[] clone(final String[] parameterTypes) {
        return Arrays.copyOf(parameterTypes, parameterTypes.length);
    }
    
    public String toString() {
        final StringBuilder sb = new StringBuilder(returnType);
        sb.append('(');
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(parameterTypes[i]);
        }
        sb.append(')');
        return sb.toString();
    }

}
