package no.nr.lancelot.analysis.code.asm;

import static org.objectweb.asm.Opcodes.CHECKCAST;
import static org.objectweb.asm.Opcodes.INSTANCEOF;
import static org.objectweb.asm.Opcodes.NEW;

import java.util.HashMap;
import java.util.Map;

import no.nr.lancelot.model.Attribute;

import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.analysis.Frame;

public final class TypeInsnNodeAnalyzer implements InstructionNodeAnalyzer {
    
    public static final Map<String, Integer> map = new HashMap<String, Integer>();

    private final TypeInsnNode node;

    public TypeInsnNodeAnalyzer(final TypeInsnNode node) {
        this.node = node;
    }

    public void check(final Frame frame, final MethodAnalysisData data) {
        final int opcode = node.getOpcode();
        if (opcode == NEW) {
            register(data);
            if (isOwnClass(data)) {
                data.setAttribute(Attribute.CREATES_OWN_CLASS_OBJECTS);
            }
            if (isException()) {
//              data.setAttribute(Attribute.CREATES_EXCEPTION_OBJECTS);
            } else {
                data.setAttribute(Attribute.CREATES_REGULAR_OBJECTS);
            }
//          data.setAttribute(Attribute.CREATES_OBJECTS);
        } else if (opcode == CHECKCAST || opcode == INSTANCEOF) {
            data.setAttribute(Attribute.TYPE_MANIPULATOR);
        }
    }
    
    private boolean isOwnClass(final MethodAnalysisData data) {
        return data.getClassName().equals(node.desc);
    }

    private boolean isException() {
        final String[] types = new String[] { "Exception", "Error", "Throwable" };
        for (int i = 0; i < types.length; i++) {
            final String t = types[i];
            if (node.desc.endsWith(t)) {
                seen(t);
                return true;
            }
        }
        return false;
    }
    
    private void register(final MethodAnalysisData data) {
        final String type = node.desc;
        
        if (isJavaType(type)) {
            registerJavaType(data, type);
        } else {
            registerNonJavaType(data, type);
        }
    }
    
    private boolean isJavaType(final String type) {
        return isJavaPackageType(type) || isJavaxPackageType(type);
    }
    
    private boolean isJavaPackageType(String type) {
        return type.startsWith("java/");
    }

    private boolean isJavaxPackageType(final String type) {
        return type.startsWith("javax/");
    }

    private void registerJavaType(final MethodAnalysisData data, final String type) {
//      data.setAttribute(Attribute.CREATES_JAVA_OBJECTS);
        if (isJavaPackageType(type)) {
            registerJavaPackageType(data, type);
        } else if (isJavaxPackageType(type)) {
            registerJavaxPackageType(data, type);
        }
    }
    
    private void registerJavaPackageType(MethodAnalysisData data, String type) {
        seen("java/*");
        final String s = type.substring("java/".length());
        if (s.endsWith("Exception")) {
            seen("java/**/*Exception");
        }
        final int nextDash = s.indexOf('/');
        if (nextDash > 0) {
            final String nextPart = s.substring(0, nextDash);
            if (isCommonJavaPackage(s)) {
                seen("java/" + nextPart + "/*");
                registerCommonJavaPackage(data, type, s);
            } else {
                seen("java/---other---");
            }
        }
    }
    
    private void registerJavaxPackageType(MethodAnalysisData data, String type) {
        seen("javax/*");
        final String s = type.substring("javax/".length());
        final int nextDash = s.indexOf('/');
        if (nextDash > 0) {
            final String nextPart = s.substring(0, nextDash);
            if (isCommonJavaxPackage(s)) {
                seen("javax/" + nextPart + "/*");
            } else {
                seen("javax/---other---");
            }
        }
    }
    
    private static boolean isCommonJavaPackage(final String s) {
        return isCommonPackage(s, new String[] { "lang", "util", "io" });
    }

    private static boolean isCommonJavaxPackage(final String s) {
        return isCommonPackage(s, new String[] { "swing", "xml", "management", "naming", "servlet" });
    }
    
    private static boolean isCommonPackage(final String s, final String[] packages) {
        for (int i = 0; i < packages.length; i++) {
            if (s.startsWith(packages[i] + "/")) {
                return true;
            }
        }
        return false;
    }

    private void registerNonJavaType(final MethodAnalysisData data, final String type) {
        seen("---other---");
        data.setAttribute(Attribute.CREATES_CUSTOM_OBJECTS);
    }

    private void registerCommonJavaPackage(final MethodAnalysisData data, final String type, final String s) {
        if (s.startsWith("lang/")) {
            if (s.endsWith("Exception")) {
                seen("java/lang/**/*Exception");
                if (isCommonException()) {
                    seen(type);
                } else {
                    seen("java/lang/**/*Exception (other)");
                }
                if (type.equals("java/"))
                seen(type);
            } else if (isCommonStringType(s)) {
                seen(type);
                data.setAttribute(Attribute.CREATES_STRING_OBJECTS);
            } else if (s.equals("lang/Object")) {
                seen(type);
            } else {
                seen("java/lang/---other---");
            }
        } else if (s.startsWith("util/")) {
            if (type.endsWith("List")) {
                seen("java/util/**/*List");
            } else if (type.endsWith("Exception")) {
                seen("java/util/**/*Exception");
            } else {
                seen("java/util/---other---");
            }
        } 
    }

    private boolean isCommonStringType(final String s) {
        final String prefix = "lang/String";
        if (s.startsWith(prefix)) {
            final String rest = s.substring(prefix.length());
            return rest.equals("") || rest.equals("Builder") || rest.equals("Buffer");
        }
        return false;
    }

    private boolean isCommonException() {
        final String type = node.desc;
        final String[] exceptions = new String[] { "IllegalArgumentException", 
                "UnsupportedOperationException", "IllegalStateException", "RuntimeException" };
        for (int i = 0; i < exceptions.length; i++) {
            if (type.equals("java/lang/" + exceptions[i])) {
                return true;
            }
        }
        return false;
    }

    private void seen(final String s) {
        if (! map.containsKey(s)) {
            map.put(s, 0);
        }
        int acc = map.remove(s);
        map.put(s, acc + 1);
    }

}
