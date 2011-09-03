package no.nr.lancelot.analysis.name.collapser;

public final class Pluralizer {

    public static String pluralize(final String singular) {
        if (singular == null) {
            throw new IllegalArgumentException("Cannot pluralize a null String!");
        }
        if (singular.isEmpty()) {
            throw new IllegalArgumentException("Cannot pluralize an empty String!");
        }
        final char lastChar = singular.charAt(singular.length() - 1);
        switch (lastChar) {
        case 's':
            return singular + "es";
        case 'y':
            if (singular.length() > 1) {
                return singular.substring(0, singular.length() - 1) + "ies";
            }
        }
        return singular + "s";
    }

}
