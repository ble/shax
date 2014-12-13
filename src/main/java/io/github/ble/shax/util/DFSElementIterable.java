package io.github.ble.shax.util;

import java.util.Iterator;
import java.util.Stack;

import com.google.common.collect.Lists;
import com.google.common.collect.Iterables;
import com.google.common.base.Predicate;
import com.google.common.base.Function;

import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

public class DFSElementIterable implements Iterable<Element> {
	private final Element root;
	private final boolean skipRoot = false;

	public DFSElementIterable(Element r) {
		this.root = r;
	}

	public Iterator<Element> iterator() {
		final Stack<Iterator<Element>> stack = new Stack<>();
		if(skipRoot) {
			stack.push(elementChildrenOf(root).iterator());
		} else {
			stack.push(Lists.newArrayList(this.root).iterator());
		}

		return new Iterator<Element>() {

			private void advance() {
				while(!stack.empty() && !stack.peek().hasNext())
					stack.pop();
			}

			public boolean hasNext() {
				advance();
				return !stack.empty() && stack.peek().hasNext();
			}

			public Element next() {
				advance();
				Element e = stack.peek().next();
				stack.push(elementChildrenOf(e).iterator());
				return e;
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	private static Iterable<Element> elementChildrenOf(Element e) {

		final Predicate<Node> isElement = new Predicate<Node>() {
			public boolean apply(Node x) {
				return x.getNodeType() == Node.ELEMENT_NODE;
			}
		};

		final Function<Node, Element> convertToElement = new Function<Node, Element>() {
			public Element apply(Node x) {
				return (Element) x;
			}
		};

		return Iterables.transform(
				Iterables.filter(
					NodeListIterable.wrap(e.getChildNodes()),
					isElement),
				convertToElement);
	}
}
