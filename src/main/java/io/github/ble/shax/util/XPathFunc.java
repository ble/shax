package io.github.ble.shax.util;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Function;

public abstract class XPathFunc<T> implements Function<Node, T>{
	protected final XPathExpression exp;

	public XPathFunc(XPathExpression exp) {
		super();
		this.exp = exp;
	}
	
	static final XPathFactory xpf = XPathFactory.newInstance();
	static final XPath xpi = xpf.newXPath();
	
	static XPathExpression forceCompile(String expText) {
		try {
			return xpi.compile(expText);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static XPathFunc<Integer> intExp(String exp) {
		return new XPathInt(forceCompile(exp));
	}
	
	public static XPathFunc<String> stringExp(String exp) {
		return new XPathString(forceCompile(exp));
	}
	
	public static XPathFunc<Iterable<Node>> nodesExp(String exp) {
		return new XPathNodes(forceCompile(exp));
	}
}

final class XPathInt extends XPathFunc<Integer> {
	public Integer apply(Node n) {
		try {
			return ((Double) exp.evaluate(n, XPathConstants.NUMBER)).intValue();
		} catch(XPathExpressionException xpe) {
			throw new RuntimeException(xpe);
		}
	}

	XPathInt(XPathExpression exp) {
		super(exp);
	}
}

final class XPathString extends XPathFunc<String> {
	public String apply(Node n) {
		try {
			return (String) exp.evaluate(n, XPathConstants.STRING);
		} catch(XPathExpressionException xpe) {
			throw new RuntimeException(xpe);
		}
	}
	
	XPathString(XPathExpression exp) {
		super(exp);
	}
}

final class XPathNodes extends XPathFunc<Iterable<Node>> {
	public Iterable<Node> apply(Node n) {
		try {
			return NodeListIterable.wrap((NodeList)exp.evaluate(n, XPathConstants.NODESET));
		} catch(XPathExpressionException xpe) {
			throw new RuntimeException(xpe);
		}
	}
	
	XPathNodes(XPathExpression exp) {
		super(exp);
	}
}
