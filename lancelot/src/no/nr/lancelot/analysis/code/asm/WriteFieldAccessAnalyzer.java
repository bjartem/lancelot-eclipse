package no.nr.lancelot.analysis.code.asm;

import no.nr.lancelot.analysis.code.dataflow.TraceValue;
import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public final class WriteFieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

    public WriteFieldAccessAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
        super(analyzer);
    }

    @Override
    protected void localCheck(final Frame frame, final MethodAnalysisData data) {
        if (frame == null) {
            System.err.println("Frame is null.");
            return;
        }
        final Value val = frame.pop();
        if (val instanceof TraceValue) {
            final TraceValue traceVal = (TraceValue) val;
            if (traceVal.isMaybeParameterValue()) {
                data.setAttribute(Attribute.PARAMETER_TO_FIELD);
            }
        }
        frame.push(val);
    }
    
    @Override
    public Attribute getAttribute() {
        return Attribute.FIELD_WRITER;
    }

}
