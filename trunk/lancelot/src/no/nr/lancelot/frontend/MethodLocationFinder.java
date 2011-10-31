package no.nr.lancelot.frontend;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class MethodLocationFinder {
    private MethodLocationFinder() {}
    
    public static String makeKey(final String methodName, final String desc) {
        return methodName + "$$$" + desc;
    }
    
    public static Map<String, Integer> findMethodLocations(final byte[] classData) 
    throws IOException {
        if (classData == null) {
            throw new IllegalArgumentException();
        }
        
        final Map<String, Integer> result = new HashMap<String, Integer>();
        
        new ClassReader(new ByteArrayInputStream(classData)).accept(
            new ClassVisitor() {
                @Override
                public MethodVisitor visitMethod(
                    final int access, 
                    final String name, 
                    final String desc,
                    final String signature, 
                    final String[] exceptions
                ) {
                    final String key = makeKey(name, desc);
                    
                    return new MethodVisitor() {
                        @Override
                        public void visitLineNumber(final int lineNumber, final Label label) {
                            if (!result.containsKey(key))
                                result.put(key, lineNumber);
                        }
                        
                        @Override
                        public void visitVarInsn(int arg0, int arg1) {
                        }
                        
                        @Override
                        public void visitTypeInsn(int arg0, String arg1) {
                        }
                        
                        @Override
                        public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2,
                                String arg3) {
                        }
                        
                        @Override
                        public void visitTableSwitchInsn(int arg0, int arg1, Label arg2,
                                Label[] arg3) {
                        }
                        
                        @Override
                        public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1,
                                boolean arg2) {
                            return null;
                        }
                        
                        @Override
                        public void visitMultiANewArrayInsn(String arg0, int arg1) {
                        }
                        
                        @Override
                        public void visitMethodInsn(int arg0, String arg1, String arg2, String arg3) {
                        }
                        
                        @Override
                        public void visitMaxs(int arg0, int arg1) {
                        }
                        
                        @Override
                        public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                        }
                        
                        @Override
                        public void visitLocalVariable(String arg0, String arg1, String arg2,
                                Label arg3, Label arg4, int arg5) {
                        }
                        
                        @Override
                        public void visitLdcInsn(Object arg0) {
                        }
                        
                        @Override
                        public void visitLabel(Label arg0) {
                        }
                        
                        @Override
                        public void visitJumpInsn(int arg0, Label arg1) {
                        }
                        
                        @Override
                        public void visitIntInsn(int arg0, int arg1) {
                        }
                        
                        @Override
                        public void visitInsn(int arg0) {
                        }
                        
                        @Override
                        public void visitIincInsn(int arg0, int arg1) {
                        }
                        
                        @Override
                        public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3,
                                Object[] arg4) {
                        }
                        
                        @Override
                        public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
                        }
                        
                        @Override
                        public void visitEnd() {
                        }
                        
                        @Override
                        public void visitCode() {
                        }
                        
                        @Override
                        public void visitAttribute(Attribute arg0) {
                        }
                        
                        @Override
                        public AnnotationVisitor visitAnnotationDefault() {
                            return null;
                        }
                        
                        @Override
                        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                            return null;
                        }
                    };
                }

                @Override
                public void visitSource(String arg0, String arg1) {}
                
                @Override
                public void visitOuterClass(String arg0, String arg1, String arg2) {}
                
                
                @Override
                public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {}
                
                @Override
                public FieldVisitor visitField(int arg0, String arg1, String arg2,
                        String arg3, Object arg4) {
                    return null;
                }
                
                @Override
                public void visitEnd() {
                    
                }
                
                @Override
                public void visitAttribute(Attribute arg0) {
                }
                
                @Override
                public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                    return null;
                }
                
                @Override
                public void visit(int arg0, int arg1, String arg2, String arg3,
                        String arg4, String[] arg5) {
                }
            }, 0);
        
        return result;
    }
}
