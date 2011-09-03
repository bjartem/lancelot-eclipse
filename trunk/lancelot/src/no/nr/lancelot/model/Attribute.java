package no.nr.lancelot.model;

public enum Attribute {
    
    // Signature
    RETURNS_VOID                      (0x1),  //  1
    RETURNS_INT                       (0x2),
    RETURNS_BOOLEAN                   (0x4),
    RETURNS_STRING                    (0x8),
    RETURNS_REFERENCE                (0x10),  //  5
    NO_PARAMETERS                    (0x20),
    RETURN_TYPE_IN_NAME              (0x40),
    PARAMETER_TYPE_IN_NAME           (0x80),

    // Data Flow
    FIELD_WRITER                    (0x100),
    FIELD_READER                    (0x200),  // 10
    PARAMETER_TO_FIELD              (0x400),
    RETURNS_FIELD_VALUE             (0x800),
    TYPE_MANIPULATOR               (0x1000),
    RETURNS_CREATED_OBJECT         (0x2000),
    
    // Control Flow
    CONTAINS_LOOP                  (0x4000),  // 15
    HAS_BRANCHES                   (0x8000), 
    MULTIPLE_RETURNS              (0x10000),

    // Object Creation
    CREATES_REGULAR_OBJECTS       (0x20000),
    CREATES_STRING_OBJECTS        (0x40000),  
    CREATES_CUSTOM_OBJECTS        (0x80000),  // 20
    CREATES_OWN_CLASS_OBJECTS    (0x100000),
    
    // Exception Handling
    THROWS_EXCEPTIONS            (0x200000),  
    CATCHES_EXCEPTIONS           (0x400000),
    EXPOSES_CHECKED_EXCEPTIONS   (0x800000), 
    
    // Method Call
    RECURSIVE_CALL              (0x1000000L), // 25
    SAME_NAME_CALL              (0x2000000L),
    SAME_VERB_CALL              (0x4000000L),
    METHOD_CALL_ON_FIELD        (0x8000000L),
    METHOD_CALL_ON_PARAMETER   (0x10000000L),
    PARAMETER_TO_FIELD_CALL    (0x20000000L), // 30
    
    // Degenerates
//  DEGENERATE_EMPTY          (0x400000000L), // 35
//  DEGENERATE_DELEGATION     (0x800000000L),
//  DEGENERATE_EXCEPTION     (0x1000000000L),
//  DEGENERATE_BIT_CONSTANT  (0x2000000000L),
//  DEGENERATE_INT_CONSTANT  (0x4000000000L),
//  DEGENERATE_CONSTANT      (0x8000000000L), // 40
//  DEGENERATE_NULL         (0x10000000000L),
//  DEGENERATE_FIELD_READ   (0x20000000000L),
//  DEGENERATE_LAZY_GETTER  (0x40000000000L)  // 43
    
//  THIS_CALL(0x2000000),
//  THAT_CALL(0x4000000),
//  LOCAL_CALL(0x8000000),
//  REMOTE_CALL(0x10000000),
//  RETURNS_ARRAY(0x20000000),
//  ACCEPTS_ARRAY(0x40000000)
    ;
    
    // RETURNS_LIST
    // ACCEPTS_LIST (AS PARAMETER, THAT IS)

    private final long flag;

    private Attribute(final long flag) {
        this.flag = flag;
    }

    public long getFlag() {
        if (flag < 0) {
            throw new RuntimeException("Negative flag! Please fix (add an L).");
        }
        return flag;
    }
}
