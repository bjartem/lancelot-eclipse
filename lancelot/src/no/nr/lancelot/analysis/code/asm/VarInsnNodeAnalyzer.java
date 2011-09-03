package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DSTORE;
import static org.objectweb.asm.Opcodes.FSTORE;
import static org.objectweb.asm.Opcodes.ISTORE;
import static org.objectweb.asm.Opcodes.LSTORE;

import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Frame;

final class VarInsnNodeAnalyzer implements InstructionNodeAnalyzer {
    
    private static final int[] STORE_VAR_INSN_CODES = {
        ISTORE, LSTORE, FSTORE, DSTORE, ASTORE
    };

    private final VarInsnNode node;

    VarInsnNodeAnalyzer(final VarInsnNode node) {
        this.node = node;
    }

    public void check(final Frame frame, final MethodAnalysisData data) {
        if (isLocalAssignmentInsn()) {
            handleLocalAssignmentInsn(frame, data);
        }
    }

    private boolean isLocalAssignmentInsn() {
        return isMember(node.getOpcode(), STORE_VAR_INSN_CODES);
    }

    private boolean isMember(final int opcode, final int[] opcodes) {
        for (int i = 0; i < opcodes.length; i++) {
            if (opcode == opcodes[i]) {
                return true;
            }
        }
        return false;
    }
    
    private void handleLocalAssignmentInsn(final Frame frame, final MethodAnalysisData data) {
//      data.setAttribute(Attribute.LOCAL_ASSIGNMENT);
    }

}
