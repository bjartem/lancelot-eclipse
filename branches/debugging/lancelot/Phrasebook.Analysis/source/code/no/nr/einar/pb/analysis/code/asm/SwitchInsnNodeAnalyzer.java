package no.nr.einar.pb.analysis.code.asm;

import no.nr.einar.pb.model.Attribute;

import org.objectweb.asm.tree.analysis.Frame;

public final class SwitchInsnNodeAnalyzer implements InstructionNodeAnalyzer {

	public void check(final Frame frame, final MethodAnalysisData data) {
		data.setAttribute(Attribute.HAS_BRANCHES);
	}

}
