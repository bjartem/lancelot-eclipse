package no.nr.lancelot.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class JavaCorpus implements Iterable<JavaApplication> {

    private final List<JavaApplication> apps = new ArrayList<JavaApplication>();

    public void addApplication(final JavaApplication application) {
        apps.add(application);
    }

    public int getNumberOfApplications() {
        return apps.size();
    }

    public Iterator<JavaApplication> iterator() {
        return apps.iterator();
    }

}
