package crawler.common;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

public class XmlWriter {
	private DocumentBuilder builder_ = null;
	private Document doc_ = null;
	
	public XmlWriter() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
		try { 
			builder_ = factory.newDocumentBuilder(); 
		} catch (ParserConfigurationException pce) { 
			System.err.println(pce); 
		}
		doc_ = builder_.newDocument();
	}


	Element root = doc.createElement("学生花名册"); 
	//根元素添加上文档 
	doc.appendChild(root); 
	//建立"学生"元素，添加到根元素 
	Element student = doc.createElement("学生"); 
	student.setAttribute("性别", studentBean.getSex()); 
	root.appendChild(student); 
	//建立"姓名"元素，添加到学生下面，下同 
	Element name = doc.createElement("姓名"); 
	student.appendChild(name); 
	Text tName = doc.createTextNode(studentBean.getName()); 
	name.appendChild(tName);
	Element age = doc.createElement("年龄"); 
	student.appendChild(age); 
	Text tAge = doc.createTextNode(String.valueOf(studentBean.getAge())); 
	age.appendChild(tAge);
	
	
	
	public static void main(String[] args) {

	}
}

