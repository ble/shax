package io.github.ble.shax.util.prob;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multiset.Entry;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class ArrayDiscreteDistribution<T> implements DiscreteDistribution<T> {

	//writing this function strengthened my resolve to pick up clojure again.
	public static<S, T>
		ImmutableMap<S, DiscreteDistribution<T>>
		normalizedTransitions(Multiset<ImmutablePair<S, T>> samples) {
		Map<S, Multiset<T>> accum = Maps.newHashMap();
		for(ImmutablePair<S, T> sample: samples) {
			if(!accum.containsKey(sample.left))
				accum.put(sample.left, HashMultiset.create());
			accum.get(sample.left).add(sample.right);
		}

		ImmutableMap.Builder<S, DiscreteDistribution<T>> b = ImmutableMap.builder();

		for(Map.Entry<S, Multiset<T>> e: accum.entrySet()) {
			b.put(e.getKey(), normalized(e.getValue()));
		}

		return b.build();
	}

	public static<T> DiscreteDistribution<T> normalized(Multiset<T> samples) {
		long sum = 0;
		Comparator<Multiset.Entry<T>> order = new Comparator<Multiset.Entry<T>>() {
			public int compare(Entry<T> o1, Entry<T> o2) { return Integer.compare(o1.getCount(), o2.getCount()); }
		};
		SortedMultiset<Multiset.Entry<T>> inOrder = TreeMultiset.create(order);
		inOrder.addAll(samples.entrySet());

		ImmutableList.Builder<T> bxs = ImmutableList.builder();
		double[] ps = new double[inOrder.size()];
		{
			int ix = 0;

			for(Multiset.Entry<T> entry: samples.entrySet()) {
				sum += entry.getCount();
				ps[ix++] = entry.getCount();
				bxs.add(entry.getElement());
			}
		}

		for(int ix = 0; ix < ps.length; ix++)
			ps[ix] /= sum;

		return new ArrayDiscreteDistribution<T>(ps, bxs.build());
	}

	public ArrayDiscreteDistribution(double[] ps, ImmutableList<T> xs) {
		this.ps = ps;
		this.xs = xs;
	}

	final double[] ps;
	final ImmutableList<T> xs;

	@Override
	public Iterator<ImmutablePair<Double, T>> iterator() {
		return new Iterator<ImmutablePair<Double, T>>() {

			private int ix = 0;
			public boolean hasNext() { return ix < ps.length; }
			public ImmutablePair<Double, T> next() {
				if(ix >= ps.length)
					throw new NoSuchElementException();
				//java specification, section 15.7, evaluation order says this is okay
				return ImmutablePair.of(ps[ix], xs.get(ix++));

			}
		};
	}

	@Override
	public ImmutablePair<double[], ImmutableList<T>> atOnce() {
		return ImmutablePair.of(ps.clone(), xs);
	}

}
