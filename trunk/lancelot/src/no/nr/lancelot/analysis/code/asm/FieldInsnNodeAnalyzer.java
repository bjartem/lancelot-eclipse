package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.model.Attribute;

public interface FieldInsnNodeAnalyzer extends InstructionNodeAnalyzer {

    Attribute getAttribute();
    
}
