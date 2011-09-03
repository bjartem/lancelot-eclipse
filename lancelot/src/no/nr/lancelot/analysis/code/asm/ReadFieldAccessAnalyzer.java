package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.model.Attribute;

public final class ReadFieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

    public ReadFieldAccessAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
        super(analyzer);
    }

    @Override
    public Attribute getAttribute() {
        return Attribute.FIELD_READER;
    }
    
}
