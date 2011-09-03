package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.analysis.Frame;

public final class NullFieldAnalyzer implements FieldInsnNodeAnalyzer {

    @Override
    public Attribute getAttribute() {
        throw new UnsupportedOperationException("Don't ask me about this!");
    }

    @Override
    public void check(final Frame frame, final MethodAnalysisData data) {}

}
