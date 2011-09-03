package no.nr.lancelot.analysis.code.asm;

import java.util.Stack;

import no.nr.lancelot.analysis.code.dataflow.TraceValue;

import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public final class InstanceFieldAccessAnalyzer extends ChainFieldInsnNodeAnalyzer {

    public InstanceFieldAccessAnalyzer(final FieldInsnNodeAnalyzer analyzer) {
        super(analyzer);
    }

    protected void localCheck(final Frame frame, final MethodAnalysisData data) {
        if (!thisOnStack(frame, data)) {
//          System.err.println("Accesses non-local instance field!" + " [" + getAttribute() + "]");
        }
    }
    
    private boolean thisOnStack(final Frame frame, final MethodAnalysisData data) {
        boolean result = false;
        final int noParams = data.getMethodTypeTuple().getParameterTypes().length;
        if (frame != null) {
            if (noParams < frame.getStackSize()) {
                final Stack<Value> stack = new Stack<Value>();
                for (int i = 0; i < noParams; i++) {
                    stack.push(frame.pop());
                }
                final Value val = frame.pop();
                if (val instanceof TraceValue) {
                    result = ((TraceValue) val).isMaybeThisReferenceValue();
                }
                frame.push(val);
                for (int i = 0; i < noParams; i++) {
                    frame.push(stack.pop());
                }
            }
        }
        return result;
    }

}
