package io.github.ble.shax;

//Types that we might throw
import static io.github.ble.shax.ShaXPath.acts;
import static io.github.ble.shax.ShaXPath.lineText;
import static io.github.ble.shax.ShaXPath.lines;
import static io.github.ble.shax.ShaXPath.scenes;
import static io.github.ble.shax.ShaXPath.speaker;
import static io.github.ble.shax.ShaXPath.speeches;

import java.io.File;


//DOM-style XML parsing types
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

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
			processLines(new File(arg));
		}
	}

	static void processLines(File f) throws Exception {
		DocumentBuilder builder = instantiateBuilder();
		Document doc = builder.parse(f);
		Element root = doc.getDocumentElement();

		int lineCount = 1;
		int actCount = 1;

		for(Node act: acts.apply(root)) {
			int sceneCount = 1;
			for(Node scene: scenes.apply(act)) {
				for(Node speech: speeches.apply(scene)) {
					String who = speaker.apply(speech);
					for(Node line: lines.apply(speech)) {
						String what = lineText.apply(line);
						String display = String.format(
							"A%dS%d %s: \"%s\"",
							actCount,
							sceneCount,
							who,
							what);
						System.out.println(display);

						lineCount++;
					}
				}
				sceneCount++;
			}
			actCount++;
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
}
