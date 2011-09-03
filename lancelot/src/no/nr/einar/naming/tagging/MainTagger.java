package no.nr.einar.naming.tagging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

// FIXME 
public class MainTagger {
    private static List<String> makeList(final String... objs) {
        return Arrays.asList(objs);
    }
    
    @SuppressWarnings("serial")
    private static List<String> 
    
    PREPOSITIONS = makeList(
        "aboard", "about", "above", "absent", "across", "after", "against",
        "along", "alongside", "amid", "amidst", "among", "amongst", "around",
        "as", "aslant", "astride", "at", "atop", "barring", "before", "behind",
        "below", "beneath", "beside", "besides", "between", "beyond", "but", "by",
        "despite", "down", "during", "except", "failing", "following", "for",
        "from", "in", "inside", "into", "like", "mid", "minus", "near", "next",
        "notwithstanding", "of", "off", "on", "onto", "opposite", "outside",
        "over", "past", "per", "plus", "regarding", "round", "save", "since",
        "than", "through", "throughout", "till", "times", "to", "toward", "towards",
        "under", "underneath", "unlike", "until", "up", "upon", "versus", "via",
        "with", "within", "without"
    ),

    COORDINATING_CONJUNCTIONS = makeList(
        "for", "and", "nor", "but", "or", "yet", "so"
    ),

    SUBORDINATING_CONJUNCTIONS = makeList(
        "after", "before", "when", "while", "since", "until",
        "because", "as", "so",
        "although", "though", "whereas",
        "if", "unless"
    ),

    personal_pronouns = makeList(
        "I", "we", "you", "he", "she", "it", "they",
        "me", "us", "him", "her", "them",
        "myself", "ourself", "ourselves", "yourself", "yourselves",
        "himself", "herself", "itself", "themselves"
    ),

    possessive_pronouns = makeList("mine", "ours", "yours", "his", "hers", "theirs"),

    possessive_determiners = makeList("my", "our", "your", "his", "hers", "theirs"),

    indefinite_pronouns = makeList(
        "all", "another", "any", "anybody", "anyone", "anything", "both",
        "each", "either", "enough", "every", "everybody", "everyone", "everything",
        "few", "less", "little", "many", "more", "most", "much",
        "neither", "nobody", "no", "none", "one",
        "several", "some", "somebody", "someone", "something"
    ),

    demonstrative_pronouns = makeList("that", "this", "such", "these", "those"),

    interrogative_pronouns = makeList("who", "which", "what"),

    PRONOUNS = new ArrayList<String>(){{
        addAll(personal_pronouns);
        addAll(possessive_pronouns); 
        addAll(possessive_determiners); 
        addAll(indefinite_pronouns);
        addAll(demonstrative_pronouns);
        addAll(interrogative_pronouns);
    }},

    ARTICLES = makeList("the", "a", "an"),

    CONJUNCTIONS = new ArrayList<String>(){{
        addAll(COORDINATING_CONJUNCTIONS);
        addAll(SUBORDINATING_CONJUNCTIONS);
    }},
    
    ALSO_VERB = makeList("backup", "shutdown", "lookup", "gen", "exec", "increment"),

    ALSO_NOUN = makeList("decl"),

    KNOWN_NOUNS = makeList(
        "action", "mouse", "key", "value", "state", "size", 
        "index", "item", "message", "bean", "model", "result", "table", "document", 
        "input", "instance", "name", "dimension", "double", "float", "long", 
        "short", "character", "object", "attribute"
    ),

    KNOWN_VERBS = makeList(
        "is", "has", "equals", "exists", "contains", "matches", 
        "needs", "save", "was", "supports", "are"
    ),

    NOUN_OR_ADJECTIVE = makeList("original", "default"),

    KNOWN_ADJECTIVE = makeList(
        "next", "best", "first", "second", "third", 
        "last", "initial", "editable", "empty"
    );
                

    private final ChainTagger remainingTaggerChain = new LingoTagger(new MorphyTagger(null));
    private final WordNet wordNet = new WordNet();
      
    public List<List<Tag>> tag(final List<String> fragments) {
        final List<List<Tag>> $ = new ArrayList<List<Tag>>();
        for (final String f : fragments) {
            $.add(tag(f));
        }
        return $;
    }
    
    private static boolean isNumber(final String word) {
        for (char c : word.toCharArray())
            if (!Character.isDigit(c))
                return false;
        return true;        
    }
    
    private static boolean isUppercase(final String word) {
        for (char c : word.toCharArray())
            if (!Character.isUpperCase(c))
                return false;
        return true;
    }

    private static boolean isFooable(final String word) {
        if (word.length() < 6)
            return false;
        
        if (word.endsWith("able")) {
            if (word.endsWith("table"))
                return word.endsWith("ttable");
            return true;
        }
        
        return false;
    };
                
    private List<Tag> tag(String word) {
        final List<Tag> possibilities = new LinkedList<Tag>();
        
        if (isNumber(word)) {
            possibilities.add(Tag.Number);
            return possibilities;
        }
        
        if (isUppercase(word)) {
            possibilities.add(Tag.Noun);
            return possibilities;
        }
        
        if (KNOWN_VERBS.contains(word)) {
            possibilities.add(Tag.Verb);
            return possibilities;
        }
                    
        if (KNOWN_NOUNS.contains(word)) {
            possibilities.add(Tag.Noun);
            return possibilities;
        }
        
        if (KNOWN_ADJECTIVE.contains(word)) {
            possibilities.add(Tag.Adjective);
            return possibilities;
        }
        
        if (NOUN_OR_ADJECTIVE.contains(word)) {
            possibilities.add(Tag.Adjective);
            possibilities.add(Tag.Noun);
            return possibilities;
        }
        
        if (ALSO_NOUN.contains(word))
            possibilities.add(Tag.Noun);
            
        if (ALSO_VERB.contains(word))
            possibilities.add(Tag.Verb);
        
        if (PREPOSITIONS.contains(word))
            possibilities.add(Tag.Preposition);
        
        if (PRONOUNS.contains(word))
            possibilities.add(Tag.Pronoun);
        
        if (CONJUNCTIONS.contains(word))
            possibilities.add(Tag.Conjunction);
        
        if (ARTICLES.contains(word))
            possibilities.add(Tag.Article);
        
        if (wordNet.isNoun(word))
            possibilities.add(Tag.Noun);
        
        if (wordNet.isVerb(word))
            possibilities.add(Tag.Verb);
        
        if (wordNet.isAdjective(word))
            possibilities.add(Tag.Adjective);
        
        if (wordNet.isAdverb(word))
            possibilities.add(Tag.Adverb);

        if (possibilities.isEmpty() && isFooable(word))
            possibilities.add(Tag.Adjective);
        
        if (possibilities.isEmpty())
            return remainingTaggerChain.tag(word);

        return possibilities;
    }
}
