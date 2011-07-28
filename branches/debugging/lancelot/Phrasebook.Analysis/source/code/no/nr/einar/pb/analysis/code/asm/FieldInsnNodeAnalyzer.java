package no.nr.einar.pb.analysis.code.asm;

import no.nr.einar.pb.model.Attribute;

public interface FieldInsnNodeAnalyzer extends InstructionNodeAnalyzer {

	Attribute getAttribute();
	
}
