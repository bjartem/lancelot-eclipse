package no.nr.einar.naming.rulebook;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Type {
    PRIMITIVE,
    INT,
    BOOLEAN,
    OBJECT,
    STRING,
    REFERENCE,
    VOID,
    ANY;
    
    public static Type fromString(final String s) {
        if (s == null) {
            return ANY;
        }
        
        for (final Type type : values()) {
            if (type.name().equalsIgnoreCase(s) && type != ANY) {
                return type;
            }
        }
        
        throw new RuntimeException("Did not recognize type '" + s + "'");
    }
    
    public static Type fromFullyQualifiedName(final String fqn) {
        final Type reverseMappingOrNull = FULLY_QUALIFIED_REVERSE_MAP.get(fqn);
        return reverseMappingOrNull != null ? reverseMappingOrNull : REFERENCE;
    }
    
    // FIXME!! WHAT ABOUT Character, Integer, etc.. ?
    @SuppressWarnings("serial")
    private static final Map<String, Type> FULLY_QUALIFIED_REVERSE_MAP =
        Collections.unmodifiableMap(
            new HashMap<String, Type>(){{
                put("java.lang.Object", OBJECT);
                put("java.lang.String", STRING);
        
                put("boolean", BOOLEAN);
                put("byte",    PRIMITIVE);
                put("char",    PRIMITIVE);
                put("short",   PRIMITIVE);
                put("int",     INT);
                put("long",    PRIMITIVE); // FIXME!! Is this correct?
                
                put("float",   PRIMITIVE);
                put("double",  PRIMITIVE);
        
                put("void",    VOID);
            }}
        );
};