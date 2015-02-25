package io.github.ble.shax.util.prob;

import io.github.ble.shax.util.IteratorDual;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.ConcurrentHashMultiset;
import com.google.common.collect.Multiset;

//I feel stateful, oh so stateful
public abstract class MarkovBuilder<State, Transition> {
	protected abstract State transitionRule(State previous, Transition transition);
	protected abstract boolean isTerminalState(State s);

	public IteratorDual<Transition> collectorStartingAt(State s) {
		observedInitials.add(s);
		MarkovCollector c = new MarkovCollector(s);
		collectors.add(c);
		return c;
	}

	//<img src="dog_selecting_data_structures.jpeg">
	protected final Multiset<ImmutablePair<State, Transition>> observedTransitions = ConcurrentHashMultiset.create();
	protected final Multiset<State> observedInitials = ConcurrentHashMultiset.create();
	protected final Set<MarkovCollector> collectors = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private class MarkovCollector implements IteratorDual<Transition> {
		private State current;
		MarkovCollector(State initial) {
			current = initial;
		}

		public void next(Transition t) {
			if(isTerminalState(current))
				throw new IllegalStateException("can't transition, already in terminal state");

			observedTransitions.add(ImmutablePair.of(current, t));
			current = transitionRule(current, t);
		}

		public void done() {
			if(!isTerminalState(current))
				throw new IllegalStateException("not in terminal state; bat country, can't stop here");

			//observedTransitions.add(ImmutablePair.of(current, null));
			current = null;
			collectors.remove(this);
		}

	}
}
