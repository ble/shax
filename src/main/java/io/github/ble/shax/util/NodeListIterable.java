package io.github.ble.shax.util;

import java.util.Iterator;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class NodeListIterable implements Iterable<Node> {

	public static Iterable<Node> wrap(NodeList nl) {
		return new NodeListIterable(nl);
	}

	public NodeListIterable(NodeList toWrap) {
		this.backing = toWrap;
	}

	private final NodeList backing;

	public Iterator<Node> iterator() {
		return new Iterator<Node>() {
			private int ix = 0;

			public boolean hasNext() {
				return ix < backing.getLength();
			}

			public Node next() {
				return backing.item(ix++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

		};
	}

}

