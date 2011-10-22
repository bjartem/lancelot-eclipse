package galahadjava;

import japa.parser.ast.*;
import japa.parser.ast.body.*;
import japa.parser.ast.expr.*;
import japa.parser.ast.stmt.*;
import japa.parser.ast.type.*;
import japa.parser.ast.visitor.*;
import java.util.*;

public final class Tracker extends VoidVisitorAdapter<Integer> {
    public static final class TrackedMethod {
        public final String qualifiedName,
                            methodName,
                            source;

        public final int beginLine,
                         endLine;

        TrackedMethod(
            final String qualifiedName,
            final String methodName,
            final int beginLine,
            final int endLine,
            final String source
        ) {
            this.qualifiedName = qualifiedName;
            this.methodName = methodName;
            this.beginLine = beginLine;
            this.endLine = endLine;
            this.source = source;
        }
    }

    private String qualifiedName = "";
    private final List<TrackedMethod> trackedMethods = 
                                             new ArrayList<TrackedMethod>(1024);

    public List<TrackedMethod> getTrackedMethods() {
        return Collections.unmodifiableList(trackedMethods);
    }

    public void visit(ClassOrInterfaceDeclaration n, Integer _) {
        final boolean isClass = !n.isInterface();
        String oldQualifiedName = qualifiedName;
        if (isClass)
            qualifiedName = qualifiedName.isEmpty() ? n.getName() :
                                                      qualifiedName + "." + n.getName();

        if (n.getMembers() != null) {
            for (BodyDeclaration member : n.getMembers()) {
                member.accept(this, _);
            }
        }

        if (isClass)
            qualifiedName = oldQualifiedName;
    }

    public void visit(MethodDeclaration n, Integer _) {
        trackedMethods.add(new TrackedMethod(
            qualifiedName,
            n.getName(),
            n.getBeginLine(),
            n.getEndLine(),
            n.toString()
        ));


        n.getType().accept(this, _);
        if (n.getParameters() != null) {
            for (Parameter p : n.getParameters()) {
                p.accept(this, _);
            }
        }
        if (n.getBody() != null) {
            n.getBody().accept(this, _);
        }
    }


    public void visit(AnnotationDeclaration n, Integer _) {
    }

    public void visit(AnnotationMemberDeclaration n, Integer _) {
    }

    public void visit(ArrayAccessExpr n, Integer _) {
        n.getName().accept(this, _);
        n.getIndex().accept(this, _);
    }

    public void visit(ArrayCreationExpr n, Integer _) {
        n.getType().accept(this, _);
        if (n.getDimensions() != null) {
            for (Expression dim : n.getDimensions()) {
                dim.accept(this, _);
            }
        } else {
            n.getInitializer().accept(this, _);
        }
    }

    public void visit(ArrayInitializerExpr n, Integer _) {
        if (n.getValues() != null) {
            for (Expression expr : n.getValues()) {
                expr.accept(this, _);
            }
        }
    }

    public void visit(AssertStmt n, Integer _) {
    }

    public void visit(AssignExpr n, Integer _) {
        n.getTarget().accept(this, _);
        n.getValue().accept(this, _);
    }

    public void visit(BinaryExpr n, Integer _) {
        n.getLeft().accept(this, _);
        n.getRight().accept(this, _);
    }

    public void visit(BlockComment n, Integer _) {
    }

    public void visit(BlockStmt n, Integer _) {
        if (n.getStmts() != null) {
            for (Statement s : n.getStmts()) {
                s.accept(this, _);
            }
        }
    }

    public void visit(BooleanLiteralExpr n, Integer _) {
    }

    public void visit(BreakStmt n, Integer _) {
    }

    public void visit(CastExpr n, Integer _) {
        n.getType().accept(this, _);
        n.getExpr().accept(this, _);
    }

    public void visit(CatchClause n, Integer _) {
        n.getExcept().accept(this, _);
        n.getCatchBlock().accept(this, _);
    }

    public void visit(CharLiteralExpr n, Integer _) {
    }

    public void visit(ClassExpr n, Integer _) {
        n.getType().accept(this, _);
    }

    public void visit(ClassOrInterfaceType n, Integer _) {
        if (n.getScope() != null) {
            n.getScope().accept(this, _);
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, _);
            }
        }
    }

    public void visit(CompilationUnit n, Integer _) {
        if (n.getTypes() != null) {
            for (TypeDeclaration typeDeclaration : n.getTypes()) {
                typeDeclaration.accept(this, _);
            }
        }
    }

    public void visit(ConditionalExpr n, Integer _) {
        n.getCondition().accept(this, _);
        n.getThenExpr().accept(this, _);
        n.getElseExpr().accept(this, _);
    }

    public void visit(ConstructorDeclaration n, Integer _) {
        n.getBlock().accept(this, _);
    }

    public void visit(ContinueStmt n, Integer _) {
    }

    public void visit(DoStmt n, Integer _) {
        n.getBody().accept(this, _);
        n.getCondition().accept(this, _);
    }

    public void visit(DoubleLiteralExpr n, Integer _) {
    }

    public void visit(EmptyMemberDeclaration n, Integer _) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, _);
        }
    }

    public void visit(EmptyStmt n, Integer _) {
    }

    public void visit(EmptyTypeDeclaration n, Integer _) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, _);
        }
    }

    public void visit(EnclosedExpr n, Integer _) {
        n.getInner().accept(this, _);
    }

    public void visit(EnumConstantDeclaration n, Integer _) {
    }

    public void visit(EnumDeclaration n, Integer _) {
    }

    public void visit(ExplicitConstructorInvocationStmt n, Integer _) {
        if (!n.isThis()) {
            if (n.getExpr() != null) {
                n.getExpr().accept(this, _);
            }
        }
        if (n.getTypeArgs() != null) {
            for (Type t : n.getTypeArgs()) {
                t.accept(this, _);
            }
        }
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, _);
            }
        }
    }

    public void visit(ExpressionStmt n, Integer _) {
        n.getExpression().accept(this, _);
    }

    public void visit(FieldAccessExpr n, Integer _) {
        n.getScope().accept(this, _);
    }

    public void visit(FieldDeclaration n, Integer _) {
        n.getType().accept(this, _);
        for (VariableDeclarator var : n.getVariables()) {
            var.accept(this, _);
        }
    }

    public void visit(ForeachStmt n, Integer _) {
        n.getVariable().accept(this, _);
        n.getIterable().accept(this, _);
        n.getBody().accept(this, _);
    }

    public void visit(ForStmt n, Integer _) {
        if (n.getInit() != null) {
            for (Expression e : n.getInit()) {
                e.accept(this, _);
            }
        }
        if (n.getCompare() != null) {
            n.getCompare().accept(this, _);
        }
        if (n.getUpdate() != null) {
            for (Expression e : n.getUpdate()) {
                e.accept(this, _);
            }
        }
        n.getBody().accept(this, _);
    }

    public void visit(IfStmt n, Integer _) {
        n.getCondition().accept(this, _);
        n.getThenStmt().accept(this, _);
        if (n.getElseStmt() != null) {
            n.getElseStmt().accept(this, _);
        }
    }

    public void visit(ImportDeclaration n, Integer _) {
        n.getName().accept(this, _);
    }

    public void visit(InitializerDeclaration n, Integer _) {
        if (n.getJavaDoc() != null) {
            n.getJavaDoc().accept(this, _);
        }
        n.getBlock().accept(this, _);
    }

    public void visit(InstanceOfExpr n, Integer _) {
        n.getExpr().accept(this, _);
        n.getType().accept(this, _);
    }

    public void visit(IntegerLiteralExpr n, Integer _) {
    }

    public void visit(IntegerLiteralMinValueExpr n, Integer _) {
    }

    public void visit(JavadocComment n, Integer _) {
    }

    public void visit(LabeledStmt n, Integer _) {
        n.getStmt().accept(this, _);
    }

    public void visit(LineComment n, Integer _) {
    }

    public void visit(LongLiteralExpr n, Integer _) {
    }

    public void visit(LongLiteralMinValueExpr n, Integer _) {
    }

    public void visit(MarkerAnnotationExpr n, Integer _) {
        n.getName().accept(this, _);
    }

    public void visit(MemberValuePair n, Integer _) {
        n.getValue().accept(this, _);
    }

    public void visit(MethodCallExpr n, Integer _) {
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, _);
            }
        }
    }

    public void visit(NameExpr n, Integer _) {
    }

    public void visit(NormalAnnotationExpr n, Integer _) {
    }

    public void visit(NullLiteralExpr n, Integer _) {
    }

    public void visit(ObjectCreationExpr n, Integer _) {
        n.getType().accept(this, _);
        if (n.getArgs() != null) {
            for (Expression e : n.getArgs()) {
                e.accept(this, _);
            }
        }
        if (n.getAnonymousClassBody() != null) {
            for (BodyDeclaration member : n.getAnonymousClassBody()) {
                member.accept(this, _);
            }
        }
    }

    public void visit(PackageDeclaration n, Integer _) {
    }

    public void visit(Parameter n, Integer _) {
        n.getType().accept(this, _);
        n.getId().accept(this, _);
    }

    public void visit(PrimitiveType n, Integer _) {
    }

    public void visit(QualifiedNameExpr n, Integer _) {
        n.getQualifier().accept(this, _);
    }

    public void visit(ReferenceType n, Integer _) {
        n.getType().accept(this, _);
    }

    public void visit(ReturnStmt n, Integer _) {
        if (n.getExpr() != null) {
            n.getExpr().accept(this, _);
        }
    }

    public void visit(SingleMemberAnnotationExpr n, Integer _) {
        n.getName().accept(this, _);
        n.getMemberValue().accept(this, _);
    }

    public void visit(StringLiteralExpr n, Integer _) {
    }

    public void visit(SuperExpr n, Integer _) {
        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, _);
        }
    }

    public void visit(SwitchEntryStmt n, Integer _) {
        if (n.getLabel() != null) {
            n.getLabel().accept(this, _);
        }
        if (n.getStmts() != null) {
            for (Statement s : n.getStmts()) {
                s.accept(this, _);
            }
        }
    }

    public void visit(SwitchStmt n, Integer _) {
        n.getSelector().accept(this, _);
        if (n.getEntries() != null) {
            for (SwitchEntryStmt e : n.getEntries()) {
                e.accept(this, _);
            }
        }
    }

    public void visit(SynchronizedStmt n, Integer _) {
        n.getExpr().accept(this, _);
        n.getBlock().accept(this, _);

    }

    public void visit(ThisExpr n, Integer _) {
        if (n.getClassExpr() != null) {
            n.getClassExpr().accept(this, _);
        }
    }

    public void visit(ThrowStmt n, Integer _) {
        n.getExpr().accept(this, _);
    }

    public void visit(TryStmt n, Integer _) {
        n.getTryBlock().accept(this, _);
        if (n.getCatchs() != null) {
            for (CatchClause c : n.getCatchs()) {
                c.accept(this, _);
            }
        }
        if (n.getFinallyBlock() != null) {
            n.getFinallyBlock().accept(this, _);
        }
    }

    public void visit(TypeDeclarationStmt n, Integer _) {
        n.getTypeDeclaration().accept(this, _);
    }

    public void visit(TypeParameter n, Integer _) {
        if (n.getTypeBound() != null) {
            for (ClassOrInterfaceType c : n.getTypeBound()) {
                c.accept(this, _);
            }
        }
    }

    public void visit(UnaryExpr n, Integer _) {
        n.getExpr().accept(this, _);
    }

    public void visit(VariableDeclarationExpr n, Integer _) {
        if (n.getAnnotations() != null) {
            for (AnnotationExpr a : n.getAnnotations()) {
                a.accept(this, _);
            }
        }
        n.getType().accept(this, _);
        for (VariableDeclarator v : n.getVars()) {
            v.accept(this, _);
        }
    }

    public void visit(VariableDeclarator n, Integer _) {
        n.getId().accept(this, _);
        if (n.getInit() != null) {
            n.getInit().accept(this, _);
        }
    }

    public void visit(VariableDeclaratorId n, Integer _) {
    }

    public void visit(VoidType n, Integer _) {
    }

    public void visit(WhileStmt n, Integer _) {
        n.getCondition().accept(this, _);
        n.getBody().accept(this, _);
    }

    public void visit(WildcardType n, Integer _) {
        if (n.getExtends() != null) {
            n.getExtends().accept(this, _);
        }
        if (n.getSuper() != null) {
            n.getSuper().accept(this, _);
        }
    }
}
