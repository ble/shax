package io.github.ble.shax.util.prob;

import java.util.Iterator;
import java.util.Random;

public abstract class MarkovChain<S, T> {

	protected abstract S transitionRule(S previous, T transition);
	protected abstract DiscreteDistribution<T> conditionalDistribution(S state);
	protected abstract boolean isTerminalState(S state);
	final DiscreteDistribution<S> initialDistribution;

	public MarkovChain(DiscreteDistribution<S> initialDistribution) {
		super();
		this.initialDistribution = initialDistribution;
	}

	public Iterable<T> fromState(S state, Random r) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new MarkovChainIterator(state, r);
			}
		};
	}

	public Iterable<T> fromInitialDistribution(Random r) {
		return new Iterable<T>() {
			public Iterator<T> iterator() {
				return new MarkovChainIterator(initialDistribution.sample(r.nextDouble()), r);
			}
		};
	}

	private class MarkovChainIterator implements Iterator<T> {
		S state;
		final Random r;

		MarkovChainIterator(S state, Random r) {
			this.state = state;
			this.r = r;
		}

		@Override
		public boolean hasNext() {
			return !isTerminalState(state);
		}

		@Override
		public T next() {
			T emitted = conditionalDistribution(state).sample(r.nextDouble());
			state = transitionRule(state, emitted);
			return emitted;
		}

	}
}

