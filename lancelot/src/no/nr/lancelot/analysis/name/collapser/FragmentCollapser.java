package no.nr.lancelot.analysis.name.collapser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import no.nr.lancelot.analysis.name.splitter.NameSplitter;
import no.nr.lancelot.model.JavaMethod;

public final class FragmentCollapser {

    private static Map<Integer, Set<Pair<String, List<String>>>> getReferencedTypeNames(final JavaMethod javaMethod, 
            final List<String> textFragments) {
        final Map<Integer, Set<Pair<String, List<String>>>> result = new HashMap<Integer, Set<Pair<String, List<String>>>>();
        final String methodName = javaMethod.getMethodName();
        final Set<String> typeNames = unqualify(javaMethod.getTypeDictionary().getTypeNames());
        final Iterator<String> itor = typeNames.iterator();
        while (itor.hasNext()) {
            final String typeName = itor.next();
            boolean typeNameInMethodName = methodName.contains(typeName);
            if (!typeNameInMethodName) {
                final char lastChar = typeName.charAt(typeName.length() - 1);
                if (lastChar == 'y') {
                    typeNameInMethodName = methodName.contains(Pluralizer.pluralize(typeName));
                }
            }
            if (typeNameInMethodName) {
                final NameSplitter splitter = new NameSplitter();
                final List<String> typeNameFragments = splitter.split(typeName);
                final List<Integer> indexes = subList(typeNameFragments, textFragments);
                if (indexes.size() == 0) {
                    itor.remove();
                } else {
                    for (final int index : indexes) {
                        if (!result.containsKey(index)) {
                            result.put(index, new HashSet<Pair<String, List<String>>>());
                        }
                        final Pair<String, List<String>> pair = new Pair<String, List<String>>(typeName, typeNameFragments);
                        result.get(index).add(pair);
                    }
                }
            } else {
                itor.remove();
            }
        }
        return result;
    }
    
    public static List<Fragment> collapse(final List<String> textFragments,
            final JavaMethod javaMethod) {
        // Map<MethodFragmentNumber, Set<Pair<TypeName, FragmentedTypeName>>>>
        final Map<Integer, Set<Pair<String, List<String>>>> referencedTypeNames = getReferencedTypeNames(javaMethod, textFragments);
        // Map<MethodFragmentNumber, Pair<TypeName, FragmentedTypeName>>>
        final Map<Integer, Pair<String, List<String>>> chosenTypeNames = chooseTypeNames(referencedTypeNames);
        final List<Fragment> result = new ArrayList<Fragment>();
        for (int i = 0; i < textFragments.size(); i++) {
            if (chosenTypeNames.containsKey(i)) {
                final Pair<String, List<String>> pair = chosenTypeNames.remove(i);
                i += pair.getSecond().size() - 1;
                result.add(new Fragment(pair.getFirst(), true));
            } else {
                result.add(new Fragment(textFragments.get(i)));
            }
        }
        return result;
    }
    
    private static Map<Integer, Pair<String, List<String>>> chooseTypeNames(
            final Map<Integer, Set<Pair<String, List<String>>>> referencedTypeNames) {
        final Map<Integer, Pair<String, List<String>>> result = new HashMap<Integer, Pair<String, List<String>>>();
        for (final Map.Entry<Integer, Set<Pair<String, List<String>>>> entry : referencedTypeNames.entrySet()) {
            Pair<String, List<String>> chosenPair = null;
            for (final Pair<String, List<String>> pair : entry.getValue()) {
                if (chosenPair == null) {
                    chosenPair = pair;
                } else {
                    if (pair.getSecond().size() >= chosenPair.getSecond().size()) {
                        chosenPair = pair;
                    }
                }
            }
            result.put(entry.getKey(), chosenPair);
        }
        return result;
    }

    private static List<Integer> subList(final List<String> typeNameFragments,
            final List<String> methodNameFragments) {
        final List<Integer> result = new ArrayList<Integer>();
        final int NOT_A_SUBLIST = -1;
        int offset = NOT_A_SUBLIST;
        final String firstTypeNameFragment = typeNameFragments.get(0);
        for (int i = 0; i < methodNameFragments.size(); i++) {
            if (pluralEquals(methodNameFragments.get(i), firstTypeNameFragment)) {
                offset = i;
                if (methodNameFragments.size() < typeNameFragments.size() + offset) {
                    continue;
                }
                boolean match = true;
                for (int j = 0; j < typeNameFragments.size(); j++, i++) {
                    final String mnFragment = methodNameFragments.get(i);
                    final String tnFragment = typeNameFragments.get(j);
                    if (!pluralEquals(mnFragment, tnFragment)) {
                        match = false;
                        i = offset;
                        break;
                    }
                }
                if (match) {
                    result.add(offset);
                }
            }
        }
        return result;
    }

    private static boolean pluralEquals(final String mnFragment,
            final String tnFragment) {
        return (mnFragment.equals(tnFragment) || mnFragment.equals(Pluralizer.pluralize(tnFragment)));
    }

    private static Set<String> unqualify(final Set<String> typeNames) {
        final Set<String> result = new HashSet<String>();
        for (final String typeName : typeNames) {
            result.add(unqualify(typeName));
        }
        return result;
    }

    private static String unqualify(final String typeName) {
        final int lastDot = typeName.lastIndexOf('.');
        if (lastDot < 0) {
            return typeName;
        }
        return typeName.substring(lastDot + 1);
    }

}
