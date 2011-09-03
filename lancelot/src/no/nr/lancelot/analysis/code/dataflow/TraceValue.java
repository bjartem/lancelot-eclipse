package no.nr.lancelot.analysis.code.dataflow;

import org.objectweb.asm.tree.analysis.Value;

public final class TraceValue implements Value {

    private final Value value;
    private boolean maybeThisReference = false;
    private boolean maybeField = false;
    private boolean maybeParameter = false;
    private boolean maybeCreatedObject = false;

    private TraceValue(final Value value) {
        this.value = value;
    }

    public final int getSize() {
        return value.getSize();
    }

    public final Value getValue() {
        return value;
    }
    
    public void markAsMaybeThisReferenceValue() {
        this.maybeThisReference = true;
    }
    
    public void markAsMaybeFieldValue() {
        this.maybeField = true;
    }
    
    public void markAsMaybeParameterValue() {
        this.maybeParameter = true;
    }
    
    public void markAsMaybeCreatedObjectValue() {
        this.maybeCreatedObject = true;
    }
    
    public boolean isMaybeThisReferenceValue() {
        return this.maybeThisReference;
    }
    
    public boolean isMaybeFieldValue() {
        return this.maybeField;
    }
    
    public boolean isMaybeParameterValue() {
        return this.maybeParameter;
    }
    
    public boolean isMaybeCreatedObjectValue() {
        return this.maybeCreatedObject;
    }
    
    public static TraceValue createThisReferenceValue(final Value value) {
        final TraceValue $ = new TraceValue(value);
        $.markAsMaybeThisReferenceValue();
        return $;
    }
    
    public static TraceValue createFieldValue(final Value value) {
        final TraceValue $ = new TraceValue(value);
        $.markAsMaybeFieldValue();
        return $;
    }
    
    public static TraceValue createParameterValue(final Value value) {
        final TraceValue $ = new TraceValue(value);
        $.markAsMaybeParameterValue();
        return $;
    }
    
    public static TraceValue createCreatedObjectValue(final Value value) {
        final TraceValue $ = new TraceValue(value);
        $.markAsMaybeCreatedObjectValue();
        return $;
    }
    
    public static TraceValue createVanillaValue(final Value value) {
        return new TraceValue(value);
    }
    
}
