package no.nr.lancelot.analysis.name.collapser;

public final class Fragment {
    
    private final String text;
    private final boolean isTypeName;

    public Fragment(final String text) {
        this(text, false);
    }
    
    public Fragment(final String text, final boolean isTypeName) {
        this.text = text;
        this.isTypeName = isTypeName;
    }
    
    public String getText() {
        return this.text;
    }
    
    public boolean isTypeName() {
        return this.isTypeName;
    }

}
