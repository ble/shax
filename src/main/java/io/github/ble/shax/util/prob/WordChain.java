package io.github.ble.shax.util.prob;

import io.github.ble.shax.util.Function2;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.base.Function;

public class WordChain {
	public static String terminalWord = "~~terminal~~";
	public static class LastWord extends FixedMarkovBuilder<String, String> {

		public LastWord() {
			super(latter, endIfTerminal);
		}


	}

	private static Function2<String, String, String> latter = new Function2<String, String, String>() {
		public String apply(String a, String b) {
			return b;
		}
	};

	private static Function<String, Boolean> endIfTerminal = new Function<String, Boolean>() {
		public Boolean apply(String a) {
			return a.equals(terminalWord);
		}
	};

	public static class LastTwoWords extends FixedMarkovBuilder<ImmutablePair<String, String>, String> {
		//quadruple-ugh
		//color me ready for scala, if not clojure
		private static Function2<ImmutablePair<String, String>, String, ImmutablePair<String, String>> rotateOn =
				new Function2<ImmutablePair<String, String>, String, ImmutablePair<String,String>>() {
					public ImmutablePair<String, String> apply(ImmutablePair<String, String> a, String b) {
						return ImmutablePair.of(a.right, b);
					}
		};

		private static Function<ImmutablePair<String, String>, Boolean> rightIsTerminal =
				new Function<ImmutablePair<String, String>, Boolean>() {

					public Boolean apply(ImmutablePair<String, String> a) {
						return endIfTerminal.apply(a.right);
					}
		};

		public LastTwoWords() {
			super(rotateOn, rightIsTerminal);
		}

	}
}
