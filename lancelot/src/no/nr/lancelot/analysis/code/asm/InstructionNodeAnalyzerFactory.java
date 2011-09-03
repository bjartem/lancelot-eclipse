package no.nr.lancelot.analysis.code.asm;


import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.GETSTATIC;
import static org.objectweb.asm.Opcodes.PUTFIELD;
import static org.objectweb.asm.Opcodes.PUTSTATIC;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class InstructionNodeAnalyzerFactory {

    private InstructionNodeAnalyzerFactory() {}

    public static InstructionNodeAnalyzer create(final AbstractInsnNode node) {
        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null!");
        }
        if (node instanceof VarInsnNode) {
            return new VarInsnNodeAnalyzer((VarInsnNode) node);
        } else if (node instanceof InsnNode) {
            return new InsnNodeAnalyzer((InsnNode) node);
        } else if (node instanceof TypeInsnNode) {
            return new TypeInsnNodeAnalyzer((TypeInsnNode) node);
        } else if (node instanceof MethodInsnNode) {
            return new MethodInsnNodeAnalyzer((MethodInsnNode) node);
        } else if (node instanceof FieldInsnNode) {
            return createFieldInsnNodeAnalyzer((FieldInsnNode) node); 
        } else if (node instanceof JumpInsnNode) {
            return new JumpInsnNodeAnalyzer((JumpInsnNode) node);
        } else if (node instanceof LabelNode) {
            return new LabelNodeAnalyzer((LabelNode) node);
        } else if (node instanceof TableSwitchInsnNode || node instanceof LookupSwitchInsnNode) {
            return new SwitchInsnNodeAnalyzer();
//      } else if (node instanceof TryCatchBlockNode) {
//          return new TryCatchBlockNodeAnalyzer();
        } else {
            return new NullInsnNodeAnalyzer();
        }
    }

    private static InstructionNodeAnalyzer createFieldInsnNodeAnalyzer(
            final FieldInsnNode node) {
        FieldInsnNodeAnalyzer analyzer = new NullFieldAnalyzer();
        switch (node.getOpcode()) {
        case GETFIELD:
            analyzer = new InstanceFieldAccessAnalyzer(new ReadFieldAccessAnalyzer(analyzer));
            break;
        case GETSTATIC:
            analyzer = new StaticFieldAccessAnalyzer(node, new ReadFieldAccessAnalyzer(analyzer));
            break;
        case PUTFIELD:
            analyzer = new InstanceFieldAccessAnalyzer(new WriteFieldAccessAnalyzer(analyzer));
            break;
        case PUTSTATIC:
            analyzer = new StaticFieldAccessAnalyzer(node, new WriteFieldAccessAnalyzer(analyzer));
            break;
            default:
                throw new IllegalArgumentException("Unexpected opcode for field: " + node.getOpcode());
        }
        return new FieldAccessAnalyzer(analyzer);
    }

}
