package crawler.parser;

import java.io.IOException;
import java.util.List;

import org.htmlparser.tags.Div;
import org.htmlparser.tags.MetaTag;

import crawler.common.Utils;
import crawler.common.XmlReader;

public class ArticleParser {
	private XmlReader xml_;
	private String html_;
	
	private String novelIntro_;
	
	public ArticleParser(String html, XmlReader xml) {
		xml_ = xml;
		html_ = html;
		Parse();
	}
	
	private int Parse() {
		
		String pattern = xml_.GetTextByName("//RuleConfigInfo/NovelIntro/Pattern");
		//System.out.println(pattern);
		List<MetaTag> authors=FastParser.parseTags(html_, MetaTag.class, "property", "og:description"); 
		if (authors != null) {
			novelIntro_ = authors.get(0).getMetaContent();
		}
		System.out.println(novelIntro_);
		
		return 0;
	}
	
	public static void main(String[] args) {
		String file = "./article.html";
		String xmlfile = "/Users/liangjianqun/Documents/java/workspace/crawler/src/main/resources/kaixinwx.xml";
		XmlReader xml = new XmlReader(xmlfile);
		String html = null;
		try {
			html = new String(Utils.ReadFile(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArticleParser parser = new ArticleParser(html, xml);
	}

}
