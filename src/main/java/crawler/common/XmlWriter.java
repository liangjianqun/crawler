package crawler.common;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

public class XmlWriter {
	private static Logger logger = Logger.getLogger(XmlWriter.class);
	private DocumentBuilder builder_ = null;
	private Document doc_ = null;
	
	public Document Doc() {
		return doc_;
	}
	
	public XmlWriter() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		try { 
			builder_ = factory.newDocumentBuilder(); 
		} catch (ParserConfigurationException pce) { 
			logger.error(pce); 
		}
		doc_ = builder_.newDocument();
	}

	public Element CreateRoot(String root) {
		Element e = doc_.createElement(root);
		doc_.appendChild(e);
		return e;
	}
	
	public Element CreateElement(Element father, String elementName, String attributeName, String attributeValue) {
		Element e = doc_.createElement(elementName);
		e.setAttribute(attributeName, attributeValue);
		father.appendChild(e);
		return e;
	}
	
	public Text CreateTextNode(Element father, String nodeName) {
		Text text = doc_.createTextNode(nodeName);
		father.appendChild(text);
		return text;
	}	
	
	public static void main(String[] args) {
		XmlWriter xml = new XmlWriter();
		Element father = xml.CreateRoot("root");
		father = xml.CreateElement(father, "xx1", "aname1", "avalue1");
		father = xml.CreateElement(father, "xx2", "aname2", "avalue2");
		Text text = xml.CreateTextNode(father, "textnode");
		Document doc = xml.Doc();
		//logger.info("" + doc.);
	}
}

