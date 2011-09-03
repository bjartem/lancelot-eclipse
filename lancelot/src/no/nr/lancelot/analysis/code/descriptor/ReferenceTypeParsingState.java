package no.nr.lancelot.analysis.code.descriptor;

public enum ReferenceTypeParsingState {

    INITIAL,
    REFERENCE_START,
    MAYBE_PACKAGE,
    INVALID_NOT_PACKAGE,
    VALID_NOT_PACKAGE,
    REFERENCE_DONE,
    ERROR

}
