package no.nr.lancelot.analysis.code.asm;


import org.objectweb.asm.tree.analysis.Frame;

public final class FieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

    public FieldAccessAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
        super(analyzer);
    }

    @Override
    protected void localCheck(final Frame frame, final MethodAnalysisData data) {
        data.setAttribute(analyzer.getAttribute());
    }

}
