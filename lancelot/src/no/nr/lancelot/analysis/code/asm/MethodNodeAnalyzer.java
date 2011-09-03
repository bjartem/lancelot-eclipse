package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.ACC_STATIC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nr.lancelot.analysis.code.dataflow.FieldFlowInterpreter;
import no.nr.lancelot.analysis.code.descriptor.FieldDescriptorParser;
import no.nr.lancelot.analysis.code.descriptor.MethodDescriptorParser;
import no.nr.lancelot.model.Attribute;
import no.nr.lancelot.model.JavaMethod;
import no.nr.lancelot.model.TypeDictionary;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TryCatchBlockNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.Frame;

public final class MethodNodeAnalyzer {
    
    private static OpcodeTree opcodeTree = new OpcodeTree();
    
    public static OpcodeTree getOpcodeTree() {
        return opcodeTree;
    }
    
    private static int delegatingMethods = 0;
    
    private static final Map<Skippable, Integer> CLASSIFICATION_MAP = new HashMap<Skippable, Integer>();
    
    private static final Map<OpcodeSequence, Integer> OPCODE_MAP = new HashMap<OpcodeSequence, Integer>();
    
    private static String calcPercent(final int count, final int totalCount) {
        final long temp = 10000L * count / totalCount;
        final long percent = temp / 100;
        final long rest = temp % 100;
        String restStr = "" + rest;
        if (restStr.length() < 2) {
            restStr = "0" + restStr;
        }
        return percent + "." + restStr;
    }
    
    public static void printClassificationMap() {
        final Set<Skippable> keys = CLASSIFICATION_MAP.keySet();
        int totalCount = 0;
        for (final Skippable s : keys) {
            totalCount += CLASSIFICATION_MAP.get(s);
        }
        for (final Skippable s : keys) {
            final int count = CLASSIFICATION_MAP.get(s);
            final String percent = calcPercent(count, totalCount);
            System.out.println(s + ": " + count + " (" + percent + "%)");
        }
        
        System.out.println("No delegating methods: " + delegatingMethods + " (" + calcPercent(delegatingMethods, totalCount) + "%)");
        
        System.out.println("Total methods: " + totalCount);
    }

    /*
     * Returns null for constructors and methods with non-standard names.
     */
    public JavaMethod analyze(final ClassNode classNode, final MethodNode node) {
        checkNode(node);
        if (isNonStandard(classNode, node)) {
            return null;
        }
        
        final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple(node.desc);
        final int noParams = tuple.getParameterTypes().length;

        final FieldFlowInterpreter interpreter = new FieldFlowInterpreter();
        final Analyzer methodAnalyzer = new Analyzer(interpreter);

        // TODO: This must be fixed.
        final String owner = "foo";

        final MethodAnalysisData data = new MethodAnalysisData(node.name, tuple, classNode.name);
        
        final boolean isStatic = isStatic(node.access);
        
//      if (isStatic) {
//          data.setAttribute(Attribute.STATIC);
//      }

        interpreter.setParameterRange(isStatic, noParams);

        Frame[] frames = null;
        try {
            frames = methodAnalyzer.analyze(owner, node);
        } catch (final AnalyzerException e) {
            e.printStackTrace();
            throw new RuntimeException("Method analysis blew up.");
        }
        
        final AbstractInsnNode[] instructions =
            node.instructions.toArray();
        
        final int[] opcodes = new int[instructions.length];
        for (int i = 0; i < opcodes.length; i++) {
            final int opcode = instructions[i].getOpcode();
            opcodes[i] = opcode;
            if (opcode == Opcodes.ALOAD) {
                VarInsnNode vin = (VarInsnNode) instructions[i];
                if (vin.var == 0) {
                    opcodes[i] = Integer.MAX_VALUE;
                }
            }
        }
        opcodeTree.addBranch(opcodes);
        final OpcodeSequence seq = new OpcodeSequence(opcodes, node.name, classNode.name);
        
        if (! OPCODE_MAP.containsKey(seq)) {
            OPCODE_MAP.put(seq, 1);
        } else {
            int acc = OPCODE_MAP.remove(seq);
            OPCODE_MAP.put(seq, acc + 1);
        }
        
        final int noInstructions = countInstructions(instructions);
        
        if (isDegenerate(instructions, data)) {
            return null;
        }
        
        final DelegationFinder delFinder = new DelegationFinder(isStatic);
        if (delFinder.delegates(instructions)) {
            return null;
        }
        
        if (containsTryCatchBlock(node)) {
            examineTryCatchBlocks(data, classNode, node);
        }
                
        final TypeDictionary typeDict = scanForTypeNames(instructions);
        typeDict.add(tuple.getReturnType());
            
        for (int i = 0; i < frames.length; i++) {
            final Frame frame = frames[i];
            final AbstractInsnNode instruction = instructions[i];
            final InstructionNodeAnalyzer nodeAnalyzer =
                InstructionNodeAnalyzerFactory.create(instruction);
            nodeAnalyzer.check(frame, data);
        }
        
        if (exposesCheckedExceptions(node)) {
            data.setAttribute(Attribute.EXPOSES_CHECKED_EXCEPTIONS);
        }
        
        final JavaMethod result = new JavaMethod(node.name,
                node.desc,
                tuple.getReturnType(),
                tuple.getParameterTypes(),
                data.getAttributeMask(),
                typeDict,
                noInstructions,
                isStatic);
        
        return result;
    }

    private boolean isDegenerate(AbstractInsnNode[] instructions,
            MethodAnalysisData data) {
        if (isEmptyMethodBody(instructions)) {
            return true;
        }
        
        if (alwaysThrowsException(instructions)) {
            return true;
        }
        
        if (alwaysReturnsNull(instructions)) {
            return true;
        }
        
        if (alwaysReturnsSmallIntConstant(instructions)) {
            return true;
        }
        
        if (alwaysReturnsMediumIntConstant(instructions)) {
            return true;
        }
        
        if (alwaysReturnsConstant(instructions)) {
            return true;
        }
                
        
        return false;
    }

    private int countInstructions(final AbstractInsnNode[] instructions) {
        int $ = 0;
        for (int i = 0; i < instructions.length; i++) {
            if (instructions[i].getOpcode() >= 0) {
                ++$;
            }
        }
        return $;
    }

    private void examineTryCatchBlocks(final MethodAnalysisData data, 
            ClassNode classNode, final MethodNode node) {
        @SuppressWarnings("rawtypes")
        Iterator itor = node.tryCatchBlocks.iterator();
        boolean hasCatchBlock = false;
        boolean hasFinallyBlock = false;
        while (itor.hasNext()) {
            final Object o = itor.next();
            final TryCatchBlockNode block = (TryCatchBlockNode) o;
            if (block.type == null) {
                hasFinallyBlock = true;
            } else {
                hasCatchBlock = true;
            }
        }
        if (hasCatchBlock) {
            data.setAttribute(Attribute.CATCHES_EXCEPTIONS);
        }
        if (hasFinallyBlock) {
            data.markAsHavingFinallyBlock();
        }
    }

    private boolean isEmptyMethodBody(AbstractInsnNode[] instructions) {
        return instructions.length == 0
            || (instructions.length == 1 && instructions[0].getOpcode() == Opcodes.RETURN);
    }

    private boolean alwaysReturnsConstant(final AbstractInsnNode[] instructions) {
        if (instructions.length == 2) {
            return instructions[0].getOpcode() == Opcodes.LDC
                && instructions[1].getOpcode() == Opcodes.ARETURN;
        }
        return false;
    }

    // NB! "small int constant" includes boolean constants, 
    // since boolean constants do not exist on the JVM level.
    private boolean alwaysReturnsSmallIntConstant(final AbstractInsnNode[] instructions) {
        if (instructions.length == 2) {
            if (instructions[1].getOpcode() == Opcodes.IRETURN) {
                final AbstractInsnNode node0 = instructions[0];
                final int opcode = instructions[0].getOpcode();
                if (node0 instanceof InsnNode) {
                    final int[] opcodes = new int[] { Opcodes.ICONST_M1, Opcodes.ICONST_0,
                            Opcodes.ICONST_1, Opcodes.ICONST_2, Opcodes.ICONST_3, 
                            Opcodes.ICONST_4, Opcodes.ICONST_5 };
                    for (int i = 0; i < opcodes.length; i++) {
                        if (opcode == opcodes[i]) {
                            return true;
                        }
                    }
                }
            } 
        }
        return false;
    }
    
    private boolean alwaysReturnsMediumIntConstant(final AbstractInsnNode[] instructions) {
        if (instructions.length == 2) {
            if (instructions[1].getOpcode() == Opcodes.IRETURN) {
                final int opcode = instructions[0].getOpcode();
                return opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH;
            } 
        }
        return false;
    }

    // TODO: Clean up this mess!
    private boolean alwaysThrowsException(final AbstractInsnNode[] instructions) {
        if (instructions.length == 4) {
            return instructions[0].getOpcode() == Opcodes.NEW 
                && instructions[1].getOpcode() == Opcodes.DUP
                && instructions[2].getOpcode() == Opcodes.INVOKESPECIAL
                && instructions[3].getOpcode() == Opcodes.ATHROW;
        }
        if (instructions.length == 5) {
            return instructions[0].getOpcode() == Opcodes.NEW 
                && instructions[1].getOpcode() == Opcodes.DUP
                && instructions[2].getOpcode() == Opcodes.LDC
                && instructions[3].getOpcode() == Opcodes.INVOKESPECIAL
                && instructions[4].getOpcode() == Opcodes.ATHROW;
        }
        return false;
    }
    
    private boolean alwaysReturnsNull(final AbstractInsnNode[] instructions) {
        if (instructions.length == 2) {
            return instructions[0].getOpcode() == Opcodes.ACONST_NULL
                && instructions[1].getOpcode() == Opcodes.ARETURN;
        }
        return false;
    }

    private static boolean exposesCheckedExceptions(final MethodNode node) {
        return ! node.exceptions.isEmpty();
    }

    private static boolean containsTryCatchBlock(final MethodNode node) {
        return node.tryCatchBlocks.size() > 0;
    }

    private TypeDictionary scanForTypeNames(final AbstractInsnNode[] instructions) {
        final TypeDictionary typeDict = new TypeDictionary();
        for (final AbstractInsnNode insn : instructions) {
            if (insn instanceof FieldInsnNode) {
                final FieldInsnNode fieldInsn = (FieldInsnNode) insn;
                final String type = FieldDescriptorParser.parse(fieldInsn.desc);
                typeDict.add(type);
            }
            if (insn instanceof MethodInsnNode) {
                final MethodInsnNode methodInsn = (MethodInsnNode) insn;
                final MethodTypeTuple tuple = MethodDescriptorParser.getMethodTypeTuple(methodInsn.desc);
                typeDict.add(tuple.getReturnType());
                final String[] paramTypes = tuple.getParameterTypes();
                for (int i = 0; i < paramTypes.length; i++) {
                    typeDict.add(paramTypes[i]);
                }
            }
        }
        return typeDict;
    }

    private boolean isNonStandard(final ClassNode classNode, final MethodNode node) {
        Skippable s = null;
        final String name = node.name;
        final int access = node.access;
        // TODO: missing abstract methods???
        if (isInterface(classNode.access)) {
            s = Skippable.INTERFACE_METHOD;
        } else if (isAbstract(node.access)) {
            s = Skippable.ABSTRACT_METHOD;
        } else if (node.instructions.size() == 0) {
            s = Skippable.EMPTY_METHOD;
        } else if (isSynthetic(access)) {
            s = Skippable.SYNTHETIC_METHOD;
        } else if (name.indexOf('$') >= 0) {
            s = Skippable.DOLLAR_SIGN_IN_METHOD_NAME;
        } else if ("<init>".equals(name) || "<clinit>".equals(name)) {
            s = Skippable.CONSTRUCTOR_METHOD;
        } else if (name.length() <= 1) {
            s = Skippable.SINGLE_LETTER_METHOD_NAME;
        } else if (name.charAt(0) == '_') {
            s = Skippable.FIRST_LETTER_IS_UNDERSCORE;
        } else if (Character.isUpperCase(name.charAt(0))) {
            s = Skippable.FIRST_LETTER_IS_UPPERCASE;
        } else if (!isAsciiLowercase(name.charAt(0))) {
            s = Skippable.FIRST_LETTER_IS_NOT_LOWERCASE_ASCII;
        } else if (name.indexOf('_') > 0) {
            s = Skippable.UNDERSCORE_IN_METHOD_NAME;
        }
        classify(s);
        return (s != null);
    }
            
    private static void classify(final Skippable skippable) {
        final Skippable s = (skippable == null) ? Skippable.NOT_SKIPPED : skippable;
        int count = 0;
        if (CLASSIFICATION_MAP.containsKey(s)) {
            count += CLASSIFICATION_MAP.remove(s);
        }
        CLASSIFICATION_MAP.put(s, count + 1);
    }

    private void checkNode(final MethodNode node) {
        if (node == null) {
            throw new IllegalArgumentException("The method node is null!");
        }
        if (node.name == null) {
            throw new IllegalArgumentException("The method name is null!");
        }
        if (node.name.length() == 0) {
            throw new IllegalArgumentException("The method name is empty!");
        }
        if (node.desc == null) {
            throw new IllegalArgumentException("The method descriptor is null!");
        }
    }

    private static boolean isStatic(final int access) {
        return ((ACC_STATIC & access) > 0);
    }
    
    private static boolean isInterface(final int access) {
        return opcodeCheck(access, Opcodes.ACC_INTERFACE);
    }
    
    private static boolean isAbstract(final int access) {
        return opcodeCheck(access, Opcodes.ACC_ABSTRACT);
    }
    
    private static boolean isAsciiLowercase(final char c) {
        return (c >= 'a' && c <= 'z');
    }
    
    private static boolean isSynthetic(final int access) {
        return opcodeCheck(access, Opcodes.ACC_SYNTHETIC);
    }
    
    private static boolean opcodeCheck(final int access, final int opcode) {
        return ((access & opcode) > 0);
    }

    public static void printOpcodeMap() {
        final Map<Integer, List<OpcodeSequence>> map = new HashMap<Integer, List<OpcodeSequence>>();
        for (final OpcodeSequence seq : OPCODE_MAP.keySet()) {
            final Integer count = OPCODE_MAP.get(seq);
            if (count < 50) {
                continue;
            }
            if (! map.containsKey(count)) {
                map.put(count, new ArrayList<OpcodeSequence>());
            }
            map.get(count).add(seq);
        }
        final List<Integer> list = new ArrayList<Integer>(map.keySet());
        Collections.sort(list);
        Collections.reverse(list);
        System.out.println();
        int runningNumber = 0;
        for (final Integer i : list) {
            System.out.println(++runningNumber);
            System.out.println();
            System.out.println(i + " occurrences:");
            for (final OpcodeSequence seq : map.get(i)) {
                System.out.println();
                System.out.println(seq);
                System.out.println("Sample method name: " + seq.getSampleName());
                System.out.println("In class: " + seq.getSampleClass());
                System.out.println();
                System.out.println();
            }
            System.out.println("------------------------------------------");
            System.out.println();
        }
    }

}
