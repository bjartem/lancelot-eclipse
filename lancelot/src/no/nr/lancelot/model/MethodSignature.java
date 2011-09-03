package no.nr.lancelot.model;


public final class MethodSignature {
    
    private enum TypeGroup {
        Void, Value, Reference;
    }
    
    private enum SpecificType {
        Void, Val_Int, Val_Boolean, Val_Other,
        Ref_Object, Ref_String, Ref_Other;
    }
    
    private enum ParameterCount {
        None, One, Two, Several;
    }

    private final TypeGroup returnTypeGroup;
    private final SpecificType specificReturnType;
    private final ParameterCount paramCount;
    private final boolean isStaticMethod;
    private final SpecificType specificParamType;
    
    private static final String[] VALUE_TYPES = new String[] { 
        "int", "boolean", "double", "char", "long", "float", "byte", "short" 
    };
    
    public MethodSignature(final JavaMethod method) {
        this.returnTypeGroup = getReturnTypeGroup(method);
        this.specificReturnType = getSpecificReturnType(method, this.returnTypeGroup);
        this.paramCount = getParameterCount(method);
        this.specificParamType = getSpecificParamType(method);
        this.isStaticMethod = method.isStaticMethod();
    }
    
    public String getReturnTypeGroup() {
        return returnTypeGroup.name();
    }
    
    public String getSpecificReturnType() {
        return specificReturnType.name();
    }
    
    public String getSpecificParamType() {
        return specificParamType.name();
    }
    
    public String getParameterCount() {
        return paramCount.name();
    }
    
    public boolean isStaticMethod() {
        return isStaticMethod;
    }
    
    private static TypeGroup getReturnTypeGroup(final JavaMethod method) {
        final String returnType = method.getReturnType();
        if (isVoidType(returnType)) {
            return TypeGroup.Void;
        }
        if (isValueType(returnType)) {
            return TypeGroup.Value;
        }
        return TypeGroup.Reference;
    }
    
    private static boolean isVoidType(final String type) {
        return type.equals("void");
    }

    private static boolean isValueType(final String type) {
        for (int i = 0; i < VALUE_TYPES.length; i++) {
            if (type.equals(VALUE_TYPES[i])) {
                return true;
            }
        }
        return false;
    }

    private static SpecificType getSpecificReturnType(final JavaMethod method, final TypeGroup group) {
        final String type = method.getReturnType();
        if (group == TypeGroup.Void) {
            return getSpecificVoidType(type);
        }
        if (group == TypeGroup.Value) {
            return getSpecificValueType(type);
        }
        return getSpecificReferenceType(type);
    }
    
    private static SpecificType getSpecificVoidType(final String type) {
        return SpecificType.Void;
    }

    private static SpecificType getSpecificValueType(final String type) {
        if (type.equals("int")) {
            return SpecificType.Val_Int;
        }
        if (type.equals("boolean")) {
            return SpecificType.Val_Boolean;
        }
        return SpecificType.Val_Other;
    }

    private static SpecificType getSpecificReferenceType(final String type) {
        if (type.equals("java.lang.Object")) {
            return SpecificType.Ref_Object;
        }
        if (type.equals("java.lang.String")) {
            return SpecificType.Ref_String;
        }
        return SpecificType.Ref_Other;
    }

    private static ParameterCount getParameterCount(final JavaMethod method) {
        final int noParams = method.getParameterTypes().length;
        switch (noParams) {
        case 0:
            return ParameterCount.None;
        case 1:
            return ParameterCount.One;
        case 2:
            return ParameterCount.Two;
        default:
            return ParameterCount.Several;
        }
    }
    
    private static SpecificType getSpecificParamType(final JavaMethod method) {
        final String[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            return SpecificType.Void;
        }
        final String paramType = paramTypes[0];
        if (isValueType(paramType)) {
            return getSpecificValueType(paramType);
        }
        return getSpecificReferenceType(paramType);
    }

}
