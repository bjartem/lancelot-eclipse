package no.nr.lancelot.analysis.code.asm;


import org.objectweb.asm.Label;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.analysis.Frame;

public final class LabelNodeAnalyzer implements InstructionNodeAnalyzer {

    private final LabelNode node;

    public LabelNodeAnalyzer(final LabelNode node) {
        this.node = node;
    }

    public void check(final Frame frame, final MethodAnalysisData data) {
        final Label label = node.getLabel();
        if (!data.hasLabel(label)) {
            data.addLabel(label);
        }
    }

}
