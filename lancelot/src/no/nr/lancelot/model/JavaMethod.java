package no.nr.lancelot.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;


public final class JavaMethod implements IJavaMethod {
    
    private static final String[] VALUE_TYPES = new String[] { 
        "int", "boolean", "double", "char", "long", "float", "byte", "short" 
    };
    
    private static String longestName = "";
    
    public static String getLongestName() {
        return longestName;
    }
    
    private final String name;
    private final String descriptor;
    private final String returnType;
    private final String[] paramTypes;
    private final TypeDictionary typeDict;
    private final int noInstructions;
    private final boolean isStatic;
    private long attributeMask;

    public JavaMethod(
        final String methodName,
        final String descriptor,
        final String returnType,
        final String[] paramTypes,
        final long attributeMask, 
        final TypeDictionary typeDict,
        final int noInstructions,
        final boolean isStatic
    ) {
        if (methodName == null) {
            throw new IllegalArgumentException("The method name cannot be null!");
        }
        if (descriptor == null) {
            throw new IllegalArgumentException("The method descriptor cannot be null!");
        }
        if (returnType == null) {
            throw new IllegalArgumentException("The return type cannot be null!");
        }
        if (paramTypes == null) {
            throw new IllegalArgumentException("The parameter type list cannot be null!");
        }
        for (final String paramType : paramTypes) {
            if (paramType == null) {
                throw new IllegalArgumentException("The parameter type list cannot contain null strings!");
            }
        }
        
        this.isStatic = isStatic;
        
        if (attributeMask < 0) {
            throw new RuntimeException("Ouch, negative attribute mask!");
        }

        long mask = attributeMask;
        if ("void".equals(returnType)) {
            mask = mask | Attribute.RETURNS_VOID.getFlag();
        } else if (isValueType(returnType)) {
            if ("int".equals(returnType)) {
                mask = mask | Attribute.RETURNS_INT.getFlag();
            } else if ("boolean".equals(returnType)) {
                mask = mask | Attribute.RETURNS_BOOLEAN.getFlag();
            }
        } else {
            if ("java.lang.String".equals(returnType)) {
                mask = mask | Attribute.RETURNS_STRING.getFlag();                   
            } else {
                mask = mask | Attribute.RETURNS_REFERENCE.getFlag();    
            }
        }
        
        if (mask < 0) {
            throw new RuntimeException("Ouch, negative mask!");
        }

        if (paramTypes.length == 0) {
            mask = mask | Attribute.NO_PARAMETERS.getFlag();
        }

        this.name = methodName;
        if (name.length() > longestName.length()) {
            longestName = name;
        }
        this.descriptor = descriptor;
        this.returnType = returnType;
        this.paramTypes = clone(paramTypes);
        this.attributeMask = mask;
        this.typeDict = typeDict;
        this.noInstructions = noInstructions;
    }

    private static boolean isValueType(final String type) {
        for (int i = 0; i < VALUE_TYPES.length; i++) {
            if (type.equals(VALUE_TYPES[i])) {
                return true;
            }
        }
        return false;
    }

    public String getMethodName() {
        return name;
    }
    
    public String getDescriptor() {
        return descriptor;
    }

    public String getReturnType() {
        return returnType;
    }

    public String[] getParameterTypes() {
        return clone(paramTypes);
    }

    private static String[] clone(final String[] parameterTypes) {
        return Arrays.copyOf(parameterTypes, parameterTypes.length);
    }

    public boolean hasReturnValue() {
        return !check(Attribute.RETURNS_VOID);
    }

    public boolean isFieldReader() {
        return check(Attribute.FIELD_READER);
    }

    public boolean isFieldWriter() {
        return check(Attribute.FIELD_WRITER);
    }

    private boolean check(final Attribute attribute) {
        return (attributeMask & attribute.getFlag()) > 0;
    }

    public boolean hasLoop() {
        return check(Attribute.CONTAINS_LOOP);
    }

    public boolean hasParameters() {
        return !check(Attribute.NO_PARAMETERS);
    }

    public boolean isExceptionThrower() {
        return check(Attribute.THROWS_EXCEPTIONS);
    }


    public boolean isSameNameDelegator() {
        return check(Attribute.SAME_NAME_CALL);
    }

    public boolean isTypeManipulator() {
        return check(Attribute.TYPE_MANIPULATOR);
    }
    
    public long getSemantics() {
        return attributeMask;
    }
    
    public Attribute[] createAttributeList() {
        final List<Attribute> list = new ArrayList<Attribute>();
        for (final Attribute candidate : Attribute.values()) {
            if (check(candidate)) {
                list.add(candidate);
            }
        }
        final Attribute[] result = new Attribute[list.size()];
        return list.toArray(result);
    }

    public boolean isParameterKeeper() {
        return check(Attribute.PARAMETER_TO_FIELD);
    }

    public boolean isStateReturner() {
        return check(Attribute.RETURNS_FIELD_VALUE);
    }

    public TypeDictionary getTypeDictionary() {
        return this.typeDict;
    }

    public boolean isStaticMethod() {
        return this.isStatic;
    }
        
    public boolean createsRegularObjects() {
        return check(Attribute.CREATES_REGULAR_OBJECTS);
    }

    public void flagAsTypeName(final String fragment) {
        if (check(fragment, getReturnType())) {
            flag(Attribute.RETURN_TYPE_IN_NAME);
        }
        for (final String paramType : getParameterTypes()) {
            if (check(fragment, paramType)) {
                flag(Attribute.PARAMETER_TYPE_IN_NAME);
            }
        }
    }
    
    private boolean check(final String fragment, final String type) {
        return fragment.equals(unqualify(type));
    }
    
    private String unqualify(final String qualifiedType) {
        return qualifiedType.substring(qualifiedType.lastIndexOf('.') + 1);
    }

    private void flag(final Attribute a) {
        this.attributeMask = this.attributeMask | a.getFlag();
    }

    public int getInstructionCount() {
        return this.noInstructions;
    }

    @SuppressWarnings("SBSC_USE_STRINGBUFFER_CONCATENATION")
    @Override
    public String toString() {
        String paramTypesStr = "";
        for (String p : paramTypes)
            paramTypesStr += p + ", ";
        if (!paramTypesStr.isEmpty())
            paramTypesStr = paramTypesStr.substring(0, paramTypesStr.length()-2);
        
        return String.format(
            "[%s %s(%s) TYPEDICT %d %s %d]",
            returnType,
            name,
            paramTypesStr,
            noInstructions,
            isStatic,
            attributeMask
        );
    }
}
