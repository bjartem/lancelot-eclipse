package no.nr.lancelot.analysis.code.asm;

import java.util.HashMap;
import java.util.Map;

import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.Label;

public final class MethodAnalysisData {

    private final String methodName;
    private final String className;
    private final Map<Label, Label> labels = new HashMap<Label, Label>();
    private long attributeMask;
    private final MethodTypeTuple mtt;
    private int noReturns = 0;
    private boolean seenMonitors = false;
    private boolean hasFinallyBlock = false;

    public MethodAnalysisData(final String methodName, final MethodTypeTuple mtt, final String className) {
        this.methodName = methodName;
        this.mtt = mtt;
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassName() {
        return className;
    }
    
    public boolean hasLabel(final Label label) {
        return labels.containsKey(label);
    }

    public void addLabel(final Label label) {
        labels.put(label, label);
    }

    public void setAttribute(final Attribute attribute) {
        attributeMask = attributeMask | attribute.getFlag();
    }

    public long getAttributeMask() {
        return attributeMask;
    }
    
    public boolean isAttributeSet(final Attribute attribute) {
        return (attributeMask & attribute.getFlag()) > 0;
    }
    
    public MethodTypeTuple getMethodTypeTuple() {
        return mtt;
    }

    public void incrementReturns() {
        ++noReturns;
    }
    
    public boolean hasMultipleReturns() {
        return noReturns > 1;
    }
    
    public void markAsSeenMonitors() {
        this.seenMonitors  = true;
    }
    
    public boolean hasSeenMonitors() {
        return this.seenMonitors;
    }

    public void markAsHavingFinallyBlock() {
        this.hasFinallyBlock = true;
    }
    
    public boolean hasFinallyBlock() {
        return this.hasFinallyBlock ;
    }

}
