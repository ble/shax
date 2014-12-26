package io.github.ble.shax.util;

import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

public class Splitters {
	private Splitters() {}

	private static final Pattern sentenceEnd = Pattern.compile("(?<=[.!?]+)"),
			                     wordEnd = Pattern.compile("(?<=[,;:-])| ");
	private static final Splitter sentenceSplit = Splitter.on(sentenceEnd).trimResults().omitEmptyStrings(),
			                      wordSplit = Splitter.on(wordEnd).trimResults().omitEmptyStrings();

	public static final Function<String, Iterable<String>> sentences = new Function<String, Iterable<String>>() {
		public Iterable<String> apply(String s) {
			return sentenceSplit.split(s);
		}
	};

	public static final Function<String, Iterable<String>> words = new Function<String, Iterable<String>>() {
		public Iterable<String> apply(String s) {
			return wordSplit.split(s);
		}
	};
}
