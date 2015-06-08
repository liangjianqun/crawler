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
			System.out.println(element.getName());
		}

		public void visit(Attribute attr) {
			System.out.println(attr.getName());
			System.out.println(attr.getValue());
		}
	}
	
	public void Iter(Element root) {
		root.accept(new MyVisitor());
	}

	public void bar(Document document) {
		List list = document.selectNodes("/foo/bar");
		Node node = document.selectSingleNode("/foo/bar/author");
		String name = node.valueOf("xxx");
	}
	
	public static void main(String[] args) {
		String file = "/Users/liangjianqun/Documents/java/workspace/crawler/src/main/resources/kaixinwx.xml";
		try {
			XmlReader xml = new XmlReader();
			Document doc = XmlReader.ReadConfFile(file);
			Element root = doc.getRootElement();
			xml.Iter(root);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		
	}
}
