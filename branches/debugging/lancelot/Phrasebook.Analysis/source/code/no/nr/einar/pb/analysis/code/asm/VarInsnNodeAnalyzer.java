package no.nr.einar.pb.analysis.code.asm;

import static org.objectweb.asm.Opcodes.*;
import no.nr.einar.pb.model.Attribute;

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
//		data.setAttribute(Attribute.LOCAL_ASSIGNMENT);
	}

}
