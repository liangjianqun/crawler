package crawler.common;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

public class XmlReader {
	private Document doc_;
	
	public XmlReader(Document doc) {
		doc_ = doc;
	}
	
	public XmlReader(String fileName) {
		try {
			doc_ = ReadConfFile(fileName);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}
	
	public static Document ReadConfFile(String fileName)
			throws MalformedURLException, DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(new File(fileName));
		return document;
	}

	public Element getRootElement(Document doc) {
		return doc.getRootElement();
	}

	// Call root.accept(new MyVisitor());
	public class MyVisitor extends VisitorSupport {
		public void visit(Element element) {
			System.out.print("["+element.getName());
			logger.info(":"+element.getText()+"]");
		}

		public void visit(Attribute attr) {
			System.out.print("["+attr.getName());
			logger.info(":"+attr.getValue()+"]");
		}
	}
	
	public void Iter(Element root) {
		root.accept(new MyVisitor());
	}

	public void bar(Document document, String element) {
		Node node = document.selectSingleNode(element);
		logger.info(node);
		String name = node.valueOf("xxx");	
	}
	
	public String GetTextByName(String name) {
		if (doc_ == null) {
			return "";
		}
		Node node = doc_.selectSingleNode(name);
		if (node == null) {
			return "";
		}
		return node.getText();
	}
	
	public static void main(String[] args) {
		String file = "/Users/liangjianqun/Documents/java/workspace/crawler/src/main/resources/kaixinwx.xml";
		XmlReader xml = new XmlReader(file);
		String node = xml.GetTextByName("//RuleConfigInfo/NovelCover/Pattern");
		logger.info(node);		
	}
}
