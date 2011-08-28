package no.nr.einar.pb.analysis.code.asm;

import no.nr.einar.pb.model.Attribute;

public final class ReadFieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

	public ReadFieldAccessAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
		super(analyzer);
	}

	@Override
	public Attribute getAttribute() {
		return Attribute.FIELD_READER;
	}
	
}
