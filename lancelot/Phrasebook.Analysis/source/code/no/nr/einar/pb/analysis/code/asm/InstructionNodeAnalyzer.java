package no.nr.einar.pb.analysis.code.asm;


import org.objectweb.asm.tree.analysis.Frame;

public interface InstructionNodeAnalyzer {

	void check(final Frame frame, final MethodAnalysisData data);

}
