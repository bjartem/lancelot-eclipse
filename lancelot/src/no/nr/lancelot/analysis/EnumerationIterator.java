package no.nr.lancelot.analysis;

import java.util.Enumeration;
import java.util.Iterator;

public final class EnumerationIterator<E> implements Iterator<E>, Iterable<E> {
    
    private final Enumeration<E> e;
    
    public EnumerationIterator(final Enumeration<E> enumeration) {
        e = enumeration;
    }

    public boolean hasNext() {
        return e.hasMoreElements();
    }

    public E next() {
        return e.nextElement();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    public Iterator<E> iterator() {
        return this;
    }

}
