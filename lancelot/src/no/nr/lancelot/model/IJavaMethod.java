package no.nr.lancelot.model;

public interface IJavaMethod {

    String getMethodName();
    String getReturnType();
    String[] getParameterTypes();
    boolean hasReturnValue();
    boolean isFieldReader();
    boolean isFieldWriter();
    boolean hasLoop();
    boolean isExceptionThrower();
    boolean isSameNameDelegator();
    boolean isTypeManipulator();
    boolean hasParameters();
    boolean isParameterKeeper();
    boolean isStateReturner();
//  boolean isFilter();

}
