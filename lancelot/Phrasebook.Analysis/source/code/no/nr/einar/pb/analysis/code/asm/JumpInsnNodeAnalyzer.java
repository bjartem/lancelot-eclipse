package no.nr.einar.pb.analysis.code.asm;

import no.nr.einar.pb.model.Attribute;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.analysis.Frame;

public final class JumpInsnNodeAnalyzer implements InstructionNodeAnalyzer {

	private final JumpInsnNode node;

	public JumpInsnNodeAnalyzer(final JumpInsnNode node) {
		this.node = node;
	}

	public void check(final Frame frame, final MethodAnalysisData data) {
		data.setAttribute(Attribute.HAS_BRANCHES);
		final Label label = node.label.getLabel();
		if (data.hasLabel(label)) {
			data.setAttribute(Attribute.CONTAINS_LOOP);
		}
	}

}
