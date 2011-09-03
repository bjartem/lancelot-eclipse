package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.analysis.Frame;

public final class SwitchInsnNodeAnalyzer implements InstructionNodeAnalyzer {

    public void check(final Frame frame, final MethodAnalysisData data) {
        data.setAttribute(Attribute.HAS_BRANCHES);
    }

}
