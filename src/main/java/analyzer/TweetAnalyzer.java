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
import java.util.regex.Pattern;

public class TweetAnalyzer extends Analyzer {

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
        // Stemming english words
        tokenFilter = new PorterStemFilter(tokenFilter);
        // Remove tokens too short and too long
        tokenFilter = new LengthFilter(tokenFilter, 3, 25);

        return new TokenStreamComponents(tokenizer, tokenFilter);
    }
}