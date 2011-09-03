package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.analysis.Frame;

public abstract class ChainFieldInsnNodeAnalyzer implements FieldInsnNodeAnalyzer {

    protected final FieldInsnNodeAnalyzer analyzer;

    public ChainFieldInsnNodeAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
        this.analyzer = analyzer;
    }
    
    @Override
    public void check(final Frame frame, final MethodAnalysisData data) {
        localCheck(frame, data);
        analyzer.check(frame, data);
    }
    
    protected void localCheck(final Frame frame, final MethodAnalysisData data) {}
    
    public Attribute getAttribute() {
        return analyzer.getAttribute();
    }
    
}
