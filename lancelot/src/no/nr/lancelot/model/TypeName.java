package no.nr.lancelot.model;

public final class TypeName {

    private final String localName;
    private final String packageName;

    public TypeName(final String localName, final String packageName) {
        this.localName = localName;
        this.packageName = packageName;
    }

    public TypeName(final String localName) {
        this(localName, "");
    }

    public String getLocalName() {
        return this.localName;
    }

    public String getPackageName() {
        return this.packageName;
    }
    
    public String toString() {
        String result = this.localName;
        if (this.packageName.length() > 0) {
            result = this.packageName + "." + result;
        }
        return result;
    }

}
