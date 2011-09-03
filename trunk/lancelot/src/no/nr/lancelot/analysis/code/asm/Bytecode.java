package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.*;

public final class Bytecode {
    
    public static String bytecode(final int opcode) {
        switch(opcode) {
        case Integer.MAX_VALUE: return "ALOAD_0";       // Special cheat case.
        case NOP: return "NOP";                         //   0
        case ACONST_NULL: return "ACONST_NULL";         //   1
        case ICONST_M1: return "ICONST_M1";             //   2
        case ICONST_0: return "ICONST_0";               //   3
        case ICONST_1: return "ICONST_1";               //   4
        case ICONST_2: return "ICONST_2";               //   5
        case ICONST_3: return "ICONST_3";               //   6
        case ICONST_4: return "ICONST_4";               //   7
        case ICONST_5: return "ICONST_5";               //   8
        case LCONST_0: return "LCONST_0";               //   9
        case LCONST_1: return "LCONST_1";               //  10
        case FCONST_0: return "FCONST_0";               //  11
        case FCONST_1: return "FCONST_1";               //  12
        case FCONST_2: return "FCONST_2";               //  13
        case DCONST_0: return "DCONST_0";               //  14
        case DCONST_1: return "DCONST_1";               //  15
        case BIPUSH: return "BIPUSH";                   //  16
        case SIPUSH: return "SIPUSH";                   //  17
        case LDC: return "LDC";                         //  18
        case ILOAD: return "ILOAD";                     //  21
        case LLOAD: return "LLOAD";                     //  22
        case FLOAD: return "FLOAD";                     //  23
        case DLOAD: return "DLOAD";                     //  24
        case ALOAD: return "ALOAD";                     //  25
        case IALOAD: return "IALOAD";                   //  46
        case LALOAD: return "LALOAD";                   //  47
        case FALOAD: return "FALOAD";                   //  48
        case DALOAD: return "DALOAD";                   //  49
        case AALOAD: return "AALOAD";                   //  50
        case BALOAD: return "BALOAD";                   //  51
        case CALOAD: return "CALOAD";                   //  52
        case SALOAD: return "SALOAD";                   //  53
        case ISTORE: return "ISTORE";                   //  54
        case LSTORE: return "LSTORE";                   //  55
        case FSTORE: return "FSTORE";                   //  56
        case DSTORE: return "DSTORE";                   //  57
        case ASTORE: return "ASTORE";                   //  58
        case IASTORE: return "IASTORE";                 //  79
        case LASTORE: return "LASTORE";                 //  80
        case FASTORE: return "FASTORE";                 //  81
        case DASTORE: return "DASTORE";                 //  82
        case AASTORE: return "AASTORE";                 //  83
        case BASTORE: return "BASTORE";                 //  84
        case CASTORE: return "CASTORE";                 //  85
        case SASTORE: return "SASTORE";                 //  86
        case POP: return "POP";                         //  87
        case POP2: return "POP2";                       //  88
        case DUP: return "DUP";                         //  89
        case DUP_X1: return "DUP_X1";                   //  90
        case DUP_X2: return "DUP_X2";                   //  91
        case DUP2: return "DUP2";                       //  92
        case DUP2_X1: return "DUP2_X1";                 //  93
        case DUP2_X2: return "DUP2_X2";                 //  94
        case SWAP: return "SWAP";                       //  95
        case IADD: return "IADD";                       //  96
        case LADD: return "LADD";                       //  97
        case FADD: return "FADD";                       //  98
        case DADD: return "DADD";                       //  99
        case ISUB: return "ISUB";                       // 100
        case LSUB: return "LSUB";                       // 101
        case FSUB: return "FSUB";                       // 102
        case DSUB: return "DSUB";                       // 103
        case IMUL: return "IMUL";                       // 104
        case LMUL: return "LMUL";                       // 105
        case FMUL: return "FMUL";                       // 106
        case DMUL: return "DMUL";                       // 107
        case IDIV: return "IDIV";                       // 108
        case LDIV: return "LDIV";                       // 109
        case FDIV: return "FDIV";                       // 110
        case DDIV: return "DDIV";                       // 111
        case IREM: return "IREM";                       // 112
        case LREM: return "LREM";                       // 113
        case FREM: return "FREM";                       // 114
        case DREM: return "DREM";                       // 115
        case INEG: return "INEG";                       // 116
        case LNEG: return "LNEG";                       // 117
        case FNEG: return "FNEG";                       // 118
        case DNEG: return "DNEG";                       // 119
        case ISHL: return "ISHL";                       // 120
        case LSHL: return "LSHL";                       // 121
        case ISHR: return "ISHR";                       // 122
        case LSHR: return "LSHR";                       // 123
        case IUSHR: return "IUSHR";                     // 124
        case LUSHR: return "LUSHR";                     // 125
        case IAND: return "IAND";                       // 126
        case LAND: return "LAND";                       // 127
        case IOR: return "IOR";                         // 128
        case LOR: return "LOR";                         // 129
        case IXOR: return "IXOR";                       // 130
        case LXOR: return "LXOR";                       // 131
        case IINC: return "IINC";                       // 132
        case I2L: return "I2L";                         // 133
        case I2F: return "I2F";                         // 134
        case I2D: return "I2D";                         // 135
        case L2I: return "L2I";                         // 136
        case L2F: return "L2F";                         // 137
        case L2D: return "L2D";                         // 138
        case F2I: return "F2I";                         // 139
        case F2L: return "F2L";                         // 140
        case F2D: return "F2D";                         // 141
        case D2I: return "D2I";                         // 142
        case D2L: return "D2L";                         // 143
        case D2F: return "D2F";                         // 144
        case I2B: return "I2B";                         // 145
        case I2C: return "I2C";                         // 146
        case I2S: return "I2S";                         // 147
        case LCMP: return "LCMP";                       // 148
        case FCMPL: return "FCMPL";                     // 149
        case FCMPG: return "FCMPG";                     // 150
        case DCMPL: return "DCMPL";                     // 151
        case DCMPG: return "DCMPG";                     // 152
        case IFEQ: return "IFEQ";                       // 153
        case IFNE: return "IFNE";                       // 154
        case IFLT: return "IFLT";                       // 155
        case IFGE: return "IFGE";                       // 156
        case IFGT: return "IFGT";                       // 157
        case IFLE: return "IFLE";                       // 158
        case IF_ICMPEQ: return "IF_ICMPEQ";             // 159
        case IF_ICMPNE: return "IF_ICMPNE";             // 160
        case IF_ICMPLT: return "IF_ICMPLT";             // 161
        case IF_ICMPGE: return "IF_ICMPGE";             // 162
        case IF_ICMPGT: return "IF_ICMPGT";             // 163
        case IF_ICMPLE: return "IF_ICMPLE";             // 164
        case IF_ACMPEQ: return "IF_ACMPEQ";             // 165
        case IF_ACMPNE: return "IF_ACMPNE";             // 166
        case GOTO: return "GOTO";                       // 167
        case JSR: return "JSR";                         // 168
        case RET: return "RET";                         // 169
        case TABLESWITCH: return "TABLESWITCH";         // 170
        case LOOKUPSWITCH: return "LOOKUPSWITCH";       // 171
        case IRETURN: return "IRETURN";                 // 172
        case LRETURN: return "LRETURN";                 // 173
        case FRETURN: return "FRETURN";                 // 174
        case DRETURN: return "DRETURN";                 // 175
        case ARETURN: return "ARETURN";                 // 176
        case RETURN: return "RETURN";                   // 177
        case GETSTATIC: return "GETSTATIC";             // 178
        case PUTSTATIC: return "PUTSTATIC";             // 179
        case GETFIELD: return "GETFIELD";               // 180
        case PUTFIELD: return "PUTFIELD";               // 181
        case INVOKEVIRTUAL: return "INVOKEVIRTUAL";     // 182
        case INVOKESPECIAL: return "INVOKESPECIAL";     // 183
        case INVOKESTATIC: return "INVOKESTATIC";       // 184
        case INVOKEINTERFACE: return "INVOKEINTERFACE"; // 185
        case NEW: return "NEW";                         // 187
        case NEWARRAY: return "NEWARRAY";               // 188
        case ANEWARRAY: return "ANEWARRAY";             // 189
        case ARRAYLENGTH: return "ARRAYLENGTH";         // 190
        case ATHROW: return "ATHROW";                   // 191
        case CHECKCAST: return "CHECKCAST";             // 192
        case INSTANCEOF: return "INSTANCEOF";           // 193
        case MONITORENTER: return "MONITORENTER";       // 194
        case MONITOREXIT: return "MONITOREXIT";         // 195
        case MULTIANEWARRAY: return "MULTIANEWARRAY";   // 197
        case IFNULL: return "IFNULL";                   // 198
        case IFNONNULL: return "IFNONNULL";             // 199
        
        }
        if (opcode >= 0) {
            System.err.println("> " + opcode);
        }
        return Integer.toString(opcode);
    }

}

