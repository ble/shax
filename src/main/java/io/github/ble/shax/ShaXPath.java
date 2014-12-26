package io.github.ble.shax;

import io.github.ble.shax.util.XPathFunc;

import org.w3c.dom.Node;

import com.google.common.base.Function;

public class ShaXPath {
	public static final XPathFunc<Iterable<Node>>
		acts = XPathFunc.nodesExp("//ACT"),
		scenes = XPathFunc.nodesExp("./SCENE"),
		speeches = XPathFunc.nodesExp("./SPEECH"),
		lines = XPathFunc.nodesExp("./LINE");

	public static final XPathFunc<Integer>
		actNumber = XPathFunc.intExp("count(./preceding-sibling::ACT) + 1"),
		sceneNumber = XPathFunc.intExp("count(./preceding-sibling::SCENE) + 1");

	public static final XPathFunc<String>
		speaker = XPathFunc.stringExp("./child::SPEAKER[text()]"),
		lineText = XPathFunc.stringExp("self::node()[text()]");

	public static final Function<Node, String>
		fullSpeechText = new Function<Node, String>() {

		public String apply(Node speech) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for(Node line: lines.apply(speech)) {
				if(!first)
					sb.append(" ");
				first = false;
				sb.append(lineText.apply(line));
			}
			return sb.toString();
		}
	};

	//Whoops, I'm using the Oracle-provided XPath implementation that doesn't
	//support the XPath 2.0 functions like string-join!
	public static final XPathFunc<String>
		fullSpeechText2PointOh = XPathFunc.stringExp("fn:string-join('a','b', ' ')");

	private ShaXPath() {}
}
