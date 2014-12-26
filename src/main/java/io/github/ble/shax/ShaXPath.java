package io.github.ble.shax;

import io.github.ble.shax.util.XPathFunc;

import org.w3c.dom.Node;

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
	
	private ShaXPath() {}
}
