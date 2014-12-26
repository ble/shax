package io.github.ble.shax.util.prob;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableList;

public interface DiscreteDistribution<T> extends Iterable<ImmutablePair<Double, T>> {
	ImmutablePair<double[], ImmutableList<T>> atOnce();

	default T sample(double p) {
		if(p > 1.0)
			throw new IllegalArgumentException();

		T last = null;
		boolean seenAny = false;

		for(Pair<Double, T> pv: this) {
			seenAny = true;
			last = pv.getRight();

			p -= pv.getLeft();
			if(p <= 0)
				return pv.getRight();
		}

		if(!seenAny)
			throw new IllegalStateException();

		return last;
	}
}