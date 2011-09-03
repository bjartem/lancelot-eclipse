package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.*;
import no.nr.lancelot.analysis.code.dataflow.TraceValue;
import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.Value;

public final class InsnNodeAnalyzer implements InstructionNodeAnalyzer {

    private static final int[] STORE_INSN_CODES = {
        IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
    };

    private final InsnNode node;

    public InsnNodeAnalyzer(final InsnNode node) {
        this.node = node;
    }

    public void check(final Frame frame, final MethodAnalysisData data) {
        final int opcode = node.getOpcode();
        if (isThrowInsn()) {
            handleThrowInsn(frame, data);
        } else if (isMonitorInsn()) {
            handleMonitorInsn(data);
        } else if (isLocalAssignmentInsn()) {
            handleLocalAssignmentInsn(data);
        } else if (isReturnInsn(opcode)) {
            handleReturnInsn(frame, data);
        }
    }
    
    private boolean isMonitorInsn() {
        return node.getOpcode() == MONITORENTER 
            || node.getOpcode() == MONITOREXIT;
    }

    private void handleMonitorInsn(final MethodAnalysisData data) {
        data.markAsSeenMonitors();
    }

    private void handleReturnInsn(final Frame frame, final MethodAnalysisData data) {
        data.incrementReturns();
        if (data.hasMultipleReturns()) {
            data.setAttribute(Attribute.MULTIPLE_RETURNS);
        }
        if (isReturnValueInsn(node.getOpcode()) && frame != null) {
            final Value val = frame.pop();
            if (val instanceof TraceValue) {
                final TraceValue traceVal = (TraceValue) val;
                if (traceVal.isMaybeFieldValue()) {
                    data.setAttribute(Attribute.RETURNS_FIELD_VALUE);
                }
//              if (traceVal.isMaybeParameterValue()) {
//                  data.setAttribute(Attribute.PARAMETER_FALLTHROUGH);
//              }
                if (traceVal.isMaybeCreatedObjectValue()) {
                    data.setAttribute(Attribute.RETURNS_CREATED_OBJECT);
                }
            }
            frame.push(val);
        }
    }

    private boolean isThrowInsn() {
        return node.getOpcode() == ATHROW;
    }
    
    private Value peek(final Frame frame) {
        final Value $ = frame.pop();
        frame.push($);
        return $;
    }
    
    private void handleThrowInsn(final Frame frame, final MethodAnalysisData data) {
        if (frame == null) {
            System.err.println("handleThrowInsn: frame is null!");
            return;
        }
        final Value val = peek(frame);
        if (shouldRegisterThrow(val, data)) {
            data.setAttribute(Attribute.THROWS_EXCEPTIONS);
        }
    }
    
    private boolean shouldRegisterThrow(final Value val, final MethodAnalysisData data) {
        if (val instanceof TraceValue) {
            return true;
        }
        // From here: Rethrow!
        if (!data.hasFinallyBlock()) {
            return true;
        }
        // From here: Rethrow, with finally block!
        if (data.isAttributeSet(Attribute.CATCHES_EXCEPTIONS)) {
            return true;
        }
        // From here: Rethrow, with finally block and no catch block!
        return false;
    }

    private void handleLocalAssignmentInsn(final MethodAnalysisData data) {
    }

    private boolean isLocalAssignmentInsn() {
        return isMember(node.getOpcode(), STORE_INSN_CODES);
    }

    private boolean isMember(final int opcode, final int[] opcodes) {
        for (int i = 0; i < opcodes.length; i++) {
            if (opcode == opcodes[i]) {
                return true;
            }
        }
        return false;
    }

    private static boolean isReturnInsn(final int opcode) {
        return opcode == RETURN || isReturnValueInsn(opcode);
    }
    
    private static boolean isReturnValueInsn(final int opcode) {
        switch (opcode) {
        case IRETURN:
        case LRETURN:
        case FRETURN:
        case DRETURN:
        case ARETURN:
            return true;
            default:
                return false;
        }
    }

}
