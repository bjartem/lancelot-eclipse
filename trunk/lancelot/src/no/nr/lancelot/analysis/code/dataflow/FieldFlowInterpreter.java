package no.nr.lancelot.analysis.code.dataflow;

import java.util.List;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.BasicInterpreter;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Interpreter;
import org.objectweb.asm.tree.analysis.Value;

public final class FieldFlowInterpreter implements Interpreter {

    private final BasicInterpreter bi = new BasicInterpreter();
    private int firstParamIndex = 0;
    private int lastParamIndex = 0;

    public Value binaryOperation(final AbstractInsnNode insn,
            final Value value1,
            final Value value2)
    throws AnalyzerException {
        return bi.binaryOperation(insn, value1, value2);
    }

    public Value copyOperation(final AbstractInsnNode insn,
            final Value value)
    throws AnalyzerException {
        switch (insn.getOpcode()) {
        case ILOAD:
        case LLOAD:
        case FLOAD:
        case DLOAD:
        case ALOAD:
            final VarInsnNode node = (VarInsnNode) insn;
            if (isLoadThis(node)) {
                return TraceValue.createThisReferenceValue(value);
            }
            if (isParameterVariable(node.var)) {
                return TraceValue.createParameterValue(value);
            }
            default:
                break;
        }
        final Value $ = bi.copyOperation(insn, value);
        return $;
    }

    private boolean isLoadThis(final VarInsnNode node) {
        return ALOAD == node.getOpcode() && 0 == node.var && firstParamIndex == 1;
    }

    private boolean isParameterVariable(final int var) {
        return (var >= firstParamIndex && var <= lastParamIndex);
    }

    public Value merge(final Value v, final Value w) {
        if (v instanceof TraceValue && w instanceof TraceValue) {
            final TraceValue $ = (TraceValue) v;
            final TraceValue dead = (TraceValue) w;
            if (dead.isMaybeCreatedObjectValue()) {
                $.markAsMaybeCreatedObjectValue();
            }
            if (dead.isMaybeFieldValue()) {
                $.markAsMaybeFieldValue();
            }
            if (dead.isMaybeParameterValue()) {
                $.markAsMaybeParameterValue();
            }
            if (dead.isMaybeThisReferenceValue()) {
                $.markAsMaybeThisReferenceValue();
            }
        }
        if (v instanceof TraceValue) {
            return v;
        }
        if (w instanceof TraceValue) {
            return w;
        }
        return bi.merge(v, w);
    }

    public Value naryOperation(final AbstractInsnNode insn,
            @SuppressWarnings("rawtypes") final List values)
            throws AnalyzerException {
        return bi.naryOperation(insn, values);
    }

    public Value newOperation(final AbstractInsnNode insn)
    throws AnalyzerException {
        final Value $ = bi.newOperation(insn);
        if (insn.getOpcode() == NEW) {
            return TraceValue.createCreatedObjectValue($);
        } else if (insn.getOpcode() == GETSTATIC) {
            return TraceValue.createFieldValue($);
        }
        return $;
    }

    public Value newValue(final Type type) {
        return bi.newValue(type);
    }

    public Value ternaryOperation(final AbstractInsnNode insn,
            final Value value1,
            final Value value2,
            final Value value3)
    throws AnalyzerException {
        return bi.ternaryOperation(insn, value1, value2, value3);
    }

    public Value unaryOperation(final AbstractInsnNode insn,
            final Value value)
    throws AnalyzerException {
        final Value $ = bi.unaryOperation(insn, value);
        if (insn.getOpcode() == GETFIELD) {
            return TraceValue.createFieldValue($);
        }
        if (insn.getOpcode() == CHECKCAST) {
            if (value instanceof TraceValue) {
                final TraceValue traceValue = (TraceValue) value;
                final TraceValue $1 = TraceValue.createVanillaValue($);
                if (traceValue.isMaybeCreatedObjectValue()) {
                    $1.markAsMaybeCreatedObjectValue();
                }
                if (traceValue.isMaybeFieldValue()) {
                    $1.markAsMaybeFieldValue();
                }
                if (traceValue.isMaybeParameterValue()) {
                    $1.markAsMaybeParameterValue();
                }
                if (traceValue.isMaybeThisReferenceValue()) {
                    $1.markAsMaybeThisReferenceValue();
                }
                return $1;
            }
        }
        return $;
    }

    public void setParameterRange(final boolean isStatic, final int noParams) {
        firstParamIndex = isStatic ? 0 : 1;
        lastParamIndex = firstParamIndex + noParams - 1;
    }

}
