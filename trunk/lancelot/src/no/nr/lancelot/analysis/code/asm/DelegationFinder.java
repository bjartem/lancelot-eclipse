package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class DelegationFinder {
    
    private static final int[] loadCodes = {
        ALOAD, DLOAD, FLOAD, ILOAD, LLOAD
    };
    
    private static final int[] returnCodes = {
        RETURN, ARETURN, DRETURN, FRETURN, IRETURN, LRETURN
    };

    private final boolean staticMethod;
    
    public DelegationFinder(final boolean isStaticMethod) {
        this.staticMethod = isStaticMethod;
    }
    
    private State state = State.START;
    
    private enum State {
        START,
        LOADED_THIS,
        READ_INSTANCE_FIELD,
        READ_CLASS_FIELD,
        LOADED_PARAMETERS,
        INVOKED_METHOD,
        NARROWED_RESULT,
        RETURNED
    }
    
    private boolean ok(final AbstractInsnNode insn) {
        if (state == State.START) {
            return start(insn);
        }
        if (state == State.LOADED_THIS) {
            return loadedThis(insn);
        }
        if (state == State.READ_INSTANCE_FIELD) {
            return readInstanceField(insn);
        }
        if (state == State.READ_CLASS_FIELD) {
            return readClassField(insn);
        }
        if (state == State.LOADED_PARAMETERS) {
            return loadedParameters(insn);
        }
        if (state == State.INVOKED_METHOD) {
            return invokedMethod(insn);
        }
        if (state == State.NARROWED_RESULT) {
            return narrowedResult(insn);
        }
        if (state == State.RETURNED) {
            return returned(insn);
        }
        return false;
    }
        
    private boolean start(final AbstractInsnNode insn) {
        if (staticMethod) {
            return startStaticMethod(insn);
        } else {
            return startInstanceMethod(insn);
        }
    }

    private boolean startStaticMethod(final AbstractInsnNode insn) {
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_PARAMETERS;
            return true;
        }
        if (isStaticFieldReadInstruction(insn.getOpcode())) {
            state = State.READ_CLASS_FIELD;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean isStaticFieldReadInstruction(final int opcode) {
        return opcode == Opcodes.GETSTATIC;
    }

    private boolean startInstanceMethod(final AbstractInsnNode insn) {
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_THIS;
            return true;
        }
        if (isStaticFieldReadInstruction(insn.getOpcode())) {
            state = State.READ_CLASS_FIELD;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean loadedThis(final AbstractInsnNode insn) {
        if (isInstanceFieldReadInstruction(insn.getOpcode())) {
            state = State.READ_INSTANCE_FIELD;
            return true;
        }
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_PARAMETERS;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean isInstanceFieldReadInstruction(final int opcode) {
        return opcode == Opcodes.GETFIELD;
    }

    private boolean readInstanceField(final AbstractInsnNode insn) {
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_PARAMETERS;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean readClassField(final AbstractInsnNode insn) {
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_PARAMETERS;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean loadedParameters(final AbstractInsnNode insn) {
        if (isLoadInstruction(insn.getOpcode())) {
            state = State.LOADED_PARAMETERS;
            return true;
        }
        if (insn instanceof MethodInsnNode) {
            state = State.INVOKED_METHOD;
            return true;
        }
        return false;
    }

    private boolean invokedMethod(final AbstractInsnNode insn) {
        if (isTypeNarrowingInstruction(insn.getOpcode())) {
            state = State.NARROWED_RESULT;
            return true;
        }
        if (isReturnInstruction(insn.getOpcode())) {
            state = State.RETURNED;
            return true;
        }
        return false;
    }

    private boolean narrowedResult(final AbstractInsnNode insn) {
        if (isReturnInstruction(insn.getOpcode())) {
            state = State.RETURNED;
            return true;
        }
        return false;
    }

    private boolean returned(final AbstractInsnNode insn) {
        return false;
    }

    public boolean delegates(final AbstractInsnNode[] instructions) {
        for (int i = 0; i < instructions.length; i++) {
            if (!ok(instructions[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean delegatesOld(final AbstractInsnNode[] instructions) {

        
        if (instructions.length < 2) {
            return false;
        }
        
        int insn = 0;
        
        // OPTIONAL:
        // Load 'this' or parameters.
        while (insn < instructions.length && instructions[insn] instanceof VarInsnNode) {
            final VarInsnNode node = (VarInsnNode) instructions[insn];
            if (isLoadInstruction(node.getOpcode())) {              
                ++insn;
            } else {
                return false;
            }
        }
        
        if (insn == instructions.length) {
            return false;
        }
                
        // REQUIRED:
        // Call method.
        if (! (instructions[insn] instanceof MethodInsnNode)) {
            return false;
        }
        
        ++insn;
        
        if (insn == instructions.length) {
            return false;
        }
        
        // OPTIONAL:
        // Type narrowing (cast)
        if (isTypeNarrowingInstruction(instructions[insn].getOpcode())) {
            ++insn;
            if (insn == instructions.length) {
                return false;
            }
        }
        
        // REQUIRED:
        // Return (RETURN or xRETURN)
        return isReturnInstruction(instructions[insn].getOpcode());
    }

    
    private boolean isTypeNarrowingInstruction(
            final int opcode) {
        return opcode == CHECKCAST;
    }

    private static boolean isIn(final int opcode, final int[] codes) {
        for (int i = 0; i < codes.length; i++) {
            if (opcode == codes[i]) {
                return true;
            }
        }
        return false;
    }

    private static boolean isReturnInstruction(final int opcode) {
        return isIn(opcode, returnCodes);
    }

    private static boolean isLoadInstruction(final int opcode) {
        return isIn(opcode, loadCodes);
    }

}
