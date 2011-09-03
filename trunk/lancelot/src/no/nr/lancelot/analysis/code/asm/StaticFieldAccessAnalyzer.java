package no.nr.lancelot.analysis.code.asm;


import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.analysis.Frame;

public final class StaticFieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

    private final FieldInsnNode node;
    
    public StaticFieldAccessAnalyzer(final FieldInsnNode node,
            final FieldInsnNodeAnalyzer analyzer) {
        super(analyzer);
        this.node = node;
    }

    @Override
    protected void localCheck(final Frame frame, final MethodAnalysisData data) {
        if (!onDefiningClass(data)) {
//          System.err.println("Accesses other type's static field!" + " [" + getAttribute() + "]");
            // TODO: Distinguish between "inherited" fields and true remote fields.
        }
    }
    
    private boolean onDefiningClass(final MethodAnalysisData data) {
        return data.getClassName().equals(node.owner);
    }

}
