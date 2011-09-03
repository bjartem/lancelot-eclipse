package no.nr.lancelot.model;

import java.util.HashSet;
import java.util.Set;

public final class TypeDictionary {
    
    private Set<String> names = new HashSet<String>();
    
    public void add(final TypeDictionary dict) {
        names.addAll(dict.getTypeNames());
    }

    public void add(final String name) {
        names.add(name);
    }
    
    public Set<String> getTypeNames() {
        final Set<String> copy = new HashSet<String>();
        copy.addAll(names);
        return copy;
    }

}
