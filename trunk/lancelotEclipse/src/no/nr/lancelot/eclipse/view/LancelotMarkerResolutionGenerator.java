/*******************************************************************************
 * Copyright (c) 2011 Norwegian Computing Center.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Norwegian Computing Center - initial API and implementation
 ******************************************************************************/
/* The code for AddIgnoreAnnotationResolution is directly derived from
 * code for the Eclipse JDT version 3.6, which is copyright (c) 
 * IBM Corporation and others and made available under the terms of 
 * the Eclipse Public License v1.0 (http://www.eclipse.org/legal/epl-v10.html).
 */
package no.nr.lancelot.eclipse.view;

import static no.nr.lancelot.eclipse.view.LancelotMarkerUtil.ALTERNATIVE_NAMES_ATTRIBUTE;
import static no.nr.lancelot.eclipse.view.LancelotMarkerUtil.ALTERNATIVE_NAMES_SEPARATOR_RE;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import no.nr.lancelot.eclipse.LancelotPlugin;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Annotation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ChildListPropertyDescriptor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.jdt.core.dom.rewrite.ImportRewrite;
import org.eclipse.jdt.core.dom.rewrite.ListRewrite;
import org.eclipse.jdt.ui.CodeStyleConfiguration;
import org.eclipse.jdt.ui.refactoring.RenameSupport;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.eclipse.ui.PlatformUI;

public final class LancelotMarkerResolutionGenerator implements IMarkerResolutionGenerator {
    private static final IMarkerResolution[] NO_RESOLUTIONS = {};
    
    private static final String[] EMPTY_STRING_ARRAY = {};

    @Override
    public IMarkerResolution[] getResolutions(final IMarker marker) {
        try {
            assertMarkerIsValid(marker);
        } catch (Exception e) {
            LancelotPlugin.logException(e);
        }

        final String[] alternativeNames = findAlternativeNames(marker);

        final IMethod method = LancelotMarkerUtil.findMethod(marker);
        if (method == null)
            return NO_RESOLUTIONS;

        final IMarkerResolution[] res = new IMarkerResolution[alternativeNames.length + 1];

        res[0] = new AddIgnoreAnnotationResolution(method, marker);

        for (int i = 0; i < alternativeNames.length; ++i)
            res[i + 1] = new LancelotRenameResolution(method, alternativeNames[i], marker);

        return res;
    }
    
    private String[] findAlternativeNames(final IMarker marker) {
        final String serializedNames = marker.getAttribute(ALTERNATIVE_NAMES_ATTRIBUTE, null);
        if (serializedNames == null) {
            LancelotPlugin.logError("marker " + marker + " has no alternative names attribute!");
            return EMPTY_STRING_ARRAY;
        }
        
        final boolean hasNoAlternatives = serializedNames.length() == 0;
        if (hasNoAlternatives)
            return EMPTY_STRING_ARRAY;

        return serializedNames.split(ALTERNATIVE_NAMES_SEPARATOR_RE);
    }

    private void assertMarkerIsValid(final IMarker marker) throws CoreException {
        if (marker == null)
            throw new RuntimeException("Marker is null!");

        if (!marker.isSubtypeOf(LancelotMarkerUtil.BUG_MARKER_TYPE))
            throw new RuntimeException("Marker is not of the correct class!");
    }

    protected static class LancelotRenameResolution implements IMarkerResolution2 {
        private final String alternativeName;
        private final IMethod method;
        private final IMarker intendedMarker;
        private final String label;

        public LancelotRenameResolution(final IMethod method, final String alternativeName, final IMarker intendedMarker) {
            this.method = method;
            this.alternativeName = alternativeName;
            this.intendedMarker = intendedMarker;
            
            // TODO. This is too ad hoc.
            final String firstSegment = extractFirstCamelCasedSegment(method.getElementName());
            if (alternativeName.equals(firstSegment + "-*"))
                label = String.format("Keep '%s', change suffix", firstSegment);
            else
                label = String.format("Rename to '%s'", alternativeName);
        }

        @Override
        public String getLabel() {
            return label;
        }
        
        // TODO. Move to utility class.
        private static String extractFirstCamelCasedSegment(final String s) {
            int p = 0;
            for (char c : s.toCharArray()) {
                if (Character.isUpperCase(c))
                    break;
                p++;
            }   
            return s.substring(0, p);
        }

        @Override
        public void run(final IMarker marker) {
            if (!intendedMarker.equals(marker))
                throw new RuntimeException("Resolution called for another marker than intended!");

            try {
                final RenameSupport renameSupport = RenameSupport.create(method, alternativeName,
                        RenameSupport.UPDATE_REFERENCES);
                final Shell defaultShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
                final boolean renamingWasDone = renameSupport.openDialog(defaultShell, false);
                if (renamingWasDone)
                    marker.delete();
            } catch (CoreException e) {
                LancelotPlugin.logException(e);
            }
        }

        @Override
        public String getDescription() {
            return "The semantics of this method is typical for methods named \"" + alternativeName + "\".";
        }

        @Override
        public Image getImage() {
            return null;
        }
    }

    /* Note that 95% of this code is directly derived from the Eclipse JDT!
     * Be very careful with edits! :-)
     */
    protected static final class AddIgnoreAnnotationResolution implements IMarkerResolution {
        private final IMethod method;
        private final IMarker intendedMarker;

        public AddIgnoreAnnotationResolution(final IMethod method, final IMarker intendedMarker) {
            this.method = method;
            this.intendedMarker = intendedMarker;
        }

        @Override
        public String getLabel() {
            final String methodName = method.getElementName();
            return "Add @SuppressWarnings 'NamingBug' to '" + methodName + "'";
        }

        @Override
        public void run(final IMarker marker) {
            if (!intendedMarker.equals(marker))
                throw new RuntimeException("Resolution called for another marker than intended!");

            final ASTRewrite rewrite = createRewrite();

            try {
                final TextEdit edit = rewrite.rewriteAST();
                method.getDeclaringType().getCompilationUnit().applyTextEdit(edit, new NullProgressMonitor());
            } catch (JavaModelException e) {
                e.printStackTrace();
            }

        }

        @SuppressWarnings("rawtypes")
        protected ASTRewrite createRewrite() {
            String fWarningToken = LancelotPlugin.SUPPRESS_WARNINGS_NAME;
            ChildListPropertyDescriptor fProperty = MethodDeclaration.MODIFIERS2_PROPERTY;

            ASTNode fNode = findMethodNode();
            AST ast = fNode.getAST();
            ASTRewrite rewrite = ASTRewrite.create(ast);

            StringLiteral newStringLiteral = ast.newStringLiteral();
            newStringLiteral.setLiteralValue(fWarningToken);

            Annotation existing = findExistingAnnotation((List) fNode.getStructuralProperty(fProperty));
            if (existing == null) {
                ListRewrite listRewrite = rewrite.getListRewrite(fNode, fProperty);

                SingleMemberAnnotation newAnnot = ast.newSingleMemberAnnotation();
                String importString = createImportRewrite((CompilationUnit) fNode.getRoot()).addImport(
                        "java.lang.SuppressWarnings"); //$NON-NLS-1$
                newAnnot.setTypeName(ast.newName(importString));

                newAnnot.setValue(newStringLiteral);

                listRewrite.insertFirst(newAnnot, null);
            } else if (existing instanceof SingleMemberAnnotation) {
                SingleMemberAnnotation annotation = (SingleMemberAnnotation) existing;
                Expression value = annotation.getValue();
                if (!addSuppressArgument(rewrite, value, newStringLiteral)) {
                    rewrite.set(existing, SingleMemberAnnotation.VALUE_PROPERTY, newStringLiteral, null);
                }
            } else if (existing instanceof NormalAnnotation) {
                NormalAnnotation annotation = (NormalAnnotation) existing;
                Expression value = findValue(annotation.values());
                if (!addSuppressArgument(rewrite, value, newStringLiteral)) {
                    ListRewrite listRewrite = rewrite.getListRewrite(annotation, NormalAnnotation.VALUES_PROPERTY);
                    MemberValuePair pair = ast.newMemberValuePair();
                    pair.setName(ast.newSimpleName("value")); //$NON-NLS-1$
                    pair.setValue(newStringLiteral);
                    listRewrite.insertFirst(pair, null);
                }
            }
            return rewrite;
        }
        
        protected ASTNode findMethodNode() {
            final ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setSource(method.getDeclaringType().getCompilationUnit());
            parser.setResolveBindings(false);

            final CompilationUnit cu = (CompilationUnit) parser.createAST(new NullProgressMonitor());
            
            final AtomicReference<ASTNode> nodeRef = new AtomicReference<ASTNode>(null);

            final int methodPosition;
            try {
                methodPosition = method.getSourceRange().getOffset();
            } catch (JavaModelException e1) {
                return null;
            }

            cu.accept(new ASTVisitor() {
                @Override
                public boolean visit(final MethodDeclaration node) {
                    if (node.getStartPosition() == methodPosition)
                        nodeRef.set(node);
                    return true; // Visit children
                }
            });

            return nodeRef.get();
        }

        @SuppressWarnings("unchecked")
        protected static boolean addSuppressArgument(
            final ASTRewrite rewrite, 
            final Expression value, 
            final StringLiteral newStringLiteral
        ) {
            if (value instanceof ArrayInitializer) {
                ListRewrite listRewrite = rewrite.getListRewrite(value, ArrayInitializer.EXPRESSIONS_PROPERTY);
                listRewrite.insertLast(newStringLiteral, null);
            } else if (value instanceof StringLiteral) {
                ArrayInitializer newArr = rewrite.getAST().newArrayInitializer();
                newArr.expressions().add(rewrite.createMoveTarget(value));
                newArr.expressions().add(newStringLiteral);
                rewrite.replace(value, newArr, null);
            } else {
                return false;
            }
            
            return true;
        }

        @SuppressWarnings("rawtypes")
        private static Expression findValue(final List keyValues) {
            for (int i = 0, len = keyValues.size(); i < len; i++) {
                final MemberValuePair curr = (MemberValuePair) keyValues.get(i);
                if ("value".equals(curr.getName().getIdentifier())) { //$NON-NLS-1$
                    return curr.getValue();
                }
            }
            
            return null;
        }

        private static ImportRewrite createImportRewrite(final CompilationUnit astRoot) {
            return CodeStyleConfiguration.createImportRewrite(astRoot, true);
        }

        @SuppressWarnings("rawtypes")
        private static Annotation findExistingAnnotation(final List modifiers) {
            for (int i = 0, len = modifiers.size(); i < len; i++) {
                Object curr = modifiers.get(i);
                if (curr instanceof NormalAnnotation || curr instanceof SingleMemberAnnotation) {
                    Annotation annotation = (Annotation) curr;
                    ITypeBinding typeBinding = annotation.resolveTypeBinding();
                    if (typeBinding != null) {
                        if ("java.lang.SuppressWarnings".equals(typeBinding.getQualifiedName())) { //$NON-NLS-1$
                            return annotation;
                        }
                    } else {
                        String fullyQualifiedName = annotation.getTypeName().getFullyQualifiedName();
                        if ("SuppressWarnings".equals(fullyQualifiedName) 
                        || "java.lang.SuppressWarnings".equals(fullyQualifiedName)) { 
                            return annotation;
                        }
                    }
                }
            }
            
            return null;
        }
    }
}
