package io.github.ble.shax;

import static io.github.ble.shax.ShaXPath.acts;
import static io.github.ble.shax.ShaXPath.fullSpeechText;
import static io.github.ble.shax.ShaXPath.scenes;
import static io.github.ble.shax.ShaXPath.speaker;
import static io.github.ble.shax.ShaXPath.speeches;
import io.github.ble.shax.util.IteratorDual;
import io.github.ble.shax.util.Splitters;
import io.github.ble.shax.util.prob.FixedMarkovBuilder;
import io.github.ble.shax.util.prob.MarkovChain;
import io.github.ble.shax.util.prob.WordChain;

import java.io.File;
import java.util.Date;
import java.util.Random;

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
			MarkovChain<String, String> learned = processLines(new File(arg));
			Random r;
			StringBuilder sb;
			for(int i = 0; i < 100; i++) {
				r = new Random((new Date()).getTime() * i);
				sb = new StringBuilder();
				Iterable<String> chain = learned.fromState("look", r);
				//Iterable<String> chain = learned.fromInitialDistribution(r);
				for(String word: chain) {
					sb.append(word).append(" ");
				}
				System.out.println(sb.toString());
			}
		}
	}

	static MarkovChain<String,String> processLines(File f) throws Exception {
		DocumentBuilder builder = instantiateBuilder();
		Document doc = builder.parse(f);
		Element root = doc.getDocumentElement();

		int actCount = 1;

		FixedMarkovBuilder<String, String> mbuilder = new WordChain.LastWord();

		for(Node act: acts.apply(root)) {
			int sceneCount = 1;
			for(Node scene: scenes.apply(act)) {
				for(Node speech: speeches.apply(scene)) {
					String who = speaker.apply(speech);
					String what = fullSpeechText.apply(speech);
					for(String sentence: Splitters.sentences.apply(what)) {
						//System.out.println(who + ":");

						IteratorDual<String> feeder = null;
						for(String word: Splitters.words.apply(sentence)) {
							if(feeder == null)
								feeder = mbuilder.collectorStartingAt(word);
							else
								feeder.next(word.toLowerCase());
							//System.out.print("|"+word+"|");
						}
						feeder.next(WordChain.terminalWord);
						feeder.done();
						//System.out.println();
						//System.out.println(String.format("%s: %s", who, sentence));
					}
				}
				sceneCount++;
			}
			actCount++;
		}
		return mbuilder.build();
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
