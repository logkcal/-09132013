package com.henry4j.text;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.charfilter.HTMLStripCharFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

/**
 * Custom Lucene Analyzer designed for aggressive feature reduction
 * for clustering the comm. text archives using an extended set of
 * stop words, excluding non-alpha-numeric tokens, and porter stemming.
 */
public final class CommTextAnalyzer extends StopwordAnalyzerBase {
    private static final Version LUCENE_VERSION = Version.LUCENE_43;

    // ruby -e 'require "open-uri"; p open("http://jmlr.org/papers/volume5/lewis04a/a11-smart-stop-list/english.stop").readlines.map { |e| e.chomp.tr(39.chr, "") }.select { |e| e.size > 1 }.uniq'
    static final CharArraySet STOP_SET = new CharArraySet(LUCENE_VERSION, Arrays.asList(
        "january", "february", "march", "april", "may", "june", "july", "august", "september", "october", "november", "december",
        "jan", "feb", "mar", "apr", "jun", "jul", "aug", "sep", "sept", "oct", "nov", "dec",
        "as", "able", "about", "above", "according", "accordingly", "across", "actually", "after", "afterwards", "again", "against", "aint", "all", "allow", "allows", "almost", "alone", "along", "already", "also", "although", "always", "am", "among", "amongst", "an", "and", "another", "any", "anybody", "anyhow", "anyone", "anything", "anyway", "anyways", "anywhere", "apart", "appear", "appreciate", "appropriate", "are", "arent", "around", "aside", "ask", "asking", "associated", "at", "available", "away", "awfully", "be", "became", "because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "believe", "below", "beside", "besides", "best", "better", "between", "beyond", "both", "brief", "but", "by", "cmon", "cs", "came", "can", "cant", "cannot", "cause", "causes", "certain", "certainly", "changes", "clearly", "co", "com", "come", "comes", "concerning", "consequently", "consider", "considering", "contain", "containing", "contains", "corresponding", "could", "couldnt", "course", "currently", "definitely", "described", "despite", "did", "didnt", "different", "do", "does", "doesnt", "doing", "dont", "done", "down", "downwards", "during", "each", "edu", "eg", "eight", "either", "else", "elsewhere", "enough", "entirely", "especially", "et", "etc", "even", "ever", "every", "everybody", "everyone", "everything", "everywhere", "ex", "exactly", "example", "except", "far", "few", "fifth", "first", "five", "followed", "following", "follows", "for", "former", "formerly", "forth", "four", "from", "further", "furthermore", "get", "gets", "getting", "given", "gives", "go", "goes", "going", "gone", "got", "gotten", "greetings", "had", "hadnt", "happens", "hardly", "has", "hasnt", "have", "havent", "having", "he", "hes", "hello", "help", "hence", "her", "here", "heres", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "hi", "him", "himself", "his", "hither", "hopefully", "how", "howbeit", "however", "id", "ill", "im", "ive", "ie", "if", "ignored", "immediate", "in", "inasmuch", "inc", "indeed", "indicate", "indicated", "indicates", "inner", "insofar", "instead", "into", "inward", "is", "isnt", "it", "itd", "itll", "its", "itself", "just", "keep", "keeps", "kept", "know", "knows", "known", "last", "lately", "later", "latter", "latterly", "least", "less", "lest", "let", "lets", "like", "liked", "likely", "little", "look", "looking", "looks", "ltd", "mainly", "many", "may", "maybe", "me", "mean", "meanwhile", "merely", "might", "more", "moreover", "most", "mostly", "much", "must", "my", "myself", "name", "namely", "nd", "near", "nearly", "necessary", "need", "needs", "neither", "never", "nevertheless", "new", "next", "nine", "no", "nobody", "non", "none", "noone", "nor", "normally", "not", "nothing", "novel", "now", "nowhere", "obviously", "of", "off", "often", "oh", "ok", "okay", "old", "on", "once", "one", "ones", "only", "onto", "or", "other", "others", "otherwise", "ought", "our", "ours", "ourselves", "out", "outside", "over", "overall", "own", "particular", "particularly", "per", "perhaps", "placed", "please", "plus", "possible", "presumably", "probably", "provides", "que", "quite", "qv", "rather", "rd", "re", "really", "reasonably", "regarding", "regardless", "regards", "relatively", "respectively", "right", "said", "same", "saw", "say", "saying", "says", "second", "secondly", "see", "seeing", "seem", "seemed", "seeming", "seems", "seen", "self", "selves", "sensible", "sent", "serious", "seriously", "seven", "several", "shall", "she", "should", "shouldnt", "since", "six", "so", "some", "somebody", "somehow", "someone", "something", "sometime", "sometimes", "somewhat", "somewhere", "soon", "sorry", "specified", "specify", "specifying", "still", "sub", "such", "sup", "sure", "ts", "take", "taken", "tell", "tends", "th", "than", "thank", "thanks", "thanx", "that", "thats", "the", "their", "theirs", "them", "themselves", "then", "thence", "there", "theres", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "theyd", "theyll", "theyre", "theyve", "think", "third", "this", "thorough", "thoroughly", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "took", "toward", "towards", "tried", "tries", "truly", "try", "trying", "twice", "two", "un", "under", "unfortunately", "unless", "unlikely", "until", "unto", "up", "upon", "us", "use", "used", "useful", "uses", "using", "usually", "uucp", "value", "various", "very", "via", "viz", "vs", "want", "wants", "was", "wasnt", "way", "we", "wed", "well", "were", "weve", "welcome", "went", "werent", "what", "whats", "whatever", "when", "whence", "whenever", "where", "wheres", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whos", "whoever", "whole", "whom", "whose", "why", "will", "willing", "wish", "with", "within", "without", "wont", "wonder", "would", "wouldnt", "yes", "yet", "you", "youd", "youll", "youre", "youve", "your", "yours", "yourself", "yourselves", "zero"
    ), false);

    // Regex used to exclude non-alpha-numeric tokens
    static final Pattern ALPHA_NUMERIC = Pattern.compile("^[a-z][a-z0-9_]+$");
    static final Matcher MATCHER = ALPHA_NUMERIC.matcher("");

    public CommTextAnalyzer() {
        super(LUCENE_VERSION, STOP_SET);
    }

    public CommTextAnalyzer(CharArraySet stopSet) {
        super(LUCENE_VERSION, stopSet);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        reader = new HTMLStripCharFilter(reader);
        Tokenizer tokenizer = new StandardTokenizer(LUCENE_VERSION, reader);
        TokenStream result = new StandardFilter(LUCENE_VERSION, tokenizer);
        result = new LowerCaseFilter(LUCENE_VERSION, result);
        result = new ASCIIFoldingFilter(result);
        result = new AlphaNumericMaxLengthFilter(result);
        result = new StopFilter(LUCENE_VERSION, result, STOP_SET);
        result = new PorterStemFilter(result);
        return new TokenStreamComponents(tokenizer, result);
    }

    /**
     * Matches alpha-numeric tokens between 2 and 40 chars long.
     */
    static class AlphaNumericMaxLengthFilter extends TokenFilter {
        private final CharTermAttribute termAtt;
        private final char[] output = new char[28];

        AlphaNumericMaxLengthFilter(TokenStream in) {
            super(in);
            termAtt = addAttribute(CharTermAttribute.class);
        }

        @Override
        public final boolean incrementToken() throws IOException {
            // return the first alpha-numeric token between 2 and 40 length
            while (input.incrementToken()) {
                int length = termAtt.length();
                if (length >= 2 && length <= 28) {
                    char[] buf = termAtt.buffer();
                    int at = 0;
                    for (int c = 0; c < length; c++) {
                        char ch = buf[c];
                        if (ch != '\'') {
                            output[at++] = ch;
                        }
                    }
                    String term = new String(output, 0, at);
                    MATCHER.reset(term);
                    if (MATCHER.matches() && !term.startsWith("a0")) {
                        termAtt.setEmpty();
                        termAtt.append(term);
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
