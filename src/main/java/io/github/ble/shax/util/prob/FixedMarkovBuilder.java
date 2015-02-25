package io.github.ble.shax.util.prob;

import io.github.ble.shax.util.Function2;

import com.google.common.base.Function;

public class FixedMarkovBuilder<S,T> extends MarkovBuilder<S, T>{
	public FixedMarkovBuilder(Function2<S, T, S> transition,
			Function<S, Boolean> isTerminal) {
		super();
		this.transition = transition;
		this.isTerminal = isTerminal;
	}

	protected final Function2<S, T, S> transition;
	protected final Function<S, Boolean> isTerminal;
	//java 8 would just let me delegate to the function field, right?
	protected S transitionRule(S s, T t) { return transition.apply(s, t); }
	protected boolean isTerminalState(S s) { return isTerminal.apply(s); }

	public FixedMarkovChain<S, T> build() {
		if(!collectors.isEmpty())
			throw new IllegalStateException("we're a finite sequence markov collector and we're too lazy to determine if the pending collectors are dangling or not.");

		return new FixedMarkovChain<S, T>(
			ArrayDiscreteDistribution.normalized(observedInitials),
			transition,
			ArrayDiscreteDistribution.normalizedTransitions(observedTransitions),
			isTerminal);
	}
}
