package crawler.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.util.NodeList;

import crawler.common.Category;
import crawler.common.Pair;
import crawler.common.Utils;
import crawler.common.XmlReader;
import crawler.novel.Article;

public class ArticleParser {
	private static XmlReader xml_;
	
	public static String kReplaceMent = "极品书";

	static {
		String xmlfile = "./kaixinwx.xml";
		XmlReader xml_ = new XmlReader(xmlfile);
	};
	
	public static String Replace(List<String> toReplace, String str) {
		for (int i = 0; toReplace != null && i < toReplace.size(); ++i) {
			str = str.replaceAll(toReplace.get(i), kReplaceMent);
		}
		
		return str;
	}
	
	public static Article ParseSome(String html, List<String> toReplace) {
		Article article = new Article();
		MetaTag tag = FastParser.parseTag(html, MetaTag.class, "property", "og:title"); 
		if (tag != null) {
			article.setArticlename(tag.getMetaContent());
		}
		
		Div div = FastParser.parseTag(html, Div.class, "class", "chapterNum");
		if (div != null) {
			NodeList node = div.getChildren();
			if (node.size() > 0) {
				List<Pair<String, String>> list = FastParser.ExtractLink(node.toHtml());
				for (int i = 0; i < list.size(); i++) {
					Pair<String, String> p = list.get(i);
					if (p.first().length() == 0 || p.second().length() == 0) {
						list.remove(i);
					}
				}
				if (list.size() > 0){
					article.setLastchapterno(list.size());
					article.setLastchapter(list.get(list.size() - 1).first());
				}
				article.setChapterList(list);
			}
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:image"); 
		if (tag != null) {
			article.setNovelCover(tag.getMetaContent());
		}
		
		return article;
	}
	
	public static Article Parse(String html, List<String> toReplace) {
		Article article = new Article();
		//String pattern = xml_.GetTextByName("//RuleConfigInfo/NovelIntro/Pattern");
		MetaTag tag = FastParser.parseTag(html, MetaTag.class, "property", "og:title"); 
		if (tag != null) {
			article.setArticlename(tag.getMetaContent());
		}
		tag = FastParser.parseTag(html, MetaTag.class, "name", "keywords"); 
		if (tag != null) {
			String str = tag.getMetaContent();
			str = Replace(toReplace, str);
			article.setKeywords(str);
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:novel:author"); 
		if (tag != null) {
			article.setAuthor(tag.getMetaContent());
			//article.setAuthorid(0);
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:novel:category"); 
		if (tag != null) {
			article.setCategory(Category.Instance().GetBigCategory(tag.getMetaContent()));
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:novel:category"); 
		if (tag != null) {
			article.setSubcategory(Category.Instance().GetSmallCategory(tag.getMetaContent()));
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:description"); 
		if (tag != null) {
			String str = tag.getMetaContent();
			str = Replace(toReplace, str);
			article.setIntro(str);
		}
		
		Div div = FastParser.parseTag(html, Div.class, "class", "chapterNum");
		if (div != null) {
			NodeList node = div.getChildren();
			if (node.size() > 0) {
				List<Pair<String, String>> list = FastParser.ExtractLink(node.toHtml());
				for (int i = 0; i < list.size(); i++) {
					Pair<String, String> p = list.get(i);
					if (p.first().length() == 0 || p.second().length() == 0) {
						list.remove(i);
					}
				}
				if (list.size() > 0){
					article.setLastchapterno(list.size());
					article.setLastchapter(list.get(list.size() - 1).first());
				}
				article.setChapterList(list);
			}
		}
		//article.setSize(100);//TODO
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:novel:status"); 
		if (tag != null) {
			article.setFullflag(!tag.getMetaContent().equals("连载中"));
		}
		tag = FastParser.parseTag(html, MetaTag.class, "property", "og:image"); 
		if (tag != null) {
			article.setNovelCover(tag.getMetaContent());
		}
		
		return article;
	}
	
	public static void main(String[] args) {
		String file = "article_test.html";
		String xmlfile = "/Users/liangjianqun/Documents/java/workspace/crawler/src/main/resources/kaixinwx.xml";
		String html = null;
		try {
			html = new String(Utils.ReadFile(file));
		} catch (IOException e) {
			e.printStackTrace();
		}	  
		List<String> toReplace = new ArrayList<String>();
		toReplace.add("开心");
		Article article = ArticleParser.Parse(html, toReplace);
		logger.info(article.Sql());
	}

}
