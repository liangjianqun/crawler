package crawler.parser;

import java.util.ArrayList;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import crawler.common.Api;
import crawler.common.Pair;
import crawler.core.Crawler;

public class FastParser {
	public static List<Pair<String, String>> ExtractLink(String input) {
		List<Pair<String, String>> list = new ArrayList();
	    try
	    {
			Parser parser = new Parser();
			parser.setInputHTML(input);

			NodeList nodeList = parser
					.extractAllNodesThatMatch(new NodeFilter() {
						public boolean accept(Node node) {
							if (node instanceof LinkTag) {
								return true;
							}
							return false;
						}

					});
			for (int i = 0; i < nodeList.size(); i++) {
				LinkTag n = (LinkTag) nodeList.elementAt(i);
				Pair<String, String> pair = new Pair<String, String>(n.getStringText(), n.extractLink());
				list.add(pair);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	    return list;
	}
	
	public static <T extends TagNode> List<T> parseTags(String html,
			final Class<T> tagType, final String attributeName,
			final String attributeValue) {
		try {
			Parser parser = new Parser();
			parser.setInputHTML(html);

			NodeList tagList = parser.parse(new NodeFilter() {
				public boolean accept(Node node) {
					if (node.getClass() == tagType) {
						T tn = (T) node;
						String attrValue = tn.getAttribute(attributeName);
						if (attrValue != null
								&& attrValue.equals(attributeValue)) {
							return true;
						}
					}
					return false;
				}
			});
			List<T> tags = new ArrayList<T>();
			for (int i = 0; i < tagList.size(); i++) {
				T t = (T) tagList.elementAt(i);
				tags.add(t);
			}
			return tags;
		} catch (ParserException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static <T extends TagNode> T parseTag(String html,
			final Class<T> tagType, final String attributeName,
			final String attributeValue) {
		List<T> tags = parseTags(html, tagType, attributeName, attributeValue);
		if (tags != null && tags.size() > 0) {
			return tags.get(0);
		}
		return null;
	}

	public static <T extends TagNode> T parseTag(String html,
			final Class<T> tagType) {
		return parseTag(html, tagType, null, null);
	}

	public static <T extends TagNode> List<T> parseTags(String html,
			final Class<T> tagType) {
		return parseTags(html, tagType, null, null);
	}
	
	public static void main(String[] args) {
		String url = "http://www.kaixinwx.com/reader/34692/14429750.html";
		
		Crawler crawler = new Crawler();
		byte[] result = crawler.FetchByGet(url, Crawler.DefaultProperties(), Api.kFetchRetry);
		
        List<Div> authors=FastParser.parseTags(new String(result), Div.class, "class", "mainContenr"); 
		for (Div div : authors) {
			System.out.println(div.getStringText());
		}
	}
}
