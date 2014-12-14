package io.github.ble;

//Types that we might throw
import io.github.ble.shax.util.DFSElementIterable;
import io.github.ble.shax.util.NodeListIterable;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;



//DOM-style XML parsing types
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

/**
 * Hello world!
 *
 */
public class App 
{
	public static void main( String[] args )
		throws Exception
	{
		for(String arg: args) {
			//processFile(new File(arg));
			countLines(new File(arg));
		}
	}

	static void countLines(File f) throws Exception {
		DocumentBuilder builder = instantiateBuilder();
		Document doc = builder.parse(f);
		Element root = doc.getDocumentElement();
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpi = xpf.newXPath();
		XPathExpression xpe = xpi.compile("//LINE");

		NodeList result = (NodeList) xpe.evaluate(root, XPathConstants.NODESET);

		Random r = new Random();
		for(int ix = 0; ix < 100; ix++) {
			int selected = r.nextInt(result.getLength());
			describeLine(result.item(selected));
		}


	}

	static final XPathFactory xpf = XPathFactory.newInstance();
	static final XPath xpi = xpf.newXPath();

	private static XPathExpression ForceCompile(XPath xp, String expText) {
		try {
			return xp.compile(expText);
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	static final XPathExpression speakerOf = ForceCompile(xpi,"./preceding-sibling::SPEAKER[text()]");
	static final XPathExpression lineNumberOf = ForceCompile(xpi, "count(./preceding::LINE)");
	static final XPathExpression textOf = ForceCompile(xpi, "self::node()[text()]");
	static final XPathExpression sceneOf = ForceCompile(xpi, "count(./ancestor::SCENE/preceding-sibling::SCENE) + 1");
	static final XPathExpression actOf = ForceCompile(xpi, "count(./ancestor::ACT/preceding-sibling::ACT) + 1");
	static final XPathExpression speechInScene = ForceCompile(xpi, "count(./ancestor::SPEECH/preceding-sibling::SPEECH) + 1");
	static final XPathExpression lineInSpeech = ForceCompile(xpi, "count(./preceding-sibling::LINE) + 1");
	static final XPathExpression linesInSpeech = ForceCompile(xpi, "count(./ancestor::SPEECH/child::LINE)");

	static int evalInt(Node n, XPathExpression xpe) throws XPathExpressionException {
		return ((Double) xpe.evaluate(n, XPathConstants.NUMBER)).intValue();
	}

	static String evalString(Node n, XPathExpression xpe) throws XPathExpressionException {
		return ((String) xpe.evaluate(n, XPathConstants.STRING));
	}

	static void describeLine(Node n) throws Exception {

		String speaker = (String) speakerOf.evaluate(n, XPathConstants.STRING);
		/*int lineNumber; {
			Double lineNum = (Double) lineNumberOf.evaluate(n, XPathConstants.NUMBER);
			lineNumber = lineNum.intValue();
		}*/
		String lineText = evalString(n, textOf);
		int actNumber = evalInt(n, actOf);
		int sceneNumber = evalInt(n, sceneOf);
		int speechNumber = evalInt(n, speechInScene);
		int lineNumber = evalInt(n, lineInSpeech);
		int totalLinesInSpeech = evalInt(n, linesInSpeech);

		System.out.println(
				String.format(
						"Act %d scene %d, speech %d, line %d of %d\n\t %s: \"%s\"",
						actNumber,
						sceneNumber,
						speechNumber,
						lineNumber,
						totalLinesInSpeech,
						speaker, lineText));


	}

	static String extractChildText(Node n) throws Exception {
		StringBuilder sb = new StringBuilder();

		for(Node c: NodeListIterable.wrap(n.getChildNodes())) {
			if(c.getNodeType() == Node.TEXT_NODE) {
				sb.append( ((Text)c).getData());
			}
		}

		return sb.toString();
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
