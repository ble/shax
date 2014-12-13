package io.github.ble;

//Types that we might throw
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException; 

import java.io.File;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

//DOM-style XML parsing types
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import io.github.ble.shax.util.NodeListIterable;
import io.github.ble.shax.util.DFSElementIterable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Function;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
		throws SAXException, ParserConfigurationException, IOException
	{
		for(String arg: args) {
			processFile(new File(arg));
		}
	}

	static void processFile(File f) throws
		ParserConfigurationException,
		SAXException,
		IOException {
		DocumentBuilder builder = instantiateBuilder();
		Document doc = builder.parse(f);
		Element root = doc.getDocumentElement();
		Joiner joiner = Joiner.on(">");
		Set<String> paths = new TreeSet<String>();
		for(Element e: new DFSElementIterable(root))
		{
			if(e.getTagName() == "LINE") {
				String speaker = getSpeaker(e);
				speaker = speaker == null ? "<MISSING>" : speaker;
				String line = extractText(e);
				System.out.println(String.format("%s: %s\n", speaker, line));
			}
			Iterable<Element> fromRoot = ancestorsFromRoot(e);
			Iterable<String> tagNames = Iterables.transform(
					fromRoot,
					new Function<Element,String>() {
						public String apply(Element e) {
							return e.getTagName();
						}
					});
			String path = joiner.join(tagNames); 
			paths.add(path);
		}
		for(String path: paths) {
			System.out.println(path);
		}
	}

	private static DocumentBuilder instantiateBuilder()
		throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(false); 
		factory.setNamespaceAware(true);
		factory.setFeature("http://xml.org/sax/features/namespaces", false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		return factory.newDocumentBuilder();
	}

	private static Iterable<Element> ancestorsFromRoot(Element e) {
		List<Element> fromHere = Lists.newArrayList(e);
		while(e != null) {
			Node n = e.getParentNode();
			if(n != null && n.getNodeType() == Node.ELEMENT_NODE) {
				e = (Element) n;
				fromHere.add(e);
			} else {
				e = null;
			}
		}
		return Lists.reverse(fromHere);
	}

	private static String extractText(Element e) {
			NodeList children = e.getChildNodes();
			if(children.getLength() < 1) {
				return null;
			}
			Node child = children.item(0);
			if(child.getNodeType() != Node.TEXT_NODE) {
				return null;
			}
			Text text = (Text) child;
			return text.getData();
	}

	private static String getSpeaker(Element e) {
		while(e != null && e.getTagName() != "SPEAKER") {
			Node n = e.getPreviousSibling();
			while(n != null && n.getNodeType() != Node.ELEMENT_NODE) {
				n = n.getPreviousSibling();
			}
			if(n != null) {
				e = (Element) n;
			} else {
				e = null;
			}
		}

		if(e == null) {
			return null;
		} else {
			return extractText(e);
		}
	}
/*
	private static Iterable<Element> elementChildrenOf(Node n) {
		return Iterables.filter(
				NodeListIterable.wrap(n.getChildNodes()),
				new Predicate<Node>(){
					public boolean apply(Node x) {
						return x.getNodeType() == Node.ELEMENT_NODE;
					} }).transform(
				new Function<Node, Element>(){
					public Element apply(Node x) {
						return (Element) x;
					}
				});

	}
	*/
}
