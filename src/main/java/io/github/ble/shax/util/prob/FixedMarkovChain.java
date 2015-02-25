package io.github.ble.shax.util.prob;

import io.github.ble.shax.util.Function2;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;

//probably should not be distinct from MarkovChain since we're already parameterizing over S and T
public class FixedMarkovChain<S, T> extends MarkovChain<S, T>{

	public FixedMarkovChain(
			DiscreteDistribution<S> initialDistribution,
			Function2<S, T, S> transition,
			ImmutableMap<S, DiscreteDistribution<T>> conditionals,
			Function<S, Boolean> isTerminal) {
		super(initialDistribution);
		this.transition = transition;
		this.conditionals = conditionals;
		this.isTerminal = isTerminal;
	}
	protected final Function2<S, T, S> transition;
	protected final ImmutableMap<S, DiscreteDistribution<T>> conditionals;
	protected final Function<S, Boolean> isTerminal;

	protected S transitionRule(S s, T t) { return transition.apply(s, t); }
	protected DiscreteDistribution<T> conditionalDistribution(S s) { return conditionals.get(s); }
	protected boolean isTerminalState(S s) { return isTerminal.apply(s); }

}
