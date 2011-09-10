package no.nr.lancelot.tagging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

// FIXME 
public class MainTagger {
    @SuppressWarnings("unchecked")
	private static List<String> 
    
    PREPOSITIONS = makeStringList(
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

    COORDINATING_CONJUNCTIONS = makeStringList(
        "for", "and", "nor", "but", "or", "yet", "so"
    ),

    SUBORDINATING_CONJUNCTIONS = makeStringList(
        "after", "before", "when", "while", "since", "until",
        "because", "as", "so",
        "although", "though", "whereas",
        "if", "unless"
    ),

    PERSONAL_PRONOUNS = makeStringList(
        "I", "we", "you", "he", "she", "it", "they",
        "me", "us", "him", "her", "them",
        "myself", "ourself", "ourselves", "yourself", "yourselves",
        "himself", "herself", "itself", "themselves"
    ),

    POSSESIVE_PRONOUNS = makeStringList("mine", "ours", "yours", "his", "hers", "theirs"),

    POSSESIVE_DETERMINERS = makeStringList("my", "our", "your", "his", "hers", "theirs"),

    INDEFINITE_PRONOUNS = makeStringList(
        "all", "another", "any", "anybody", "anyone", "anything", "both",
        "each", "either", "enough", "every", "everybody", "everyone", "everything",
        "few", "less", "little", "many", "more", "most", "much",
        "neither", "nobody", "no", "none", "one",
        "several", "some", "somebody", "someone", "something"
    ),

    DEMONSTRATIVE_PRONOUNS = makeStringList("that", "this", "such", "these", "those"),

    INTERROGATIVE_PRONOUNS = makeStringList("who", "which", "what"),

    PRONOUNS = joinStringLists(
        PERSONAL_PRONOUNS,
        POSSESIVE_PRONOUNS,
        POSSESIVE_DETERMINERS, 
        INDEFINITE_PRONOUNS,
        DEMONSTRATIVE_PRONOUNS,
        INTERROGATIVE_PRONOUNS
    ),

    ARTICLES = makeStringList("the", "a", "an"),

    CONJUNCTIONS = joinStringLists(
        COORDINATING_CONJUNCTIONS,
        SUBORDINATING_CONJUNCTIONS
    ),
    
    ALSO_VERB = makeStringList("backup", "shutdown", "lookup", "gen", "exec", "increment"),

    ALSO_NOUN = makeStringList("decl"),

    KNOWN_NOUNS = makeStringList(
        "action", "mouse", "key", "value", "state", "size", 
        "index", "item", "message", "bean", "model", "result", "table", "document", 
        "input", "instance", "name", "dimension", "double", "float", "long", 
        "short", "character", "object", "attribute"
    ),

    KNOWN_VERBS = makeStringList(
        "is", "has", "equals", "exists", "contains", "matches", 
        "needs", "save", "was", "supports", "are"
    ),

    NOUN_OR_ADJECTIVE = makeStringList("original", "default"),

    KNOWN_ADJECTIVE = makeStringList(
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
    
    private List<Tag> tag(final String word) {
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
        
        if (ALSO_NOUN.contains(word)) {
            possibilities.add(Tag.Noun);
        }
            
        if (ALSO_VERB.contains(word)) {
            possibilities.add(Tag.Verb);
        }
        
        if (PREPOSITIONS.contains(word)) {
            possibilities.add(Tag.Preposition);
        }
        
        if (PRONOUNS.contains(word)) {
            possibilities.add(Tag.Pronoun);
        }
        
        if (CONJUNCTIONS.contains(word)) {
            possibilities.add(Tag.Conjunction);
        }
        
        if (ARTICLES.contains(word)) {
            possibilities.add(Tag.Article);
        }
        
        if (wordNet.isNoun(word)) {
            possibilities.add(Tag.Noun);
        }
        
        if (wordNet.isVerb(word)) {
            possibilities.add(Tag.Verb);
        }
        
        if (wordNet.isAdjective(word)) {
            possibilities.add(Tag.Adjective);
        }
        
        if (wordNet.isAdverb(word)) {
            possibilities.add(Tag.Adverb);
        }

        if (possibilities.isEmpty() && isFooable(word)) {
            possibilities.add(Tag.Adjective);
        }
        
        if (possibilities.isEmpty()) {
            return remainingTaggerChain.tag(word);
        }

        return possibilities;
    }
    
    protected static boolean isNumber(final String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;        
    }
    
    protected static boolean isUppercase(final String word) {
        for (char c : word.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    protected static boolean isFooable(final String word) {
        if (word.length() < 6) {
            return false;
        }
        
        if (word.endsWith("able")) {
            if (word.endsWith("table")) {
                return word.endsWith("ttable");
            }
            return true;
        }
        
        return false;
    };
                
    
    protected static List<String> makeStringList(final String... objs) {
        return Arrays.asList(objs);
    }

    protected static List<String> joinStringLists(final List<String>... lists) {
    	final List<String> res = new ArrayList<String>(1024);
    	for (final List<String> list : lists) {
    		res.addAll(list);
    	}
    	return res;
    }
}
