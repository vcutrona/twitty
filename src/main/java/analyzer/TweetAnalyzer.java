package analyzer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceCharFilter;
import org.apache.lucene.analysis.pattern.PatternReplaceFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TweetAnalyzer extends Analyzer {
	
    public static final String ENGLISH = "a about above after again against all am an and any are aren't as at be because been before being below between both but by can't cannot could couldn't did didn't do does doesn't doing don't down during each few for from further had hadn't has hasn't have haven't having he he'd he'll he's her here here's hers herself him himself his how how's i i'd i'll i'm i've if in into is isn't it it's its itself let's me more most mustn't my myself no nor not of off on once only or other ought our ours ourselves out over own same shan't she she'd she'll she's should shouldn't so some such than that that's the their theirs them themselves then there there's these they they'd they'll they're they've this those through to too under until up very was wasn't we we'd we'll we're we've were weren't what what's when when's where where's which while who who's whom why why's with won't would wouldn't you you'd you'll you're you've your yours yourself yourselves ";

	
    @Override
    protected Reader initReader(String fieldName, Reader reader) {

        // Pre-Tokenization
        CharFilter charFilter;

        // Replace # to avoid tokenization
        charFilter = new PatternReplaceCharFilter(Pattern.compile("#"), "__________", reader);
        // Replace @ to avoid tokenization
        charFilter = new PatternReplaceCharFilter(Pattern.compile("@"), "___________", charFilter);
        // Delete Tweet Links
        charFilter = new PatternReplaceCharFilter(Pattern.compile("(https://t.co/[0-9A-Za-z]+)"), "", charFilter);
        // Delete Tweet IDs
        charFilter = new PatternReplaceCharFilter(Pattern.compile("[0-9]{18}"), "", charFilter);

        return charFilter;
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {

        // Tokenization
        Tokenizer tokenizer = new StandardTokenizer();

        // Post-Tokenization
        TokenStream tokenFilter;

        // Restore @
        tokenFilter = new PatternReplaceFilter(tokenizer, Pattern.compile("___________"), "@", true);
        // Restore #
        tokenFilter = new PatternReplaceFilter(tokenFilter, Pattern.compile("__________"), "#", true);
        // Normalize tokens from StandardTokenizer
        tokenFilter = new StandardFilter(tokenFilter);
        // Remove english possessive
        tokenFilter = new EnglishPossessiveFilter(tokenFilter);
        // Tokens to lower case
        tokenFilter = new LowerCaseFilter(tokenFilter);
        // Remove english stop words
        tokenFilter = new StopFilter(tokenFilter, CharArraySet.copy(StandardAnalyzer.STOP_WORDS_SET));
        
        //new stopwords
        List<String> stopWords = new ArrayList <String> ();
        stopWords.addAll(Arrays.asList(ENGLISH.split(" ")));
        tokenFilter = new StopFilter(tokenFilter, StopFilter.makeStopSet(stopWords));
        
        // Stemming english words
        tokenFilter = new PorterStemFilter(tokenFilter);
        // Remove tokens too short and too long
        tokenFilter = new LengthFilter(tokenFilter, 3, 25);

        return new TokenStreamComponents(tokenizer, tokenFilter);
    }
}